/// Base Client Module
module JavaMatrixClient {

    // Required for all our http code
    requires java.net.http;

    // Required for serialization
    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.core;
    requires tools.jackson.databind;

    // Required for MediaProcessor, might be deleted
    requires java.desktop;

    // Init
    exports org.hik.api;
    exports org.hik.context;

    // Records and Interfaces
    exports org.hik.payloads.roomstate;
    exports org.hik.payloads.roomevents;
    exports org.hik.responses;

    // Common exceptions
    exports org.hik.exceptions;

}