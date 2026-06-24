package org.hik.api;

import org.hik.exceptions.MatrixIOException;
import org.hik.responses.DiscoveryResponse;
import org.hik.services.modules.Events;
import org.hik.services.modules.Room;
import org.hik.services.networking.HttpTransport;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;

/// A [MatrixClient] provides all the functionality required to interact with a Matrix compliant server.
public class MatrixClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ClientCredentials credentials;
    private final HttpTransport httpTransport = new HttpTransport();
    private DiscoveryResponse discoveryResponse;

    private final Events events;
    private final Room room;

    private MatrixClient(String unprocessedBaseUrl, String username, String authToken) {
        credentials = new ClientCredentials(unprocessedBaseUrl, username, authToken);
        this.events = new Events(this);
        this.room = new Room(this);
    }

    public Events events() {
        return this.events;
    }

    public Room room() {
        return this.room;
    }

    public DiscoveryResponse getDiscoveryResponse() {
        return discoveryResponse;
    }

    public ClientCredentials getCredentials() {
        return credentials;
    }


    /// Default factory, which will make the initial payloads to request necessary data for further requests
    ///
    /// @param unprocessedBaseUrl the full qualified url of the server.
    /// @param username           the username assigned to a registered account.
    /// @param authToken          a valid non-expired auth token.
    /// @return an authenticated client.
    /// @throws InterruptedException when the HTTP Client is interrupted
    public static MatrixClient create(String unprocessedBaseUrl, String username, String authToken) throws InterruptedException {
        MatrixClient apiClient = new MatrixClient(unprocessedBaseUrl, username, authToken);
        apiClient.getWellKnown();
        return apiClient;
    }

    /// Method used to obtain the .well-known data and store the base url.º
    ///
    /// @throws IllegalArgumentException when the homeserver url violates RFC 2396 or is null (since we concat a constant)
    /// @throws MatrixIOException        when the payload cannot be processed
    /// @throws InterruptedException     when the HTTP Client is interrupted
    private void getWellKnown() throws InterruptedException {
        try {
            URI uri = URI.create(credentials.baseUrl() + "/.well-known/matrix/client");
            var response = httpTransport.getEvent(uri, null);
            this.discoveryResponse = objectMapper.readValue(response, DiscoveryResponse.class);

        } catch (JacksonException e) {
            throw new MatrixIOException("Failed to parse Matrix discovery JSON ", e);
        } catch (IOException e) {
            throw new MatrixIOException("Network error during Matrix discovery ", e);
        }
    }


}
