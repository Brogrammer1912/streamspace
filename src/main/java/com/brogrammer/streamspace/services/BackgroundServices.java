package com.brogrammer.streamspace.services;

import com.brogrammer.streamspace.content.Indexer;
import com.brogrammer.streamspace.preferences.Preference;
import com.brogrammer.streamspace.preferences.UserPreferences;
import com.brogrammer.streamspace.torrentengine.TorrentDownloadManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackgroundServices {

    final Indexer indexer;
    final TorrentDownloadManager torrentDownloadManager;
    final UserPreferences userPreferences;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent() {
        log.info("Indexing Local Media");

        // Check if user preferences need to be configured
        if (userPreferences.count() == 0) {
            configurePreferences();
        }

        // Index local media asynchronously
        indexer.indexLocalMedia(new HashSet<>(ContentDirectoryServices.mediaFolders.values()))
                .thenRun(torrentDownloadManager::startAllPendingDownloads) // Start background downloads once indexing is done
                .exceptionally(throwable -> { // Handle any errors during indexing or download initiation
                    log.error("Error during media indexing or starting background downloads", throwable);
                    return null; // Return null or handle the error as appropriate
                });
    }

    private void configurePreferences() {
        var features = List.of(
                new Preference().setPrefId(1).setName("DARK_MODE_ENABLED")
        );
        userPreferences.saveAll(features);
    }

    /* private void runAsStructuredConcurrent(List<BackgroundDownloadTask> backgroundDownloadTasks) {
        ConJob conJob = new ConJob();
        conJob.executeAll(backgroundDownloadTasks);
    }*/
}
