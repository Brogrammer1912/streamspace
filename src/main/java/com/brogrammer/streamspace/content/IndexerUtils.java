package com.brogrammer.streamspace.content;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class IndexerUtils {

    @Value("${video.file.extensions.streaming}")
    private String videoFileExtensions;
    @Value("${audio.file.extensions.streaming}")
    private String audioFileExtensions;

    public static String cachedGlobPattern;

    public String buildGlobPattern() {
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

    public String[] getVideoFileExtensions() {
        return videoFileExtensions.split(",");
    }

    public String[] getAudioFileExtensions() {
        return audioFileExtensions.split(",");
    }
}
