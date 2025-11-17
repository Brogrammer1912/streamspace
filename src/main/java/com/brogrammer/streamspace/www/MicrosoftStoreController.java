package com.brogrammer.streamspace.www;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/microsoft")
@RequiredArgsConstructor
public class MicrosoftStoreController {

    final MicrosoftStoreAPI microsoftStoreAPI;

    @GetMapping("/newreleases")
    String newReleases(Model model) {
        var productsList = microsoftStoreAPI.newReleases(1, 24, "movies", "AllProducts").productsList();
        // Workaround for .active class to one of the slides,
        model.addAttribute("activeItem", productsList.getFirst());
        productsList.removeFirst();
        productsList.addAll(microsoftStoreAPI.topRated(1, 24, "movies", new FilteredCategories().comedy()).productsList());
        productsList.addAll(microsoftStoreAPI.topSelling(1, 24, "movies", "AllProducts").productsList());
        productsList.addAll(microsoftStoreAPI.topRented(1, 24, "movies", "AllProducts").productsList());
        model.addAttribute("productsList", productsList);
        return "movies :: msftfeatured";
    }

    record FilteredCategories(
            String actionAdv,
            String animation,
            String anime,
            String comedy,
            String documentary,
            String drama,
            String family,
            String foreignIndependent,
            String horror,
            String other,
            String romance,
            String romanticComedy,
            String sciFiFantasy,
            String sports,
            String thrillerMystery,
            String tvMovies) {

        public FilteredCategories() {
            this("Action/Adventure", "Animation", "Anime", "Comedy", "Documentary",
                    "Drama", "Family", "Foreign/Independent", "Horror", "Other",
                    "Romance", "Romantic Comedy", "Sci-Fi/Fantasy", "Sports",
                    "Thriller/Mystery", "TV Movies");
        }

    }

}
