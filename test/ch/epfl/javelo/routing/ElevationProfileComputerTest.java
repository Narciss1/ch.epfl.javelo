package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.routing.ElevationProfileComputer.*;
import static ch.epfl.test.TestRandomizer.newRandom;
import static java.lang.Float.NaN;
import static org.junit.jupiter.api.Assertions.*;


public class ElevationProfileComputerTest {

    public class RouteTest1 implements Route {

        private List<Edge> edges;
        private PointCh point1 = new PointCh(2485010, 1075010); //NotARealAttribute

        public RouteTest1(List<Edge> edges) {
            if (edges.isEmpty()) {
                throw new IllegalArgumentException();
            } else {
                this.edges = edges;
            }
        }

        @Override
        public double length() {
            double length = 0;
            for (int i = 0; i < edges.size(); ++i) {
                length += edges.get(i).length();
            }
            return length;
        }

        @Override
        public int indexOfSegmentAt(double position) {
            return 0;
        }

        @Override
        public List<Edge> edges() {
            return edges;
        }

        @Override
        public List<PointCh> points() {
            List<PointCh> points = new ArrayList<>();
            for (int i = 0; i < edges.size(); ++i) {
                points.add(edges.get(i).fromPoint());
            }
            points.add(edges.get(edges.size() - 1).toPoint());
            return points;
        }

        public RoutePoint pointClosestTo(PointCh point) {
            RoutePoint route1 = new RoutePoint(point1, 10, 20);
            return route1;
        }

        @Override
        public PointCh pointAt(double position) {
            return point1;
        }


        //J'AI INVENTE UNE SORTE D'ALGO BUT NOT SURE.
        @Override
        public double elevationAt(double position) {
            double oldPosition = position;
            double newPosition = position;
            for (int i = 0; i < edges.size(); ++i) {
                newPosition = oldPosition - edges.get(i).length();
                if (newPosition <= 0) {
                    return edges.get(i).elevationAt(oldPosition);
                } else {
                    oldPosition = newPosition;
                }
            }
            return 444;
        }

        @Override
        public int nodeClosestTo(double position) {
            return 444;
        }
    }

    @Test
    void elevationProfileWorksForEmptyProfiles() {
        PointCh fromPoint = new PointCh(2485010, 1075010);
        PointCh toPoint = new PointCh(2485020, 1075020);
        DoubleUnaryOperator nan = Functions.constant(Double.NaN);
        Edge edge0 = new Edge(1, 2, fromPoint, toPoint, 10, nan);
        Edge edge1 = new Edge(2, 3, fromPoint, toPoint, 10, nan);
        Edge edge2 = new Edge(4, 5, fromPoint, toPoint, 10, nan);
        Edge edge3 = new Edge(5, 6, fromPoint, toPoint, 10, nan);
        Edge edge4 = new Edge(6, 7, fromPoint, toPoint, 10, nan);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge0);
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        RouteTest1 routeTest1 = new RouteTest1(edges);
        ElevationProfile elevationProfileTesting = elevationProfile(routeTest1, 10);
        var rng = newRandom();
        for (var i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i += 1) {
            var position = rng.nextDouble(0, 50);
            assertEquals(0, elevationProfileTesting.elevationAt(position));
        }
    }

    @Test
    void elevationProfileOneEdgeNotNaN(){
        PointCh fromPoint = new PointCh(2485010, 1075010);
        PointCh toPoint = new PointCh(2485020, 1075020);
        DoubleUnaryOperator nan = Functions.constant(Double.NaN);
        DoubleUnaryOperator cst = Functions.constant(5.5);
        Edge edge0 = new Edge(1, 2, fromPoint, toPoint, 10, nan);
        Edge edge1 = new Edge(2, 3, fromPoint, toPoint, 10, nan);
        Edge edge2 = new Edge(4, 5, fromPoint, toPoint, 10, cst);
        Edge edge3 = new Edge(5, 6, fromPoint, toPoint, 10, nan);
        Edge edge4 = new Edge(6, 7, fromPoint, toPoint, 10, nan);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge0);
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        RouteTest1 routeTest1 = new RouteTest1(edges);
        ElevationProfile elevationProfileTesting = elevationProfile(routeTest1, 10);
        assertEquals(5.5, elevationProfileTesting.elevationAt(25));
        assertEquals(5.5, elevationProfileTesting.elevationAt(35));
        assertEquals(5.5, elevationProfileTesting.elevationAt(45));
        var rng = newRandom();
        for (var i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i += 1) {
            var position = rng.nextDouble(0, 50);
            assertEquals(5.5, elevationProfileTesting.elevationAt(position));
        }
    }

    @Test
    void elevationProfileWith1NaNInTheMiddleSimple(){
        PointCh fromPoint = new PointCh(2485010, 1075010);
        PointCh toPoint = new PointCh(2485020, 1075020);
        DoubleUnaryOperator nan = Functions.constant(Double.NaN);
        DoubleUnaryOperator cst = Functions.constant(5.5);
        Edge edge0 = new Edge(1, 2, fromPoint, toPoint, 10, cst);
        Edge edge1 = new Edge(2, 3, fromPoint, toPoint, 10, cst);
        Edge edge2 = new Edge(4, 5, fromPoint, toPoint, 10, nan);
        Edge edge3 = new Edge(5, 6, fromPoint, toPoint, 10, cst);
        Edge edge4 = new Edge(6, 7, fromPoint, toPoint, 10, cst);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge0);
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        RouteTest1 routeTest1 = new RouteTest1(edges);
        ElevationProfile elevationProfileTesting = elevationProfile(routeTest1, 10);
        var rng = newRandom();
        for (var i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i += 1) {
            var position = rng.nextDouble(0, 50);
            assertEquals(5.5, elevationProfileTesting.elevationAt(position));
        }
    }

    @Test
    void elevationProfileWith1NaNInTheMiddleDouble(){
        PointCh fromPoint = new PointCh(2485010, 1075010);
        PointCh toPoint = new PointCh(2485020, 1075020);
        DoubleUnaryOperator nan = Functions.constant(Double.NaN);
        DoubleUnaryOperator cst = Functions.constant(5);
        DoubleUnaryOperator cst2 = Functions.constant(6);
        Edge edge0 = new Edge(1, 2, fromPoint, toPoint, 1, cst);
        Edge edge1 = new Edge(2, 3, fromPoint, toPoint, 1, cst);
        Edge edge2 = new Edge(4, 5, fromPoint, toPoint, 1, nan);
        Edge edge3 = new Edge(5, 6, fromPoint, toPoint, 1, cst2);
        Edge edge4 = new Edge(6, 7, fromPoint, toPoint, 1, cst2);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge0);
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        RouteTest1 routeTest1 = new RouteTest1(edges);
        ElevationProfile elevationProfileTesting = elevationProfile(routeTest1, 1);
        assertEquals(5, elevationProfileTesting.elevationAt(0));
        assertEquals(5, elevationProfileTesting.elevationAt(2));
        //assertEquals(5.5, elevationProfileTesting.elevationAt(2.5));
        //J'ai l'impression je sais juste pas le tester pck l'interpolation seule marche... A revoir
        assertEquals(6,elevationProfileTesting.elevationAt(4));
    }

    @Test
    void elevationProfileCompleteTest(){
        PointCh fromPoint = new PointCh(2485010, 1075010);
        PointCh toPoint = new PointCh(2485020, 1075020);
        DoubleUnaryOperator nan = Functions.constant(Double.NaN);
        DoubleUnaryOperator cst = Functions.constant(5);
        float[] squaredSamples = {0,4,16,36,64,100};
        DoubleUnaryOperator squared = Functions.sampled(squaredSamples, 10);
        Edge edge0 = new Edge(1, 2, fromPoint, toPoint, 10, nan);
        Edge edge1 = new Edge(2, 3, fromPoint, toPoint, 10, cst);
        Edge edge2 = new Edge(4, 5, fromPoint, toPoint, 10, nan);
        Edge edge3 = new Edge(5, 6, fromPoint, toPoint, 10,squared );
        Edge edge4 = new Edge(6, 7, fromPoint, toPoint, 10, nan);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge0);
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        RouteTest1 routeTest1 = new RouteTest1(edges);
        ElevationProfile elevationProfileTesting = elevationProfile(routeTest1, 2.5);
        float[] expected = {5,5,5,5,5,5,5,5,5,5.4f,5.8f,6.2f,6.6f,7,26,57,100,100,100,100,100};
        assertEquals(5,elevationProfileTesting.elevationAt(5));
        assertEquals(5,elevationProfileTesting.elevationAt(15));
        assertEquals(5.4f,elevationProfileTesting.elevationAt(22.5));
        assertEquals(5.8f,elevationProfileTesting.elevationAt(25));
        assertEquals(6.6f,elevationProfileTesting.elevationAt(30));
        assertEquals(7,elevationProfileTesting.elevationAt(32.5));
        assertEquals(100,elevationProfileTesting.elevationAt(50));
    }



    @Test
    void fillBeginningAndEndWorks(){
        float[] samplesExamples ={NaN, NaN, 5, NaN, NaN};
        float[] expected = {5,5,5,5,5};
        assertArrayEquals(expected, fillBeginningAndEnd(samplesExamples));
    }

    @Test
    void interpolateElevationWorks(){
        float[] samplesExamples = {5,5,NaN,6,6};
        float[] expected = {5,5,5.5f,6,6};
        assertArrayEquals(expected, interpolateElevation(samplesExamples));
    }

    @Test
    void interpolateElevationWorksFor3NaNSimple(){
        float[] samplesExamples = {5,NaN, NaN,NaN, 6};
        float[] expected = {5, 5.25f, 5.5f, 5.75f, 6};
        assertArrayEquals(expected, interpolateElevation(samplesExamples));
    }

    @Test
    void elevationProfileOneEdgeNotNa(){
        Edge edge = new Edge(0, 0, null, null, 24,
                Functions.sampled(new float[]{Float.NaN, 4, 0, Float.NaN, 8, 8, Float.NaN}, 24));
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        RouteTest1 routeTest1 = new RouteTest1(edges);
        System.out.println(routeTest1.elevationAt(8));
        ElevationProfile elevationProfileTesting = elevationProfile(routeTest1, 4);
        Assertions.assertEquals(4, elevationProfileTesting.elevationAt(0));
        Assertions.assertEquals(4, elevationProfileTesting.elevationAt(2));
        Assertions.assertEquals(2, elevationProfileTesting.elevationAt(6));
        Assertions.assertEquals(6, elevationProfileTesting.elevationAt(14));
        Assertions.assertEquals(8, elevationProfileTesting.elevationAt(16));
        Assertions.assertEquals(8, elevationProfileTesting.elevationAt(21));
        Assertions.assertEquals(8, elevationProfileTesting.elevationAt(24));
        Assertions.assertEquals(8, elevationProfileTesting.elevationAt(25));
        Assertions.assertEquals(4, elevationProfileTesting.elevationAt(-1));
    }

    PointCh A = new PointCh(2600123, 1200456);
    PointCh B = new PointCh(2600456, 1200789);
    PointCh C = new PointCh(2600789, 1200123);
    PointCh D = new PointCh(2601000, 1201000);
    PointCh E = new PointCh(2601283, 1201110);
    PointCh F = new PointCh(2602000, 1201999);
    PointCh G = new PointCh(2602500, 1201010);
    PointCh H = new PointCh(2602877, 1200829);
    PointCh I = new PointCh(2603000, 1201086);
    PointCh J = new PointCh(2603124, 1198878);

    Edge edge1 = new Edge(1, 2, A, B, A.distanceTo(B), x -> 4);
    Edge edge2 = new Edge(3, 4, B, C, B.distanceTo(C), x -> Double.NaN);
    Edge edge3 = new Edge(4, 5, C, D, C.distanceTo(D), x -> 6.);
    Edge edge4 = new Edge(5, 6, D, E, D.distanceTo(E), x -> 9.5);
    Edge edge5 = new Edge(7, 8, E, F, E.distanceTo(F), x -> Double.NaN);
    Edge edge6 = new Edge(9, 10, F, G, F.distanceTo(G), x -> Double.NaN);
    Edge edge7 = new Edge(10, 11, G, H, G.distanceTo(H), x -> 15);
    Edge edge8 = new Edge(11, 12, H, I, H.distanceTo(I), x -> 283.2987492);
    Edge edge9 = new Edge(12, 13, I, J, I.distanceTo(J), x -> Double.NaN);
    public SingleRoute ourRoute() {
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);edges.add(edge2);edges.add(edge3);edges.add(edge4);
        edges.add(edge5);edges.add(edge6);edges.add(edge7);edges.add(edge8);edges.add(edge9);
        return new SingleRoute(edges);
    }
    @Test
    void test1(){
        float[] tab = { 4f, 5f, 6f, 9.5f, 11.5f, 13f, 15, 283.2987492f, 283.2987492f };
        ElevationProfile expected = new ElevationProfile(ourRoute().length(), tab );
        assertEquals(expected, ElevationProfileComputer.elevationProfile(ourRoute(), ourRoute().length()/tab.length));
    }
}
