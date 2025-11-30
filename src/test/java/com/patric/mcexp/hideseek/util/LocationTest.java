package com.patric.mcexp.hideseek.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LocationTest {

    @Test
    void defaultLocationIsNotSetup() {
        Location def = Location.getDefault();
        assertTrue(def.isNotSetup(), "Default location should be considered not setup");
    }

    @Test
    void inBoundsAndOutOfBoundsDetection() {
        Location center = new Location("world", 0.0, 64.0, 0.0);
        assertFalse(center.isNotInBounds(-10, 10, -10, 10), "Center should be within bounds");

        Location outsideX = new Location("world", -11.0, 64.0, 0.0);
        assertTrue(outsideX.isNotInBounds(-10, 10, -10, 10), "Location with X < min should be out of bounds");

        Location outsideZ = new Location("world", 0.0, 64.0, 11.0);
        assertTrue(outsideZ.isNotInBounds(-10, 10, -10, 10), "Location with Z > max should be out of bounds");
    }
}

