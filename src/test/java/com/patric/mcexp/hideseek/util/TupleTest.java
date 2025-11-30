package com.patric.mcexp.hideseek.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TupleTest {

    @Test
    void returnsAllTupleValues() {
        Tuple<String, Integer, String> tuple = new Tuple<>("left", 10, "right");

        assertEquals("left", tuple.getLeft());
        assertEquals(10, tuple.getCenter());
        assertEquals("right", tuple.getRight());
    }
}
