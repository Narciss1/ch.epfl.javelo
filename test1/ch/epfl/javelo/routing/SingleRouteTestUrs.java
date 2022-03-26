package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static java.lang.Float.NaN;
import static org.junit.jupiter.api.Assertions.*;

class SingleRouteTestUrs {
    PointCh point1 = new PointCh(2535123, 1152123);
    PointCh point2 = new PointCh(2535234, 1152234) ;
    PointCh point3 = new PointCh(2535345, 1152345);
    PointCh point4 = new PointCh(2535456, 1152456);
    PointCh point5 = new PointCh(2535567, 1152567);
    PointCh point6 = new PointCh(2535678, 1152678);

    double xMax = 156.97770542341354;
    DoubleUnaryOperator profile1 = Functions.sampled(new float[]
            {-0.03125f, -0.125f, -0.0625f}, xMax);
    DoubleUnaryOperator profile2 = Functions.sampled(new float[]
            {-0.0625f, -0.125f, -0.125f, -0.0625f, -0.03125f}, xMax);
    DoubleUnaryOperator profile3 = Functions.sampled(new float[]
            {NaN, NaN, NaN, NaN}, xMax);
    DoubleUnaryOperator profile4 = Functions.sampled(new float[]
            {NaN, NaN}, xMax);
    DoubleUnaryOperator profile5 = Functions.sampled(new float[]
            {-0.125f, -0.0625f}, xMax);

    Edge edge1 = new Edge(0, 1, point1, point2, point1.distanceTo(point2), profile1);
    Edge edge2 = new Edge(1, 2, point2, point3, point2.distanceTo(point3), profile2);
    Edge edge3 = new Edge(2, 3, point3, point4, point3.distanceTo(point4), profile3);
    Edge edge4 = new Edge(3, 4, point4, point5, point4.distanceTo(point5), profile4);
    Edge edge5 = new Edge(4, 5, point5, point6, point5.distanceTo(point6), profile5);

    List<Edge> edges;
    List<PointCh> points;
    SingleRoute singleRoute;

    private void create() {

        points = new ArrayList<>();
        points.add(point1);
        points.add(point2);
        points.add(point3);
        points.add(point4);
        points.add(point5);
        points.add(point6);

        edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        edges.add(edge5);

        singleRoute = new SingleRoute(edges);
    }

    @Test
    void indexOfSegmentAt() {
        create();

        assertEquals(0, singleRoute.indexOfSegmentAt(-1082));
        assertEquals(0, singleRoute.indexOfSegmentAt(12409));
        assertEquals(0, singleRoute.indexOfSegmentAt(9265));
        assertEquals(0, singleRoute.indexOfSegmentAt(2094));
        assertEquals(0, singleRoute.indexOfSegmentAt(3160));

    }

    @Test
    void length() {
        create();

        assertEquals(784.8885271170677, singleRoute.length());
    }

    @Test
    void edges() {
        create();

        List<Edge> expectedEdges = new ArrayList<>();
        expectedEdges.add(edge1);
        expectedEdges.add(edge2);
        expectedEdges.add(edge3);
        expectedEdges.add(edge4);
        expectedEdges.add(edge5);

        assertTrue(expectedEdges.equals(edges));
    }

    @Test
    void points() {
        create();

        List<PointCh> expectedPoints = new ArrayList<>();
        expectedPoints.add(point1);
        expectedPoints.add(point2);
        expectedPoints.add(point3);
        expectedPoints.add(point4);
        expectedPoints.add(point5);
        expectedPoints.add(point6);

        assertTrue(expectedPoints.equals(points));
    }

    @Test
    void pointAt() {
        create();

        PointCh expectedPoint1 = new PointCh(2535219.166522241, 1152219.1665222414);
        PointCh actualPoint1 = singleRoute.pointAt(136);
        PointCh expectedPoint2 = new PointCh(2535305.433549546, 1152305.4335495462);
        PointCh actualPoint2 = singleRoute.pointAt(258);
        PointCh expectedPoint3 = new PointCh(2535337.9604614805, 1152337.9604614808);
        PointCh actualPoint3 = singleRoute.pointAt(304);
        PointCh expectedPoint4 = new PointCh(2535515.4442635584, 1152515.4442635586);
        PointCh actualPoint4 = singleRoute.pointAt(555);
        PointCh expectedPoint5 = new PointCh(2535557.1635636487, 1152557.1635636485);
        PointCh actualPoint5 = singleRoute.pointAt(614);
        PointCh expectedPoint6 = point6;
        PointCh actualPoint6 = singleRoute.pointAt(800);

        assertEquals(expectedPoint1, actualPoint1);
        assertEquals(expectedPoint2, actualPoint2);
        assertEquals(expectedPoint3, actualPoint3);
        assertEquals(expectedPoint4, actualPoint4);
        assertEquals(expectedPoint5, actualPoint5);
        assertEquals(expectedPoint6, actualPoint6);
    }

    @Test
    void elevationAt() {
        create();

        assertEquals(-0.03125, singleRoute.elevationAt(-570));
        assertEquals(-0.06253550596010804, singleRoute.elevationAt(157));
       // assertEquals(-0.10185428987243944, singleRoute.elevationAt(250));
        assertEquals(-0.11700157983734075, singleRoute.elevationAt(648));
        assertEquals(-0.08801683140827192, singleRoute.elevationAt(173));
        assertEquals(-0.0625, singleRoute.elevationAt(999));
        assertEquals(Double.NaN, singleRoute.elevationAt(456));

    }

    @Test
    void nodeClosestTo() {
        create();

        assertEquals(0, singleRoute.nodeClosestTo(-290));
        assertEquals(0, singleRoute.nodeClosestTo(33));
        assertEquals(1, singleRoute.nodeClosestTo(230));
        assertEquals(2, singleRoute.nodeClosestTo(285));
        assertEquals(3, singleRoute.nodeClosestTo(461));
        assertEquals(5, singleRoute.nodeClosestTo(920));
        assertEquals(4, singleRoute.nodeClosestTo(571));
        assertEquals(1, singleRoute.nodeClosestTo(169));
        assertEquals(5, singleRoute.nodeClosestTo(860));
        assertEquals(1, singleRoute.nodeClosestTo(86));
        assertEquals(5, singleRoute.nodeClosestTo(739));
        assertEquals(5, singleRoute.nodeClosestTo(10300));
        assertEquals(5, singleRoute.nodeClosestTo(11000));
        assertEquals(5, singleRoute.nodeClosestTo(11500));
        assertEquals(5, singleRoute.nodeClosestTo(12500));
        assertEquals(5, singleRoute.nodeClosestTo(13100));
        assertEquals(5, singleRoute.nodeClosestTo(15000));
    }

    @Test
    void pointClosestTo() {
        create();

        PointCh expectedPoint1 = new PointCh(2535219.166522241, 1152219.1665222414);
        PointCh expectedPoint2 = new PointCh(2535305.433549546, 1152305.4335495462);
        PointCh expectedPoint3 = new PointCh(2535337.9604614805, 1152337.9604614808);
        PointCh expectedPoint4 = new PointCh(2535515.4442635584, 1152515.4442635584);
        PointCh expectedPoint5 = new PointCh(2535557.1635636487, 1152557.1635636487);
        PointCh expectedPoint6 = new PointCh(2535678.0, 1152678.0);

        RoutePoint route1, route2, route3, route4, route5, route6;
        route1 = new RoutePoint(expectedPoint1, 135.99999999992008, 0.0);
        route2 = new RoutePoint(expectedPoint2, 258.000000000059, 0.0);
        route3 = new RoutePoint(expectedPoint3, 303.99999999992787, 0.0);
        route4 = new RoutePoint(expectedPoint4, 554.9999999999316, 2.3283064365386963E-10);
        route5 = new RoutePoint(expectedPoint5, 614.0000000000676, 2.3283064365386963E-10);
        route6 = new RoutePoint(expectedPoint6, 784.8885271170677, 0.0);

        //debug si besoin de verifier les valeurs des routes
        assertEquals(route1, singleRoute.pointClosestTo(singleRoute.pointAt(136)));
        assertEquals(route2, singleRoute.pointClosestTo(singleRoute.pointAt(258)));
        assertEquals(route3, singleRoute.pointClosestTo(singleRoute.pointAt(304)));
        assertEquals(route4, singleRoute.pointClosestTo(singleRoute.pointAt(555)));
        assertEquals(route5, singleRoute.pointClosestTo(singleRoute.pointAt(614)));
        assertEquals(route6, singleRoute.pointClosestTo(singleRoute.pointAt(800)));

    }


    // tests mimi et marwa ->

    PointCh p0 = new PointCh(2600100, 1200400);
    PointCh p1 = new PointCh(2600123, 1200456);
    PointCh p2 = new PointCh(2600456, 1200789);
    PointCh p3 = new PointCh(2600789, 1200123);
    PointCh p4 = new PointCh(2601000, 1201000);

    Edge myEdge1 = new Edge(1, 2, p1, p2, p1.distanceTo(p2), x -> Double.NaN);
    Edge myEdge2 = new Edge(2, 3, p2, p3, p2.distanceTo(p3), x -> Math.sin(x));
    Edge edgeBeforeRoute  =  new Edge(3, 4, p0, p1, p0.distanceTo(p1), x -> Double.NaN);

    private SingleRoute ourRoute() {

        List<Edge> edges = new ArrayList<>();
        edges.add(myEdge1);
        edges.add(myEdge2);

        return new SingleRoute(edges);
    }

    @Test
    void indexOfSegmentAtMM() {
        int expected = 0;
        int actual1 = ourRoute().indexOfSegmentAt(23456);
        int actual2 = ourRoute().indexOfSegmentAt(2343456);
        assertEquals(expected, actual1, "ATTENTION LÀÀÀÀ OH");
        assertEquals(expected, actual2, "ATTENTION LÀÀÀÀ OH");

    }

    @Test
    void constructorThrowsRightException() {
        List<Edge> edgesVide = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> {
            SingleRoute route1 = new SingleRoute(edgesVide);
        });
    }

    @Test
    void lengthMM() {
        double expected = 1215.5437527776;
        double actual = ourRoute().length();
        assertEquals(expected,actual,Math.pow(10,-5));
    }


    @Test
    void pointsMM() {
        List<PointCh> expected = new ArrayList<>();
        expected.add(p1);
        expected.add(p2);
        expected.add(p3);

        assertEquals(expected, ourRoute().points(), "lolilol c faux");

    }

    @Test
    void pointAtMM() {

        PointCh pInter = myEdge1.pointAt(myEdge1.length()/2d); // expected intermediate point :
        PointCh actualInter = ourRoute().pointAt(myEdge1.length()/2d); // actual
        PointCh actual1 = ourRoute().pointAt(0); // the point at the beginning of our SingleRoute
        PointCh actual2 = ourRoute().pointAt(myEdge1.length()); //  the point between the first and second edges on our SingleRoute
        PointCh actual3 = ourRoute().pointAt(myEdge1.length() + myEdge2.length()); // the point at the edge of our SingleRoute
        PointCh actual3bis = ourRoute().pointAt(ourRoute().length()); // just another way to compute the point at the end of the route

        assertEquals(p1,actual1, "oh no…our code…it's broken");
        assertEquals(p2,actual2, "oh no…our code…it's broken");
        assertEquals(p3,actual3, "oh no…our code…it's broken");
        assertEquals(p3, actual3bis, "oh no…our code…it's broken");
        assertEquals(pInter, actualInter, "oh no…our code…it's broken");

    }

    @Test
    void pointAtWorksWithPointsBeyondRoute() {
        PointCh pInter = myEdge1.pointAt(myEdge1.length()/2d); // expected intermediate point :
        PointCh actualInter = ourRoute().pointAt(myEdge1.length()/2d); // actual
        PointCh actual1 = ourRoute().pointAt(0); // the point at the beginning of our SingleRoute
        PointCh actual2 = ourRoute().pointAt(myEdge1.length()); //  the point between the first and second edges on our SingleRoute
        PointCh actual3 = ourRoute().pointAt(myEdge1.length() + myEdge2.length()); // the point at the edge of our SingleRoute
        PointCh actual3bis = ourRoute().pointAt(ourRoute().length()); // just another way to compute the point at the end of the route

        assertEquals(p1,actual1, "oh no…our code…it's broken");
        assertEquals(p2,actual2, "oh no…our code…it's broken");
        assertEquals(p3,actual3, "oh no…our code…it's broken");
        assertEquals(p3, actual3bis, "oh no…our code…it's broken");
        assertEquals(pInter, actualInter, "oh no…our code…it's broken");
    }

    @Test
    void elevationAtMM() {
        SingleRoute r = ourRoute();

        double actual1 = r.elevationAt(myEdge1.length()/2d);
        double actual2 = r.elevationAt(myEdge1.length()+(myEdge2.length()/3d));
        //System.out.println(myEdge2.elevationAt(myEdge2.length()/3d));

        assertTrue(Double.isNaN(actual1));
        assertEquals(Math.sin(myEdge2.length()/3d), actual2, Math.pow(10, -5));
    }

    @Test
    void elevationAtWorksWithPointsBeyondRoute() {
        SingleRoute r = ourRoute();


        double actual1 = r.elevationAt(0); // edgeIndex = 0 :
        double actual2 = r.elevationAt(myEdge1.length()-1); //  positive edgeIndex on first edge
        double actual3 = r.elevationAt(myEdge1.length() + (myEdge2.length()/2d));
        double actual4 = r.elevationAt(r.length()+29); // position beyond the route
        double actual5 = r.elevationAt(edgeBeforeRoute.length()/2d); // position before the route


        assertEquals(myEdge1.elevationAt(0), actual1, "oh no…our code…it's broken");
        assertTrue(Double.isNaN(actual2), "oh no…our code…it's broken");
        assertEquals(Math.sin(myEdge2.length()/2d), actual3, Math.pow(10, -5), "oh no…our code…it's broken");
        assertEquals(Math.sin(myEdge2.length()), actual4, "oh no…our code…it's broken");
        assertTrue(Double.isNaN(actual5));
    }

    @Test
    void nodeClosestToMM() {
        // limit points (at the edges' extremity)
        assertEquals(1, ourRoute().nodeClosestTo(0));
        assertEquals(2, ourRoute().nodeClosestTo(myEdge1.length()));
        assertEquals(3, ourRoute().nodeClosestTo(ourRoute().length()));

        // testing intermediate points :
        assertEquals(1, ourRoute().nodeClosestTo(myEdge1.length() - (2*myEdge1.length()/3d))); //  the first node should be the closest one
        assertEquals(3, ourRoute().nodeClosestTo(myEdge1.length() + myEdge2.length()-3));

    }


}