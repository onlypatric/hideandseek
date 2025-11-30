package com.patric.mcexp.hideseek.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PairTest {

    @Test
    void returnsLeftAndRightValues() {
        Pair<String, Integer> pair = new Pair<>("left", 42);

        assertEquals("left", pair.getLeft());
        assertEquals(42, pair.getRight());
    }
}
