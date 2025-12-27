package com.brogrammer.streamspace.content;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.content.commons.annotations.ContentId;
import org.springframework.content.commons.annotations.ContentLength;
import org.springframework.content.commons.annotations.MimeType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class Video implements Serializable, Persistable<String> {

    /**
     * movieCode will be using the FileName
     * so that Android TV browsers can stream
     * videos using Native Media players rather
     * than HTML5 Video for a better UX
     */
    @Id
    private String movieCode;
    private String name;
    @CreatedDate
    private LocalDateTime created;
    private String summary;
    @ContentId
    private String contentId;
    @ContentLength
    private long contentLength;
    @MimeType
    private String contentMimeType;
    @Enumerated(EnumType.STRING)
    private SOURCE source;

    @Transient
    private boolean isNew = true;

    @Override
    public String getId() {
        return movieCode;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public Video markNotNew() {
        this.isNew = false;
        return this;
    }
}
