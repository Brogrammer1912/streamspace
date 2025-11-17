package com.brogrammer.streamspace.preferences;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferences extends ListCrudRepository<Preference, Integer> {
}

