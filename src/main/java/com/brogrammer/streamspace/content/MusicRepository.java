package com.brogrammer.streamspace.content;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RepositoryRestResource(path="music", collectionResourceRel="music")
public interface MusicRepository extends ListCrudRepository<Song, String> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Song")
    void bulkDeleteAll();

    // Add to MusicRepository interface
    @Query("SELECT s.contentId FROM Song s")
    List<String> findAllContentIds();

    @Transactional
    default void saveMusicList(List<Song> songs) {
        Set<String> existingContentIds = new HashSet<>(findAllContentIds());

        List<Song> nonExistingSongs = songs.stream()
                .filter(song -> !existingContentIds.contains(song.getContentId()))
                .toList();

        saveAll(nonExistingSongs);
    }
}
