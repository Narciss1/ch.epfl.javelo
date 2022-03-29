/*
package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static java.lang.Float.NaN;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class SingleRouteTestOther {
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
    List<PointCh> points = new ArrayList<>();
    SingleRoute singleRoute;

    private void create() {

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
        assertEquals(-0.10185428987243944, singleRoute.elevationAt(250));
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

        PointCh expectedPoint1 = new PointCh(2535663.166522241, 1152663.1665222412);
        PointCh expectedPoint2 = new PointCh(2535749.433549546, 1152749.4335495462);
        PointCh expectedPoint3 = new PointCh(2535781.9604614805, 1152781.9604614805);
        PointCh expectedPoint4 = new PointCh(2535959.4442635584, 1152959.4442635584);
        PointCh expectedPoint5 = new PointCh(2536001.1635636487, 1153001.1635636487);
        PointCh expectedPoint6 = new PointCh(2536122.0, 1153122.0);

        RoutePoint route1, route2, route3, route4, route5, route6;
        route1 = new RoutePoint(expectedPoint1, 135.99999999991996, -491.9108216937342);
        route2 = new RoutePoint(expectedPoint2, 258.00000000005895, -369.9108216935952);
        route3 = new RoutePoint(expectedPoint3, 303.9999999999278, -323.91082169372635);
        route4 = new RoutePoint(expectedPoint4, 554.9999999999316, -72.91082169372255);
        route5 = new RoutePoint(expectedPoint5, 614.0000000000676, -13.910821693586541);
        route6 = new RoutePoint(expectedPoint6, 784.8885271170677, 156.97770542341357);

        //debug si besoin de verifier les valeurs des routes
        assertTrue(equalRoutes(route1, singleRoute.pointClosestTo(singleRoute.pointAt(136))));
        assertTrue(equalRoutes(route2, singleRoute.pointClosestTo(singleRoute.pointAt(258))));
        assertTrue(equalRoutes(route3, singleRoute.pointClosestTo(singleRoute.pointAt(304))));
        assertTrue(equalRoutes(route4, singleRoute.pointClosestTo(singleRoute.pointAt(555))));
        assertTrue(equalRoutes(route5, singleRoute.pointClosestTo(singleRoute.pointAt(614))));
        assertTrue(equalRoutes(route6, singleRoute.pointClosestTo(singleRoute.pointAt(800))));


    }

    private boolean equalRoutes(RoutePoint route1, RoutePoint route2) {
        return route1.point().equals(route2.point()) && route1.position() == route2.position()
                && route1.distanceToReference() == route2.distanceToReference();
    }
}
*/
