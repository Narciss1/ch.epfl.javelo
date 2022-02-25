package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebMercatorTest {
    public static final double DELTA = 1e-7;
    public static final double DELTA1 = 1e-4;

    @Test
    void webMercatorXWorksOnKnownValues() {
        var actual1 = WebMercator.x(2600);
        var expected1 = 414.302852;
        assertEquals(expected1, actual1, DELTA1);

        var actual2 = WebMercator.x(2600100);
        var expected2 = 413819.2675;
        assertEquals(expected2, actual2, DELTA1);

        var actual3 = WebMercator.x(2698145);
        var expected3 = 429423.6139;
        assertEquals(expected3, actual3, DELTA1);

        var actual4 = WebMercator.x(234);
        var expected4 = 37.74225668;
        assertEquals(expected4, actual4, DELTA1);
    }

    @Test
    void webMercatorYWorksOnKnownValues() {
        var actual1 = WebMercator.y(26000000);
        var expected1 = 0.4795547189;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = WebMercator.y(26000100);
        var expected2 = 0.570067498;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = WebMercator.y(2698145);
        var expected3 = 0.3321478035;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = WebMercator.y(2340000);
        var expected4 = 0.4263356991;
        assertEquals(expected4, actual4, DELTA);
    }

    /*@Test
    void webMercatorLatWorksOnKnownValues() {
        var actual1 = WebMercator.lat(23746);
        var expected1 = 0.0;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = WebMercator.lat(23);
        var expected2 = 10000.0;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = WebMercator.lat(346610);
        var expected3 = 10000.0;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = WebMercator.lat(2786);
        var expected4 = 3045512.0;
        assertEquals(expected4, actual4, DELTA);
    }

    @Test
    void webMercatorLonWorksOnKnownValues() {
        var actual1 = WebMercator.lon(23746);
        var expected1 = 0.0;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = WebMercator.lon(23);
        var expected2 = 10000.0;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = WebMercator.lon(346610);
        var expected3 = 10000.0;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = WebMercator.lon(2786);
        var expected4 = 3045512.0;
        assertEquals(expected4, actual4, DELTA);
    }*/
}
