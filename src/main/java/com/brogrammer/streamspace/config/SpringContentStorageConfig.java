package com.brogrammer.streamspace.config;

import com.brogrammer.streamspace.services.ContentDirectoryServices;
import lombok.RequiredArgsConstructor;
import org.springframework.content.fs.config.EnableFilesystemStores;
import org.springframework.content.fs.io.FileSystemResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@EnableFilesystemStores
@RequiredArgsConstructor
public class SpringContentStorageConfig {

    @Bean
    File filesystemRoot() {
        return new File(ContentDirectoryServices.userHomePath);
    }

    @Bean
    FileSystemResourceLoader fsResourceLoader() {
        return new FileSystemResourceLoader(filesystemRoot().getAbsolutePath());
    }
}