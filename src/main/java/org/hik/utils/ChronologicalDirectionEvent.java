package org.hik.utils;

public enum ChronologicalDirectionEvent {
    CHRONOLOGICAL_ORDER("f"),
    REVERSE_CHRONOLOGICAL_ORDER("b");

    public final String order;

    ChronologicalDirectionEvent(String order) {
        this.order = order;
    }
}
