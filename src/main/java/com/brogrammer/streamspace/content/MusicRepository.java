package com.brogrammer.streamspace.content;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
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
    
    @Query("SELECT s.contentId FROM Song s WHERE s.contentId IN :contentIds")
    List<String> findExistingContentIds(@Param("contentIds") List<String> contentIds);

    @Transactional
    default void saveMusicList(List<Song> songs) {
        if (songs.isEmpty()) {
            return;
        }
        
        // Extract content IDs from the new songs
        List<String> newContentIds = songs.stream()
                .map(Song::getContentId)
                .toList();
        
        // Query only for existing IDs from the new batch
        Set<String> existingContentIds = new HashSet<>(findExistingContentIds(newContentIds));

        List<Song> nonExistingSongs = songs.stream()
                .filter(song -> !existingContentIds.contains(song.getContentId()))
                .toList();

        saveAll(nonExistingSongs);
    }
}
