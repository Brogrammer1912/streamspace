package com.brogrammer.streamspace.config;

import com.brogrammer.streamspace.torrentengine.DownloadProgressHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    final DownloadProgressHandler downloadProgressHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(downloadProgressHandler, "/download-progress")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
                        // Extract the torrentHash from the request URL
                        String query = request.getURI().getQuery();
                        Map<String, String> queryPairs = Arrays.stream(query.split("&"))
                                .map(pair -> pair.split("="))
                                .collect(Collectors.toMap(pair -> pair[0], pair -> pair[1]));

                        String torrentHash = queryPairs.get("torrentHash");

                        // Store the torrentHash in the session attributes
                        attributes.put("torrentHash", torrentHash);

                        return true;
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                               WebSocketHandler wsHandler, Exception exception) {

                    }
                })//;
        .setAllowedOrigins("*"); //Remove for PROD
    }
}
