module JavaMatrixClient {
    requires com.fasterxml.jackson.annotation;
    requires java.net.http;
    requires tools.jackson.core;
    requires tools.jackson.databind;

    exports org.hik.api;
    exports org.hik.dtos.payloads;
    exports org.hik.dtos.payloads.events;
    exports org.hik.dtos.responses;
    exports org.hik.exceptions;
}