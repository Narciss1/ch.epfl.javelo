package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebMercatorTestRayan {

    private static final double DELTA = 1e-7;
    //x=0.518275214444, y=0.353664894749
    // \lambda=6.5790772, \phi=46.5218976
    @Test
    void x() {
        var expected = 0.518275214444;
        var actual = WebMercator.x(6.5790772);
        assertEquals(expected, actual, DELTA);
    }

    @Test
    void y() {
        var expected = 0.353664894749;
        var actual = WebMercator.y(46.5218976);
        assertEquals(expected, actual, DELTA);
    }

    @Test
    void lon() {
        var expected = 6.5790772;
        var actual = WebMercator.lon(0.518275214444);
        assertEquals(expected, actual, DELTA);
    }

    @Test
    void lat() {
        var expected = 46.5218976;
        var actual = WebMercator.lat(0.353664894749);
        assertEquals(expected, actual, DELTA);
    }
}