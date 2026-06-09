package org.hik.dtos.payloads;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MatrixImageMessage(
         String msgtype,
         String body,
         String url,
         ImageInfo info
) implements MatrixMessagePayload {

    public record ImageInfo(
            @JsonProperty("h") Integer h,
            @JsonProperty("w") Integer w,
            @JsonProperty("mimetype") String mimetype,
            @JsonProperty("size") Long size) {}
}