package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PointWebMercatorTest {

    public static final double DELTA = 1e-7;

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
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var x = rng.nextDouble(0, 1);
            var y = rng.nextDouble(0, 1);
            new PointCh(x, y);
        }
    }

    @Test
    void pointWebMercatorOfWorksOnKnownValues() {
        var actual1 = PointWebMercator.of(3,0,0.2);
        var expected1 = new PointWebMercator(1,1);
        assertEquals(expected1, actual1);

        var actual2 = PointWebMercator.of(10,0.8,1);
        var expected2 = new PointWebMercator(1,1);
        assertEquals(expected2, actual2);

        var actual3 = PointWebMercator.of(19,1,1);
        var expected3 = new PointWebMercator(1,1);
        assertEquals(expected3, actual3);

        var actual4 = PointWebMercator.of(0,0.9,0.6);
        var expected4 = new PointWebMercator(1,1);
        assertEquals(expected4, actual4);
    }

    @Test
    void pointWebMercatorOfPointChWorksOnKnownValues() {
        var actual1 = PointWebMercator.ofPointCh(new PointCh(2600000, 1200000));
        var expected1 = new PointWebMercator(1,1);
        assertEquals(expected1, actual1);

        var actual2 = PointWebMercator.ofPointCh(new PointCh(2600100, 1200000));
        var expected2 = new PointWebMercator(1,1);
        assertEquals(expected2, actual2);

        var actual3 = PointWebMercator.ofPointCh(new PointCh(2600000, 1200100));
        var expected3 = new PointWebMercator(1,1);
        assertEquals(expected3, actual3);

        var actual4 = PointWebMercator.ofPointCh(new PointCh(2601234, 1201234));
        var expected4 = new PointWebMercator(1,1);
        assertEquals(expected4, actual4);
    }

    @Test
    void pointWebMercatorXAtZoomLevelWorksOnKnownValues() {
        var actual1 = new PointWebMercator(0.5,1).xAtZoomLevel(17);
        var expected1 = 0.0;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = new PointWebMercator(0.5,0).xAtZoomLevel(17);
        var expected2 = 10000.0;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = new PointWebMercator(1,1).xAtZoomLevel(17);
        var expected3 = 10000.0;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = new PointWebMercator(0.5,0.9).xAtZoomLevel(17);
        var expected4 = 3045512.0;
        assertEquals(expected4, actual4, DELTA);
    }

    @Test
    void pointWebMercatorYAtZoomLevelWorksOnKnownValues() {
        var actual1 = new PointWebMercator(0.5,1).yAtZoomLevel(17);
        var expected1 = 0.0;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = new PointWebMercator(0.5,0).yAtZoomLevel(17);
        var expected2 = 10000.0;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = new PointWebMercator(1,1).yAtZoomLevel(17);
        var expected3 = 10000.0;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = new PointWebMercator(0.5,0.9).yAtZoomLevel(17);
        var expected4 = 3045512.0;
        assertEquals(expected4, actual4, DELTA);
    }

    @Test
    void pointWebMercatorToPointChWorksOnKnownValues() {
        var actual1 = new PointWebMercator(0.5,0.8)
                .toPointCh();
        var expected1 = new PointCh(2234,948474);
        assertEquals(expected1, actual1);

        var actual2 = new PointWebMercator(0,1)
                .toPointCh();
        var expected2 = new PointCh(2234,948474);
        assertEquals(expected2, actual2);

        var actual3 = new PointWebMercator(0.3837,0.281)
                .toPointCh();
        var expected3 = new PointCh(2234,948474);
        assertEquals(expected3, actual3);

        var actual4 = new PointWebMercator(0.1993, 0.1777)
                .toPointCh();
        var expected4 = new PointCh(2234,948474);
        assertEquals(expected4, actual4);
    }
}
