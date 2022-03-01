package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FunctionsTestRayan {

    @Test
    void constant() {
        assertEquals(2, Functions.constant(2).applyAsDouble(2));
    }

    @Test
    void sampled() {
        assertEquals(8.55, Functions.sampled(new float[]{10, 8 ,6, 9, 11, 12}, 10).applyAsDouble(5.7));
    }
}