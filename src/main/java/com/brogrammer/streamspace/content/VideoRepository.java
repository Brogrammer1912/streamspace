package com.brogrammer.streamspace.content;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RepositoryRestResource(path="videos", collectionResourceRel="videos")
public interface VideoRepository extends ListCrudRepository<Video, String> {

    List<Video> findAllByName(String name);

    boolean existsByContentId(String contentId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Video")
    void bulkDeleteAll();

    @Modifying
    @Transactional
    @Query("DELETE FROM Video v where v.name=:name")
    void deleteAllByName(@Param("name") String name);

    // Add to VideoRepository interface
    @Query("SELECT v.contentId FROM Video v")
    List<String> findAllContentIds();
}
