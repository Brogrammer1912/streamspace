package com.brogrammer.streamspace.services;

import com.brogrammer.streamspace.common.CONTENTTYPE;
import com.brogrammer.streamspace.common.ApplicationConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@Setter
@Service
public class ContentDirectoryServices {

    public static final Map<CONTENTTYPE, String> mediaFolders = new HashMap<>(3);
    public static final String userHomePath = System.getProperty("user.home");
    private String downloadsFolderPath;
    private String musicFolderPath;
    private String moviesFolderPath;
    private String musicContentStore;
    private String moviesContentStore;

    public ContentDirectoryServices() {
        String os = System.getProperty("os.name").toLowerCase();
        log.info("OS {}", os);

        if (os.contains("win")) {
            downloadsFolderPath = userHomePath + File.separator + "Downloads";
            musicFolderPath = userHomePath + File.separator + ApplicationConstants.MUSIC;
            moviesFolderPath = userHomePath + File.separator + "Videos";
            musicContentStore = ApplicationConstants.MUSIC + File.separator;
            moviesContentStore = "Videos" + File.separator;
        } else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("bsd")) {
            //Need to revisit for linux
            downloadsFolderPath = userHomePath + File.separator + "Downloads";
            musicFolderPath = userHomePath + File.separator + ApplicationConstants.MUSIC;
            moviesFolderPath = userHomePath + File.separator + "Movies";
            musicContentStore = ApplicationConstants.MUSIC + File.separator;
            moviesContentStore = "Movies" + File.separator;
        } else {
            log.warn("Unknown OS file folder structure! Defaulting to user.home...");
            downloadsFolderPath = userHomePath;
            musicFolderPath = userHomePath;
            moviesFolderPath = userHomePath;
            musicContentStore = userHomePath;
            moviesContentStore = userHomePath;
        }

        log.info("Final locations paths");
        log.info("Downloads: {}", downloadsFolderPath);
        log.info("MUSIC: {}", musicContentStore);
        log.info("MOVIES: {}", moviesContentStore);
        mediaFolders.put(CONTENTTYPE.VIDEO, moviesFolderPath);
        mediaFolders.put(CONTENTTYPE.AUDIO, musicFolderPath);
        mediaFolders.put(CONTENTTYPE.OTHER, downloadsFolderPath);
    }
}
