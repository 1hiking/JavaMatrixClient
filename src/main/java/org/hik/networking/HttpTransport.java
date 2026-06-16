package org.hik.networking;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * A {@link HttpTransport} is responsible for the construction of asynchronous {@linkplain HttpRequest requests}, this class is transparent
 * such that all methods require providing required datatypes for the payloads, such as with {@link URI URI} and with {@link HttpRequest.BodyPublisher}
 */
public class HttpTransport {
    private final HttpClient client = HttpClient.newHttpClient();


    public CompletableFuture<String> getJson(URI path, String authToken) {
        var builderRequest = HttpRequest.newBuilder()
                .uri(path)
                .header("Content-Type", "application/json")
                .GET();

        if (authToken != null) {
            builderRequest.header("Authorization", "Bearer " + authToken);
        }
        var request = builderRequest.build();


        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);

    }

    public CompletableFuture<String> postJson(URI path, String body, String authToken) {
        var builderRequest = HttpRequest.newBuilder()
                .uri(path)
                .header("Content-Type", "application/json");

        builderRequest.POST(body != null ? HttpRequest.BodyPublishers.ofString(body) : HttpRequest.BodyPublishers.noBody());
        if (authToken != null) {
            builderRequest.header("Authorization", "Bearer " + authToken);
        }
        var request = builderRequest.build();


        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);

    }

    public CompletableFuture<String> putJson(URI path, String body, String authToken) {


        var builderRequest = HttpRequest.newBuilder()
                .uri(path)
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json");

        builderRequest.PUT(body != null ? HttpRequest.BodyPublishers.ofString(body) : HttpRequest.BodyPublishers.noBody());
        var request = builderRequest.build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);
    }


    public CompletableFuture<HttpResponse<String>> putFile(URI path, Path resource, String authToken) throws IOException {
        var uploadRequest = HttpRequest.newBuilder()
                .uri(path)
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", Files.probeContentType(resource))
                .PUT(HttpRequest.BodyPublishers.ofFile(resource))
                .build();

        return client.sendAsync(uploadRequest, HttpResponse.BodyHandlers.ofString());

    }
}
