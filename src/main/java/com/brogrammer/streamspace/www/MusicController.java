package com.brogrammer.streamspace.www;

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/music")
@RequiredArgsConstructor
public class MusicController {

    final APIBayClient apiBayClient;

    @HxRequest
    @GetMapping("/featured/flac")
    String getFeaturedFLAC(Model model) {
        model.addAttribute("music", apiBayClient.getLosslessFLACAudio());
        return "music :: featured-lossless-audio";
    }
}
