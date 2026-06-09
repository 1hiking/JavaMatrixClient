package org.hik.utils;

public enum MatrixEventType {
    IMAGE("m.image"),
    FILE("m.file"),
    VIDEO("m.audio"),
    UNFORMATTED_TEXT("m.room.message");

    public final String type;

    MatrixEventType(String type) {
        this.type = type;
    }

}
