package org.hik.services.modules;

import org.hik.api.MatrixClient;
import org.hik.exceptions.MatrixIOException;
import org.hik.exceptions.MatrixNetworkException;
import org.hik.payloads.roomevents.MatrixEvent;
import org.hik.services.networking.HttpTransport;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Events {


    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpTransport httpTransport = new HttpTransport();

    private final MatrixClient client;

    public Events(MatrixClient client) {
        this.client = client;
    }

    /// Asynchronously requests the posting of a message to a Matrix room.
    ///
    /// @param roomId      the id of the room to post the event
    /// @param matrixEvent a well constructed [MatrixEvent]
    /// @return A [CompletableFuture] with a [String] representing a unique identifier of the event
    /// @throws MatrixIOException      when the payload cannot be processed
    /// @throws MatrixNetworkException when the response status is not successful
    /// @throws InterruptedException   when the HTTP Client is interrupted
    public String publishRoomMessage(String roomId, MatrixEvent matrixEvent) throws InterruptedException {

        String jsonPayload;
        try {
            jsonPayload = objectMapper.writeValueAsString(matrixEvent);
        } catch (JacksonException e) {
            throw new MatrixIOException("Failed to parse input data", e);
        }

        try {
            String queryResponse = httpTransport.putEvent(URI.create(client.getDiscoveryResponse().homeserver().baseUrl() + "/_matrix/client/v3/rooms/" + roomId + "/send/m.room.message/" + UUID.randomUUID()),
                    jsonPayload,
                    client.getCredentials().token());
            JsonNode responsePayload = objectMapper.readTree(queryResponse);
            JsonNode idNode = responsePayload.path("event_id");
            if (idNode.isMissingNode()) {
                throw new MatrixIOException("Missing 'event_id' in server response ");
            }
            return idNode.stringValue();
        } catch (IOException e) {
            throw new MatrixIOException("Network error while attempting to publish an event ", e);
        } catch (JacksonException e) {
            throw new MatrixIOException("Failed to parse Matrix response JSON ", e);
        }


    }

    /// Synchronously creates a new mxc:// for immediate usage.
    ///
    /// @return a [String] representing the MXC
    private String createAndReserveMXC() throws IOException, InterruptedException {
        var queryResponse = httpTransport.postEvent(URI.create(client.getDiscoveryResponse().homeserver().baseUrl() + "/_matrix/media/v1/create"), null, this.client.getCredentials().token());
        JsonNode responsePayload = objectMapper.readTree(queryResponse);
        return responsePayload.get("content_uri").stringValue();
    }

    /// Synchronously uploads a local multimedia resource to the Matrix media server.
    ///
    /// @param resource the local path of the resource to upload
    /// @return A [String] containing the MXC URI string upon successful upload.
    /// @throws InterruptedException when the HTTP Client is interrupted
    public String uploadResource(Path resource) throws InterruptedException {
        try {
            String mxc = createAndReserveMXC();

            String rawPath = mxc.replace("mxc://", "");
            URI uploadTargetUri = URI.create(client.getDiscoveryResponse().homeserver().baseUrl() + "/_matrix/media/v3/upload/" + rawPath + "?filename=" + resource.getFileName().toString());
            httpTransport.putResource(uploadTargetUri, resource, client.getCredentials().token());

            return mxc;
        } catch (IOException e) {
            throw new MatrixIOException("Network error while attempting to publish a resource ", e);
        }
    }
}
