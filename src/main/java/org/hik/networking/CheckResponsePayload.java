package org.hik.networking;

import org.hik.dtos.responses.MatrixErrorResponse;
import org.hik.exceptions.MatrixNetworkException;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;

public class CheckResponsePayload {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private CheckResponsePayload() {
    }


    /**
     * @param stringHttpResponse The response from a {@link java.net.http.HttpRequest}
     * @return The response with no modifications
     * @throws MatrixNetworkException if the status code is not 200
     */
    public static HttpResponse<String> getStringHttpResponse(HttpResponse<String> stringHttpResponse) {
        if (stringHttpResponse.statusCode() != 200) {
            MatrixErrorResponse payload = objectMapper.readValue(stringHttpResponse.body(), MatrixErrorResponse.class);
            throw new MatrixNetworkException("The following error has occurred: " + payload.error() + ". Error code:" + payload.errCode());
        }
        return stringHttpResponse;
    }
}
