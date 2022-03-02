package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static ch.epfl.javelo.projection.Ch1903.*;
import static ch.epfl.javelo.projection.WebMercator.x;
import static ch.epfl.javelo.projection.WebMercator.y;
import static ch.epfl.test.TestRandomizerRayan.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizerRayan.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PointWebMercatorTest {

    public static final double DELTA = 1e-11;
    public static final double DELTA1 = 2;
    public static final double DELTA2 = 3;

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

        var actual1 = new PointWebMercator(x(lon(2500000.0,1175000.0)),y(lat(2500000.0,1175000.0)))
                .toPointCh();
        var expectedE1 = 2500000.0;
        var expectedN1 = 1175000.0;
        assertEquals(expectedE1, actual1.e(), DELTA1);
        assertEquals(expectedN1, actual1.n(), DELTA1);

        var actual2 = new PointWebMercator(x(lon(2524000,1175001)),y(lat(2524000,1175001)))
                .toPointCh();
        var expectedE2 = 2524000.0;
        var expectedN2 = 1175001.0;
        assertEquals(expectedE2, actual2.e(), DELTA1);
        assertEquals(expectedN2, actual2.n(), DELTA1);

        //Vérifier sur piazza que le delta n'est pas trop grand.
        var actual3 = new PointWebMercator(x(lon(2485200,1075200)),y(lat(2485200,1075200)))
                .toPointCh();
        var expectedE3 = 2485200;
        var expectedN3 = 1075200;
        assertEquals(expectedE3, actual3.e(), DELTA2);
        assertEquals(expectedN3, actual3.n(), DELTA2);

        var actual4 = new PointWebMercator(x(lon(2833800,1294000)),y(lat(2833800,1294000)))
                .toPointCh();
        var expectedE4 = 2833800;
        var expectedN4 = 1294000;
        assertEquals(expectedE4, actual4.e(), DELTA1);
        assertEquals(expectedN4, actual4.n(), DELTA1);
    }
}
