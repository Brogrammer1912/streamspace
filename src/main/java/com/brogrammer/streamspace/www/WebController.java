package com.brogrammer.streamspace.www;

import com.brogrammer.streamspace.preferences.Preference;
import com.brogrammer.streamspace.preferences.UserPreferences;
import com.brogrammer.streamspace.watchlist.WatchList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class WebController {

    final UserPreferences userPreferences;
    final WatchList watchList;

    @GetMapping("/")
    public String index(Model model) {
        Optional<Preference> darkModePreference = userPreferences.findById(1);
        boolean darkModeEnabled = darkModePreference.map(Preference::isEnabled).orElse(false);
        model.addAttribute("darkmodeenabled", darkModeEnabled);
        model.addAttribute("watchListItems", watchList.findAll());
        return "index";
    }

}
