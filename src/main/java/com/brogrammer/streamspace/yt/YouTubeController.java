package com.brogrammer.streamspace.yt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/yt")
@RequiredArgsConstructor
public class YouTubeController {

    final YoutubeCrawler youtubeCrawler;

    @GetMapping("/search")
    String search(@RequestParam String query, Model model) {
        // We pass the search query, scrape the search results and show it to the user
        // And we click on an interested item, It'll pass the videoId of that item to the search box
        // And the Search box will pull that particular video to play YT videos without any ads
        model.addAttribute("videos",youtubeCrawler.getVideos(query));
        return "watchlistitems :: ytVideos";
    }

    @GetMapping("/watch/{v}")
    String watch(@PathVariable("v") String v, Model model) {
        YouTubeResponseDTO dto = new YouTubeResponseDTO(null, v, null);
        model.addAttribute("youtubeTrailers", dto);
        return "yt :: youtubeTrailer";
    }


    @GetMapping("/crawl/trailer/{movie}")
    String getYoutubeTrailer(@PathVariable("movie") String movie, Model model) {
        YouTubeResponseDTO youTubeResponseDTO = youtubeCrawler.getYoutubeTrailersByTitle(movie);
        if (youTubeResponseDTO != null) {
            model.addAttribute("youtubeTrailers", youTubeResponseDTO);
            return "yt :: youtubeTrailer";
        } else {
            return "yt :: notFound";
        }
    }
}

