package com.brogrammer.streamspace.downloads;

import com.brogrammer.streamspace.common.CONTENTTYPE;
import com.brogrammer.streamspace.common.DOWNLOADTYPE;
import com.brogrammer.streamspace.torrentengine.TorrentDownloadManager;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/download")
@RequiredArgsConstructor
public class DownloadsController {

    final Downloads downloads;
    final TorrentDownloadManager torrentDownloadManager;

    @GetMapping("")
    String getAllDownloads(Model model) {
        List<DownloadTask> listOfDownloads = downloads.findAll();

        if (listOfDownloads.isEmpty()) {
            return "downloads :: showNoDownloads";
        } else {
            model.addAttribute("tasks", listOfDownloads);
            return "downloads :: showAllDownloads";

        }
    }

    @ResponseBody
    @GetMapping(value = "/count", produces= MediaType.TEXT_HTML_VALUE)
    String count() {
        return String.valueOf(downloads.count());
        //return "<span class=\"badge text-bg-secondary\">"+ downloadTaskRepository.count() +"</span>";
    }

    @GetMapping("/form")
    String downloadForm() {
        return "downloads :: downloadTorrent";
    }

    /*@PostMapping("/togglePause")
    public ResponseEntity<String> togglePause() {
        TorrentClient.toggleStartStop();
        return ResponseEntity.ok("Toggled Pause! Click to Resume");
    }*/

    @HxRequest
    @PostMapping("/torrent")
    String downloadTorrent(
            @RequestParam("selectedOption") String torrentHash,
            @RequestParam(value = "sequentialCheck", required = false) String sequentialCheck,
            @RequestParam(value = "torrentName", required = false) String torrentName,
            Model model) {
        log.info("Selected Option: {}", torrentHash);
        log.info("Strategy: {}", sequentialCheck);
        model.addAttribute("torrentHash", torrentHash);

        DownloadTask task = new DownloadTask(torrentHash, torrentName !=null ? torrentName:torrentHash, torrentHash, CONTENTTYPE.VIDEO);
        if (sequentialCheck != null && sequentialCheck.equals("on")) {
            task.setDownloadType(DOWNLOADTYPE.SEQUENTIAL);
        }
        torrentDownloadManager.startDownload(task);
        return "downloads :: downloadProgress";
    }

    @HxRequest
    @PostMapping("/torrent/{torrentHash}")
    String downloadTorrent(Model model,@PathVariable String torrentHash) {
        log.info("Selected Option: {}", torrentHash);

        DownloadTask task = new DownloadTask(torrentHash, torrentHash, torrentHash, CONTENTTYPE.AUDIO);
        task.setDownloadType(DOWNLOADTYPE.SEQUENTIAL);
        torrentDownloadManager.startDownload(task);
        model.addAttribute("torrentHash", torrentHash);
        return "downloads :: downloadProgress";
    }

    @HxRequest
    @PostMapping("/torrent/file")
    String downloadTorrent(@RequestParam("torrentFile") MultipartFile torrentFile,
                          @RequestParam(value = "sequentialCheck", required = false) String sequentialCheck,
                          Model model) {
        try {
            if (torrentFile.isEmpty()) {
                throw new IllegalArgumentException("Torrent file is empty");
            }

            // Validate file extension
            String fileName = torrentFile.getOriginalFilename();
            if (fileName == null || !fileName.endsWith(".torrent")) {
                throw new IllegalArgumentException("Invalid torrent file");
            }

            // Extract torrent hash from the file using proper Bencode parsing
            String torrentHash = torrentDownloadManager.extractTorrentHash(torrentFile.getBytes());

            // Convert MultipartFile to File for storage
            File metaInfoFile = torrentDownloadManager.convertMultipartFileToFile(torrentFile.getBytes(), fileName);

            // Create a download task with proper initialization
            String torrentName = fileName.substring(0, fileName.lastIndexOf('.'));
            DownloadTask task = new DownloadTask(torrentHash, torrentName, torrentHash, CONTENTTYPE.VIDEO);
            // Set the metainfo file
            task.setMetaInfoFile(metaInfoFile);

            if (sequentialCheck != null && sequentialCheck.equals("on")) {
                task.setDownloadType(DOWNLOADTYPE.SEQUENTIAL);
            }
            // Start the download
            torrentDownloadManager.startDownload(task);

            model.addAttribute("torrentHash", torrentHash);
            return "downloads :: downloadProgress";
        } catch (Exception e) {
            log.error("Error processing torrent file upload: {}", e.getMessage(), e);
            model.addAttribute("error", "Failed to process torrent file: " + e.getMessage());
            return "downloads :: downloadTorrent";
        }
    }

    @PostMapping("/pause/{hashString}")
    ResponseEntity<String> pauseDownload(@PathVariable("hashString") String pauseHash) {
        torrentDownloadManager.pauseDownload(pauseHash);
        return ResponseEntity.ok("<i hx-post=/download/torrent/" + pauseHash+ " class=\"bi bi-arrow-clockwise\" hx-target=\"#download-container\" hx-swap=\"outerHTML\"></i>");
    }

    @HxRequest
    @DeleteMapping("/{hashString}")
    ResponseEntity<String> cancelDownload(@PathVariable("hashString") String cancelHash) {
        torrentDownloadManager.cancelDownload(cancelHash);
        return ResponseEntity.ok("Cancelled!");
    }
}
