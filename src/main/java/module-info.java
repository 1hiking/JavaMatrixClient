module JavaMatrixClient {
    requires com.fasterxml.jackson.annotation;
    requires java.net.http;
    requires tools.jackson.core;
    requires tools.jackson.databind;
    requires java.desktop;

    exports org.hik.api;
    exports org.hik.payloads.roomevents;
    exports org.hik.payloads.instantmessaging;
    exports org.hik.responses;
    exports org.hik.exceptions;
    exports org.hik.services;
}