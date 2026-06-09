package org.hik.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MatrixDiscoveryResponse(
        @JsonProperty("m.homeserver") HomeserverInfo homeserver,
        @JsonProperty("m.identity_server") IdentityServerInfo identityServer,
        @JsonProperty("org.matrix.msc2965.authentication") AuthInfo authentication,
        @JsonProperty("org.matrix.msc4143.rtc_foci") List<RtcFocus> rtcFoci
) {
    public record HomeserverInfo(
            @JsonProperty("base_url") String baseUrl
    ) {
    }

    public record IdentityServerInfo(
            @JsonProperty("base_url") String baseUrl
    ) {
    }

    public record AuthInfo(
            @JsonProperty("issuer") String issuer,
            @JsonProperty("account") String account
    ) {
    }

    public record RtcFocus(
            @JsonProperty("type") String type,
            @JsonProperty("livekit_service_url") String livekitServiceUrl
    ) {
    }
}