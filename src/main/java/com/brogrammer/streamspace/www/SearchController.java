package com.brogrammer.streamspace.www;

import com.brogrammer.streamspace.preferences.Preference;
import com.brogrammer.streamspace.preferences.UserPreferences;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    final YTSAPIClient ytsapiClient;
    final UserPreferences userPreferences;

    @GetMapping("/yts")
    public String ytsSearch(@RequestParam("term") String term, Model model) {
        Optional<Preference> darkModePreference = userPreferences.findById(1);
        boolean darkModeEnabled = darkModePreference.map(Preference::isEnabled).orElse(false);
        model.addAttribute("darkmodeenabled", darkModeEnabled);
        if(term == null) {
            return "";
        } else {
            model.addAttribute("results", ytsapiClient.ytsSearchV2(term).data());
            return "index";
        }
    }

    @HxRequest
    @GetMapping("/yts")
    public String ytsSearchAsync(@RequestParam("term") String term, Model model) {
        Optional<Preference> darkModePreference = userPreferences.findById(1);
        boolean darkModeEnabled = darkModePreference.map(Preference::isEnabled).orElse(false);
        model.addAttribute("darkmodeenabled", darkModeEnabled);
        if(term == null) {
            return "";
        } else {
            model.addAttribute("results", ytsapiClient.ytsSearchV2(term).data());
            //model.addAttribute("musicResults", apiBayClient.searchMusic(term));
            return "index :: search-results";
        }
    }

}
