package org.hik.dtos.payloads;

public record MatrixTextMessage(
        String msgtype,
        String body
) implements MatrixMessagePayload {}
