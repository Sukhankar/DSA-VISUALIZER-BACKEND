package com.codeloom.dsa.visualization.dto;

public record PointDto(
        double x,
        double y,
        String label
) {
    public PointDto(double x, double y) {
        this(x, y, null);
    }
}
