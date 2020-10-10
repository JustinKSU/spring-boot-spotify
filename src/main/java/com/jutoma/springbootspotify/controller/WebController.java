package com.jutoma.springbootspotify.controller;

import com.jutoma.springbootspotify.constant.Genres;
import com.jutoma.springbootspotify.model.SearchCriteria;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("searchCriteria", new SearchCriteria());
        model.addAttribute("genres", Genres.genres);
        return "spotify";
    }
}