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

@RepositoryRestResource(path="videos", collectionResourceRel="videos")
public interface VideoRepository extends ListCrudRepository<Video, String> {

    List<Video> findAllByName(String name);

    @Modifying
    @Transactional
    @Query("DELETE FROM Video")
    void bulkDeleteAll();

    @Modifying
    @Transactional
    @Query("DELETE FROM Video v where v.name=:name")
    void deleteAllByName(@Param("name") String name);

    @Query("SELECT v.contentId FROM Video v")
    List<String> findAllContentIds();
    
    @Query("SELECT v.contentId FROM Video v WHERE v.contentId IN :contentIds")
    List<String> findExistingContentIds(@Param("contentIds") List<String> contentIds);

    @Transactional
    default void saveVideos(List<Video> videos) {
        if (videos.isEmpty()) {
            return;
        }
        
        // Extract content IDs from the new videos
        List<String> newContentIds = videos.stream()
                .map(Video::getContentId)
                .toList();
        
        // Query only for existing IDs from the new batch
        Set<String> existingContentIds = new HashSet<>(findExistingContentIds(newContentIds));

        List<Video> nonExistingVideos = videos.stream()
                .filter(video -> !existingContentIds.contains(video.getContentId()))
                .toList();

        saveAll(nonExistingVideos);
    }
}
