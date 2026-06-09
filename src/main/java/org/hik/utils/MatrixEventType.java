package org.hik.utils;

public enum MatrixEventType {
    IMAGE("m.image"),
    FILE("m.file"),
    AUDIO("m.audio");

    public final String type;

    MatrixEventType(String type) {
        this.type = type;
    }

}
