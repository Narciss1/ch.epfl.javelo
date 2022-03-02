package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointWebMercatorTestRayan {

    private static final double DELTA = 1e-5;

    @Test
    void cannotCreateWithoutGoodCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> {
            PointWebMercator lolilol = new PointWebMercator(2, 4);
        });
    }

    @Test
    void of() {

    }

    @Test
    void ofPointCh() {
    }

    // x_{19} = 69561722, y_{19} = 47468099
    @Test
    void xAtZoomLevel() {
        PointWebMercator test = new PointWebMercator(0.518275214444,0.353664894749);
        var expected = 69561722;
        var actual = Math.round(test.xAtZoomLevel(19));
        assertEquals(expected, actual, DELTA);
    }

    @Test
    void yAtZoomLevel() {
    }

    @Test
    void lon() {
    }

    @Test
    void lat() {
    }

    @Test
    void toPointCh() {
        PointWebMercator test = new PointWebMercator(0.518275214444,0.353664894749);
        PointCh converted = test.toPointCh();
        PointCh expected = new PointCh(Ch1903.e(Math.toRadians(test.lon()), Math.toRadians(test.lat())), Ch1903.n(Math.toRadians(test.lon()), Math.toRadians(test.lat())));

        assertEquals(expected, converted);
    }


    @Test
    void x() {
    }

    @Test
    void y() {
    }
}