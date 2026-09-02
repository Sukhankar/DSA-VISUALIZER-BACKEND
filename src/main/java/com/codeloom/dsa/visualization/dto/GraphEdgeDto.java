package com.codeloom.dsa.visualization.dto;

public record GraphEdgeDto(
        String id,
        String source,
        String target,
        String from,
        String to,
        Double weight
) {
    public GraphEdgeDto {
        if (source == null && from != null) source = from;
        if (target == null && to != null) target = to;
        if (from == null && source != null) from = source;
        if (to == null && target != null) to = target;
        if (id == null && source != null && target != null) id = source + "-" + target;
    }

    public GraphEdgeDto(String from, String to) {
        this(from + "-" + to, from, to, from, to, null);
    }

    public GraphEdgeDto(String from, String to, Double weight) {
        this(from + "-" + to, from, to, from, to, weight);
    }

    public GraphEdgeDto(String id, String source, String target, Double weight) {
        this(id, source, target, source, target, weight);
    }
}
