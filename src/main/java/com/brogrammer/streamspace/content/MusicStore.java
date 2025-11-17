package com.brogrammer.streamspace.content;

import org.springframework.content.commons.store.ContentStore;
import org.springframework.content.rest.StoreRestResource;
import org.springframework.stereotype.Component;

@Component
@StoreRestResource(path="music")
public interface MusicStore extends ContentStore<Song, String> {}