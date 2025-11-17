package com.brogrammer.streamspace.watchlist;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WatchList extends ListCrudRepository<Watch, Integer> {

    boolean existsByName(String name);

}

