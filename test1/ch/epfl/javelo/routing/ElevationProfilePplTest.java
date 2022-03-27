package ch.epfl.javelo.routing;

import ch.epfl.javelo.Bits;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ElevationProfilePplTest {
    @Test
    void constructorThrowsOnNegativeLength(){
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(-1, new float[3]);
        });
    }
    @Test
    void constructorThrowsOnNullLength(){
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(0, new float[3]);
        });
    }
    @Test
    void constructorThrowsOnInvalidTab(){
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(5, new float[1]);
        });
    }
    @Test
    void lengthWorks(){
        ElevationProfile l = new ElevationProfile(5, new float[5]);
        double actual = l.length();
        double expected = 5;
        assertEquals(expected, actual);
    }
    @Test
    void minElevationWorks(){
        float[] tab = {5F, 6F, 3F, 7F, 8F, 2F};
        ElevationProfile l = new ElevationProfile(5, tab);
        assertEquals(2F, l.minElevation());
    }
    @Test
    void maxElevationWorks(){
        float[] tab = {5F, 6F, 3F, 7F, 8F, 2F};
        ElevationProfile l = new ElevationProfile(5, tab);
        assertEquals(8F, l.maxElevation());
    }
    @Test
    void totalAscentWorks(){
        float[] tab = {5F, 6F, 3F, 7F, 8F, 2F};
        ElevationProfile l = new ElevationProfile(5, tab);
        assertEquals(6F, l.totalAscent());
    }

    @Test
    void totalDescentWorks(){
        float[] tab = {5F, 6F, 3F, 7F, 8F, 2F};
        ElevationProfile l = new ElevationProfile(5, tab);
        assertEquals(9F, l.totalDescent());
    }

    @Test
    void elevationAtWorks(){
        float[] tab = {5F, 6F, 3F, 7F, 8F, 2F};
        ElevationProfile l = new ElevationProfile(5, tab);
        assertEquals(3F,l.elevationAt(2));
    }
}