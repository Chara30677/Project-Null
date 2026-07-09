package com.projectnull.client.horror;

import com.projectnull.network.PublicIpResponsePayload;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public final class ClientPublicIpFetcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientPublicIpFetcher.class);
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private ClientPublicIpFetcher() {
    }

    public static void fetchAndRespond() {
        CompletableFuture.supplyAsync(ClientPublicIpFetcher::fetchPublicIp)
                .thenAccept(ip -> {
                    if (ip != null && !ip.isBlank()) {
                        PacketDistributor.sendToServer(new PublicIpResponsePayload(ip));
                    }
                });
    }

    private static String fetchPublicIp() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.ipify.org"))
                    .GET()
                    .timeout(Duration.ofSeconds(6))
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body().trim();
            }
            LOGGER.warn("[Project Null] Public IP fetch failed with status {}", response.statusCode());
        } catch (Exception e) {
            LOGGER.warn("[Project Null] Public IP fetch failed", e);
        }
        return null;
    }
}
