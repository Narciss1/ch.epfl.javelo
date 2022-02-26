package ch.epfl.javelo;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import org.junit.jupiter.api.Test;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BitsTest {

    public static final double DELTA = 1e-11;

    @Test
    void bitsExtractSignedThrowsOnInvalidRange() {
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(2038,-1,28);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(20,6,33);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(203881,0,40);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(2,-1,2);
        });
    }

    @Test
    void bitsExtractSignedWorksOnValidRange() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i++) {
            var x = rng.nextInt(-1927368293, 1937362718);
            var y = rng.nextInt(0, 31);
            var z = rng.nextInt(0,32);
            Bits.extractSigned(x,y,z);
        }
    }

    @Test
    void bitsExtractSignedWorksOnKnownValues() {
        var actual1 = Bits.extractSigned(20,6,8);
        var expected1 = 0;
        assertEquals(expected1, actual1);

        var actual2 = Bits.extractSigned(27452,3,10);
        var expected2 = 359;
        assertEquals(expected2, actual2);

        var actual3 = Bits.extractSigned(38563,0,5);
        var expected3 = 3;
        assertEquals(expected3, actual3);

        var actual4 = Bits.extractSigned(19270,4,11);
        var expected4 = 0b11111111111111111111110010110100;
        assertEquals(expected4, actual4);
    }

   @Test
    void bitsExtractUnsignedThrowsOnInvalidRange() {
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(2038,-1,28);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(19,5,32);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(2073532,0,40);
        });assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(2,-2,2);
        });
    }

    @Test
    void bitsExtractUnsignedWorksOnValidRange() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i++) {
            var x = rng.nextInt(-1927368293, 1937362718);
            var y = rng.nextInt(0, 31);
            var z = rng.nextInt(0,31);
            Bits.extractUnsigned(x,y,z);
        }
    }

    @Test
    void bitsExtractUnsignedWorksOnKnownValues() {

        //Question: Pourquoi mes calculs à la main ne correspondent pas aux résultats de la machine?
        //(pour la 1 et la 3)
        var actual1 = Bits.extractUnsigned(291820,6,8);
        var expected1 = 199;
        assertEquals(expected1, actual1);

        var actual2 = Bits.extractUnsigned(27822,3,10);
        var expected2 = 405;
        assertEquals(expected2, actual2);

        var actual3 = Bits.extractUnsigned(385392,0,5);
        var expected3 =11;
        assertEquals(expected3, actual3);

        var actual4 = Bits.extractUnsigned(19000,4,11);
        var expected4 = 1187;
        assertEquals(expected4, actual4);
    }
}
