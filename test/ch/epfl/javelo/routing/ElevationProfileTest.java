package ch.epfl.javelo.routing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ElevationProfileTest {

    @Test
    void ConstructorThrowsException(){
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(-8, new float[]{2,5,8,10}));
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(-203182, new float[]{2,5,8,10}));
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(0, new float[]{2,5,8,10}));
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(5, new float[]{}));
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(5, new float[]{1}));
    }

    @Test
    void FirstProfileWorks(){
        ElevationProfile firstProfile = new ElevationProfile(10, new float[]{384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f, 384.0625f});
        assertEquals(10, firstProfile.length());
        assertEquals(384.0625f, firstProfile.minElevation());
        assertEquals(384.75f, firstProfile.maxElevation());
        assertEquals(384.75f, firstProfile.elevationAt(0));
        assertEquals(384.0625f, firstProfile.elevationAt(10));
        assertEquals(384.6875f, firstProfile.elevationAt(1));
        assertEquals(0, firstProfile.totalAscent());
        assertEquals(0.6875, firstProfile.totalDescent());
    }

    @Test
    void ThrowElevationProfile()
    {
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(-1, new float[]{1,2,3}));
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(0, new float[]{1,2,3}));
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(10, new float[]{1}));
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(10, new float[]{}));
    }

    @Test
    void TestWithCalculationsActualFunction()
    {
        float[] values = new float[]{5,7,0,2,10};
        double length = 20;
        ElevationProfile ep = new ElevationProfile(length, values);
        assertEquals(20, ep.length());
        assertEquals(0, ep.minElevation());
        assertEquals(10, ep.maxElevation());
        assertEquals(12, ep.totalAscent());
        assertEquals(7, ep.totalDescent());
        assertEquals(6, ep.elevationAt(2.5));
        assertEquals(7, ep.elevationAt(5));
        assertEquals(1, ep.elevationAt(12.5));
        assertEquals(10, ep.elevationAt(21));
        assertEquals(5, ep.elevationAt(-1));
        assertEquals(0, ep.elevationAt(10));
    }

    @Test
    void TestWithCalculationsCst()
    {
        float[] values = new float[]{5,5};
        double length = 20;
        ElevationProfile ep = new ElevationProfile(length, values);
        assertEquals(20, ep.length());
        assertEquals(5, ep.minElevation());
        assertEquals(5, ep.maxElevation());
        assertEquals(0, ep.totalAscent());
        assertEquals(0, ep.totalDescent());
        assertEquals(5, ep.elevationAt(2.5));
        assertEquals(5, ep.elevationAt(5));
        assertEquals(5, ep.elevationAt(12.5));
        assertEquals(5, ep.elevationAt(21));
        assertEquals(5, ep.elevationAt(-1));
        assertEquals(5, ep.elevationAt(10));
    }

    @Test
    public void checkElevationProfilePreconditions() {
        assertThrows(IllegalArgumentException.class, () -> {new ElevationProfile(0, new float[]{0, 1, 2});});
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(3, new float[]{0}));
    }

    @Test
    public void lengthWorks() {
        ElevationProfile e = new ElevationProfile(4.3, new float[]{0, 1, 2, 5});
        double expected = 4.3;
        assertEquals(expected, e.length());
    }

    @Test
    public void minElevationReturnsMinAltitudeInMeters() {
        ElevationProfile e = new ElevationProfile(4.3, new float[]{0.1f, 98234, 4.6f, 3});
        double expected = 0.1;
        assertEquals(expected, e.minElevation(), 1e-3);
    }

    @Test
    public void maxElevationReturnsMaxAltitudeInMeters() {
        ElevationProfile e = new ElevationProfile(4.3, new float[]{0.1f, 98234, 4.6f, 3});
        double expected = 98234;
        assertEquals(expected, e.maxElevation(), 1e-3);
    }

    @Test
    public void totalAscentWorks() {
        ElevationProfile e = new ElevationProfile(4.3, new float[]{-0.1f, 98234, 4.4f, -3});
        double expected = 98234+0.1;
        assertEquals(expected, e.totalAscent(), 1e-2);
        ElevationProfile e2 = new ElevationProfile(4.3, new float[]{5, -2, 4, -3});
        double expected2 = 6;
        assertEquals(expected2, e2.totalAscent(), 1e-2);
    }

    @Test
    public void totalDescentWorks() {
        ElevationProfile e = new ElevationProfile(4.3, new float[]{-0.1f, 98234, 4.4f, -3});
        double expected = -(4.4f-98234 + -3-4.4f);
        assertEquals(expected, e.totalDescent(), 1e-2);
    }

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
    void lengthWorks1(){
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
    void totalAscentWorks1(){
        float[] tab = {5F, 6F, 3F, 7F, 8F, 2F};
        ElevationProfile l = new ElevationProfile(5, tab);
        assertEquals(6F, l.totalAscent());
    }

    @Test
    void totalDescentWorks1(){
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
