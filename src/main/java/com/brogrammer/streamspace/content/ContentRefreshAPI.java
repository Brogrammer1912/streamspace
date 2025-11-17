package com.brogrammer.streamspace.content;

import com.brogrammer.streamspace.services.ContentDirectoryServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashSet;

@Controller
@RequestMapping("/refresh")
@RequiredArgsConstructor
public class ContentRefreshAPI {

    final VideoRepository videoRepository;
    final MusicRepository musicRepository;
    final Indexer indexer;

    @GetMapping("/personalmedia")
    public String refreshPersonalMedia(Model model) {
        videoRepository.bulkDeleteAll();
        musicRepository.bulkDeleteAll();
        indexer.indexLocalMedia(new HashSet<>(ContentDirectoryServices.mediaFolders.values())).join();
        model.addAttribute("videos", videoRepository.findAll());
        model.addAttribute("music", musicRepository.findAll());
        return "personalmedia :: personalMediaPlayer";
    }

}
