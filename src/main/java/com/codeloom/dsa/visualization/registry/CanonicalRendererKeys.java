package com.codeloom.dsa.visualization.registry;

import java.util.Collections;
import java.util.Set;

public final class CanonicalRendererKeys {

    public static final String ARRAY = "array";
    public static final String POINTER_ARRAY = "pointer-array";
    public static final String LINKED_LIST = "linked-list";
    public static final String STACK = "stack";
    public static final String QUEUE = "queue";
    public static final String TREE = "tree";
    public static final String HEAP = "heap";
    public static final String GRAPH = "graph";
    public static final String HASH_TABLE = "hash-table";
    public static final String TRIE = "trie";
    public static final String RECURSION_TREE = "recursion-tree";
    public static final String DP_TABLE = "dp-table";
    public static final String STRING = "string";
    public static final String GEOMETRY = "geometry";

    public static final Set<String> ALL_KEYS = Collections.unmodifiableSet(Set.of(
            ARRAY,
            POINTER_ARRAY,
            LINKED_LIST,
            STACK,
            QUEUE,
            TREE,
            HEAP,
            GRAPH,
            HASH_TABLE,
            TRIE,
            RECURSION_TREE,
            DP_TABLE,
            STRING,
            GEOMETRY
    ));

    private CanonicalRendererKeys() {
    }

    public static boolean isValid(String rendererKey) {
        return rendererKey != null && ALL_KEYS.contains(rendererKey.trim().toLowerCase());
    }
}
