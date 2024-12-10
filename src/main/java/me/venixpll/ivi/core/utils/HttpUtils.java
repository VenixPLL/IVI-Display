package me.venixpll.ivi.core.utils;

import com.alibaba.fastjson2.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class HttpUtils {

    public static JSONObject getJsonObject(final String url) throws Exception {
        try(final var client = HttpClient.newHttpClient()) {
            final var request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            final var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return JSONObject.parseObject(response.body());
        }
    }

    public static JSONObject getJsonArray(final String url) throws Exception {
        try(final var client = HttpClient.newHttpClient()) {
            final var request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            final var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return JSONObject.parse(response.body());
        }
    }

    public static String getRaw(final String url) throws Exception {
        try(final var client = HttpClient.newHttpClient()) {
            final var request = HttpRequest.newBuilder().uri(URI.create(url)).build();
            final var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        }
    }

}
