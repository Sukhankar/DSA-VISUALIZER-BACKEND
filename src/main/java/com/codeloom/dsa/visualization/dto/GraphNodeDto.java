package com.codeloom.dsa.visualization.dto;

public record GraphNodeDto(
        String id,
        String label,
        Double x,
        Double y
) {
    public GraphNodeDto(String id, String label) {
        this(id, label, null, null);
    }

    public GraphNodeDto(String id) {
        this(id, id, null, null);
    }
}
