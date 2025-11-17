package com.brogrammer.streamspace.watchlist;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.time.LocalDate;

@Controller
@RequestMapping("/watchlist")
@RequiredArgsConstructor
public class WatchListController {

    final WatchList watchList;

    @HxRequest
    @GetMapping("")
    public String getWatchList(Model model) {
        model.addAttribute("watchListItems", watchList.findAll());
        return "watchlistitems :: watchListSection";
    }

    @HxRequest
    @PostMapping("")
    public ResponseEntity<String> addToWatchList(@RequestParam("movie-title") String movieTitle,
                                                 @RequestParam("item-url") String itemUrl,
                                                 @RequestParam("thumbnail-url") String thumbnailUrl) {

        if (!watchList.existsByName(movieTitle)) {
            Watch watch = new Watch();
            watch.setName(movieTitle);
            watch.setItemUrl(itemUrl);
            watch.setThumbnailUrl(thumbnailUrl);
            watch.setAddedDate(Date.valueOf(LocalDate.now()));
            watchList.save(watch);
        }

        return ResponseEntity.ok("<i class=\"bi bi-heart-fill\" style=\"color: #eb5282;\"></i> Added!");
    }

    @HxRequest
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFromWatchList(@PathVariable("id") int id) {
        watchList.deleteById(id);
        return ResponseEntity.ok("Deleted!");
    }
}
