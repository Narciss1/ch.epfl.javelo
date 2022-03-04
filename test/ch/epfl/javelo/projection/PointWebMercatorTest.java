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
        var expected1 = new PointWebMercator(0,0.00009765625);
        assertEquals(expected1.x(), actual1.x(), DELTA );
        assertEquals(expected1.y(), actual1.y(), DELTA);

        var actual2 = PointWebMercator.of(10,0.8,1);
        var expected2 = new PointWebMercator(0.00000305175,0.00000381469);
        assertEquals(expected2.x(), actual2.x(), DELTA );
        assertEquals(expected2.y(), actual2.y(), DELTA);

        var actual3 = PointWebMercator.of(19,1,1);
        var expected3 = new PointWebMercator(0.00000000745,0.00000000745);
        assertEquals(expected3.x(), actual3.x(), DELTA );
        assertEquals(expected3.y(), actual3.y(), DELTA);

        var actual4 = PointWebMercator.of(0,0.9,0.6);
        var expected4 = new PointWebMercator(0.003515625,0.00234375);
        assertEquals(expected4.x(), actual4.x(), DELTA );
        assertEquals(expected4.y(), actual4.y(), DELTA);
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
        var expected1 = 0.5*Math.pow(2, 17 + 8);
        assertEquals(expected1, actual1, DELTA);

        var actual2 = new PointWebMercator(0.5,0).xAtZoomLevel(13);
        var expected2 = 0.5*Math.pow(2, 13 + 8);
        assertEquals(expected2, actual2, DELTA);

        var actual3 = new PointWebMercator(1,1).xAtZoomLevel(17);
        var expected3 = Math.pow(2, 17 + 8);
        assertEquals(expected3, actual3, DELTA);

        var actual4 = new PointWebMercator(0.5,0.9).xAtZoomLevel(5);
        var expected4 = 0.5*Math.pow(2, 5 + 8);
        assertEquals(expected4, actual4, DELTA);
    }

    @Test
    void pointWebMercatorYAtZoomLevelWorksOnKnownValues() {

        var actual1 = new PointWebMercator(0.5,1).yAtZoomLevel(1);
        var expected1 = Math.pow(2, 1 + 8);
        assertEquals(expected1, actual1, DELTA);

        var actual2 = new PointWebMercator(0.5,0).yAtZoomLevel(15);
        var expected2 = 0;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = new PointWebMercator(1,0.274).yAtZoomLevel(11);
        var expected3 = 0.274*Math.pow(2, 11 + 8);
        assertEquals(expected3, actual3, DELTA);

        var actual4 = new PointWebMercator(0.5,0.9).yAtZoomLevel(17);
        var expected4 = 0.9*Math.pow(2, 17 + 8);
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

    @Test
    void ofTestWithZoomedValues() {
        PointCh pointCh = new PointCh(2_700_000, 1_200_000);
        assertEquals(pointCh.lon(), PointWebMercator.ofPointCh(pointCh).lon(), DELTA);
        assertEquals(pointCh.lat(), PointWebMercator.ofPointCh(pointCh).lat(), DELTA);

        assertThrows(IllegalArgumentException.class, () -> {
                    PointCh pointCh2 = new PointCh(SwissBounds.MAX_E + 1, SwissBounds.MAX_N);

                }
        );
        assertThrows(IllegalArgumentException.class, () -> {
                    PointCh pointCh2 = new PointCh(SwissBounds.MAX_E , SwissBounds.MAX_N + 1);

                }
        );

        PointCh pointCh2 = new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N);
        PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(pointCh);


        assertEquals(pointWebMercator.x(), PointWebMercator.of(8,
                pointWebMercator.xAtZoomLevel(8)
                ,pointWebMercator.yAtZoomLevel(8)).x());
        assertEquals(pointWebMercator.y(), PointWebMercator.of(8,
                pointWebMercator.xAtZoomLevel(8)
                ,pointWebMercator.yAtZoomLevel(8)).y());

        PointWebMercator pointWebMercator2 = PointWebMercator.ofPointCh(pointCh2);


        assertEquals(pointWebMercator2.x(), PointWebMercator.of(8,
                pointWebMercator2.xAtZoomLevel(8)
                ,pointWebMercator2.yAtZoomLevel(8)).x());
        assertEquals(pointWebMercator2.y(), PointWebMercator.of(8,
                pointWebMercator2.xAtZoomLevel(8)
                ,pointWebMercator2.yAtZoomLevel(8)).y());

        double lon = 6.5790772, lat = 46.5218976;
        double x= 0.518275214444, y= 0.353664894749;
        PointWebMercator pointWebMercator1 = new PointWebMercator(x, y);
        System.out.println(pointWebMercator1.xAtZoomLevel(19));
        System.out.println(pointWebMercator1.yAtZoomLevel(19));
    }


    //S

    @Test
    void PointWebMercatorConstructorConstructorThrowsOnInvalidCoordinates(){

        assertThrows(IllegalArgumentException.class, ()->{
            new PointWebMercator(2, 1);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            new PointWebMercator(-1, 1);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            new PointWebMercator(1,  2);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            new PointWebMercator(1, -1);
        });

    }

    @Test
    void PointWebMercatorWorksOnValidCoordinates(){
        var rng = newRandom();

        for(int i = 0; i<RANDOM_ITERATIONS; i++){
            var x  = rng.nextDouble(0, 1);
            var y = rng.nextDouble(0, 1);
            new PointWebMercator(x,y);
        }
    }

    @Test
    void ofToWorksOnValidValues(){

        var actual1 = PointWebMercator.of(3, 0, 0);
        var expected1 = new PointWebMercator(0, 0);
        assertEquals(expected1, actual1);

        var actual2 = PointWebMercator.of(5, 256, 125);
        var expected2 = new PointWebMercator(0.03125, 0.0152587890625);
        assertEquals(expected2, actual2);

        var actual3 = PointWebMercator.of(8, 356, 678);
        var expected3 = new PointWebMercator(0.00543212890625, 0.010345458984375);
        assertEquals(expected3, actual3);

        var actual4 = PointWebMercator.of(6, 2056, 1028);
        var expected4 = new PointWebMercator(0.12548828125, 0.062744140625);
        assertEquals(expected4, actual4);

    }

    @Test
    void ofThrowsOnInvalidValues(){

        assertThrows(IllegalArgumentException.class, () -> {
            PointWebMercator.of(1, 513, 512);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            PointWebMercator.of(2, 312, 1026);
        });
    }

    @Test
    void ofPointChWorksOnValidValues(){
        var actual1 = PointWebMercator.ofPointCh(new PointCh(2485001, 1075001));
        var expected1 = new PointWebMercator(0.5165531653847774,0.3564927149520306);
        assertEquals(expected1, actual1);

        // Ã  faire d'autres plus tard
    }

    @Test
    void xAtZoomLevelWorksOnValidValues(){
        var actual1 = new PointWebMercator(0.4, 0.6);
        var zoomTest1 = actual1.xAtZoomLevel(4);
        var expected1 = 1638.4;
        assertEquals(expected1, zoomTest1);

        var actual2 = new PointWebMercator(0.5, 0.8);
        var zoomTest2 = actual2.xAtZoomLevel(8);
        var expected2 = 32768;
        assertEquals(expected2, zoomTest2);

        var actual3 = new PointWebMercator(0.75, 0);
        var zoomTest3 = actual3.xAtZoomLevel(0);
        var expected3 = 192;
        assertEquals(expected3, zoomTest3);
    }




}
