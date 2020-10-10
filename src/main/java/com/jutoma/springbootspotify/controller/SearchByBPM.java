package com.jutoma.springbootspotify.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jutoma.springbootspotify.constant.Genres;
import com.jutoma.springbootspotify.model.SearchCriteria;
import com.jutoma.springbootspotify.model.Song;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Controller
public class SearchByBPM {

    private final Logger log = LoggerFactory.getLogger(SearchByBPM.class);

    private final WebClient webClient;

    public SearchByBPM(WebClient webClient) {
        this.webClient = webClient;
    }

    @PostMapping("/searchByBPM")
    public String post(@ModelAttribute SearchCriteria searchCriteria, Model model) throws JsonProcessingException {
        log.info("BPM: " + searchCriteria.getBpm());
        log.info("Popularity: " + searchCriteria.getPopularity());
        String json = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/recommendations")
                        .queryParam("seed_genres", searchCriteria.getGenre())
                        .queryParam("target_popularity", searchCriteria.getPopularity())
                        .queryParam("target_tempo", searchCriteria.getBpm())
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.info(json);

        List<Song> songs = parseJsonToSongs(json);

        songs.sort(Comparator.comparing(song -> song.getArtistName().toUpperCase()));

        model.addAttribute("genres", Genres.genres);
        model.addAttribute("json", json);
        model.addAttribute("songs", songs);
        return "spotify";
    }

    private List<Song> parseJsonToSongs(String body) throws JsonProcessingException {
        List<Song> songs = new ArrayList<>();
        JsonNode json = new ObjectMapper().readTree(body);
        JsonNode tracks = json.get("tracks");
        for (int i = 0; i < tracks.size(); i++) {
            JsonNode track = tracks.get(i);
            Song song = new Song();
            song.setId(track.get("id").textValue());
            song.setArtistName(track.get("artists").get(0).get("name").textValue());
            song.setSongName(track.get("name").textValue());
            log.info("Artist '{}' song name '{}' with the ID {}", song.getArtistName(), song.getSongName(), song.getId());
            songs.add(song);
        }
        return songs;
    }
}
