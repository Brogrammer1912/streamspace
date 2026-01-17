package com.brogrammer.streamspace.content;

import bt.metainfo.TorrentFile;
import bt.metainfo.TorrentId;
import com.brogrammer.streamspace.services.ContentDirectoryServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class Indexer {

    @Value("${video.file.extensions.streaming}")
    private String videoFileExtensions;

    @Value("${audio.file.extensions.streaming}")
    private String audioFileExtensions;
    
    private String cachedGlobPattern;

    private final UnaryOperator<String> decodePathSegment = pathSegment -> UriUtils.decode(pathSegment, StandardCharsets.UTF_8.name());
    private final Function<Path, String> decodeContentType = fileEntryPath -> MediaTypeFactory.getMediaType(new FileSystemResource(fileEntryPath)).orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
    final ContentDirectoryServices contentDirectoryServices;
    final VideoRepository videoRepository;
    final MusicRepository musicRepository;

    public void indexMovie(TorrentFile file, String torrentName, String fileName, TorrentId torrentId, String contentMimeType) {
        log.info("FileName {}", fileName);
        log.info("TorrentName {}", torrentName);
        videoRepository.deleteAllByName(fileName);
        Video video = createVideoEntityTorrentSource(file, torrentName, fileName, torrentId, contentMimeType);
        log.debug("Content ID {}", contentDirectoryServices.getMoviesContentStore() + torrentName + "/" + fileName);
        videoRepository.save(video);
    }

    public void indexMusic(TorrentFile file, String torrentName, String fileName, TorrentId torrentId) {
        log.info("FileName {}", fileName);
        log.info("TorrentName {}", torrentName);
        Song song = new Song();
        song.setContentLength(file.getSize());
        song.setName(fileName);
        song.setSummary(fileName);
        song.setContentMimeType(decodeContentType.apply(Paths.get(fileName)));
        log.info("Content ID for music: {}", contentDirectoryServices.getMusicContentStore() + torrentName + "/" + fileName);
        song.setContentId(contentDirectoryServices.getMusicContentStore() + torrentName + "/" + fileName);
        song.setSongId(torrentId.toString().toUpperCase());
        song.setSource(SOURCE.TORRENT);
        musicRepository.save(song);
    }

    public CompletableFuture<Object> indexLocalMedia(Set<String> locations) {
        return findLocalMediaFiles(locations)
                .thenCompose(paths -> {
                    List<Path> musicPaths = filterPaths(paths, audioFileExtensions.split(","));
                    List<Path> videoFilePaths = filterPaths(paths, videoFileExtensions.split(","));

                    List<Video> finalVideos = createVideoEntities(videoFilePaths);
                    List<Song> finalSongs = createMusicEntities(musicPaths);

                    videoRepository.saveVideos(finalVideos);
                    musicRepository.saveMusicList(finalSongs);

                    return CompletableFuture.completedFuture(null);
                })
                .exceptionally(
                        throwable -> {
                            log.error("Error indexing local media files", throwable);
                            throw new RuntimeException(throwable);
                        }
                );
    }

    public CompletableFuture<List<Path>> findLocalMediaFiles(Set<String> locations) {
        final String pattern = buildGlobPattern();
        List<CompletableFuture<List<Path>>> futures = new ArrayList<>();
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher(pattern);

        for (String location : locations) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                List<Path> matchingPaths = new ArrayList<>();
                try {
                    Path start = Paths.get(location);
                    if (Files.exists(start)) {
                        Files.walkFileTree(start, new SimpleFileVisitor<>() {
                            @Override
                            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
                                if (matcher.matches(path)) {
                                    matchingPaths.add(path);
                                }
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    }
                } catch (IOException e) {
                    log.error("Error finding personal media files in location: {}", location, e);
                }
                return matchingPaths;
            }));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .flatMap(future -> future.join().stream())
                        .toList());
    }

    private String buildGlobPattern() {
        if (cachedGlobPattern == null) {
            String extensions = Stream.concat(
                            Arrays.stream(videoFileExtensions.split(",")),
                            Arrays.stream(audioFileExtensions.split(",")))
                    .map(String::trim)
                    .map(ext -> ext.startsWith(".") ? ext.substring(1) : ext)
                    .collect(Collectors.joining(","));
            cachedGlobPattern = "glob:**/*.{".concat(extensions).concat("}");
        }
        return cachedGlobPattern;
    }

    private List<Path> filterPaths(List<Path> paths, String... extensions) {
        Set<String> extensionSet = Arrays.stream(extensions)
                .map(String::toLowerCase)
                .map(String::trim)
                .collect(Collectors.toSet());
        return paths.parallelStream()
                .filter(path -> {
                    String pathString = path.toString().toLowerCase();
                    return extensionSet.stream().anyMatch(pathString::endsWith);
                })
                .toList();
    }

    private List<Video> createVideoEntities(List<Path> paths) {
        Path userHomePath = Paths.get(ContentDirectoryServices.userHomePath);

        return paths.parallelStream().map(entry -> {
            try {
                String encodedFileName = decodePathSegment.apply(entry.getFileName().toString());
                Path relativePath = userHomePath.relativize(entry);

                return new Video()
                        .setName(encodedFileName)
                        .setContentLength(Files.size(entry))
                        .setSummary(entry.getFileName().toString())
                        .setContentId(File.separator + decodePathSegment.apply(relativePath.toString()))
                        .setContentMimeType(decodeContentType.apply(entry))
                        .setMovieCode(encodedFileName)
                        .setSource(SOURCE.LOCAL);
            } catch (IOException e) {
                log.error("Error creating video entity for {}", entry, e);
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

    private List<Song> createMusicEntities(List<Path> paths) {
        Path userHomePath = Paths.get(ContentDirectoryServices.userHomePath);

        return paths.parallelStream().map(entry -> {
            try {
                log.debug(entry.toString());
                String encodedFileName = decodePathSegment.apply(entry.getFileName().toString());
                Path relativePath = userHomePath.relativize(entry);
                return new Song()
                        .setName(encodedFileName)
                        .setContentLength(Files.size(entry))
                        .setSummary(entry.getFileName().toString())
                        .setContentId(File.separator + decodePathSegment.apply(relativePath.toString()))
                        .setContentMimeType(decodeContentType.apply(entry))
                        .setSongId(encodedFileName)
                        .setSource(SOURCE.LOCAL);
            } catch (IOException e) {
                log.error("Error creating song entity for {}", entry, e);
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

    private Video createVideoEntityTorrentSource(TorrentFile file, String torrentName,
                                                 String fileName, TorrentId torrentId,
                                                 String contentMimeType) {
        return new Video()
                .setContentLength(file.getSize())
                .setName(fileName)
                .setCreated(LocalDateTime.now())
                .setSummary(fileName)
                .setContentMimeType(contentMimeType)
                .setContentId(contentDirectoryServices.getMoviesContentStore() + torrentName + "/" + fileName)
                .setMovieCode(torrentId.toString().toUpperCase())
                .setSource(SOURCE.TORRENT);
    }
}
