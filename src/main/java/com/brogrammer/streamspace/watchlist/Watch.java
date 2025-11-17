package com.brogrammer.streamspace.watchlist;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Watch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String itemUrl;
    private String thumbnailUrl;
    @CreatedDate
    private Date addedDate;
}
