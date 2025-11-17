/*package com.akshathsaipittala.streamspace.www;

import com.akshathsaipittala.streamspace.downloads.Downloads;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SSEController {

    final Downloads downloads;
    final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @GetMapping(value = "/count", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getCount() {
        SseEmitter emitter = new SseEmitter();
        emitters.put("count", emitter);
        emitter.onCompletion(() -> emitters.remove("count"));
        emitter.onTimeout(() -> emitters.remove("count"));
        emitter.onError((ex) -> emitters.remove("count"));
        try {
            // Send initial count
            emitter.send(String.valueOf(downloads.count()), MediaType.TEXT_PLAIN);
        } catch (IOException e) {
            log.error("Error sending initial count", e);
            emitters.remove("count");
        }
        return emitter;
    }

    @GetMapping(value = "/progress", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamProgress(@RequestParam String hashString) {
        SseEmitter emitter = new SseEmitter();
        emitters.put(hashString, emitter);
        emitter.onCompletion(() -> emitters.remove(hashString));
        emitter.onTimeout(() -> emitters.remove(hashString));
        emitter.onError((ex) -> emitters.remove(hashString));
        return emitter;
    }

    @Async
    public void sendCountUpdate() {
        SseEmitter emitter = emitters.get("count");
        if (emitter != null) {
            try {
                emitter.send(String.valueOf(downloads.count()), MediaType.TEXT_PLAIN);
            } catch (IOException e) {
                log.error("Error sending count update", e);
                emitters.remove("count");
            }
        }
    }

    @Async
    public void sendProgressUpdate(String hashString, String message, String downloaded, String uploaded, int peerCount, String remainingTime, boolean complete) {
        SseEmitter emitter = emitters.get(hashString);
        if (emitter != null) {
            String webResponse = "<div id=\"progress-bar-" + hashString + "\" class=\"progress progress-bar\" role=\"progressbar\" style=\"width: " + message + ";height:5px;\" aria-valuenow=\"" + message + "\" aria-valuemin=\"0\" aria-valuemax=\"100\"></div><div id=\"torrent-stats-" + hashString + "\" class=\"container\"> <div class=\"row\"> <div class=\"col\"> <p class=\"text-body-secondary\">" + message + "</p></div> <div class=\"col\"> <p class=\"text-body-secondary\"><i class=\"bi bi-arrow-down\"></i> " + downloaded + "</p> </div> <div class=\"col\"> <p class=\"text-body-secondary\"><i class=\"bi bi-arrow-up\"></i> " + uploaded + "</p> </div> <div class=\"col\"> <p class=\"text-body-secondary\">" + peerCount + "P</p> </div> <div class=\"col\"> <p class=\"text-body-secondary\">ETA " + remainingTime + "</p> </div> </div> </div>";
            try {
                if (complete) {
                    emitter.complete();
                    emitters.remove(hashString);
                    log.info("Closed SSE emitter for hashString: {}", hashString);
                } else {
                    emitter.send(webResponse);
                }
            } catch (IOException e) {
                log.error("Error sending progress update or closing session for hashString: {}", hashString, e);
                emitters.remove(hashString);
            }
        }
    }

}*/
