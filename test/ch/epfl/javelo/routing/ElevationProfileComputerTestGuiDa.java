package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import jdk.swing.interop.SwingInterOpUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.javelo.routing.ElevationProfileComputer.elevationProfile;

public class ElevationProfileComputerTestGuiDa {
    @Test
    void elevationProfileOneEdgeNotNaNazhudhaiodjaozdjoaizdjoazdja(){
        Edge edge = new Edge(0, 0, null, null, 24, Functions.sampled(new float[]{Float.NaN, 4, 0, Float.NaN, 8, 8, Float.NaN}, 24));
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute routeTest1 = new SingleRoute(edges);

        System.out.println(routeTest1.elevationAt(8));
        ElevationProfile elevationProfileTesting = elevationProfile(routeTest1, 4);
        //Assertions.assertEquals(4, elevationProfileTesting.elevationAt(0));
        //Assertions.assertEquals(4, elevationProfileTesting.elevationAt(2));

        Assertions.assertEquals(0, elevationProfileTesting.elevationAt(8));
        /*Assertions.assertEquals(2, elevationProfileTesting.elevationAt(6));
        Assertions.assertEquals(6, elevationProfileTesting.elevationAt(14));
        Assertions.assertEquals(8, elevationProfileTesting.elevationAt(16));
        Assertions.assertEquals(8, elevationProfileTesting.elevationAt(21));
        Assertions.assertEquals(8, elevationProfileTesting.elevationAt(24));
        Assertions.assertEquals(8, elevationProfileTesting.elevationAt(25));
        Assertions.assertEquals(4, elevationProfileTesting.elevationAt(-1));*/
    }

    @Test
    void elevationProfileOneEdgeNotNaNazhudhaiodjaozdjoaieazezdjoazdja(){
        Edge edge = new Edge(0, 0, null, null, 24, Functions.sampled(new float[]{Float.NaN,Float.NaN}, 24));
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute routeTest1 = new SingleRoute(edges);
        ElevationProfile elevationProfileTesting = elevationProfile(routeTest1, 4);
        Assertions.assertEquals(0, elevationProfileTesting.elevationAt(0));
        Assertions.assertEquals(0, elevationProfileTesting.elevationAt(24));

        Assertions.assertEquals(0, elevationProfileTesting.elevationAt(25));
        Assertions.assertEquals(0, elevationProfileTesting.elevationAt(-1));
    }
    @Test
    void elevationProfileOneEdgeNotNaNazhudhaiodjaozdjoaieazezdjoazdjeazea(){
        Edge edge = new Edge(0, 0, null, null, 2, Functions.sampled(new float[]{Float.NaN,Float.NaN, 10}, 2));
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute routeTest1 = new SingleRoute(edges);
        ElevationProfile elevationProfileTesting = elevationProfile(routeTest1, 4);
        Assertions.assertEquals(10, elevationProfileTesting.elevationAt(0));
        Assertions.assertEquals(10, elevationProfileTesting.elevationAt(2));

        Assertions.assertEquals(10, elevationProfileTesting.elevationAt(25));
        Assertions.assertEquals(10, elevationProfileTesting.elevationAt(-1));
    }
    @Test
    void elevationProfileOneEdgeNotNaNazhudhaiodjaozdjoaieazezezaedjoazdjeazea(){
        Edge edge = new Edge(0, 0, null, null, 5, Functions.sampled(new float[]{8,Float.NaN,Float.NaN, 10, Float.NaN, Float.NaN}, 5));
        List<Edge> edges = new ArrayList<>();
        edges.add(edge);
        SingleRoute routeTest1 = new SingleRoute(edges);
        ElevationProfile elevationProfileTesting = elevationProfile(routeTest1, 4);
        Assertions.assertEquals(8, elevationProfileTesting.elevationAt(0));
        Assertions.assertEquals(9, elevationProfileTesting.elevationAt(1));
        Assertions.assertEquals(10, elevationProfileTesting.elevationAt(2));
        Assertions.assertEquals(10, elevationProfileTesting.elevationAt(3));
        Assertions.assertEquals(10, elevationProfileTesting.elevationAt(4));
        Assertions.assertEquals(10, elevationProfileTesting.elevationAt(5));
        Assertions.assertEquals(10, elevationProfileTesting.elevationAt(1000));

        Assertions.assertEquals(8, elevationProfileTesting.elevationAt(-1));
    }
}
