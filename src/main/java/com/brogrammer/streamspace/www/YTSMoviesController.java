package com.brogrammer.streamspace.www;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.function.Function;

@Slf4j
@Controller
@RequestMapping("/yts")
@RequiredArgsConstructor
public class YTSMoviesController {

    final YTSAPIClient ytsapiClient;

    @HxRequest
    @GetMapping("/movies/{id}")
    String getNewOverview(@PathVariable("id") int id, Model model) {

        model.addAttribute("ytsMovieRecord", ytsapiClient.getMovieDetails(id));
        model.addAttribute("ytsSuggestedRecord", ytsapiClient.getSuggestedMovies(id));

        return "ytsMovie :: ytsMovieOverview";
    }

    @GetMapping("/movies/{id}")
    String getYTSMovieOverview(@PathVariable("id") int id, Model model) {

        model.addAttribute("ytsMovieRecord", ytsapiClient.getMovieDetails(id));
        model.addAttribute("ytsSuggestedRecord", ytsapiClient.getSuggestedMovies(id));

        return "ytsMovie";
    }

    @GetMapping("/movies/cat/{category}")
    String viewAllPage(Model model,
                                    @RequestParam(defaultValue = "1") int page,
                                    @PathVariable("category") String category) {

        model.addAttribute("category", category);

        Map<String, Function<Integer, YTSAPIClient.YTSMoviesRecord>> categoryMap = Map.of(
                "latest", ytsapiClient::getLatestMovies,
                "mostliked", ytsapiClient::getMostLiked,
                "imdbrating", ytsapiClient::getIMDBHighestRated,
                "mostwatched", ytsapiClient::getMostWatchedMovies,
                "latestcomedies", ytsapiClient::getLatestComedyMovies,
                "mustwatch", ytsapiClient::getMustWatch
        );

        model.addAttribute("ytsMoviesRecord", categoryMap.get(category).apply(page));
        model.addAttribute("currentPage", page);

        return "viewAll :: gallery";
    }

    @HxRequest
    @GetMapping("/ytsMostWatched")
    String ytsMostWatched(Model model) {
        model.addAttribute("ytsMostWatchedRecord", ytsapiClient.getMostWatchedMovies());

        return "movies :: ytsMostWatched";
    }

    @HxRequest
    @GetMapping("/ytsLatest")
    String ytsLatest(Model model) {
        model.addAttribute("ytsLatestRecord", ytsapiClient.getLatestMovies());

        return "movies :: ytsLatest";
    }

    @HxRequest
    @GetMapping("/ytsMostLiked")
    String ytsMostLiked(Model model) {
        model.addAttribute("ytsMostLikedRecord", ytsapiClient.getMostLiked());

        return "movies :: ytsMostLiked";
    }

    @HxRequest
    @GetMapping("/ytsImdbRating")
    String ytsImdbRating(Model model) {
        model.addAttribute("ytsIMDBHighestRatedRecord", ytsapiClient.getIMDBHighestRated());

        return "movies :: ytsImdbRating";
    }

    @HxRequest
    @GetMapping("/ytsLatestComedies")
    String ytsLatestComedies(Model model) {
        model.addAttribute("ytsLatestComedyRecord", ytsapiClient.getLatestComedyMovies());

        return "movies :: ytsLatestComedies";
    }

    @HxRequest
    @GetMapping("/ytsMustWatch")
    String ytsMustWatch(Model model) {

        model.addAttribute("ytsMustWatchRecord", ytsapiClient.getMustWatch());

        return "movies :: ytsMustWatch";
    }

}
