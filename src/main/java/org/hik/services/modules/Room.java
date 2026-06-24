package org.hik.services.modules;

import org.hik.api.MatrixClient;
import org.hik.exceptions.MatrixIOException;
import org.hik.exceptions.MatrixNetworkException;
import org.hik.payloads.roomstate.*;
import org.hik.responses.MessagesResponse;
import org.hik.services.networking.HttpTransport;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;

public class Room {

    /**
     * Common endpoint for many Room events.
     */
    public static final String COMMON_ROOM_ENDPOINT = "/_matrix/client/v3/rooms/";
    /**
     * Common endpoint for many Directory events.
     */
    public static final String COMMON_DIRECTORY_ENDPOINT = "/_matrix/client/v3/directory/list/room/";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpTransport httpTransport = new HttpTransport();
    private final MatrixClient client;

    public Room(MatrixClient client) {
        this.client = client;
    }

    /// Creates a room, this method will let the homeserver choose the default configuration for most tasks
    /// and the following values will overwrite them if set to a non-null value.
    ///
    /// @param isFederated if the room will be federated
    /// @param name        the room's name, if any.
    /// @param aliasName   the room's canonical alias, if any
    /// @param topic       the room's topic, if any.
    /// @param type        the [CreationRoomType]
    /// @param isVisible   if the room will be visible to the public
    /// @return the created room’s ID.
    /// @throws MatrixIOException      when the payload cannot be processed
    /// @throws MatrixNetworkException when the response status is not successful
    /// @throws InterruptedException   when the HTTP Client is interrupted
    public String create(boolean isFederated, String name, String aliasName, String topic, CreationRoomType type, boolean isVisible) throws InterruptedException {

        String visibility = isVisible ? "public" : "private";

        MatrixRoom roomPayload = new MatrixRoom(new MatrixRoom.CreationContent(isFederated),
                null,
                null,
                null,
                name,
                type.getValue(),
                aliasName,
                topic,
                visibility);

        String jsonPayload;
        try {
            jsonPayload = objectMapper.writeValueAsString(roomPayload);
        } catch (JacksonException e) {
            throw new MatrixIOException("Failed to parse input data", e);
        }

        String queryResponse = null;
        try {
            queryResponse = httpTransport.postEvent(URI.create(client.getDiscoveryResponse().homeserver().baseUrl() + "/_matrix/client/v3/createRoom"),
                    jsonPayload, client.getCredentials().token());

            JsonNode responsePayload = objectMapper.readTree(queryResponse);
            JsonNode idNode = responsePayload.path("room_id");
            if (idNode.isMissingNode()) {
                throw new MatrixIOException("Missing 'room_id' in server response ");
            }
            return idNode.stringValue();
        } catch (IOException e) {
            throw new MatrixIOException("Network error while attempting to create a room ", e);
        } catch (JacksonException e) {
            throw new MatrixIOException("Failed to parse Matrix response JSON ", e);
        }
    }

    /// Sends a request to leave the room, upon success, you will forget all messages from this room.
    /// If all users on a room forget it, the room is eligible for deletion.
    /// You must [forget](#forget(String)) the room first before calling this method.
    ///
    /// @param roomId the target room ID.
    /// @return true if the roomId was set and the request finished with no issue.
    /// @throws MatrixIOException      when the payload cannot be processed.
    /// @throws MatrixNetworkException when the response status is not successful.
    /// @throws InterruptedException   when the HTTP Client is interrupted.
    public boolean forget(String roomId) throws InterruptedException {
        if (roomId != null) {
            try {
                httpTransport.postEvent(
                        URI.create(client.getDiscoveryResponse().homeserver().baseUrl() + COMMON_ROOM_ENDPOINT + roomId + "/forget"),
                        null, this.client.getCredentials().token());
            } catch (IOException e) {
                throw new MatrixIOException("Network error while attempting to forget the room ", e);
            }
            return true;
        }
        return false;
    }

    /// Sends a request to leave the room, upon success, you will no longer receive new messages from this room.
    /// If the user was invited to the room, but had not joined, this call serves to reject the invite.
    /// Some servers MAY additionally `forget` the room when leaving.
    ///
    /// @param roomId the target room ID.
    /// @return true if the roomId was set and the request finished with no issue.
    /// @throws MatrixIOException      when the payload cannot be processed
    /// @throws MatrixNetworkException when the response status is not successful
    /// @throws InterruptedException   when the HTTP Client is interrupted
    public boolean leave(String roomId) throws InterruptedException {
        if (roomId != null) {
            try {
                httpTransport.postEvent(
                        URI.create(client.getDiscoveryResponse().homeserver().baseUrl() + COMMON_ROOM_ENDPOINT + roomId + "/leave"),
                        null, this.client.getCredentials().token());
            } catch (IOException e) {
                throw new MatrixIOException("Network error while attempting to leave the room ", e);
            }
            return true;
        }
        return false;
    }

    /// Sends a request to kick someone from a room. Caller must have a configured power level to perform this operation.
    ///
    /// @param roomId the target room ID.
    /// @param event  the body to supply the request.
    /// @return `true` if the request finished with no issue.
    /// @throws MatrixIOException    when the payload cannot be processed.
    /// @throws InterruptedException when the HTTP Client is interrupted.
    /// @throws NullPointerException when the roomId is null.
    public boolean kick(String roomId, ExpulsionProperties event) throws InterruptedException {
        var payloadRoomId = Objects.requireNonNull(roomId);
        try {
            var jsonPayload = objectMapper.writeValueAsString(event);
            httpTransport.postEvent(URI.create(client.getDiscoveryResponse().homeserver().baseUrl() + COMMON_ROOM_ENDPOINT + payloadRoomId + "/kick"),
                    jsonPayload, this.client.getCredentials().token());
        } catch (IOException e) {
            throw new MatrixIOException("Network error while attempting to kick someone from a room ", e);
        } catch (JacksonException e) {
            throw new MatrixIOException("Failed to parse Matrix response JSON ", e);
        }
        return false;
    }

    /// Sends a request to ban someone from a room. Caller must have a configured power level to perform this operation.
    ///
    /// @param roomId the target room ID.
    /// @param event  the body to supply the request.
    /// @return `true` if the request finished with no issue.
    /// @throws MatrixIOException    when the payload cannot be processed.
    /// @throws InterruptedException when the HTTP Client is interrupted.
    /// @throws NullPointerException when the roomId is null.
    public boolean ban(String roomId, ExpulsionProperties event) throws InterruptedException {
        var payloadRoomId = Objects.requireNonNull(roomId);
        try {
            var jsonPayload = objectMapper.writeValueAsString(event);
            httpTransport.postEvent(URI.create(client.getDiscoveryResponse().homeserver().baseUrl() + COMMON_ROOM_ENDPOINT + payloadRoomId + "/ban"),
                    jsonPayload, this.client.getCredentials().token());
        } catch (IOException e) {
            throw new MatrixIOException("Network error while attempting to kick someone from a room ", e);
        } catch (JacksonException e) {
            throw new MatrixIOException("Failed to parse Matrix response JSON ", e);
        }
        return false;
    }

    /// Sends a request to unban someone from a room. Caller must have a configured power level to perform this operation.
    ///
    /// @param roomId the target room ID.
    /// @param event  the body to supply the request.
    /// @return `true` if the request finished with no issue.
    /// @throws MatrixIOException    when the payload cannot be processed.
    /// @throws InterruptedException when the HTTP Client is interrupted.
    /// @throws NullPointerException when the roomId is null.
    public boolean unban(String roomId, ExpulsionProperties event) throws InterruptedException {
        var payloadRoomId = Objects.requireNonNull(roomId);
        try {
            var jsonPayload = objectMapper.writeValueAsString(event);
            httpTransport.postEvent(URI.create(client.getDiscoveryResponse().homeserver().baseUrl() + COMMON_ROOM_ENDPOINT + payloadRoomId + "/unban"),
                    jsonPayload, this.client.getCredentials().token());
        } catch (IOException e) {
            throw new MatrixIOException("Network error while attempting to kick someone from a room ", e);
        } catch (JacksonException e) {
            throw new MatrixIOException("Failed to parse Matrix response JSON ", e);
        }
        return false;
    }

    /// Gets the visibility of a given room in the server’s published room directory.
    /// NOTE: This does NOT guarantee join rules are public.
    ///
    /// @param roomId the target room ID.
    /// @return a [String] with the room visibility.
    /// @throws MatrixIOException    when the payload cannot be processed.
    /// @throws InterruptedException when the HTTP Client is interrupted.
    /// @throws NullPointerException when the roomId is null.
    public String getRoomDirectoryVisibilityType(String roomId) throws InterruptedException {
        var payloadRoomId = Objects.requireNonNull(roomId);
        try {
            var queryResponse = httpTransport.getEvent(
                    URI.create(client.getDiscoveryResponse().homeserver().baseUrl() + COMMON_DIRECTORY_ENDPOINT + payloadRoomId),
                    null);
            return objectMapper.readTree(queryResponse).get("visibility").stringValue();
        } catch (IOException e) {
            throw new MatrixIOException("Network error while attempting to kick someone from a room ", e);
        } catch (JacksonException e) {
            throw new MatrixIOException("Failed to parse Matrix response JSON ", e);
        }
    }

    ///
    ///
    /// @param roomId   the target room ID.
    /// @param roomType a [VisibilityRoomType] with the room visibility type
    /// @return `true` if the request finished with no issue.
    /// @throws MatrixIOException    when the payload cannot be processed.
    /// @throws InterruptedException when the HTTP Client is interrupted.
    /// @throws NullPointerException when the roomId is null.
    public boolean setRooomDirectoryVisibilityType(String roomId, VisibilityRoomType roomType) throws InterruptedException {
        var payloadRoomId = Objects.requireNonNull(roomId);
        try {
            var queryResponse = httpTransport.getEvent(
                    URI.create(client.getDiscoveryResponse().homeserver().baseUrl() + COMMON_DIRECTORY_ENDPOINT + payloadRoomId),
                    null);
            return "{}".equals(queryResponse);
        } catch (IOException e) {
            throw new MatrixIOException("Network error while attempting to kick someone from a room ", e);
        } catch (JacksonException e) {
            throw new MatrixIOException("Failed to parse Matrix response JSON ", e);
        }
    }

    /// Returns a list of message and state events for a room. It uses pagination query parameters to paginate history in the room.
    ///
    /// The content is not parsed or escaped which means newlines (\n) and such sequences will be treated as they are.
    ///
    /// @param roomId the target room ID.
    /// @param params the [QueryParametersMessages] for the operation
    /// @param dir    the [ChronologicalDirectionType] to return events from.
    /// @return A [CompletableFuture] containing a [MessagesResponse] with the messages from the room
    /// @throws MatrixIOException    when the payload cannot be processed.
    /// @throws InterruptedException when the HTTP Client is interrupted
    /// @throws NullPointerException when the roomId is null.
    public MessagesResponse getListOfMessages(String roomId, ChronologicalDirectionType dir, QueryParametersMessages params) throws InterruptedException {
        var payloadRoomId = Objects.requireNonNull(roomId);
        String finalUrl = getFinalUrl(payloadRoomId, dir, params);

        try {
            var queryResponse = httpTransport.getEvent(URI.create(finalUrl), client.getCredentials().token());
            return objectMapper.readValue(queryResponse, MessagesResponse.class);
        } catch (IOException e) {
            throw new MatrixIOException("Network error while attempting to fetch messages ", e);
        } catch (JacksonException e) {
            throw new MatrixIOException("Failed to parse Matrix response JSON ", e);
        }

    }

    private String getFinalUrl(String roomId, ChronologicalDirectionType dir, QueryParametersMessages params) {
        String basePath = client.getDiscoveryResponse().homeserver().baseUrl() + COMMON_ROOM_ENDPOINT + roomId + "/messages";

        StringJoiner queryParams = new StringJoiner("&");
        if (dir != null && dir.getValue() != null) {
            queryParams.add("dir=" + dir.getValue());
        }
        if (params.from() != null) {
            queryParams.add("from=" + params.from());
        }
        if (params.limit() > 0) {
            queryParams.add("limit=" + params.limit());
        }
        if (params.to() != null) {
            queryParams.add("to=" + params.to());
        }
        return basePath + "?" + queryParams;
    }

}
