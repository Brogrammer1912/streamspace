package com.brogrammer.streamspace.downloads;

import com.brogrammer.streamspace.common.CONTENTTYPE;
import com.brogrammer.streamspace.common.DOWNLOADTYPE;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.io.File;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class DownloadTask {

    @Id
    private String torrentHash;
    private File metaInfoFile;
    private String torrentName;
    private String movieCode;
    private double progress = 0;
    @Enumerated(EnumType.STRING)
    private CONTENTTYPE mediaType;
    @Enumerated(EnumType.STRING)
    private DOWNLOADTYPE downloadType;
    @CreatedDate
    private LocalDateTime createdDate;

    public DownloadTask(String torrentHash, String torrentName, String movieCode, CONTENTTYPE mediaType) {
        this.torrentHash = torrentHash;
        this.torrentName = torrentName;
        this.movieCode = movieCode;
        this.mediaType = mediaType;
        this.createdDate = LocalDateTime.now();
        this.downloadType = DOWNLOADTYPE.RANDOMIZED;
    }
}