package com.brogrammer.streamspace.content;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.content.commons.annotations.MimeType;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class Song implements Serializable {

    @Id
    private String songId;
    private String name;
    @ContentId
    private String contentId;
    @ContentLength
    private long contentLength;
    private String summary;
    @MimeType
    private String contentMimeType;
    @Enumerated(EnumType.STRING)
    private SOURCE source;
}