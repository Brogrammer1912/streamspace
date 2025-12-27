package com.brogrammer.streamspace.content;

import bt.metainfo.TorrentFile;
import bt.metainfo.TorrentId;
import com.brogrammer.streamspace.services.ContentDirectoryServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@Slf4j
@Service
@RequiredArgsConstructor
public class Indexer {

    private final UnaryOperator<String> decodePathSegment = pathSegment -> UriUtils.decode(pathSegment, StandardCharsets.UTF_8.name());
    private final Function<Path, String> decodeContentType = fileEntryPath -> MediaTypeFactory.getMediaType(new FileSystemResource(fileEntryPath)).orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
    final ContentDirectoryServices contentDirectoryServices;
    final VideoRepository videoRepository;
    final MusicRepository musicRepository;

    public void indexMovie(TorrentFile file, String torrentName, String fileName, TorrentId torrentId, String contentMimeType) {
        log.info("FileName {}", fileName);
        log.info("TorrentName {}", torrentName);

        Video video;
        List<Video> videos = videoRepository.findAllByName(fileName);

        if (!videos.isEmpty()) {
            videos.forEach(vid -> log.info("Movie Found: {}", vid.getName()));
            videoRepository.deleteAllByName(fileName);
            video = createVideoEntity(file, torrentName, fileName, torrentId, contentMimeType);
            video.setMovieCode(torrentId.toString().toUpperCase());
            log.info("{} already indexed", fileName);
            videoRepository.save(video);
        } else {
            video = createVideoEntity(file, torrentName, fileName, torrentId, contentMimeType);
            log.debug("Content ID {}", contentDirectoryServices.getMoviesContentStore() + torrentName + "/" + fileName);
            videoRepository.save(video);
        }

    }

    public void indexMusic(TorrentFile file, String torrentName, String fileName, TorrentId torrentId) {
        log.info("FileName {}", fileName);
        log.info("TorrentName {}", torrentName);
        Song song = new Song();
        song.setContentLength(file.getSize());
        song.setName(fileName);
        song.setSummary(fileName);
        song.setContentMimeType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        log.info(contentDirectoryServices.getMoviesContentStore() + torrentName + "/" + fileName);
        song.setContentId(contentDirectoryServices.getMoviesContentStore() + torrentName + "/" + fileName);
        song.setSongId(torrentId.toString().toUpperCase());
        song.setSource(SOURCE.TORRENT);
        musicRepository.save(song);
    }

    /**
     * Concurrent indexer
     */
    public CompletableFuture<Void> indexLocalMedia(Set<String> locations) {
        return findLocalMediaFiles(locations)
                .thenCompose(paths -> {
                    List<Path> musicPaths = filterPaths(paths, ".mp3", ".flac");
                    List<Path> videoFolderPaths = filterPaths(paths, ".mp4", ".mkv", ".avi", ".mpeg");

                    try {
                        List<Video> finalVideos = createVideoEntities(videoFolderPaths);
                        List<Song> finalSongs = createMusicEntities(musicPaths);

                        CompletableFuture<Void> videosFuture = saveVideosAsync(finalVideos);
                        CompletableFuture<Void> musicFuture = saveMusicAsync(finalSongs);

                        return CompletableFuture.allOf(videosFuture, musicFuture);
                    } catch (IOException e) {
                        log.error("Error creating media entities", e);
                        return CompletableFuture.failedFuture(e);
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Error indexing media", throwable);
                    return null;
                });
    }

    public CompletableFuture<List<Path>> findLocalMediaFiles(Set<String> locations) {
        final String pattern = "glob:**/*.{mp4,mpeg,mp3,mkv,flac}";
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

    private List<Path> filterPaths(List<Path> paths, String... extensions) {
        Set<String> extensionSet = Set.of(extensions);
        return paths.parallelStream()
                .filter(path -> {
                    String pathString = path.toString().toLowerCase();
                    for (String ext : extensionSet) {
                        if (pathString.endsWith(ext)) {
                            return true;
                        }
                    }
                    return false;
                })
                .toList();
    }

    private List<Video> createVideoEntities(List<Path> paths) throws IOException {
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

    private CompletableFuture<Void> saveVideosAsync(List<Video> videos) {
        return CompletableFuture.runAsync(() -> {
            // Get all content IDs in one query
            Set<String> existingContentIds = new HashSet<>();
            videoRepository.findAllContentIds().forEach(existingContentIds::add);

            List<Video> nonExistingVideos = videos.stream()
                    .filter(video -> !existingContentIds.contains(video.getContentId()))
                    .toList();

            // Process in batches of 500
            final int batchSize = 500;
            for (int i = 0; i < nonExistingVideos.size(); i += batchSize) {
                List<Video> batch = nonExistingVideos.subList(
                    i, Math.min(i + batchSize, nonExistingVideos.size())
                );
                videoRepository.saveAll(batch);
            }
        }).thenRun(() -> log.info("Finished Indexing Videos"));
    }

    private CompletableFuture<Void> saveMusicAsync(List<Song> songs) {
        return CompletableFuture.runAsync(() -> {
            // Get all content IDs in one query to avoid N+1 problem
            Set<String> existingContentIds = new HashSet<>();
            musicRepository.findAllContentIds().forEach(existingContentIds::add);

            List<Song> nonExistingSongs = songs.stream()
                    .filter(song -> !existingContentIds.contains(song.getContentId()))
                    .toList();

            // Process in batches of 500
            final int batchSize = 500;
            for (int i = 0; i < nonExistingSongs.size(); i += batchSize) {
                List<Song> batch = nonExistingSongs.subList(
                    i, Math.min(i + batchSize, nonExistingSongs.size())
                );
                musicRepository.saveAll(batch);
            }
        }).thenRun(() -> log.info("Finished Indexing Music"));
    }

    private List<Song> createMusicEntities(List<Path> paths) throws IOException {
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

    private Video createVideoEntity(TorrentFile file, String torrentName,
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
