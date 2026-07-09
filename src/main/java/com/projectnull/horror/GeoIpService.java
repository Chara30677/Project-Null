package com.projectnull.horror;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.projectnull.ProjectNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class GeoIpService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeoIpService.class);
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private static final Map<String, PlayerDossier> CACHE = new ConcurrentHashMap<>();

    private GeoIpService() {
    }

    public static PlayerDossier lookup(String ip) {
        if (ip == null || ip.isBlank() || isUnknown(ip)) {
            return unknown(ip == null || ip.isBlank() ? "0.0.0.0" : ip);
        }

        return CACHE.computeIfAbsent(ip, GeoIpService::fetch);
    }

    private static PlayerDossier fetch(String ip) {
        try {
            String encoded = URLEncoder.encode(ip, StandardCharsets.UTF_8);
            String url = "http://ip-api.com/json/" + encoded + "?fields=status,message,country,regionName,city,query";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .timeout(Duration.ofSeconds(6))
                    .header("User-Agent", ProjectNull.MODID)
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                LOGGER.warn("[Project Null] GeoIP lookup failed for {} with status {}", ip, response.statusCode());
                return unknown(ip);
            }

            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            if (!"success".equalsIgnoreCase(json.get("status").getAsString())) {
                String message = json.has("message") ? json.get("message").getAsString() : "lookup failed";
                LOGGER.warn("[Project Null] GeoIP lookup rejected for {}: {}", ip, message);
                return unknown(ip);
            }

            String city = textOrDefault(json, "city", "Unknown");
            String state = textOrDefault(json, "regionName", "Unknown");
            String country = textOrDefault(json, "country", "Unknown");
            String query = textOrDefault(json, "query", ip);

            return new PlayerDossier(query, city, state, country);
        } catch (Exception e) {
            LOGGER.warn("[Project Null] GeoIP lookup error for {}", ip, e);
            return unknown(ip);
        }
    }

    private static String textOrDefault(JsonObject json, String key, String fallback) {
        if (!json.has(key) || json.get(key).isJsonNull()) {
            return fallback;
        }
        String value = json.get(key).getAsString();
        return value == null || value.isBlank() ? fallback : value;
    }

    private static boolean isUnknown(String ip) {
        return "0.0.0.0".equals(ip) || "unknown".equalsIgnoreCase(ip);
    }

    private static PlayerDossier unknown(String ip) {
        return new PlayerDossier(ip, "Unknown", "Unknown", "Unknown");
    }
}
