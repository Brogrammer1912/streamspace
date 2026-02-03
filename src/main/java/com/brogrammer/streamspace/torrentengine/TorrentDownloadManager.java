package com.brogrammer.streamspace.torrentengine;

import com.brogrammer.streamspace.common.DOWNLOADTYPE;
import com.brogrammer.streamspace.content.Indexer;
import com.brogrammer.streamspace.downloads.Downloads;
import com.brogrammer.streamspace.services.ContentDirectoryServices;
import com.brogrammer.streamspace.downloads.DownloadTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.dampcake.bencode.Bencode;
import com.dampcake.bencode.Type;

@Slf4j
@Service
@RequiredArgsConstructor
public class TorrentDownloadManager {

    private final Map<String, TorrentClient> clients = new ConcurrentHashMap<>();
    final Downloads downloads;
    final Indexer indexer;
    final DownloadProgressHandler downloadProgressHandler;

    public void startDownload(DownloadTask downloadTask) {
        String torrentHash = downloadTask.getTorrentHash();
        TorrentClient torrentClient = clients.get(torrentHash);
        boolean isNewDownload = !downloads.existsById(torrentHash);

        try {
            if (torrentClient == null) {
                torrentClient = new TorrentClient(
                        downloadTaskToOptions(downloadTask),
                        indexer,
                        downloadProgressHandler,
                        this);
                clients.put(torrentHash, torrentClient);
            }
            torrentClient.resume();

            if (isNewDownload) {
                downloads.save(downloadTask);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void startAllPendingDownloads() {
        var downloadTasks = downloads.findAll();
        if (downloadTasks.isEmpty()) {
            log.info("No pending downloads");
        } else  {
            log.info("Starting background downloads");
            downloadTasks.forEach(this::startDownload);
        }
    }

    public void pauseDownload(String torrentHash) {
        clients.get(torrentHash).pause();
    }

    public void onComplete(String torrentHash) {
        downloads.deleteById(torrentHash);
        var torrentClient = clients.get(torrentHash);
        if (torrentClient.client.isStarted()) {
            torrentClient.client.stop();
            log.info("Torrent Client has been stopped {}", torrentHash);
        }
        clients.remove(torrentHash);
    }

    public void cancelDownload(String torrentHash) {
        clients.get(torrentHash).pause();
        downloads.deleteById(torrentHash);
        clients.remove(torrentHash);
    }

    private Options downloadTaskToOptions(DownloadTask downloadTask) {
        Options options = new Options();
        options.setMetainfoFile(downloadTask.getMetaInfoFile());
        options.setTorrentHash(downloadTask.getTorrentHash());
        options.setMagnetUri(createMagnetUri(downloadTask.getTorrentHash()));
        options.setTargetDirectory(new File(ContentDirectoryServices.mediaFolders.get(downloadTask.getMediaType())));
        options.setSeedAfterDownloaded(false);
        options.setSequential(downloadTask.getDownloadType() == DOWNLOADTYPE.SEQUENTIAL);
        options.setEnforceEncryption(true);
        options.setDisableUi(true);
        options.setDisableTorrentStateLogs(false);
        options.setVerboseLogging(false);
        options.setTraceLogging(false);
        // options.setIface(null);
        // options.setPort(BitTorrentPort);
        // options.setDhtPort(null);
        options.setDownloadAllFiles(true);
        log.info("{}", options);
        return options;
    }

    private static String createMagnetUri(String torrentHash) {
        return "magnet:?xt=urn:btih:" + torrentHash;
    }

    /**
     * Extracts torrent hash from torrent file bytes using proper Bencode parsing
     * Returns the hash in the same format as Bt library's TorrentId.toString().toUpperCase()
     */
    public String extractTorrentHash(byte[] torrentFileBytes) throws Exception {
        try {
            Bencode bencode = new Bencode();
            Map<String, Object> torrentData = bencode.decode(torrentFileBytes, Type.DICTIONARY);

            // Get the info dictionary
            Map<String, Object> info = (Map<String, Object>) torrentData.get("info");
            if (info == null) {
                throw new IllegalArgumentException("Invalid torrent file: no info dictionary found");
            }

            // Encode the info dictionary back to bytes to calculate the hash
            byte[] infoBytes = bencode.encode(info);

            // Calculate SHA-1 hash of the info dictionary (this is the torrent's info hash)
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] hash = sha1.digest(infoBytes);

            // Convert to hex string in the same format as Bt library
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            String torrentHash = hexString.toString().toUpperCase();
            log.info("Extracted torrent hash: {}", torrentHash);
            return torrentHash;

        } catch (Exception e) {
            log.error("Error extracting torrent hash: {}", e.getMessage(), e);
            throw new Exception("Failed to extract torrent hash from file", e);
        }
    }

    /**
     * Saves MultipartFile to a temporary File and returns the File object
     */
    public File convertMultipartFileToFile(byte[] fileBytes, String fileName) throws IOException {
        // Create a temporary file
        Path tempFile = Files.createTempFile("torrent_", "_" + fileName);

        // Write the bytes to the temporary file
        Files.write(tempFile, fileBytes, StandardOpenOption.WRITE);

        // Return the File object
        return tempFile.toFile();
    }
}


