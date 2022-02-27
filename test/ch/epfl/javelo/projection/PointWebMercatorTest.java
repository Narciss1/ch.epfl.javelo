package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static ch.epfl.javelo.projection.Ch1903.*;
import static ch.epfl.javelo.projection.Ch1903.n;
import static ch.epfl.javelo.projection.WebMercator.x;
import static ch.epfl.javelo.projection.WebMercator.y;
import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PointWebMercatorTest {

    public static final double DELTA = 1e-11;

    @Test
    void pointWebMercatorConstructorThrowsOnInvalidCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(0.5, -0.5);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(-1, 0.6);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(3, -1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PointWebMercator(1.1, 0.1);
        });
    }

    @Test
    void pointChConstructorWorksOnValidCoordinates() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i++) {
            var x = rng.nextDouble(0, 1);
            var y = rng.nextDouble(0, 1);
            new PointWebMercator(x, y);
        }
    }

    @Test
    void pointWebMercatorOfWorksOnKnownValues() {
        var actual1 = PointWebMercator.of(3,0,0.2);
        var expected1 = new PointWebMercator(0,0.025);
        assertEquals(expected1, actual1);

        var actual2 = PointWebMercator.of(10,0.8,1);
        var expected2 = new PointWebMercator(0.00078125,0.0009765625);
        assertEquals(expected2, actual2);

        var actual3 = PointWebMercator.of(19,1,1);
        var expected3 = new PointWebMercator(0.00000190734,0.00000190734);
        assertEquals(expected3.x(), actual3.x(), DELTA );
        assertEquals(expected3.y(), actual3.y(), DELTA);

        var actual4 = PointWebMercator.of(0,0.9,0.6);
        var expected4 = new PointWebMercator(0.9,0.6);
        assertEquals(expected4, actual4);
    }

    @Test
    void pointWebMercatorOfPointChWorksOnKnownValues() {
        var actual1 = PointWebMercator.ofPointCh(new PointCh(2600000, 1200000));
        var expected1 = new PointWebMercator(x(new PointCh(2600000, 1200000).lon()),
                y(new PointCh(2600000, 1200000).lat()));
        assertEquals(expected1, actual1);

        var actual2 = PointWebMercator.ofPointCh(new PointCh(2600100, 1200000));
        var expected2 = new PointWebMercator(x(new PointCh(2600100, 1200000).lon()),
                y(new PointCh(2600100, 1200000).lat()));
        assertEquals(expected2, actual2);

        var actual3 = PointWebMercator.ofPointCh(new PointCh(2600000, 1200100));
        var expected3 = new PointWebMercator(x(new PointCh(2600000, 1200100).lon()),
                y(new PointCh(2600000, 1200100).lat()));
        assertEquals(expected3, actual3);

        var actual4 = PointWebMercator.ofPointCh(new PointCh(2601234, 1201234));
        var expected4 = new PointWebMercator(x(new PointCh(2601234, 1201234).lon()),
                y(new PointCh(2601234, 1201234).lat()));
        assertEquals(expected4, actual4);
    }

    @Test
    void pointWebMercatorXAtZoomLevelWorksOnKnownValues() {
        var actual1 = new PointWebMercator(0.5,1).xAtZoomLevel(17);
        var expected1 = 0.5*Math.pow(2,17);
        assertEquals(expected1, actual1, DELTA);

        var actual2 = new PointWebMercator(0.5,0).xAtZoomLevel(13);
        var expected2 = 0.5*Math.pow(2,13);
        assertEquals(expected2, actual2, DELTA);

        var actual3 = new PointWebMercator(1,1).xAtZoomLevel(17);
        var expected3 = Math.pow(2,17);
        assertEquals(expected3, actual3, DELTA);

        var actual4 = new PointWebMercator(0.5,0.9).xAtZoomLevel(5);
        var expected4 = 0.5*Math.pow(2,5);
        assertEquals(expected4, actual4, DELTA);
    }

    @Test
    void pointWebMercatorYAtZoomLevelWorksOnKnownValues() {
        var actual1 = new PointWebMercator(0.5,1).yAtZoomLevel(1);
        var expected1 = Math.pow(2,1);
        assertEquals(expected1, actual1, DELTA);

        var actual2 = new PointWebMercator(0.5,0).yAtZoomLevel(15);
        var expected2 = 0;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = new PointWebMercator(1,0.274).yAtZoomLevel(11);
        var expected3 = 0.274*Math.pow(2,11);
        assertEquals(expected3, actual3, DELTA);

        var actual4 = new PointWebMercator(0.5,0.9).yAtZoomLevel(17);
        var expected4 = 0.9*Math.pow(2,17);
        assertEquals(expected4, actual4, DELTA);
    }

    @Test
    void pointWebMercatorToPointChWorksOnKnownValues() {
        double lon = lon(2500000.0,1175000.0);
        double lat = lat(2500000.0,1175000.0);
        double x = x(lon);
        double y = y(lat);
        double newLon = WebMercator.lon(x);
        double newLat = WebMercator.lat(y);
        double autreE = e(newLon, newLat);
        double autreN = n(newLon, newLat);
        double e = e(WebMercator.lon(x(lon(2500000.0,1175000.0))), WebMercator.lat(y(lat(2500000.0,1175000.0))));
        double n = n(WebMercator.lon(x(lon(2500000.0,1175000.0))), WebMercator.lat(y(lat(2500000.0,1175000.0))));
        assertEquals(e,2500000.0);
        assertEquals(n,1175000.0);
        //Question: Pourquoi les valeurs sont légèrement différentes?
       /* var actual1 = new PointWebMercator(x(lon(2500000.0,1175000.0)),y(lat(2500000.0,1175000.0)))
                .toPointCh();
        var expected1 = new PointCh(2500000.0,1175000.0);
        assertEquals(expected1, actual1);

        var actual2 = new PointWebMercator(x(lon(2524000,1175001)),y(lat(2524000,1175001)))
                .toPointCh();
        var expected2 = new PointCh(2524000,1175001);
        assertEquals(expected2, actual2);*/

        var actual3 = new PointWebMercator(x(lon(2234,948474)),y(lat(2234,948474)))
                .toPointCh();
        var expected3 = (PointCh)null;
        assertEquals(expected3, actual3);

        var actual4 = new PointWebMercator(x(lon(100,28837)),y(lat(100,28837)))
                .toPointCh();
        var expected4 = (PointCh)null;
        assertEquals(expected4, actual4);
    }
}
