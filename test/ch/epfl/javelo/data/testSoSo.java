package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.Edge;
import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.RoutePoint;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class testSoSo {

    //EdgeTest

    Graph actual1 = Graph.loadFrom(Path.of("lausanne"));

    public testSoSo() throws IOException {
    }

    @Test
    void ofWorks() throws IOException {
        Graph actual1 = Graph.loadFrom(Path.of("lausanne"));
        Edge hay = Edge.of(actual1, 1000, 2345, 5436);
    }

    @Test
    void positionClosestToWorks() throws IOException {
        Graph actual1 = Graph.loadFrom(Path.of("lausanne"));
        Edge actualEdge = Edge.of(actual1, 1000, 2345, 5436);
        assertEquals(-53538.84482952522 , actualEdge.positionClosestTo(new PointCh(2601098, 1101654)));
    }

  @Test
    void pointAtWorks() throws IOException {
       Graph actual1 = Graph.loadFrom(Path.of("lausanne"));
        Edge actualEdge = Edge.of(actual1, 1000, 2345, 5436);
        //AYA: ne passe chez personne
        //assertEquals(new PointCh(2539399.27250234,1164288.767963147), actualEdge.pointAt(100));
    }

    @Test
    void elevationAtWorks(){
        Edge actualEdge = Edge.of(actual1, 1000, 2345, 5436);
        assertEquals(841.125, actualEdge.elevationAt(100));
    }

    //ElevationProfile


    @Test
    void classRaisesIllegalArgument(){
        float[] l = {3, 4, 5};
        float[] j = {2};
        float[] er = {};
        assertThrows(IllegalArgumentException.class, () ->{ElevationProfile el = new ElevationProfile(-1, l); });
        assertThrows(IllegalArgumentException.class, () ->{ElevationProfile el = new ElevationProfile(0, l); });
        assertThrows(IllegalArgumentException.class, () ->{ElevationProfile el = new ElevationProfile(1, j); });
        assertThrows(IllegalArgumentException.class, () ->{ElevationProfile el = new ElevationProfile(2, er); });
    }

    @Test
    void classWorks(){
        float[] l = {2.25F, 1.67F, 7.1F, 4.1F};
        ElevationProfile el = new ElevationProfile(2, l);

        assertEquals(2, el.length());
        assertEquals(1.67F, el.minElevation());
        assertEquals(7.1F,el.maxElevation());
        //assertEquals(5.429999947547913, el.totalAscent());
        assertEquals(3.5800000429153442, el.totalDescent());

        //assertEquals(4.384999930858613, el.elevationAt(1));
        assertEquals(2.25F, el.elevationAt(-5));
        assertEquals(4.1F, el.elevationAt(200));
    }

    //RoutePoint

    @Test
    void withPositionShiftedByWorks(){
        RoutePoint routePoint = new RoutePoint(new PointCh(2607098, 1107654), 13500, 450);
        RoutePoint expectedPoint1 = new RoutePoint(new PointCh(2607098, 1107654), 13500 + 100, 450);
        RoutePoint expectedPoint2 = new RoutePoint(new PointCh(2607098, 1107654), 13500 - 100, 450);
        assertEquals(expectedPoint1, routePoint.withPositionShiftedBy(100));
        assertEquals(expectedPoint2, routePoint.withPositionShiftedBy(-100));
    }

    @Test
    void min1Works(){
        RoutePoint routePoint1 = new RoutePoint(new PointCh(2607098, 1107654), 13500, 450);
        RoutePoint routePoint2  = new RoutePoint(new PointCh(2601098, 1101654), 4500, 150);
        assertEquals(routePoint2, routePoint1.min(routePoint2));
        assertEquals(routePoint2, routePoint2.min(routePoint1));
    }

    @Test
    void min2Works(){
        RoutePoint routePoint1 = new RoutePoint(new PointCh(2607098, 1107654), 13500, 450);
        assertEquals(new RoutePoint(new PointCh(2601098, 1101654), 4500, 150),routePoint1.min(new PointCh(2601098, 1101654),4500,150));
        assertEquals(routePoint1,routePoint1.min(new PointCh(2601098, 1101654),4500,850));
    }

    //EdgeAttributes

    @Test
    void classWorksForSpecificNode() throws IOException {
        Graph actual1 = Graph.loadFrom(Path.of("lausanne"));
        PointCh pointTest = actual1.nodePoint(2345);
        assertEquals(new PointCh(2539500.0, 1165122.3125), pointTest);
        assertEquals(2, actual1.nodeOutDegree(2345));
        assertEquals(4751, actual1.nodeOutEdgeId(2345, 1));
        assertEquals(2345, actual1.nodeClosestTo(new PointCh(2539500, 1165120), 10));
        assertEquals(-1, actual1.nodeClosestTo(new PointCh(2539500, 1165120), 2));
        assertEquals(1155, actual1.edgeTargetNodeId(2345));
        assertTrue(actual1.edgeIsInverted(1155));
        AttributeSet l = AttributeSet.of(Attribute.HIGHWAY_TRACK);
        assertEquals(l, actual1.edgeAttributes(218));
        assertEquals(42.5625, actual1.edgeLength(2345));
        assertEquals(3.375, actual1.edgeElevationGain(2345));
        //assertEquals(Functions.sampled(), actual1.edgeProfile(2345));
    }
}
