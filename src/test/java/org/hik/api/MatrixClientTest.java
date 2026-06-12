package org.hik.api;


import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.hik.utils.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;


class MatrixClientTest {
    private MatrixClient matrixClient;
    private static final String USER = "test";
    private static final String AUTH_TOKEN = "1234";


    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().dynamicPort())
            .build();

    @BeforeEach
    void setUp() {
        wireMockServer.stubFor(get(urlEqualTo("/.well-known/matrix/client"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"m.homeserver\": {\"base_url\": \"" + wireMockServer.baseUrl() + "\"}}")));


    }

    // Initialization tests

    @Test
    void getWellKnown_WithAllRequiredProperties_thenReturnCorrectSerialization() {
        matrixClient = new MatrixClient(wireMockServer.baseUrl(), USER, AUTH_TOKEN);

        assertDoesNotThrow(() -> matrixClient, "The client should not throw given a good url.");
    }

    @Test
    void getWellKnown_WithBadUrl_thenReturnAnException() {
        assertThrows(IllegalArgumentException.class, () -> new MatrixClient("INCORRECT.ORG", USER, AUTH_TOKEN), "The client should throw when given a bad url.");
    }


    // Publishing messages
    @Test
    void sendPublishRoomMessage_WithACorrectPayload_thenReturnAString() {
        String roomId = "1234";
        String roomMessageType = "m.room.message";
        String expectedEventId = "$h29asdf8q348hju9a:matrix.org";


        wireMockServer.stubFor(put(urlPathMatching("/_matrix/client/v3/rooms/" + roomId + "/send/" + roomMessageType + "/[^/]+"))
                .withRequestBody(containing("Hello World"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"event_id\": \"" + expectedEventId + "\"}")));

        matrixClient = new MatrixClient(wireMockServer.baseUrl(), USER, AUTH_TOKEN);

        String actualEventId = matrixClient.publishRoomMessage("Hello World", roomId).join();

        assertNotNull(actualEventId, "The returned event ID should not be null");
        assertEquals(expectedEventId, actualEventId, "The client did not return the expected event ID");
    }


    @Test
    void sendPublishRoomMessageFile_WithACorrectPayload_thenReturnAString(@TempDir Path tempDir) throws IOException {
        // Arrange
        String roomId = "1234";
        String roomMessageType = "m.room.message";
        String expectedEventId = "$h29asdf8q348hju9a:matrix.org";

        String serverName = "matrix.org";
        String mediaId = "fakeMediaId123";
        String mockMxcUri = "mxc://" + serverName + "/" + mediaId;

        Path tempFile = tempDir.resolve("file.txt");
        Files.writeString(tempFile, "testfile");

        // Mock the MXC Request (v1 create endpoint)
        wireMockServer.stubFor(post(urlEqualTo("/_matrix/media/v1/create"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"content_uri\": \"" + mockMxcUri + "\"}")));

        // Mock the File Upload (v3 upload endpoint with filename query param)
        wireMockServer.stubFor(put(urlEqualTo("/_matrix/media/v3/upload/" + serverName + "/" + mediaId + "?filename=file.txt"))
                .withRequestBody(containing("testfile"))
                .willReturn(aResponse()
                        .withStatus(200)));

        // Mock the Message Publication (v3 client send timeline endpoint)
        wireMockServer.stubFor(put(urlPathMatching("/_matrix/client/v3/rooms/" + roomId + "/send/" + roomMessageType + "/[^/]+"))
                .withRequestBody(containing(mockMxcUri))
                .withRequestBody(containing("file.txt"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"event_id\": \"" + expectedEventId + "\"}")));

        // Initialize client pointing to local WireMock server
        matrixClient = new MatrixClient(wireMockServer.baseUrl(), USER, AUTH_TOKEN);

        // Act
        String actualEventId = matrixClient.publishRoomMessage(tempFile, roomId, EventType.FILE).join();

        // Assert
        assertNotNull(actualEventId, "The returned event ID should not be null");
        assertEquals(expectedEventId, actualEventId, "The client did not return the expected event ID");
    }

}