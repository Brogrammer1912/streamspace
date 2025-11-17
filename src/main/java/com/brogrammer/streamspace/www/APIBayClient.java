package com.brogrammer.streamspace.www;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(url = "https://apibay.org/", accept = MediaType.APPLICATION_JSON_VALUE, contentType = MediaType.APPLICATION_JSON_VALUE)
public interface APIBayClient {

    //@GetExchange("https://apibay.org/q.php?q=FLAC 2023&&cat=101")
    @GetExchange("q.php?q=FLAC&&cat=101")
    APIBayTorrent[] getLosslessFLACAudio();

    @GetExchange("q.php?q=ALAC&&cat=101")
    APIBayTorrent[] getLosslessALACAudio();

    @GetExchange("q.php?q=Hi-Res&&cat=101")
    APIBayTorrent[] getHiResAudio();

    @GetExchange("q.php?q={term}&&cat=101")
    APIBayTorrent[] searchMusic(@PathVariable String term);

    @GetExchange("q.php?q={term}&&cat=207")
    APIBayTorrent[] getHDMovies(@PathVariable String term);

    @GetExchange("q.php?q={term}&&cat=208")
    APIBayTorrent[] getHDTVShows(@PathVariable String term);

    record APIBayTorrent(String id,
                         String name,
                         String info_hash,
                         String leechers,
                         String seeders,
                         String num_files,
                         String size,
                         String username,
                         String added,
                         String status,
                         String category,
                         String imdb) {
    }
}

