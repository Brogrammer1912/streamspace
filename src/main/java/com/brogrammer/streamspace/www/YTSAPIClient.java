package com.brogrammer.streamspace.www;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange(url = "https://yts.mx/api/v2/", accept = MediaType.APPLICATION_JSON_VALUE, contentType = MediaType.APPLICATION_JSON_VALUE)
public interface YTSAPIClient {

    @GetExchange("list_movies.json?limit=8&sort_by=download_count")
    YTSMoviesRecord getMostWatchedMovies();

    @GetExchange("list_movies.json?limit=50&sort_by=download_count&page={page}")
    YTSMoviesRecord getMostWatchedMovies(@PathVariable int page);

    @GetExchange("list_movies.json?sort=date_added&order_by=desc&limit=8")
    YTSMoviesRecord getLatestMovies();

    @GetExchange("list_movies.json?sort=date_added&order_by=desc&limit=50&page={page}")
    YTSMoviesRecord getLatestMovies(@PathVariable int page);

    @GetExchange("list_movies.json?genre=comedy&sort=date_added&limit=8")
    YTSMoviesRecord getLatestComedyMovies();

    @GetExchange("list_movies.json?genre=comedy&sort=date_added&limit=50&page={page}")
    YTSMoviesRecord getLatestComedyMovies(@PathVariable int page);

    @GetExchange("list_movies.json?genre=comedy&sort_by=download_count&limit=8")
    YTSMoviesRecord getMustWatch();

    @GetExchange("list_movies.json?genre=comedy&sort_by=download_count&limit=50&page={page}")
    YTSMoviesRecord getMustWatch(@PathVariable int page);

    @GetExchange("list_movies.json?minimum_rating=7&limit=8")
    YTSMoviesRecord getIMDBHighestRated();

    @GetExchange("list_movies.json?minimum_rating=7&limit=50&page={page}")
    YTSMoviesRecord getIMDBHighestRated(@PathVariable int page);

    @GetExchange("list_movies.json?sort_by=like_count&order_by=desc&limit=8")
    YTSMoviesRecord getMostLiked();

    @GetExchange("list_movies.json?sort_by=like_count&order_by=desc&limit=50&page={page}")
    YTSMoviesRecord getMostLiked(@PathVariable int page);

    /**
     * TODO: Enhance API to fetch some more info
     * https://yts.mx/api/v2/movie_details.json?movie_id=36846&with_images=true&with_cast=true
     */
    @GetExchange("movie_details.json?movie_id={id}")
    YTSMovieRecord getMovieDetails(@PathVariable int id);

    @GetExchange("movie_suggestions.json?movie_id={id}")
    YTSMoviesRecord getSuggestedMovies(@PathVariable int id);

    @GetExchange("list_movies.json?query_term={term}")
    YTSMoviesRecord ytsSearchV2(@PathVariable String term);

    record YTSMovieRecord(String status, String status_message, YTSMovieData data) {
    }

    record YTSMovieData(YTSMovieDetails movie) {
    }

    record YTSMoviesRecord(String status, String status_message, YTSData data) {
    }

    record YTSData(List<YTSMovieDetails> movies) {
    }

    record YTSMovieDetails(
            String id,
            String url,
            String imdb_code,
            String title,
            String title_english,
            String title_long,
            String year,
            String rating,
            String runtime,
            String[] genres,
            String description_intro,
            String language,
            Object background_image,
            Object background_image_original,
            Object small_cover_image,
            Object medium_cover_image,
            Object large_cover_image,
            List<YTSTorrent> torrents) {
    }

    record YTSTorrent(
            String url,
            String hash,
            String quality,
            String type,
            String is_repack,
            String video_codec,
            String bit_depth,
            String audio_channels,
            int seeds,
            int peers,
            String size,
            long size_bytes,
            String date_uploaded) {
    }
}

