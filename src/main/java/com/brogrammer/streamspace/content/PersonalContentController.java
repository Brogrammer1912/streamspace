package com.brogrammer.streamspace.content;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/personal")
@RequiredArgsConstructor
public class PersonalContentController {

    final VideoRepository videoRepository;
    final MusicRepository musicRepository;

    @HxRequest
    @GetMapping("/media")
    String getTitles(Model model) {

        model.addAttribute("videos", videoRepository.findAll());
        model.addAttribute("music", musicRepository.findAll());

        return "personalmedia :: personalMediaPlayer";
    }

}
