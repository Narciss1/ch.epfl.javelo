package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebMercatorTest {
    public static final double DELTA = 1e-7;
    public static final double DELTA1 = 1e-4;
    public static final double DELTA2 = 1e-3;

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
        var expected2 = 0.5659245401;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = WebMercator.y(2698145);
        var expected3 = 0.3748732921;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = WebMercator.y(2340000);
        var expected4 = 0.4311093046;
        assertEquals(expected4, actual4, DELTA);
    }

    @Test
    void webMercatorLonWorksOnKnownValues() {
        var actual1 = WebMercator.lon(23746);
        var expected1 = 47491*Math.PI;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = WebMercator.lon(23);
        var expected2 = 45*Math.PI;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = WebMercator.lon(346610);
        var expected3 = 2177811.718;
        assertEquals(expected3, actual3, DELTA2);

        var actual4 = WebMercator.lon(2786);
        var expected4 = 5571*Math.PI;
        assertEquals(expected4, actual4, DELTA);
    }

    @Test
    void webMercatorLatWorksOnKnownValues() {
        assertEquals(0, WebMercator.lat(WebMercator.y(Math.PI)));
        var actual1 = WebMercator.lat(1);
        var expected1 = Math.toRadians(-85.05112878);
        assertEquals(expected1, actual1, DELTA);

        var actual2 = WebMercator.lat(0.5);
        var expected2 = 0;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = WebMercator.lat(0.2198);
        var expected3 = Math.toRadians(70.48674468);
        assertEquals(expected3, actual3, DELTA);

        var actual4 = WebMercator.lat(0.543);
        var expected4 = Math.toRadians(-15.29503692);
        assertEquals(expected4, actual4, DELTA);
    }

    @Test
    void TestReverseTestingX() {
        assertEquals(WebMercator.x(WebMercator.lon(0.5)), 0.5);
        assertEquals(WebMercator.x(WebMercator.lon(1)), 1);
        assertEquals(WebMercator.x(WebMercator.lon(0)), 0);
        assertEquals(WebMercator.x(WebMercator.lon(0.333)), 0.333);
    }

    @Test
    void TestReverseTestingY() {
        assertEquals(WebMercator.y(WebMercator.lat(0.5)), 0.5);
        assertEquals(WebMercator.y(WebMercator.lat(1)), 1, DELTA);
        assertEquals(WebMercator.y(WebMercator.lat(0)), 0, DELTA);
        assertEquals(WebMercator.y(WebMercator.lat(0.333)), 0.333);
    }

    @Test
    void TestReverseTestingLon() {
        assertEquals(WebMercator.lon(WebMercator.x(Math.PI / 2)), Math.PI / 2);
        assertEquals(WebMercator.lon(WebMercator.x(Math.PI)), Math.PI);
        assertEquals(WebMercator.lon(WebMercator.x(Math.PI / 4)), Math.PI / 4);
        assertEquals(WebMercator.lon(WebMercator.x(0)), 0);
    }

    @Test
    void TestReverseTestingLat() {
        assertEquals(Math.PI / 2, WebMercator.lat(WebMercator.y(Math.PI / 2)), DELTA);
        assertEquals(0, WebMercator.lat(WebMercator.y(Math.PI)));
        assertEquals(WebMercator.lat(WebMercator.y(Math.PI / 4)), Math.PI / 4, DELTA);
        assertEquals(WebMercator.lat(WebMercator.y(0)), 0);
    }



    //S

    @Test
    void xWorksWithKnownValues(){
        var actual1 = WebMercator.x(12.25643);
        var expected1 = 2.4506714;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = WebMercator.x(45.98762);
        var expected2 = 7.8191570;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = WebMercator.x(0);
        var expected3 = 0.5;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = WebMercator.x(197.87764565);
        var expected4 = 31.9932054;
        assertEquals(expected4, actual4, DELTA);
    }

    @Test
    void yWorksWithKnownValues(){
        var actual1 = WebMercator.y(0);
        var expected1 = 0.5;
        assertEquals(actual1, expected1, DELTA);

        var actual2 = WebMercator.y(25.78209);
        var expected2 = 0.3885162;
        assertEquals(actual2, expected2, DELTA);

        var actual3 = WebMercator.y(76.99745);
        var expected3 = 1.1769481;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = WebMercator.y(50);
        var expected4 = 0.5427581;
        assertEquals(expected4, actual4, DELTA);
    }

    @Test
    void lonWorksWithKnownValues(){
        var actual1 = WebMercator.lon(0);
        var expected1 = -Math.PI;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = WebMercator.lon(0.875);
        var expected2 = 2.35619449;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = WebMercator.lon(0.5);
        var expected3 = 0;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = WebMercator.lon(1);
        var expected4 = 3.14159265;
        assertEquals(expected4, actual4, DELTA);
    }

    @Test
    void latWoksWithKnownValues(){

        var actual1 = WebMercator.lat(0);
        var expected1 = 1.4844222284;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = WebMercator.lat(1);
        var expected2 = -1.4844222296;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = WebMercator.lat(0.5);
        var expected3 = 0;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = WebMercator.lat(0.734);
        var expected4 = -1.1189169804;
        assertEquals(expected4, actual4, DELTA);
    }

}
