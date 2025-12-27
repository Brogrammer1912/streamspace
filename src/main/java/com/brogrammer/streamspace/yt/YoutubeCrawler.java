package com.brogrammer.streamspace.yt;

import com.brogrammer.streamspace.resilience.RetryService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class YoutubeCrawler {

    private static final Pattern POLYMER_INITIAL_DATA_REGEX = Pattern.compile("(window\\[\"ytInitialData\"]|var ytInitialData)\\s*=\\s*(.*);");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public YouTubeResponseDTO getYoutubeTrailersByTitle(String searchQuery) {

        Content content = crawlSearchResults(searchQuery)
                .twoColumnSearchResultsRenderer
                .primaryContents
                .sectionListRenderer.contents.getFirst().itemSectionRenderer.contents.getFirst();

        YouTubeResponseDTO youTubeResponseDTO = null;

        if (content.videoRenderer != null) {
            youTubeResponseDTO = new YouTubeResponseDTO(
                    content.videoRenderer.title.runs.getFirst().text,
                    content.videoRenderer.videoId,
                    null
            );
        }

        log.debug("{}", youTubeResponseDTO);
        return youTubeResponseDTO;
    }

    public List<YouTubeResponseDTO> getVideos(String searchQuery) {
        Content content = crawlSearchResults(searchQuery);

        List<Content> contentList = new ArrayList<>(
                content
                        .twoColumnSearchResultsRenderer
                        .primaryContents
                        .sectionListRenderer
                        .contents.getFirst().itemSectionRenderer.contents
        );

        return contentList.stream()
        .filter(content1 -> content1.videoRenderer() != null)
        .map(content1 -> new YouTubeResponseDTO(
                content1.videoRenderer().title().runs().getFirst().text,
                content1.videoRenderer().videoId,
                content1.videoRenderer().thumbnail.thumbnails.getFirst().url))
        .collect(Collectors.toList());
    }

    private Content crawlSearchResults(String searchQuery) {
        RetryService<Content> retryService = new RetryService<>();

        return retryService.retry(() -> {
            Document document = Jsoup.connect("https://www.youtube.com/results?search_query=" + searchQuery + " trailer")
                    .get();

            // document.getElementsByTag("a").forEach(System.out::println); // This will get all links in the document
            // Match the JSON from the HTML. It should be within a script tag
            // String matcher0 = matcher.group(0);
            // String matcher1 = matcher.group(1);
            // String matcher2 = matcher.group(2);
            Matcher matcher = POLYMER_INITIAL_DATA_REGEX.matcher(document.html());
            if (!matcher.find()) {
                log.warn("Failed to match ytInitialData JSON object");
            }

            JsonNode jsonNode = OBJECT_MAPPER.readTree(matcher.group(2));
            JsonNode contents = jsonNode.get("contents");
            return Objects.requireNonNull(OBJECT_MAPPER.treeToValue(contents, Content.class));
        });
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Content(
            ItemSectionRenderer itemSectionRenderer,
            VideoRenderer videoRenderer,
            TwoColumnSearchResultsRenderer twoColumnSearchResultsRenderer
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ItemSectionRenderer(
            ArrayList<Content> contents
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record VideoRenderer(
            String videoId,
            Title title,
            Thumbnail thumbnail
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Thumbnail(
            ArrayList<Thumbnails> thumbnails
    ){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Thumbnails(
            String url,
            String width,
            String height
    ){}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record TwoColumnSearchResultsRenderer(
            PrimaryContents primaryContents
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record PrimaryContents(
            SectionListRenderer sectionListRenderer
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record SectionListRenderer(
            ArrayList<Content> contents
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Title(
            ArrayList<Run> runs
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Run(
            String text
    ) {
    }
}
