package com.brogrammer.streamspace.preferences;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPreferences extends ListCrudRepository<Preference, Integer> {
    
    @Override
    @Cacheable("preferences")
    Optional<Preference> findById(Integer id);
}

