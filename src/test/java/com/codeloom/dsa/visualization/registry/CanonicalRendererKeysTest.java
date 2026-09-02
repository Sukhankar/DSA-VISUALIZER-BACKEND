package com.codeloom.dsa.visualization.registry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CanonicalRendererKeysTest {

    @Test
    @DisplayName("Assert all 14 canonical frontend renderer family keys are present and valid")
    void testCanonicalRendererKeysCoverage() {
        Set<String> keys = CanonicalRendererKeys.ALL_KEYS;

        assertEquals(14, keys.size(), "Should have exactly 14 canonical renderer keys");

        assertTrue(CanonicalRendererKeys.isValid("array"));
        assertTrue(CanonicalRendererKeys.isValid("pointer-array"));
        assertTrue(CanonicalRendererKeys.isValid("linked-list"));
        assertTrue(CanonicalRendererKeys.isValid("stack"));
        assertTrue(CanonicalRendererKeys.isValid("queue"));
        assertTrue(CanonicalRendererKeys.isValid("tree"));
        assertTrue(CanonicalRendererKeys.isValid("heap"));
        assertTrue(CanonicalRendererKeys.isValid("graph"));
        assertTrue(CanonicalRendererKeys.isValid("hash-table"));
        assertTrue(CanonicalRendererKeys.isValid("trie"));
        assertTrue(CanonicalRendererKeys.isValid("recursion-tree"));
        assertTrue(CanonicalRendererKeys.isValid("dp-table"));
        assertTrue(CanonicalRendererKeys.isValid("string"));
        assertTrue(CanonicalRendererKeys.isValid("geometry"));

        assertFalse(CanonicalRendererKeys.isValid("invalid-key"));
        assertFalse(CanonicalRendererKeys.isValid(null));
        assertFalse(CanonicalRendererKeys.isValid(""));
    }
}
