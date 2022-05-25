package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.test.TestRandomizer;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

public class MapViewParametersTest {

    @Test
    void topLeftTestWithRandomCoordinates() {
        var rng = newRandom();
        for (var i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i += 1) {
            var x = rng.nextDouble(1, 100000);
            var y = rng.nextDouble(1, 100000);
            MapViewParameters map = new MapViewParameters(5, x, y);
            Point2D point2D = new Point2D(x, y);
            assertEquals(point2D, map.topLeft());
        }
    }

    @Test
    void withXYonRandomXY() {
        var rng = newRandom();
        for (var i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i += 1) {
            var x = rng.nextDouble(1, 100000);
            var y = rng.nextDouble(1, 100000);
            var zoom = rng.nextInt(0, 19);
            MapViewParameters map = new MapViewParameters(zoom, 535, 72615);
            MapViewParameters expectedMap = new MapViewParameters(zoom, x, y);
            assertEquals(expectedMap, map.withMinXY(x, y));
        }
    }

    @Test
    void pointAtWorksOnNapoleon(){
            PointWebMercator expected = PointWebMercator.of(19, 69561722,47468099);
            MapViewParameters mapViewParameters = new MapViewParameters(19, 0, 0);
            assertEquals(expected, mapViewParameters.pointAtPointWebMercator(69561722, 47468099));
    }

    @Test
    void viewsWorkOnRandomValues(){
        var rng = newRandom();
        for (var i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i += 1) {
            var x1 = rng.nextDouble(0, 1);
            var y1 = rng.nextDouble(0, 1);
            PointWebMercator pointWebMercator = new PointWebMercator(x1, y1);
            var x2 = rng.nextDouble(0, 10000);
            var y2 = rng.nextDouble(0, 10000);
            var zoom = rng.nextInt(0, 19);
            MapViewParameters mapViewParameters = new MapViewParameters(zoom, x2, y2);
            double expectedX = pointWebMercator.xAtZoomLevel(zoom) - x2;
            double expectedY = pointWebMercator.yAtZoomLevel(zoom) - y2;
            assertEquals(expectedX, mapViewParameters.viewX(pointWebMercator));
            assertEquals(expectedY, mapViewParameters.viewY(pointWebMercator));
        }
    }

    MapViewParameters mapViewParameters0 = new MapViewParameters(2, 162075, 168000);
    MapViewParameters mapViewParameters1 = new MapViewParameters(5, 156075, 149000);
    MapViewParameters mapViewParameters2 = new MapViewParameters(8, 120075, 100000);
    MapViewParameters mapViewParameters3 = new MapViewParameters(13, 162985, 168111);
    MapViewParameters mapViewParameters4 = new MapViewParameters(10, 130985, 90111);
    MapViewParameters mapViewParameters5 = new MapViewParameters(6,  2000, 1000);
    MapViewParameters mapViewParameters6 = new MapViewParameters(8, 356, 678);
    MapViewParameters mapViewParameters7 = new MapViewParameters(5, 256, 125);

    PointWebMercator point0 = new PointWebMercator(0,0.00009765625);
    PointWebMercator point1 = new PointWebMercator(0.00000305175,0.00000381469);
    PointWebMercator point2 = new PointWebMercator(0.00000000745,0.00000000745);
    PointWebMercator point3 = new PointWebMercator(0.003515625,0.00234375);

    //J'ai du mal à get c'est quoi les cas limites, so j'ai juste mis random values

    @Test
    public void topLeftTest() {
        Point2D actual0 = mapViewParameters0.topLeft();
        Point2D expected0 = new Point2D(162075, 168000);
        assertEquals(actual0, expected0);
        Point2D actual1 = mapViewParameters1.topLeft();
        Point2D expected1 = new Point2D(156075, 149000);
        assertEquals(actual1, expected1);
        Point2D actual2 = mapViewParameters2.topLeft();
        Point2D expected2 = new Point2D(120075, 100000);
        assertEquals(actual2, expected2);
        Point2D actual3 = mapViewParameters3.topLeft();
        Point2D expected3 = new Point2D(162985, 168111);
        assertEquals(actual3, expected3);
    }

    @Test
    public void  withMinXYTest() {
        MapViewParameters actual0 = mapViewParameters0.withMinXY(100000, 200000);
        MapViewParameters expected0 = new MapViewParameters(mapViewParameters0.zoomLevel(), 100000, 200000);
        assertEquals(actual0, expected0);
        MapViewParameters actual1 = mapViewParameters1.withMinXY(145000, 500000);
        MapViewParameters expected1 = new MapViewParameters(mapViewParameters1.zoomLevel(), 145000, 500000);
        assertEquals(actual1, expected1);
        MapViewParameters actual2 = mapViewParameters2.withMinXY(145780, 524500);
        MapViewParameters expected2 = new MapViewParameters(mapViewParameters2.zoomLevel(), 145780, 524500);
        assertEquals(actual2, expected2);
        MapViewParameters actual3 = mapViewParameters3.withMinXY(118280, 399500);
        MapViewParameters expected3 = new MapViewParameters(mapViewParameters3.zoomLevel(), 118280, 399500);
        assertEquals(actual3, expected3);
    }

    @Test
    public void  pointAtTest() {
        PointWebMercator actual0 = mapViewParameters4.pointAtPointWebMercator(135735, 92327);
        PointWebMercator expected0 = PointWebMercator.of(10, 135735, 92327);
        assertEquals(actual0, expected0);
        PointWebMercator actual1 = mapViewParameters5.pointAtPointWebMercator(2056, 1028);
        PointWebMercator expected1 = PointWebMercator.of(6, 2056, 1028);
        assertEquals(actual1, expected1);
        PointWebMercator actual2 = mapViewParameters6.pointAtPointWebMercator(356, 678);
        PointWebMercator expected2 = PointWebMercator.of(8, 356, 678);
        assertEquals(actual2, expected2);
        PointWebMercator actual3 = mapViewParameters7.pointAtPointWebMercator(256, 125);
        PointWebMercator expected3 = PointWebMercator.of(5, 256, 125);
        assertEquals(actual3, expected3);
    }

    @Test
    public void  viewXTest() {
        /*double actual0 = mapViewParameters0.viewX(point0);
        double expected0 = ;
        assertEquals(actual0, expected0);
        double actual1 = mapViewParameters1.viewX(point1);
        double expected1 = ;
        assertEquals(actual1, expected1);
        double actual2 = mapViewParameters2.viewX(point2);
        double expected2 = ;
        assertEquals(actual2, expected2);
        double actual3 = mapViewParameters3.viewX(point2);
        double expected3 = ;
        assertEquals(actual3, expected3);*/
    }

    @Test
    public void  viewYTest() {
        /*double actual0 = mapViewParameters0.viewY(point0);
        double expected0 = ;
        assertEquals(actual0, expected0);
        double actual1 = mapViewParameters1.viewY(point1);
        double expected1 = ;
        assertEquals(actual1, expected1);
        double actual2 = mapViewParameters2.viewY(point2);
        double expected2 = ;
        assertEquals(actual2, expected2);
        double actual3 = mapViewParameters3.viewY(point2);
        double expected3 = ;
        assertEquals(actual3, expected3);*/
    }
}
