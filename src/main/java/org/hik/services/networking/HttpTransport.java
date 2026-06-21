package org.hik.services.networking;

import org.hik.exceptions.MatrixNetworkException;
import org.hik.responses.ErrorResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

/**
 * A {@link HttpTransport} is responsible for the construction of asynchronous {@linkplain HttpRequest requests}, this class is transparent
 * such that all methods require providing required datatypes for the payloads, such as with {@link URI URI} and with {@link HttpRequest.BodyPublisher}
 */
public class HttpTransport {
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    private void validateHeaders(int code, String body) {
        if (code != 200) {
            ErrorResponse errorResponse = new ObjectMapper().readValue(body, ErrorResponse.class);
            throw new MatrixNetworkException("Error processing exception: " + errorResponse.error() + ", with code: " + errorResponse.errCode());

        }
    }

    /**
     * @param path
     * @param authToken
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public String getJson(URI path, String authToken) throws IOException, InterruptedException {
        var builderRequest = HttpRequest.newBuilder()
                .uri(path)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .GET();

        if (authToken != null) {
            builderRequest.header(AUTHORIZATION, BEARER + authToken);
        }
        var request = builderRequest.build();


        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        this.validateHeaders(response.statusCode(), response.body());
        return response.body();

    }


    /**
     * @param path
     * @param body
     * @param authToken
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public String postJson(URI path, String body, String authToken) throws IOException, InterruptedException {
        var builderRequest = HttpRequest.newBuilder()
                .uri(path);

        if (body != null) {
            builderRequest.header(CONTENT_TYPE, APPLICATION_JSON);
        }

        builderRequest.POST(body != null ? HttpRequest.BodyPublishers.ofString(body) : HttpRequest.BodyPublishers.noBody());

        if (authToken != null) {
            builderRequest.header(AUTHORIZATION, BEARER + authToken);
        }
        var request = builderRequest.build();


        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        this.validateHeaders(response.statusCode(), response.body());
        return response.body();

    }


    /**
     * @param path
     * @param body
     * @param authToken
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public String putJson(URI path, String body, String authToken) throws IOException, InterruptedException {


        var builderRequest = HttpRequest.newBuilder()
                .uri(path)
                .header(AUTHORIZATION, BEARER + authToken)
                .header(CONTENT_TYPE, APPLICATION_JSON);

        builderRequest.PUT(body != null ? HttpRequest.BodyPublishers.ofString(body) : HttpRequest.BodyPublishers.noBody());
        var request = builderRequest.build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        this.validateHeaders(response.statusCode(), response.body());
        return response.body();
    }


    /**
     * @param path
     * @param resource
     * @param authToken
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public String putFile(URI path, Path resource, String authToken) throws IOException, InterruptedException {
        var uploadRequest = HttpRequest.newBuilder()
                .uri(path)
                .header(AUTHORIZATION, BEARER + authToken)
                .header(CONTENT_TYPE, Files.probeContentType(resource))
                .PUT(HttpRequest.BodyPublishers.ofFile(resource))
                .build();

        var response = client.send(uploadRequest, HttpResponse.BodyHandlers.ofString());
        this.validateHeaders(response.statusCode(), response.body());
        return response.body();


    }
}
