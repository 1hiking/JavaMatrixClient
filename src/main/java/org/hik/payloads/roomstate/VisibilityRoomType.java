package org.hik.payloads.roomstate;

/// The new visibility type for the room.
public enum VisibilityRoomType {
    /// Look forward in time (from the oldest message towards the newest messages).
    PRIVATE("f"),
    /// Look backward in time (from the newest message towards the oldest historical messages).
    PUBLIC("b");


    private final String value;

    VisibilityRoomType(String value) {
        this.value = value;
    }

    /// Returns the string query parameter value ('f' or 'b') expected by the Matrix homeserver.
    ///
    /// @return the raw query parameter string
    public String getValue() {
        return this.value;
    }
}
