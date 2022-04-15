package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Stream;

import static ch.epfl.test.TestRandomizer.newRandom;
import static java.lang.Double.isNaN;
import static java.lang.Float.NaN;
import static java.lang.Float.POSITIVE_INFINITY;
import static org.junit.jupiter.api.Assertions.*;

public class MultiRouteTest {
    @Test
    public MultiRoute multiRouteConstructorForTest(){
        List<Edge> l0 = new ArrayList<Edge>();
        List<Edge> l1 = new ArrayList<Edge>();
        List<Route> listeRoutes = new ArrayList<>();

        PointCh fromPoint0 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint0 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab0 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile0 = Functions.sampled(profileTab0, 5);
        Edge edge0 = new Edge(0, 1, fromPoint0, toPoint0, 5, profile0);

        PointCh fromPoint1 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint1 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab1 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile1 = Functions.sampled(profileTab1, 5);
        Edge edge1 = new Edge(1, 2, fromPoint1, toPoint1, 5, profile1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        float[] profileTab2 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab2, 5);
        Edge edge2 = new Edge(2, 3, fromPoint2, toPoint2, 10, profile2);

        PointCh fromPoint3 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        PointCh toPoint3 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N);
        float[] profileTab3 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile3 = Functions.sampled(profileTab3, 5);
        Edge edge3 = new Edge(3, 4, fromPoint3, toPoint3, 15, profile3);

        l0.add(edge0);
        l0.add(edge1);
        listeRoutes.add(new SingleRoute(l0));
        l1.add(edge2);
        l1.add(edge3);
        listeRoutes.add(new SingleRoute(l1));
        return new MultiRoute(listeRoutes);
    }

    @Test
    public void indexOfSegmentAtTest(){
        MultiRoute m = multiRouteConstructorForTest();
        //Cas sup à la length
        int expected0 = 1;
        int actual0 = m.indexOfSegmentAt(SwissBounds.MIN_E + 25);
        assertEquals(expected0, actual0);
        //Cas position négative
        int expected1 = 0;
        int actual1 = m.indexOfSegmentAt(-25);
        assertEquals(expected1, actual1);
        //Cas position nulle
        int expected2 = expected1;
        int actual2 = m.indexOfSegmentAt(0);
        assertEquals(expected2, actual2);
        //Cas position = length
        int expected3 = expected0;
        int actual3 = m.indexOfSegmentAt(25);
        assertEquals(expected3, actual3);
        //Cas normal
        int expected4 = 1;
        int actual4 = m.indexOfSegmentAt(25);
        assertEquals(expected4, actual4);
    }

    @Test
    public void lengthTest0(){
        MultiRoute m = multiRouteConstructorForTest();
        double expected0 = 35.0;
        double actual0 = m.length();
        assertEquals(expected0, actual0);
    }

    @Test
    public void edgesTest0(){
        List<Edge> l0 = new ArrayList<Edge>();
        List<Edge> l1 = new ArrayList<Edge>();
        List<Edge> allEdges = new ArrayList<>();
        List<Route> listeRoutes = new ArrayList<>();

        PointCh fromPoint0 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint0 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab0 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile0 = Functions.sampled(profileTab0, 5);
        Edge edge0 = new Edge(0, 1, fromPoint0, toPoint0, 5, profile0);

        PointCh fromPoint1 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint1 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab1 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile1 = Functions.sampled(profileTab1, 5);
        Edge edge1 = new Edge(1, 2, fromPoint1, toPoint1, 5, profile1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        float[] profileTab2 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab2, 5);
        Edge edge2 = new Edge(2, 3, fromPoint2, toPoint2, 10, profile2);

        PointCh fromPoint3 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        PointCh toPoint3 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N);
        float[] profileTab3 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile3 = Functions.sampled(profileTab3, 5);
        Edge edge3 = new Edge(3, 4, fromPoint3, toPoint3, 15, profile3);

        l0.add(edge0);
        l0.add(edge1);
        listeRoutes.add(new SingleRoute(l0));
        l1.add(edge2);
        l1.add(edge3);
        listeRoutes.add(new SingleRoute(l1));
        MultiRoute m = new MultiRoute(listeRoutes);

        allEdges.add(edge0);
        allEdges.add(edge1);
        allEdges.add(edge2);
        allEdges.add(edge3);
        List<Edge> expected0 = allEdges;
        List<Edge> actual0 = m.edges();
        assertEquals(expected0, actual0);
    }

    @Test
    public void pointsTest0(){
        MultiRoute m = multiRouteConstructorForTest();
        List<PointCh> allPoints = new ArrayList<>();

        PointCh fromPoint0 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh fromPoint1 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        PointCh fromPoint3 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        PointCh toPoint3 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N);

        allPoints.add(fromPoint0);
        allPoints.add(fromPoint1);
        allPoints.add(fromPoint2);
        allPoints.add(fromPoint3);
        allPoints.add(toPoint3);

        List<PointCh> expected0 = allPoints;
        List<PointCh> actual0 = m.points();
        assertEquals(expected0, actual0);
    }

    @Test
    public void pointAtTest1(){
        MultiRoute m = multiRouteConstructorForTest();
        //Cas normal
        PointCh expected0 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh actual0 = m.pointAt(5);
        assertEquals(expected0, actual0);
        //Cas sup à la length
        PointCh expected1 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N);
        PointCh actual1 = m.pointAt(SwissBounds.MIN_E + 5);
        assertEquals(expected1, actual1);
        //Cas position négative
        PointCh expected2 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh actual2 = m.pointAt(-30);
        assertEquals(expected2, actual2);
        //Cas position nulle
        PointCh expected3 = expected2;
        PointCh actual3 = m.pointAt(0);
        assertEquals(expected3, actual3);
        //Cas position = length
        PointCh expected4 = expected1;
        PointCh actual4 = m.pointAt(35.0);
        assertEquals(expected4, actual4);
        //Cas normal 2.0
        PointCh expected5 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        PointCh actual5 = m.pointAt(20.0);
        assertEquals(expected5, actual5);
    }

    @Test
    public void elevationAtTest0(){
        List<Edge> l0 = new ArrayList<Edge>();
        List<Edge> l1 = new ArrayList<Edge>();
        List<Route> listeRoutes = new ArrayList<>();

        PointCh fromPoint0 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint0 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab0 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile0 = Functions.sampled(profileTab0, 5);
        Edge edge0 = new Edge(0, 1, fromPoint0, toPoint0, 5, profile0);

        PointCh fromPoint1 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint1 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab1 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile1 = Functions.sampled(profileTab1, 5);
        Edge edge1 = new Edge(1, 2, fromPoint1, toPoint1, 5, profile1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        float[] profileTab2 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab2, 5);
        Edge edge2 = new Edge(2, 3, fromPoint2, toPoint2, 10, profile2);

        PointCh fromPoint3 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        PointCh toPoint3 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N);
        float[] profileTab3 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile3 = Functions.sampled(profileTab3, 5);
        Edge edge3 = new Edge(3, 4, fromPoint3, toPoint3, 15, profile3);

        l0.add(edge0);
        l0.add(edge1);
        listeRoutes.add(new SingleRoute(l0));
        l1.add(edge2);
        l1.add(edge3);
        listeRoutes.add(new SingleRoute(l1));
        MultiRoute m = new MultiRoute(listeRoutes);

        //Cas normal
        double expected0 = edge1.elevationAt(3);
        double actual0 = m.elevationAt(8);
        assertEquals(expected0, actual0);
        //Cas sup à la length
        double expected1 = edge3.elevationAt(15);
        double actual1 = m.elevationAt(SwissBounds.MIN_E + 5);
        assertEquals(expected1, actual1);
        //Cas position négative
        double expected2 = edge0.elevationAt(0);
        double actual2 = m.elevationAt(-30);
        assertEquals(expected2, actual2);
        //Cas position nulle
        double expected3 = expected2;
        double actual3 = m.elevationAt(0);
        assertEquals(expected3, actual3);
        //Cas position = length
        double expected4 = expected1;
        double actual4 = m.elevationAt(35.0);
        assertEquals(expected4, actual4);
        //Cas normal 2.0
        double expected5 = edge2.elevationAt(7);
        double actual5 = m.elevationAt(17);
        assertEquals(expected5, actual5);
    }

    @Test
    public void nodeClosestToTest0(){
        MultiRoute m = multiRouteConstructorForTest();
        //Cas normal
        int expected0 = 3;
        int actual0 = m.nodeClosestTo(25);
        assertEquals(expected0, actual0);
        //Cas sup à la length
        int expected1 = 4;
        int actual1 = m.nodeClosestTo(SwissBounds.MIN_E + 5);
        assertEquals(expected1, actual1);
        //Cas position négative
        int expected2 = 0;
        int actual2 = m.nodeClosestTo(-30);
        assertEquals(expected2, actual2);
        //Cas position nulle
        int expected3 = expected2;
        int actual3 = m.nodeClosestTo(0);
        assertEquals(expected3, actual3);
        //Cas position = length
        int expected4 = expected1;
        int actual4 = m.nodeClosestTo(35.0);
        assertEquals(expected4, actual4);
        //Cas normal 2.0
        int expected5 = 2;
        int actual5 = m.nodeClosestTo(12);
        assertEquals(expected5, actual5);
    }

    @Test
    public void pointClosestToTest0(){
        MultiRoute m = multiRouteConstructorForTest();
        //itinéraire horizontal
        //Cas normal
        PointCh point0 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N + 1);
        PointCh expected0 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh actual0 = m.pointClosestTo(point0).point();
        assertEquals(expected0, actual0);
        //Cas point.e() sup à length
        PointCh point1 = new PointCh(SwissBounds.MIN_E + 40, SwissBounds.MIN_N + 40);
        PointCh expected1 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N);
        PointCh actual1 = m.pointClosestTo(point1).point();
        assertEquals(expected1, actual1);
        //Cas point en dessus du premier de l'itinéraire
        PointCh point2 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 40);
        PointCh expected2 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh actual2 = m.pointClosestTo(point2).point();
        assertEquals(expected2, actual2);
        //Cas point en dessus du dernier de l'itinéraire
        PointCh point3 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N + 40);
        PointCh expected3 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N);
        PointCh actual3 = m.pointClosestTo(point3).point();
        //Ne devrait pas passer: GOOD
        //assertEquals(expected3, actual3);
        //Cas normal 2.0
        PointCh point4 = new PointCh(SwissBounds.MIN_E + 22, SwissBounds.MIN_N + 40);
        PointCh expected4 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        PointCh actual4 = m.pointClosestTo(point4).point();
        //Ne devrait pas passer: GOOD
        //assertEquals(expected4, actual4);
        //Cas point sur l'itinéraire
        PointCh point5 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        PointCh expected5 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        PointCh actual5 = m.pointClosestTo(point5).point();
        assertEquals(expected5, actual5);
    }

    @Test
    void testInitializeSingleRoute(){
        List<Edge> l = new ArrayList<Edge>();

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N);
        float[] profileTab = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 1000);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 1000, profile);

        l.add(edge1);

        SingleRoute s1 = new SingleRoute(l);
        List<Edge> l2 = new ArrayList<Edge>();

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 2000, SwissBounds.MIN_N);
        float[] profileTab2 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab2, 1000);
        Edge edge2 = new Edge(1, 2, fromPoint2, toPoint2, 1000, profile2);

        l2.add(edge2);

        SingleRoute s2 = new SingleRoute(l2);
        List<Edge> l3 = new ArrayList<Edge>();

        PointCh fromPoint3 = new PointCh(SwissBounds.MIN_E + 2000, SwissBounds.MIN_N);
        PointCh toPoint3 = new PointCh(SwissBounds.MIN_E + 3000, SwissBounds.MIN_N);
        float[] profileTab3 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile3 = Functions.sampled(profileTab3, 1000);
        Edge edge3 = new Edge(2, 3, fromPoint3, toPoint3, 1000, profile3);

        l3.add(edge3);

        SingleRoute s3 = new SingleRoute(l3);

        List<Route> lRoute1 = new ArrayList<Route>();
        lRoute1.add(s1);
        lRoute1.add(s2);
        lRoute1.add(s3);

        MultiRoute m1 = new MultiRoute(lRoute1);

        assertThrows(IllegalArgumentException.class, () -> {
            new MultiRoute(new ArrayList<Route>());
        });
    }

    @Test
    void testMultiRouteFunctions(){
        List<Edge> allEdges = new ArrayList<>();
        List<PointCh> allPoints = new ArrayList<>();
        List<Edge> l = new ArrayList<Edge>();

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N);
        float[] profileTab = {500f, 505f, 510f, 515f, 520f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 1000);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 1000, profile);

        l.add(edge1);
        allEdges.add(edge1);
        allPoints.add(fromPoint);

        SingleRoute s1 = new SingleRoute(l);
        List<Edge> l2 = new ArrayList<Edge>();

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 2000, SwissBounds.MIN_N);
        float[] profileTab2 = {520f, 525f, 530f, 535f, 540f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab2, 1000);
        Edge edge2 = new Edge(1, 2, fromPoint2, toPoint2, 1000, profile2);

        l2.add(edge2);
        allEdges.add(edge2);
        allPoints.add(fromPoint2);

        SingleRoute s2 = new SingleRoute(l2);
        List<Edge> l3 = new ArrayList<Edge>();

        PointCh fromPoint3 = new PointCh(SwissBounds.MIN_E + 2000, SwissBounds.MIN_N);
        PointCh toPoint3 = new PointCh(SwissBounds.MIN_E + 3000, SwissBounds.MIN_N);
        float[] profileTab3 = {540f, 545f, 550f, 555f, 560f};
        DoubleUnaryOperator profile3 = Functions.sampled(profileTab3, 1000);
        Edge edge3 = new Edge(2, 3, fromPoint3, toPoint3, 1000, profile3);

        l3.add(edge3);
        allEdges.add(edge3);
        allPoints.add(fromPoint3);

        SingleRoute s3 = new SingleRoute(l3);

        List<Route> lRoute1 = new ArrayList<Route>();
        lRoute1.add(s1);
        lRoute1.add(s2);
        lRoute1.add(s3);

        MultiRoute m1 = new MultiRoute(lRoute1);

        List<Edge> l4 = new ArrayList<Edge>();

        PointCh fromPoint4 = new PointCh(SwissBounds.MIN_E + 3000, SwissBounds.MIN_N);
        PointCh toPoint4 = new PointCh(SwissBounds.MIN_E + 4000, SwissBounds.MIN_N);
        float[] profileTab4 = {560f, 565f, 570f, 575f, 580f};
        DoubleUnaryOperator profile4 = Functions.sampled(profileTab4, 1000);
        Edge edge4 = new Edge(3, 4, fromPoint4, toPoint4, 1000, profile4);

        l4.add(edge4);
        allEdges.add(edge4);
        allPoints.add(fromPoint4);

        SingleRoute s4 = new SingleRoute(l4);
        List<Edge> l5 = new ArrayList<Edge>();

        PointCh fromPoint5 = new PointCh(SwissBounds.MIN_E + 4000, SwissBounds.MIN_N);
        PointCh toPoint5 = new PointCh(SwissBounds.MIN_E + 5000, SwissBounds.MIN_N);
        float[] profileTab5 = {580f, 585f, 590f, 595f, 600f};
        DoubleUnaryOperator profile5 = Functions.sampled(profileTab5, 1000);
        Edge edge5 = new Edge(4, 5, fromPoint5, toPoint5, 1000, profile5);

        l5.add(edge5);
        allEdges.add(edge5);
        allPoints.add(fromPoint5);

        SingleRoute s5 = new SingleRoute(l5);
        List<Edge> l6 = new ArrayList<Edge>();

        PointCh fromPoint6 = new PointCh(SwissBounds.MIN_E + 5000, SwissBounds.MIN_N);
        PointCh toPoint6 = new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N);
        float[] profileTab6 = {600f, 605f, 610f, 615f, 620f};
        DoubleUnaryOperator profile6 = Functions.sampled(profileTab6, 1000);
        Edge edge6 = new Edge(5, 6, fromPoint6, toPoint6, 1000, profile6);

        l6.add(edge6);
        allEdges.add(edge6);
        allPoints.add(fromPoint6);
        allPoints.add(toPoint6);

        SingleRoute s6 = new SingleRoute(l6);

        List<Route> lRoute2 = new ArrayList<Route>();
        lRoute2.add(s4);
        lRoute2.add(s5);
        lRoute2.add(s6);

        MultiRoute m2 = new MultiRoute(lRoute2);

        List<Route> lRouteF = new ArrayList<Route>();
        lRouteF.add(m1);
        lRouteF.add(m2);

        MultiRoute m = new MultiRoute(lRouteF);

        assertEquals(6000, m.length());


        //Test pointAt()

        for(int i = 0; i <= 6000; i++){
            assertEquals(new PointCh(SwissBounds.MIN_E + i, SwissBounds.MIN_N), m.pointAt(i));
        }

        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), m.pointAt(-2));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), m.pointAt(-3));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), m.pointAt(-2000));

        assertEquals(new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N), m.pointAt(6003));
        assertEquals(new PointCh(SwissBounds.MIN_E + 5001, SwissBounds.MIN_N), m.pointAt(5001));
        assertEquals(new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N), m.pointAt(7002));


        //Test elevationAt()

        for(int i = 0; i <= 6000; i++){
            assertEquals(500 + (0.02 * i), m.elevationAt(i), 1e-6);
        }

        assertEquals(500, m.elevationAt(-1), 1e-6);
        assertEquals(500, m.elevationAt(0), 1e-6);
        assertEquals(500, m.elevationAt(-1001), 1e-6);

        assertEquals(620, m.elevationAt(6001), 1e-6);
        assertEquals(620, m.elevationAt(7001), 1e-6);
        assertEquals(620, m.elevationAt(8001), 1e-6);

        //Test nodeClosestTo()

        for(int i = 0; i <= 6000; i++){
            if(i <= 500){
                assertEquals(0, m.nodeClosestTo(i));
            }else if(i <= 1500){
                assertEquals(1, m.nodeClosestTo(i));
            }else if(i <= 2500) {
                assertEquals(2, m.nodeClosestTo(i));
            }else if(i <= 3500) {
                assertEquals(3, m.nodeClosestTo(i));
            }else if(i <= 4500) {
                assertEquals(4, m.nodeClosestTo(i));
            }else if(i <= 5500) {
                assertEquals(5, m.nodeClosestTo(i));
            }else{
                assertEquals(6, m.nodeClosestTo(i));
            }
        }

        assertEquals(0, m.nodeClosestTo(-1));
        assertEquals(0, m.nodeClosestTo(-2));
        assertEquals(0, m.nodeClosestTo(-1001));


        assertEquals(6, m.nodeClosestTo(6001));
        assertEquals(6, m.nodeClosestTo(60012));


        //Test edges()
        assertEquals(allEdges, m.edges());

        //Test points()
        assertEquals(allPoints, m.points());

        //Test pointClosestTo()
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 1, SwissBounds.MIN_N), 1, 1), m.pointClosestTo(new PointCh(SwissBounds.MIN_E + 1, SwissBounds.MIN_N + 1)));
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N), 1000, 20), m.pointClosestTo(new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N + 20)));
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N), 6000, Math.sqrt(20000)), m.pointClosestTo(new PointCh(SwissBounds.MIN_E + 6100, SwissBounds.MIN_N + 100)));
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N), 6000, 100), m.pointClosestTo(new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N + 100)));
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 3000, SwissBounds.MIN_N), 3000, 100), m.pointClosestTo(new PointCh(SwissBounds.MIN_E + 3000, SwissBounds.MIN_N + 100)));
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 0, 10000), m.pointClosestTo(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 10000)));


        //Test indexOfSegment()
        //One multi route with two routes inside (real length is 3000)
        for(int i = -1500; i < 4500; i++){
            if (i <= 1000) {
                assertEquals(0, m1.indexOfSegmentAt(i));
            }else if (i <= 2000) {
                assertEquals(1, m1.indexOfSegmentAt(i));
            }else if (i <= 3000) {
                assertEquals(2, m1.indexOfSegmentAt(i));
            }else{
                assertEquals(2, m1.indexOfSegmentAt(i));
            }
        }


        //One multi route with two sub multiroutes (real length is 6000)
        for(int i = -1500; i < 7500; i++){
            if (i <= 1000) {
                assertEquals(0, m.indexOfSegmentAt(i));
            }else if (i <= 2000) {
                assertEquals(1, m.indexOfSegmentAt(i));
            }else if (i <= 3000) {
                assertEquals(2, m.indexOfSegmentAt(i));
            }else if (i <= 4000) {
                assertEquals(3, m.indexOfSegmentAt(i));
            }else if (i <= 5000) {
                assertEquals(4, m.indexOfSegmentAt(i));
            } else if (i <= 6000) {
                assertEquals(5, m.indexOfSegmentAt(i));
            }else{
                assertEquals(5, m.indexOfSegmentAt(i));
            }
        }
    }

    @Test
    public void TestConstructeurthrow() {
        List<Route> newRoute = new ArrayList<>();
        Assertions.assertThrows(IllegalArgumentException.class, () -> new MultiRoute(newRoute));
    }
    @Test
    public void testConstructeur(){
        List<Route> newRoute = new ArrayList<Route>(Collections.singleton(new SingleRoute(new ArrayList<>(Arrays.asList(
                new Edge(3, 4, null, null, 15, null),
                new Edge(4, 4, null, null, 15, null)
        )))));
        Assertions.assertDoesNotThrow(() -> new MultiRoute(newRoute));
    }

    @Test
    public void testPrivateVariable()
    {
        MultiRoute mr = new MultiRoute(new ArrayList<Route>(Collections.singleton(new SingleRoute(new ArrayList<>(Arrays.asList(
                new Edge(3, 4, null, null, 15, null),
                new Edge(4, 4, null, null, 15, null)
        ))))));
        for (Field declaredField : mr.getClass().getDeclaredFields()) {
            Assertions.assertThrows(IllegalAccessException.class, () -> declaredField.set(mr, null));
        }
    }

    @Test
    public void testFinalVariable()
    {
        MultiRoute mr = new MultiRoute(new ArrayList<Route>(Collections.singleton(new SingleRoute(new ArrayList<>(Arrays.asList(
                new Edge(3, 4, null, null, 15, null),
                new Edge(4, 4, null, null, 15, null)
        ))))));
        for (Field declaredField : mr.getClass().getDeclaredFields()) {
            Assertions.assertTrue(Modifier.isFinal(declaredField.getModifiers()));
        }
    }

    @Test
    public void testPrivateVariable2()
    {
        for (Field declaredField : RouteComputer.class.getDeclaredFields()) {
            Assertions.assertTrue(Modifier.isPrivate(declaredField.getModifiers()));
        }
    }

    @Test
    public void testFinalVariable2()
    {
        for (Field declaredField : RouteComputer.class.getDeclaredFields()) {
            Assertions.assertTrue(Modifier.isFinal(declaredField.getModifiers()));
        }
    }

    @Test
    public void lengtheaze()
    {
        MultiRoute mr = new MultiRoute(new ArrayList<Route>(Collections.singleton(new SingleRoute(new ArrayList<>(Arrays.asList(
                new Edge(3, 4, null, null, 15, null),
                new Edge(4, 4, null, null, 15, null)
        ))))));
        Assertions.assertEquals(30, mr.length());
    }
    @Test
    public void lengtheaze2()
    {
        MultiRoute mr = new MultiRoute(new ArrayList<Route>(Collections.singleton(new SingleRoute(new ArrayList<>(Arrays.asList(
                new Edge(3, 4, null, null, 5, null),
                new Edge(4, 4, null, null, 15, null)
        ))))));
        Assertions.assertEquals(20, mr.length());
    }
    @Test
    public void lengtheaze3()
    {
        MultiRoute mr = new MultiRoute(new ArrayList<Route>(Collections.singleton(new SingleRoute(new ArrayList<>(Arrays.asList(
                new Edge(3, 4, null, null, 60, null),
                new Edge(4, 4, null, null, 9, null)
        ))))));
        Assertions.assertEquals(69, mr.length());
    }
    @Test
    public void lengtheaze4()
    {
        MultiRoute mr = new MultiRoute(new ArrayList<Route>(Collections.singleton(new SingleRoute(new ArrayList<>(Arrays.asList(
                new Edge(3, 4, null, null, 0, null),
                new Edge(4, 4, null, null, 0, null)
        ))))));
        Assertions.assertEquals(0, mr.length());
    }
    @Test
    public void lengtheaze5()
    {
        MultiRoute mr = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 400, null),
                                new Edge(4, 4, null, null, 5, null))
                )
                ),
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 5, null),
                                new Edge(4, 4, null, null, 10, null))
                )
                )
        )));
        Assertions.assertEquals(420, mr.length());
    }
    @Test
    public void lengtheaze10()
    {
        MultiRoute mr1 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 400, null),
                                new Edge(4, 4, null, null, 5, null))
                )
                ),
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 5, null),
                                new Edge(4, 4, null, null, 10, null))
                )
                )
        )));
        MultiRoute mr2 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 400, null),
                                new Edge(4, 4, null, null, 5, null))
                )
                )
                ,
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 5, null),
                                new Edge(4, 4, null, null, 10, null))
                )
                ), mr1

        )));
        Assertions.assertEquals(840, mr2.length());
    }
    @Test
    public void edgesSize()
    {
        MultiRoute mr1 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 400, null),
                                new Edge(4, 4, null, null, 5, null))
                )
                ),
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 5, null),
                                new Edge(4, 4, null, null, 10, null))
                )
                )
        )));
        MultiRoute mr2 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 400, null),
                                new Edge(4, 4, null, null, 5, null))
                )
                )
                ,
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 5, null),
                                new Edge(4, 4, null, null, 10, null))
                )
                ), mr1

        )));
        Assertions.assertEquals(8, mr2.edges().size());
    }
    @Test
    public void pointsSize()
    {
        MultiRoute mr1 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 400, null),
                                new Edge(4, 4, null, null, 5, null))
                )
                ),
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 5, null),
                                new Edge(4, 4, null, null, 10, null))
                )
                )
        )));
        MultiRoute mr2 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 400, null),
                                new Edge(4, 4, null, null, 5, null))
                )
                )
                ,
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 5, null),
                                new Edge(4, 4, null, null, 10, null))
                )
                ), mr1

        )));
        Assertions.assertEquals(9, mr2.points().size());
    }

    @Test
    public void pointAtTest0()
    {
        MultiRoute mr1 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, pointCreator(0, 0), pointCreator(6, 0), 6, null),
                                new Edge(4, 4, pointCreator(6, 0), pointCreator(15, 0), 9, null))
                )
                ),
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, pointCreator(15, 0), pointCreator(435, 0), 420, null),
                                new Edge(4, 4, pointCreator(435, 0), pointCreator(436, 0), 1, null))
                )
                )
        )));
        for (int i = 0; i < mr1.length(); i++) {
            Assertions.assertEquals(SwissBounds.MIN_E + i, mr1.pointAt(i).e() );
        }

    }

    @Test
    public void NodeClosestTo()
    {
        MultiRoute mr1 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, pointCreator(0, 0), pointCreator(6, 0), 6, null),
                                new Edge(4, 5, pointCreator(6, 0), pointCreator(15, 0), 9, null))
                )
                ),
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(5, 6, pointCreator(15, 0), pointCreator(435, 0), 420, null),
                                new Edge(6, 7, pointCreator(435, 0), pointCreator(436, 0), 1, null))
                )
                )
        )));
        Assertions.assertEquals(3, mr1.nodeClosestTo(2));
        Assertions.assertEquals(4, mr1.nodeClosestTo(5));
        Assertions.assertEquals(5, mr1.nodeClosestTo(14));
    }

    @Test
    public void pointClosestTo()
    {
        MultiRoute mr1 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, pointCreator(0, 0), pointCreator(10, 0), 10, null),
                                new Edge(4, 5, pointCreator(10, 0), pointCreator(20, 0), 10, null))
                )
                ),
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(5, 6, pointCreator(20, 0), pointCreator(30, 0), 10, null),
                                new Edge(6, 7, pointCreator(30, 0), pointCreator(40, 0), 10, null))
                )
                )
        )));
        for (int i = 0; i < 10; i++) {
            Assertions.assertEquals(i + SwissBounds.MIN_E, mr1.pointClosestTo(pointCreator(i, 1)).point().e());
        }
    }

    @Test
    public void ElevationAt()
    {
        MultiRoute mr1 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, pointCreator(0, 0), pointCreator(6, 0), 6, d -> 5),
                                new Edge(4, 5, pointCreator(6, 0), pointCreator(15, 0), 9, d -> 0))
                )
                ),
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(5, 6, pointCreator(15, 0), pointCreator(435, 0), 420, Functions.sampled(new float[]{1,1,1}, 2)),
                                new Edge(6, 7, pointCreator(435, 0), pointCreator(436, 0), 1, d -> 10))
                )
                )
        )));
        Assertions.assertEquals(5, mr1.elevationAt(1));
        Assertions.assertEquals(5, mr1.elevationAt(2));
        Assertions.assertEquals(5, mr1.elevationAt(3));
        Assertions.assertEquals(5, mr1.elevationAt(5));
        Assertions.assertEquals(0, mr1.elevationAt(6));
        Assertions.assertEquals(0, mr1.elevationAt(7));
        Assertions.assertEquals(0, mr1.elevationAt(8));
        Assertions.assertEquals(0, mr1.elevationAt(9));
        Assertions.assertEquals(1, mr1.elevationAt(16));

        Assertions.assertEquals(10, mr1.elevationAt(436));
    }


    private PointCh pointCreator(int e, int n)
    {
        return new PointCh(SwissBounds.MIN_E + e, SwissBounds.MIN_N + n);
    }

    @Test
    public void indexOf()
    {
        MultiRoute mr1 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 100, null),
                                new Edge(4, 4, null, null, 100, null))
                )
                ),
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 100, null),
                                new Edge(4, 4, null, null, 100, null))
                )
                )
        )));
        MultiRoute mr2 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 100, null),
                                new Edge(4, 4, null, null, 100, null))
                )
                )
                ,
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, null, null, 100, null),
                                new Edge(4, 4, null, null, 100, null))
                )
                ), mr1

        )));
        System.out.println(mr2.indexOfSegmentAt(-1));
        System.out.println(mr2.indexOfSegmentAt(0));
        System.out.println(mr2.indexOfSegmentAt(201));
        System.out.println(mr2.indexOfSegmentAt(401));
        System.out.println(mr2.indexOfSegmentAt(601));
        System.out.println(mr2.indexOfSegmentAt(600001));
    }

    private static final int ORIGIN_N = 1_200_000;
    private static final int ORIGIN_E = 2_600_000;
    private static final double EDGE_LENGTH = 100.25;

    @Test
    void indexOfSegmentAtWorksWithOnlySimpleRoutes() {
        Edge e = new Edge(0, 1, null, null, 1000, Functions.constant(NaN));
        SingleRoute sr0 = new SingleRoute(List.of(e));
        SingleRoute sr1 = new SingleRoute(List.of(e));
        SingleRoute sr2 = new SingleRoute(List.of(e));
        MultiRoute mr = new MultiRoute(List.of(sr0, sr1, sr2));
        int actual1 = mr.indexOfSegmentAt(-500);
        int expected1 = 0;
        assertEquals(expected1, actual1);

        int actual2 = mr.indexOfSegmentAt(100);
        int expected2 = 0;
        assertEquals(expected2, actual2);
        int actual3 = mr.indexOfSegmentAt(1500);
        int expected3 = 1;
        assertEquals(expected3, actual3);

        int actual4 = mr.indexOfSegmentAt(3000);
        int expected4 = 2;
        assertEquals(expected4, actual4);

        int actual5 = mr.indexOfSegmentAt(4500);
        int expected5 = 2;
        assertEquals(expected5, actual5);
    }

    @Test
    void indexOfSegmentAtWorksWithMultiRoutesAndSingleRoutes1() {
        Edge e = new Edge(0, 1, null, null, 1000, Functions.constant(NaN));
        SingleRoute sr0 = new SingleRoute(List.of(e));
        SingleRoute sr1 = new SingleRoute(List.of(e));
        SingleRoute sr2 = new SingleRoute(List.of(e));
        MultiRoute mr1 = new MultiRoute(List.of(sr0, sr1, sr2));
        MultiRoute mr2 = new MultiRoute(List.of(sr0, sr1, sr2));
        MultiRoute m = new MultiRoute(List.of(mr1, mr2));

        int expected1 = 0;
        int actual1 = m.indexOfSegmentAt(-1000);
        assertEquals(expected1, actual1);

        int expected2 = 0;
        int actual2 = m.indexOfSegmentAt(0);
        assertEquals(expected2, actual2);

        int expected3 = 0;
        int actual3 = m.indexOfSegmentAt(750);
        assertEquals(expected3, actual3);

        int expected4 = 2;
        int actual4 = m.indexOfSegmentAt(2100);
        assertEquals(expected4, actual4);

        int expected5 = 5;
        int actual5 = m.indexOfSegmentAt(5500);
        assertEquals(expected5, actual5);

        int expected6 = 5;
        int actual6 = m.indexOfSegmentAt(76098);
        assertEquals(expected6, actual6);
    }

    @Test
    void indexOfSegmentAtWorksWithMultiRoutesAndSingleRoutes2() {
        Edge e300 = new Edge(0, 1, null, null, 300, Functions.constant(NaN));
        Edge e400 = new Edge(1, 2, null, null, 400, Functions.constant(NaN));
        SingleRoute s300 = new SingleRoute(List.of(e300));
        SingleRoute s400 = new SingleRoute(List.of(e400));
        SingleRoute s700 = new SingleRoute(List.of(e300, e400));

        MultiRoute mr1 = new MultiRoute(List.of(s300, s400));
        MultiRoute mr2 = new MultiRoute(List.of(mr1, s700));
        MultiRoute mr3 = new MultiRoute(List.of(s300, mr1, mr2, s300, s700));

        int expected1 = 0;
        int actual1 = mr3.indexOfSegmentAt(-9073.5);
        assertEquals(expected1, actual1);

        int expected2 = 0;
        int actual2 = mr3.indexOfSegmentAt(0);
        assertEquals(expected2, actual2);

        int expected3 = 2;
        int actual3 = mr3.indexOfSegmentAt(1000);
        assertEquals(expected3, actual3);

        int expected4 = 5;
        int actual4 = mr3.indexOfSegmentAt(2000);
        assertEquals(expected4, actual4);

        int expected5 = 6;
        int actual5 = mr3.indexOfSegmentAt(2500);
        assertEquals(expected5, actual5);

        int expected6 = 7;
        int actual6 = mr3.indexOfSegmentAt(3400);
        assertEquals(expected6, actual6);
    }

    @Test
    void lengthWorks() {
        var rng = newRandom();
        double edgeLength = newRandom().nextDouble(100000);
        Edge e = new Edge(0, 1, null, null, edgeLength, Functions.constant(NaN));

        int nbEdges = rng.nextInt(10000);
        List<Route> segments = new ArrayList<>();
        for (int i = 0; i < nbEdges; i++) {
            segments.add(new SingleRoute(List.of(e)));
        }
        MultiRoute mr = new MultiRoute(segments);
        double delta = mr.length() / 1000000000;
        System.out.println("La précision de l'égalité suivante est de l'ordre de " +  delta);
        assertEquals(mr.length(), nbEdges * edgeLength, delta);
    }

    @Test
    void edgesWorks() {
        List<Edge> edgesList = randomListOfEdges(10000);
        Route r1 = new SingleRoute(edgesList);
        Route r2 = new MultiRoute(List.of(r1));
        assertArrayEquals(r2.edges().toArray(new Edge[0]), r1.edges().toArray(new Edge[0]));

        Route r3 = new MultiRoute(List.of(r1, r2));
        List<Edge> l = new ArrayList<>();
        l.addAll(r1.edges());
        l.addAll(r2.edges());
        assertArrayEquals(r3.edges().toArray(new Edge[0]), l.toArray(new Edge[0]));
    }

    List<Edge> randomListOfEdges(int nbEdges) {
        var rng = newRandom();
        List<Edge> l = new ArrayList<>();
        for (int i = 0; i < nbEdges; i++) {
            double edgeLength = rng.nextDouble(1000);
            Edge e = new Edge(i, i + 1, randomPointCh(), randomPointCh(), edgeLength, Functions.constant(NaN));
            l.add(e);
        }
        return l;
    }

    PointCh randomPointCh() {
        var rng = new Random();
        return new PointCh(rng.nextDouble(SwissBounds.MIN_E, SwissBounds.MAX_E), rng.nextDouble(SwissBounds.MIN_N, SwissBounds.MAX_N));
    }

    @Test
    void pointsWorks() {
        PointCh p0 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh p1 = new PointCh(p0.e() + 100, p0.n() + 100);
        PointCh p2 = new PointCh(p1.e() + 100, p1.n() + 100);
        PointCh p3 = new PointCh(p1.e() + 100, p1.n() + 100);

        Edge e0 = new Edge(0, 1, p0, p1 , p1.distanceTo(p1), Functions.constant(NaN));
        Edge e1 = new Edge(1, 2, p1,  p2, p2.distanceTo(p1), Functions.constant(NaN));
        Edge e2 = new Edge(2, 3, p3, p2, p3.distanceTo(p1), Functions.constant(NaN));

        SingleRoute s0 = new SingleRoute(List.of(e0, e1));
        SingleRoute s1 = new SingleRoute(List.of(e2));
        MultiRoute m0 = new MultiRoute(List.of(s0));
        MultiRoute m1 = new MultiRoute(List.of(s1));
        MultiRoute m2 = new MultiRoute(List.of(m0, m1));

        List<PointCh> expected1 = List.of(p0, p1, p2, p3);
        List<PointCh> actual1 = m2.points();
        assertArrayEquals(expected1.toArray(new PointCh[0]), actual1.toArray(new PointCh[0]));

    }

    @Test
    void pointAtWorksWithOnlySingleRoutesInSegments() {
        PointCh origin = new PointCh(ORIGIN_E, ORIGIN_N);
        PointCh p1 = new PointCh(origin.e() + EDGE_LENGTH, origin.n());
        PointCh p2 = new PointCh(p1.e(), p1.n() + EDGE_LENGTH);
        Edge e0 = new Edge(0, 1, origin, p1, p1.distanceTo(origin), Functions.constant(NaN));
        Edge e1 = new Edge(1, 2, p1, p2 , p2.distanceTo(p1), Functions.constant(NaN));
        SingleRoute s0 = new SingleRoute(List.of(e0, e1));
        MultiRoute m0 = new MultiRoute(List.of(s0));

        PointCh expected1 = origin;
        PointCh actual1 = m0.pointAt(0);
        assertEquals(expected1, actual1);

        PointCh expected2 = origin;
        PointCh actual2 = m0.pointAt( - 100);
        assertEquals(expected2, actual2);

        PointCh expected3 = s0.pointAt(EDGE_LENGTH + 10 );
        PointCh actual3 = m0.pointAt(EDGE_LENGTH + 10);
        assertEquals(expected3, actual3);

        PointCh expected4 = p2;
        PointCh actual4 = m0.pointAt(Double.POSITIVE_INFINITY);
        assertEquals(expected4, actual4);
    }

    @Test
    void pointAtWorksWithMultiRouteAndSingleRoutesInSegments() {
        PointCh origin = new PointCh(ORIGIN_E, ORIGIN_N);
        PointCh p1 = new PointCh(origin.e() + EDGE_LENGTH, origin.n());
        PointCh p2 = new PointCh(p1.e(), p1.n() + EDGE_LENGTH);
        PointCh p3 = new PointCh( p2.e() + EDGE_LENGTH, p2.n() + EDGE_LENGTH);
        PointCh p4 = new PointCh(p3.e(), p3.n() + EDGE_LENGTH + EDGE_LENGTH);
        PointCh p5 = new PointCh(p4.e() + EDGE_LENGTH + EDGE_LENGTH, p2.n());

        Edge e0 = new Edge(0, 1, origin, p1, p1.distanceTo(origin), Functions.constant(NaN));
        Edge e1 = new Edge(1, 2, p1, p2 , p2.distanceTo(p1), Functions.constant(NaN));
        Edge e2 = new Edge(2, 3, p2, p3, p3.distanceTo(p2), Functions.constant(NaN));
        Edge e3 = new Edge(3, 4, p3, p4, p4.distanceTo(p3), Functions.constant(NaN));
        Edge e4 = new Edge( 4, 5, p4, p5, p5.distanceTo(p4), Functions.constant(NaN));

        SingleRoute s0 = new SingleRoute(List.of(e0, e1));
        SingleRoute s1 = new SingleRoute(List.of(e2, e3));
        SingleRoute s2 = new SingleRoute(List.of(e4));

        MultiRoute m0 = new MultiRoute(List.of(s0));
        MultiRoute m1 = new MultiRoute(List.of(m0, s1, s2));

        PointCh expected1 = origin;
        PointCh actual1 =  m1.pointAt(-5654);
        assertEquals(expected1, actual1);

        PointCh expected2 = origin;
        PointCh actual2 = m1.pointAt(0);
        assertEquals(expected2, actual2);

        PointCh expected3 = p4;
        PointCh actual3 = m1.pointAt(401 + e2.length());
        assertEquals(expected3, actual3);

        PointCh expected4 = e4.pointAt(550 - 401 - e2.length());
        PointCh actual4 = m1.pointAt(550);
        assertEquals(expected4, actual4);

        PointCh expected5 = p5;
        PointCh actual5 = m1.pointAt(904.243);
        assertEquals(expected5, actual5);

        PointCh expected6 = p5;
        PointCh actual6 = m1.pointAt(POSITIVE_INFINITY);
        assertEquals(expected6, actual6);
    }

    @Test
    void elevationAtWorksWithMultiRoutesAndSingleRoutesInSegments() {
        PointCh origin = new PointCh(ORIGIN_E, ORIGIN_N);
        PointCh p1 = new PointCh(origin.e() + EDGE_LENGTH, origin.n());
        PointCh p2 = new PointCh(p1.e(), p1.n() + EDGE_LENGTH);
        PointCh p3 = new PointCh( p2.e() + EDGE_LENGTH, p2.n() + EDGE_LENGTH);
        PointCh p4 = new PointCh(p3.e(), p3.n() + EDGE_LENGTH + EDGE_LENGTH);
        PointCh p5 = new PointCh(p4.e() + EDGE_LENGTH + EDGE_LENGTH, p2.n());

        List<float[]> randomSamplesList = listOfRandomFloatArrays(4);
        float[] samples0 = randomSamplesList.get(0);
        Edge e0 = new Edge(0, 1, origin, p1, p1.distanceTo(origin),
                Functions.sampled((samples0) , p1.distanceTo(origin)));
        Edge e1 = new Edge(1, 2, p1, p2 , p2.distanceTo(p1),
                Functions.constant(NaN));
        float[] samples2 = randomSamplesList.get(1);
        Edge e2 = new Edge(2, 3, p2, p3, p3.distanceTo(p2),
                Functions.sampled(samples2, p3.distanceTo(p2)));
        float[] samples3 = randomSamplesList.get(2);
        Edge e3 = new Edge(3, 4, p3, p4, p4.distanceTo(p3),
                Functions.sampled(samples3, p4.distanceTo(p3)));
        float[] samples4 = randomSamplesList.get(3);
        Edge e4 = new Edge( 4, 5, p4, p5, p5.distanceTo(p4),
                Functions.sampled(samples4, p5.distanceTo(p4)));

        SingleRoute s0 = new SingleRoute(List.of(e0, e1));
        SingleRoute s1 = new SingleRoute(List.of(e2, e3));
        SingleRoute s2 = new SingleRoute(List.of(e4));

        MultiRoute m0 = new MultiRoute(List.of(s0));
        MultiRoute m1 = new MultiRoute(List.of(m0, s1, s2));


        double actual1 = m1.elevationAt(150);
        assertTrue(isNaN(actual1));

        double expected2 = e4.elevationAt(0);
        double actual2 = m1.elevationAt(401 + e2.length());
        //assertEquals(expected2, actual2);

        double expected3 = e0.elevationAt(50);
        double actual3 = m1.elevationAt(50);
        assertEquals(expected3, actual3);

        assertEquals(m1.elevationAt(0), m1.elevationAt(-9879));
        assertEquals(m1.elevationAt(m1.length()), m1.elevationAt(8769876));

    }

    private List<float[]> listOfRandomFloatArrays(int numberOfArrays) {
        var rng = newRandom();
        List<float[]> l= new ArrayList<>();
        for (int i = 0; i < numberOfArrays; i++) {
            int arrayLength = rng.nextInt(100);
            float[] arr = new float[arrayLength];
            for (int j = 0; j < arr.length; j++) {
                arr[j] = rng.nextFloat(200);
            }
            l.add(arr);
        }
        return l;
    }

    @Test
    void nodeClosestToWorks() {
        PointCh origin = new PointCh(ORIGIN_E, ORIGIN_N);
        PointCh p1 = new PointCh(origin.e() + EDGE_LENGTH, origin.n());
        PointCh p2 = new PointCh(p1.e(), p1.n() + EDGE_LENGTH);
        PointCh p3 = new PointCh( p2.e() + EDGE_LENGTH, p2.n() + EDGE_LENGTH);
        PointCh p4 = new PointCh(p3.e(), p3.n() + EDGE_LENGTH + EDGE_LENGTH);
        PointCh p5 = new PointCh(p4.e() + EDGE_LENGTH + EDGE_LENGTH, p2.n());

        Edge e0 = new Edge(0, 1, origin, p1, p1.distanceTo(origin), Functions.constant(NaN));
        Edge e1 = new Edge(1, 2, p1, p2, p2.distanceTo(p1), Functions.constant(NaN));
        Edge e2 = new Edge(2, 3, p2, p3, p3.distanceTo(p2), Functions.constant(NaN));
        Edge e3 = new Edge(3, 4, p3, p4, p4.distanceTo(p3), Functions.constant(NaN));
        Edge e4 = new Edge( 4, 5, p4, p5, p5.distanceTo(p4),Functions.constant(NaN));

        SingleRoute s0 = new SingleRoute(List.of(e0, e1));
        SingleRoute s1 = new SingleRoute(List.of(e2, e3));
        SingleRoute s2 = new SingleRoute(List.of(e4));

        MultiRoute m0 = new MultiRoute(List.of(s0));
        MultiRoute m1 = new MultiRoute(List.of(m0, s1, s2));

        int expected1 = 0;
        int actual1 = m1.nodeClosestTo(-8679);
        assertEquals(expected1, actual1);

        int expected2 = 0;
        int actual2 = m1.nodeClosestTo(0);
        assertEquals(expected2, actual2);

        int expected3  = 3;
        int actual3 = m1.nodeClosestTo(p1.distanceTo(origin)
                + p2.distanceTo(p1)
                + p3.distanceTo(p2)
        );
        assertEquals(expected3, actual3);

        int expected4 = 5;
        int actual4 = m1.nodeClosestTo(p1.distanceTo(origin)
                + p2.distanceTo(p1)
                + p3.distanceTo(p2)
                + p4.distanceTo(p3)
                + p5.distanceTo(p4)
        );
        assertEquals(expected4, actual4);

        int expected5 = 5;
        int actual5 = m1.nodeClosestTo(45678);
        assertEquals(expected5, actual5);
    }

    public static final List<PointCh> ALL_POINTS = allPoints();
    public static final List<Edge> ALL_EDGES = allEdges();
    public static final MultiRoute MULTI_ROUTE = multiRoute();

    public static List<PointCh> allPoints() {
        PointCh point0 = new PointCh(2_550_000, 1_152_300);
        PointCh point1 = new PointCh(2_550_500, 1_152_300);
        PointCh point2 = new PointCh(2_551_000, 1_152_300);
        PointCh point3 = new PointCh(2_551_500, 1_152_300);
        PointCh point4 = new PointCh(2_552_000, 1_152_300);
        PointCh point5 = new PointCh(2_552_500, 1_152_300);
        PointCh point6 = new PointCh(2_553_000, 1_152_300);
        PointCh point7 = new PointCh(2_553_500, 1_152_300);
        PointCh point8 = new PointCh(2_554_000, 1_152_300);
        PointCh point9 = new PointCh(2_554_500, 1_152_300);
        PointCh point10 = new PointCh(2_555_000, 1_152_300);
        PointCh point11 = new PointCh(2_555_500, 1_152_300);
        PointCh point12 = new PointCh(2_556_000, 1_152_300);

        List<PointCh> allPoints = new ArrayList<>(List.of(point0, point1, point2, point3, point4, point5, point6,
                point7, point8, point9, point10, point11, point12));

        return allPoints;
    }

    public static List<Edge> allEdges() {

        Edge edge0 = new Edge(0, 1, ALL_POINTS.get(0), ALL_POINTS.get(1), 500, DoubleUnaryOperator.identity());
        Edge edge1 = new Edge(1, 2, ALL_POINTS.get(1), ALL_POINTS.get(2), 500, DoubleUnaryOperator.identity());
        Edge edge2 = new Edge(2, 3, ALL_POINTS.get(2), ALL_POINTS.get(3), 500, DoubleUnaryOperator.identity());
        Edge edge3 = new Edge(3, 4, ALL_POINTS.get(3), ALL_POINTS.get(4), 500, DoubleUnaryOperator.identity());
        Edge edge4 = new Edge(4, 5, ALL_POINTS.get(4), ALL_POINTS.get(5), 500, DoubleUnaryOperator.identity());
        Edge edge5 = new Edge(5, 6, ALL_POINTS.get(5), ALL_POINTS.get(6), 500, DoubleUnaryOperator.identity());
        Edge edge6 = new Edge(6, 7, ALL_POINTS.get(6), ALL_POINTS.get(7), 500, DoubleUnaryOperator.identity());
        Edge edge7 = new Edge(7, 8, ALL_POINTS.get(7), ALL_POINTS.get(8), 500, DoubleUnaryOperator.identity());
        Edge edge8 = new Edge(8, 9, ALL_POINTS.get(8), ALL_POINTS.get(9), 500, DoubleUnaryOperator.identity());
        Edge edge9 = new Edge(9, 10, ALL_POINTS.get(9), ALL_POINTS.get(10), 500, DoubleUnaryOperator.identity());
        Edge edge10 = new Edge(10, 11, ALL_POINTS.get(10), ALL_POINTS.get(11), 500, DoubleUnaryOperator.identity());
        Edge edge11 = new Edge(11, 12, ALL_POINTS.get(11), ALL_POINTS.get(12), 500, DoubleUnaryOperator.identity());

        List<Edge> edges0 = new ArrayList<>(List.of(edge0, edge1));
        List<Edge> edges1 = new ArrayList<>(List.of(edge2, edge3));
        List<Edge> edges2 = new ArrayList<>(List.of(edge4, edge5));
        List<Edge> edges3 = new ArrayList<>(List.of(edge6, edge7));
        List<Edge> edges4 = new ArrayList<>(List.of(edge8, edge9));
        List<Edge> edges5 = new ArrayList<>(List.of(edge10, edge11));

        List<Edge> allEdges = new ArrayList<>();
        Stream.of(edges0, edges1, edges2, edges3, edges4, edges5).forEach(allEdges::addAll);

        return allEdges;
    }

    public static MultiRoute multiRoute() {

        Route singleRoute0 = new SingleRoute(ALL_EDGES.subList(0,2));
        Route singleRoute1 = new SingleRoute(ALL_EDGES.subList(2,4));
        Route singleRoute2 = new SingleRoute(ALL_EDGES.subList(4,6));
        Route singleRoute3 = new SingleRoute(ALL_EDGES.subList(6,8));
        Route singleRoute4 = new SingleRoute(ALL_EDGES.subList(8,10));
        Route singleRoute5 = new SingleRoute(ALL_EDGES.subList(10,12));

        List<Route> segment0 = new ArrayList<>(List.of(singleRoute0, singleRoute1, singleRoute2));
        List<Route> segment1 = new ArrayList<>(List.of(singleRoute3, singleRoute4, singleRoute5));

        MultiRoute multiRoute0 = new MultiRoute(segment0);
        MultiRoute multiRoute1 = new MultiRoute(segment1);

        List<Route> segments = List.of(multiRoute0, multiRoute1);

        //Stream.of(segment0, segment1).forEach(segments::addAll);

        return new MultiRoute(segments);
    }

    @Test
    void indexOfSegmentAtWorksOnKnownValues() {
        assertEquals(0, MULTI_ROUTE.indexOfSegmentAt(-400));
        assertEquals(0, MULTI_ROUTE.indexOfSegmentAt(100));
        assertEquals(3, MULTI_ROUTE.indexOfSegmentAt(4000));
        assertEquals(4, MULTI_ROUTE.indexOfSegmentAt(4100));
        assertEquals(5, MULTI_ROUTE.indexOfSegmentAt(5500));
        assertEquals(5, MULTI_ROUTE.indexOfSegmentAt(6000));
        assertEquals(5, MULTI_ROUTE.indexOfSegmentAt(12000));
    }

    @Test
    void edgesWorksForAllEdges() {
        assertEquals(ALL_EDGES, MULTI_ROUTE.edges());
    }

    @Test
    void pointsWorksForAllPoints() {
        assertEquals(ALL_POINTS, MULTI_ROUTE.points());
    }

    @Test
    void pointAtWorksOnKnownValues() {
        PointCh pointOnRoute5 = new PointCh(2_555_500, 1_152_300);
        PointCh pointOnRoute0 = new PointCh(2_550_000, 1_152_300);
        PointCh pointOnRoute5end = new PointCh(2_556_000, 1_152_300);
        //  assertEquals(pointOnRoute5, MULTI_ROUTE.pointAt(5500));
        assertEquals(pointOnRoute5end, MULTI_ROUTE.pointAt(6000));
        assertEquals(pointOnRoute5end, MULTI_ROUTE.pointAt(7000));
        assertEquals(pointOnRoute0, MULTI_ROUTE.pointAt(0));
        assertEquals(pointOnRoute0, MULTI_ROUTE.pointAt(-2));
    }

    @Test
    void elevationAtWorksOnKnownValues() {
        assertEquals(0, MULTI_ROUTE.elevationAt(5500));
        assertEquals(500, MULTI_ROUTE.elevationAt(6000));
        assertEquals(0, MULTI_ROUTE.elevationAt(0));
    }

    @Test
    void pointClosestTo0() {
        PointCh point6 = new PointCh(2_553_000 + 10, 1_152_300);
        PointCh point6Reference = new PointCh(2_553_000 + 10, 1_152_300 + 50);
        RoutePoint routePoint6 = new RoutePoint(point6 , 3000 + 10, 50);

        PointCh point11 = new PointCh(2_555_500, 1_152_300);
        PointCh point11Reference = new PointCh(2_555_500, 1_152_300 + 50);
        RoutePoint routePoint11 = new RoutePoint(point11, 5500, 50);

        assertEquals(routePoint6, MULTI_ROUTE.pointClosestTo(point6Reference));
        assertEquals(routePoint11, MULTI_ROUTE.pointClosestTo(point11Reference));
    }

    @Test
    void nodeClosestToWorksOnKnownValues2() {
        assertEquals(6,MULTI_ROUTE.nodeClosestTo(3000));
        assertEquals(6,MULTI_ROUTE.nodeClosestTo(2990));
        assertEquals(11,MULTI_ROUTE.nodeClosestTo(5575));
        assertEquals(11,MULTI_ROUTE.nodeClosestTo(5500));
        assertEquals(11,MULTI_ROUTE.nodeClosestTo(5750));
        assertEquals(12,MULTI_ROUTE.nodeClosestTo(5751));
        assertEquals(12,MULTI_ROUTE.nodeClosestTo(7700));
        assertEquals(0,MULTI_ROUTE.nodeClosestTo(-1));
    }

    PointCh point1 = new PointCh(2535123, 1152123);
    PointCh point2 = new PointCh(2535234, 1152234);
    PointCh point3 = new PointCh(2535345, 1152345);
    PointCh point4 = new PointCh(2535460, 1152263);
    PointCh point5 = new PointCh(2535617, 1152381);
    PointCh point6 = new PointCh(2535843, 1152401);
    PointCh point7 = new PointCh(2535747.34, 1152285.4);
    PointCh point8 = new PointCh(2535656, 1152175);

    DoubleUnaryOperator profile1 = Functions.sampled(new float[]
            {50, 30, 40, 60}, point1.distanceTo(point2));
    DoubleUnaryOperator profile2 = Functions.sampled(new float[]
            {60, 80, 100, 80}, point2.distanceTo(point3));
    DoubleUnaryOperator profile3 = Functions.sampled(new float[]
            {80, 50, 10, 30}, point3.distanceTo(point4));
    DoubleUnaryOperator profile4 = Functions.sampled(new float[]
            {30, 50, 80, 60}, point4.distanceTo(point5));
    DoubleUnaryOperator profile5 = Functions.sampled(new float[]
            {60, 70, 90, 100}, point5.distanceTo(point6));
    DoubleUnaryOperator profile6 = Functions.sampled(new float[]
            {100, 90, 80, 70}, point6.distanceTo(point7));
    DoubleUnaryOperator profile7 = Functions.sampled(new float[]
            {70, 60, 80, 70}, point7.distanceTo(point8));
    DoubleUnaryOperator profile3Reverse = Functions.sampled(new float[]
            {30, 10, 50, 80}, point4.distanceTo(point3));
    DoubleUnaryOperator profile5Reverse = Functions.sampled(new float[]
            {100, 90, 70, 60}, point6.distanceTo(point5));
    DoubleUnaryOperator profile7Reverse = Functions.sampled(new float[]
            {70, 80, 60, 70}, point8.distanceTo(point7));


    Edge edge1 = new Edge(0, 1, point1, point2, point1.distanceTo(point2), profile1);
    Edge edge2 = new Edge(1, 2, point2, point3, point2.distanceTo(point3), profile2);
    Edge edge3 = new Edge(2, 3, point3, point4, point3.distanceTo(point4), profile3);
    Edge edge4 = new Edge(3, 4, point4, point5, point4.distanceTo(point5), profile4);
    Edge edge5 = new Edge(4, 5, point5, point6, point5.distanceTo(point6), profile5);
    Edge edge6 = new Edge(5, 6, point6, point7, point6.distanceTo(point7), profile6);
    Edge edge7 = new Edge(6, 7, point7, point8, point7.distanceTo(point8), profile7);
    Edge edge3Reverse = new Edge(3, 2, point4, point3, point4.distanceTo(point3), profile3Reverse);
    Edge edge5Reverse = new Edge(5, 4, point6, point5, point6.distanceTo(point5), profile5Reverse);
    Edge edge7Reverse = new Edge(7, 6, point8, point7, point8.distanceTo(point7), profile7Reverse);

    List<Edge> edges1;
    List<PointCh> points1;
    SingleRoute singleRoute1;

    List<Edge> edges2;
    List<PointCh> points2;
    SingleRoute singleRoute2;

    List<Edge> edges3;
    List<PointCh> points3;
    SingleRoute singleRoute3;

    List<Edge> edges4;
    List<PointCh> points4;
    SingleRoute singleRoute4;

    MultiRoute multiRoute1;
    MultiRoute multiRoute2;
    MultiRoute multiRoute3;
    MultiRoute multiRoute4;
    MultiRoute multiRoute5;

    void create() {
        this.edges11 = List.of(edge11, edge21);
        this.edges21 = List.of(edge31, edge3Reverse, edge31);
        this.edges31 = List.of(edge41, edge5, edge5Reverse, edge5);
        this.edges41 = List.of(edge6, edge7, edge7Reverse, edge7);
        this.points1 = List.of(point11, point21, point31);
        this.points2 = List.of(point31, point41, point31, point41);
        this.points3 = List.of(point41, point51, point61, point51, point61);
        this.points4 = List.of(point61, point7, point8, point7, point8);

        singleRoute1 = new SingleRoute(edges11);
        singleRoute2 = new SingleRoute(edges21);
        singleRoute3 = new SingleRoute(edges31);
        singleRoute4 = new SingleRoute(edges41);

        List<Route> routes1 = List.of(singleRoute1, singleRoute2, singleRoute3, singleRoute4);
        multiRoute1 = new MultiRoute(List.copyOf(routes1));

        List<Route> routeIntermediaire1 = List.of(singleRoute1, singleRoute2);
        multiRoute2 = new MultiRoute((List.copyOf(routeIntermediaire1)));

        List<Route> routes2 = List.of(singleRoute3, singleRoute4);
        multiRoute3 = new MultiRoute(List.copyOf(routes2));

        List<Route> routes3 = List.of(multiRoute2, singleRoute3, singleRoute4);
        multiRoute4 = new MultiRoute(List.copyOf(routes3));

        List<Route> routes4 = List.of(multiRoute2, multiRoute3);
        multiRoute5 = new MultiRoute(List.copyOf(routes4));

    }

    @Test
    void MultiRouteThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new MultiRoute(List.of());
        });
    }

    @Test
    void IndexOfSegmentAtWorksOnSingleRoutesOnly() {
        create();

        //multiRoute 1
        assertEquals(0, multiRoute1.indexOfSegmentAt(100));
        assertEquals(1, multiRoute1.indexOfSegmentAt(500));
        assertEquals(1, multiRoute1.indexOfSegmentAt(600));
        assertEquals(2, multiRoute1.indexOfSegmentAt(1000));
        assertEquals(3, multiRoute1.indexOfSegmentAt(2100));

        //multiRoute 2
        assertEquals(0, multiRoute2.indexOfSegmentAt(100));
        assertEquals(0, multiRoute2.indexOfSegmentAt(200));
        assertEquals(1, multiRoute2.indexOfSegmentAt(650));
        assertEquals(1, multiRoute2.indexOfSegmentAt(700));

        //multiRoute 3
        assertEquals(0, multiRoute3.indexOfSegmentAt(100));
        assertEquals(0, multiRoute3.indexOfSegmentAt(500));
        assertEquals(0, multiRoute3.indexOfSegmentAt(650));
        assertEquals(0, multiRoute3.indexOfSegmentAt(800));
        assertEquals(1, multiRoute3.indexOfSegmentAt(1000));
        assertEquals(1, multiRoute3.indexOfSegmentAt(1200));
        assertEquals(1, multiRoute3.indexOfSegmentAt(1400));
    }

    @Test
    void IndexOfSegmentAtWorksOnMixedRoutes() {
        create();

        //multiRoute4
        assertEquals(0, multiRoute4.indexOfSegmentAt(100));
        assertEquals(0, multiRoute4.indexOfSegmentAt(200));
        assertEquals(1, multiRoute4.indexOfSegmentAt(650));
        assertEquals(1, multiRoute4.indexOfSegmentAt(700));
        assertEquals(1, multiRoute4.indexOfSegmentAt(725));
        assertEquals(2, multiRoute4.indexOfSegmentAt(1000));
        assertEquals(2, multiRoute4.indexOfSegmentAt(1500));
        assertEquals(3, multiRoute4.indexOfSegmentAt(1750));
        assertEquals(3, multiRoute4.indexOfSegmentAt(2100));

    }

    @Test
    void IndexOfSegmentAtWorksOnMultiRoutesOfMultiRoutes() {
        create();

        //multiRoute5
        assertEquals(0, multiRoute5.indexOfSegmentAt(100));
        assertEquals(0, multiRoute5.indexOfSegmentAt(200));
        assertEquals(1, multiRoute5.indexOfSegmentAt(650));
        assertEquals(1, multiRoute5.indexOfSegmentAt(700));
        assertEquals(2, multiRoute5.indexOfSegmentAt(1000));
        assertEquals(2, multiRoute5.indexOfSegmentAt(1500));
        assertEquals(3, multiRoute5.indexOfSegmentAt(1750));
        assertEquals(3, multiRoute5.indexOfSegmentAt(2100));
    }

    @Test
    void IndexOfSegmentAtWorksOnEdgeCases() {
        create();

        //clamp works on negative cases
        assertEquals(0, multiRoute1.indexOfSegmentAt(-1));
        assertEquals(0, multiRoute2.indexOfSegmentAt(-1));
        assertEquals(0, multiRoute3.indexOfSegmentAt(-1));
        assertEquals(0, multiRoute4.indexOfSegmentAt(-1));
        assertEquals(0, multiRoute5.indexOfSegmentAt(-1));

        //clamp works on position > length of the road
        assertEquals(3, multiRoute1.indexOfSegmentAt(multiRoute1.length() + 1));
        assertEquals(1, multiRoute2.indexOfSegmentAt(multiRoute2.length() + 1));
        assertEquals(1, multiRoute3.indexOfSegmentAt(multiRoute3.length() + 1));
        assertEquals(3, multiRoute4.indexOfSegmentAt(multiRoute4.length() + 1));
        assertEquals(3, multiRoute5.indexOfSegmentAt(multiRoute5.length() + 1));

        //works for position = 0
        assertEquals(0, multiRoute1.indexOfSegmentAt(0));
        assertEquals(0, multiRoute2.indexOfSegmentAt(0));
        assertEquals(0, multiRoute3.indexOfSegmentAt(0));
        assertEquals(0, multiRoute4.indexOfSegmentAt(0));
        assertEquals(0, multiRoute5.indexOfSegmentAt(0));

        //works for position = length
        assertEquals(3, multiRoute1.indexOfSegmentAt(multiRoute1.length()));
        assertEquals(1, multiRoute2.indexOfSegmentAt(multiRoute2.length()));
        assertEquals(1, multiRoute3.indexOfSegmentAt(multiRoute3.length()));
        assertEquals(3, multiRoute4.indexOfSegmentAt(multiRoute4.length()));
        assertEquals(3, multiRoute5.indexOfSegmentAt(multiRoute4.length()));

    }

    @Test
    void LengthWorks() {
        create();

        double expectedLength1And4And5 = singleRoute1.length() + singleRoute2.length() + singleRoute3.length() + singleRoute4.length();
        double expectedLength2 = singleRoute1.length() + singleRoute2.length();
        double expectedLength3 = singleRoute3.length() + singleRoute4.length();

        assertEquals(expectedLength1And4And5, multiRoute1.length());
        assertEquals(expectedLength2, multiRoute2.length());
        assertEquals(expectedLength3, multiRoute3.length());
        assertEquals(expectedLength1And4And5, multiRoute4.length());
        assertEquals(expectedLength1And4And5, multiRoute5.length());
    }

    @Test
    void EdgesWorks() {
        create();

        List<Edge> expected1And4And5 = new ArrayList<>();
        expected1And4And5.addAll(edges11);
        expected1And4And5.addAll(edges21);
        expected1And4And5.addAll(edges31);
        expected1And4And5.addAll(edges41);

        List<Edge> expected2 = new ArrayList<>();
        expected2.addAll(edges11);
        expected2.addAll(edges21);

        List<Edge> expected3 = new ArrayList<>();
        expected3.addAll(edges31);
        expected3.addAll(edges41);

        assertEquals(expected1And4And5, multiRoute1.edges());
        assertEquals(expected2, multiRoute2.edges());
        assertEquals(expected3, multiRoute3.edges());
        assertEquals(expected1And4And5, multiRoute4.edges());
        assertEquals(expected1And4And5, multiRoute5.edges());
    }

    @Test
    void PointsWorks() {
        create();

        List<PointCh> expectedPoints1And4And5 = List.of(point11, point21, point31, point41, point31, point41, point51, point61, point51, point61, point7, point8, point7, point8);
        List<PointCh> expectedPoints2 = List.of(point11, point21, point31, point41, point31, point41);
        List<PointCh> expectedPoints3 = List.of(point41, point51, point61, point51, point61, point7, point8, point7, point8);

        assertEquals(expectedPoints1And4And5, multiRoute1.points());
        assertEquals(expectedPoints2, multiRoute2.points());
        assertEquals(expectedPoints3, multiRoute3.points());
        assertEquals(expectedPoints1And4And5, multiRoute4.points());
        assertEquals(expectedPoints1And4And5, multiRoute5.points());
    }

    @Test
    void PointAtWorksOnKnownValues() {
        create();

        //multiRoute1
        assertEquals(new PointCh(2535193.7106781187, 1152193.7106781187), multiRoute1.pointAt(100));
        assertEquals(new PointCh(2535423.5203409707, 1152289.0115829601), multiRoute1.pointAt(500));
        assertEquals(new PointCh(2535347.900817107, 1152342.9315912803), multiRoute1.pointAt(600));
        assertEquals(new PointCh(2535682.665075113, 1152386.811068594), multiRoute1.pointAt(1000));
        assertEquals(new PointCh(2535716.326987979, 1152247.915474851), multiRoute1.pointAt(2100));

        //multiRoute2
        assertEquals(new PointCh(2535193.7106781187, 1152193.7106781187), multiRoute2.pointAt(100));
        assertEquals(new PointCh(2535264.4213562375, 1152264.4213562373), multiRoute2.pointAt(200));
        assertEquals(new PointCh(2535388.611396146, 1152313.9031784004), multiRoute2.pointAt(650));
        assertEquals(new PointCh(2535429.3219751846, 1152284.8747655204), multiRoute2.pointAt(700));

        //multiRoute3
        assertEquals(new PointCh(2535539.9388587554, 1152323.0814352431), multiRoute3.pointAt(100));
        assertEquals(new PointCh(2535766.5819807285, 1152394.2373434273), multiRoute3.pointAt(500));
        assertEquals(new PointCh(2535617.1659131846, 1152381.0146825828), multiRoute3.pointAt(650));
        assertEquals(new PointCh(2535766.250154359, 1152394.2079782619), multiRoute3.pointAt(800));
        assertEquals(new PointCh(2535764.6152768503, 1152306.2762492567), multiRoute3.pointAt(1000));
        assertEquals(new PointCh(2535674.879029043, 1152197.8185330231), multiRoute3.pointAt(1200));
        assertEquals(new PointCh(2535692.308568288, 1152218.8851099082), multiRoute3.pointAt(1400));

        //multiRoute4
        assertEquals(new PointCh(2535193.7106781187, 1152193.7106781187), multiRoute4.pointAt(100));
        assertEquals(new PointCh(2535264.4213562375, 1152264.4213562373), multiRoute4.pointAt(200));
        assertEquals(new PointCh(2535388.611396146, 1152313.9031784004), multiRoute4.pointAt(650));
        assertEquals(new PointCh(2535429.3219751846, 1152284.8747655204), multiRoute4.pointAt(700));
        assertEquals(new PointCh(2535682.665075113, 1152386.811068594), multiRoute4.pointAt(1000));
        assertEquals(new PointCh(2535728.7186335917, 1152390.8866047426), multiRoute4.pointAt(1500));
        assertEquals(new PointCh(2535756.759730425, 1152296.7832410324), multiRoute4.pointAt(1750));
        assertEquals(new PointCh(2535716.326987979, 1152247.915474851), multiRoute4.pointAt(2100));

        //multiRoute5
        assertEquals(new PointCh(2535193.7106781187, 1152193.7106781187), multiRoute5.pointAt(100));
        assertEquals(new PointCh(2535264.4213562375, 1152264.4213562373), multiRoute5.pointAt(200));
        assertEquals(new PointCh(2535388.611396146, 1152313.9031784004), multiRoute5.pointAt(650));
        assertEquals(new PointCh(2535429.3219751846, 1152284.8747655204), multiRoute5.pointAt(700));
        assertEquals(new PointCh(2535682.665075113, 1152386.811068594), multiRoute5.pointAt(1000));
        assertEquals(new PointCh(2535728.7186335917, 1152390.8866047426), multiRoute5.pointAt(1500));
        assertEquals(new PointCh(2535756.759730425, 1152296.7832410324), multiRoute5.pointAt(1750));
        assertEquals(new PointCh(2535716.326987979, 1152247.915474851), multiRoute5.pointAt(2100));
    }

    @Test
    void PointAtWorksOnEdgeValues() {
        create();

        //works on negative values
        assertEquals(point11, multiRoute1.pointAt(-1));
        assertEquals(point11, multiRoute2.pointAt(-1));
        assertEquals(point41, multiRoute3.pointAt(-1));
        assertEquals(point11, multiRoute4.pointAt(-1));
        assertEquals(point11, multiRoute5.pointAt(-1));

        //works on values greater than length
        assertEquals(point8, multiRoute1.pointAt(multiRoute1.length() + 1));
        assertEquals(point41, multiRoute2.pointAt(multiRoute2.length() + 1));
        assertEquals(point8, multiRoute3.pointAt(multiRoute3.length() + 1));
        assertEquals(point8, multiRoute4.pointAt(multiRoute4.length() + 1));
        assertEquals(point8, multiRoute5.pointAt(multiRoute4.length() + 1));

        //works on values equal to 0
        assertEquals(point11, multiRoute1.pointAt(0));
        assertEquals(point11, multiRoute2.pointAt(0));
        assertEquals(point41, multiRoute3.pointAt(0));
        assertEquals(point11, multiRoute4.pointAt(0));
        assertEquals(point11, multiRoute5.pointAt(0));

        //works on values equal to length
        assertEquals(point8, multiRoute1.pointAt(multiRoute1.length()));
        assertEquals(point41, multiRoute2.pointAt(multiRoute2.length()));
        assertEquals(point8, multiRoute3.pointAt(multiRoute3.length()));
        assertEquals(point8, multiRoute4.pointAt(multiRoute4.length()));
        assertEquals(point8, multiRoute5.pointAt(multiRoute4.length()));
    }

    @Test
    void ElevationAtWorksOnKnownValues() {
        create();

        //multiRoute1
        assertEquals(39.11099408612291, multiRoute1.elevationAt(100), 1e-12);
        assertEquals(10.96713441948415, multiRoute1.elevationAt(500), 1e-12);
        assertEquals(77.72979530758599, multiRoute1.elevationAt(600), 1e-12);
        assertEquals(68.7166028911134, multiRoute1.elevationAt(1000), 1e-12);
        assertEquals(60.37202453747647, multiRoute1.elevationAt(2100), 1e-12);

        //multiRoute2
        assertEquals(39.11099408612291, multiRoute2.elevationAt(100), 1e-12);
        assertEquals(76.44397634449163, multiRoute2.elevationAt(200), 1e-12);
        assertEquals(44.49245619568782, multiRoute2.elevationAt(650), 1e-12);
        assertEquals(13.994074009369504, multiRoute2.elevationAt(700), 1e-12);

        //multiRoute3
        assertEquals(65.8248234904391, multiRoute3.elevationAt(100), 1e-12);
        assertEquals(89.7120302818561, multiRoute3.elevationAt(500), 1e-12);
        assertEquals(60.02202387408028, multiRoute3.elevationAt(650), 1e-12);
        assertEquals(89.62393478553496, multiRoute3.elevationAt(800), 1e-12);
        assertEquals(75.4177117448635, multiRoute3.elevationAt(1000), 1e-12);
        assertEquals(76.20068832149688, multiRoute3.elevationAt(1200), 1e-12);
        assertEquals(76.14939678896384, multiRoute3.elevationAt(1400), 1e-12);

        //multiRoute4
        assertEquals(39.11099408612291, multiRoute4.elevationAt(100), 1e-12);
        assertEquals(76.44397634449163, multiRoute4.elevationAt(200), 1e-12);
        assertEquals(44.49245619568782, multiRoute4.elevationAt(650), 1e-12);
        assertEquals(13.994074009369504, multiRoute4.elevationAt(700), 1e-12);
        assertEquals(68.7166028911134, multiRoute4.elevationAt(1000), 1e-12);
        assertEquals(79.65981422787853, multiRoute4.elevationAt(1500), 1e-12);
        assertEquals(72.95412829561062, multiRoute4.elevationAt(1750), 1e-12);
        assertEquals(60.37202453747647, multiRoute4.elevationAt(2100), 1e-12);

        //multiRoute5
        assertEquals(39.11099408612291, multiRoute5.elevationAt(100), 1e-12);
        assertEquals(76.44397634449163, multiRoute5.elevationAt(200), 1e-12);
        assertEquals(44.49245619568782, multiRoute5.elevationAt(650), 1e-12);
        assertEquals(13.994074009369504, multiRoute5.elevationAt(700), 1e-12);
        assertEquals(68.7166028911134, multiRoute5.elevationAt(1000), 1e-12);
        assertEquals(79.65981422787853, multiRoute5.elevationAt(1500), 1e-12);
        assertEquals(72.95412829561062, multiRoute5.elevationAt(1750), 1e-12);
        assertEquals(60.37202453747647, multiRoute5.elevationAt(2100), 1e-12);
    }

    @Test
    void ElevationAtWorksOnEdgeValues() {
        create();

        //works on negative values
        assertEquals(50, multiRoute1.elevationAt(-1), 1e-12);
        assertEquals(50, multiRoute2.elevationAt(-1), 1e-12);
        assertEquals(30, multiRoute3.elevationAt(-1), 1e-12);
        assertEquals(50, multiRoute4.elevationAt(-1), 1e-12);
        assertEquals(50, multiRoute5.elevationAt(-1), 1e-12);

        //works on values greater than length
        assertEquals(70, multiRoute1.elevationAt(multiRoute1.length() + 1), 1e-12);
        assertEquals(30, multiRoute2.elevationAt(multiRoute2.length() + 1), 1e-12);
        assertEquals(70, multiRoute3.elevationAt(multiRoute3.length() + 1), 1e-12);
        assertEquals(70, multiRoute4.elevationAt(multiRoute4.length() + 1), 1e-12);
        assertEquals(70, multiRoute5.elevationAt(multiRoute5.length() + 1), 1e-12);

        //works on values equal to 0
        assertEquals(50, multiRoute1.elevationAt(0), 1e-12);
        assertEquals(50, multiRoute2.elevationAt(0), 1e-12);
        assertEquals(30, multiRoute3.elevationAt(0), 1e-12);
        assertEquals(50, multiRoute4.elevationAt(0), 1e-12);
        assertEquals(50, multiRoute5.elevationAt(0), 1e-12);

        //works on values equal to length
        assertEquals(70, multiRoute1.elevationAt(multiRoute1.length()), 1e-12);
        assertEquals(30, multiRoute2.elevationAt(multiRoute2.length()), 1e-12);
        assertEquals(70, multiRoute3.elevationAt(multiRoute3.length()), 1e-12);
        assertEquals(70, multiRoute4.elevationAt(multiRoute4.length()), 1e-12);
        assertEquals(70, multiRoute5.elevationAt(multiRoute5.length()), 1e-12);
    }

    @Test
    void NodeClosestToWorksOnKnownValues() {
        create();

        //multiRoute1
        assertEquals(1, multiRoute1.nodeClosestTo(100));
        assertEquals(3, multiRoute1.nodeClosestTo(500));
        assertEquals(2, multiRoute1.nodeClosestTo(600));
        assertEquals(4, multiRoute1.nodeClosestTo(1000));
        assertEquals(6, multiRoute1.nodeClosestTo(2100));

        //multiRoute2
        assertEquals(1, multiRoute2.nodeClosestTo(100));
        assertEquals(1, multiRoute2.nodeClosestTo(200));
        assertEquals(2, multiRoute2.nodeClosestTo(650));
        assertEquals(3, multiRoute2.nodeClosestTo(700));

        //multiRoute3
        assertEquals(4, multiRoute3.nodeClosestTo(100));
        assertEquals(5, multiRoute3.nodeClosestTo(500));
        assertEquals(4, multiRoute3.nodeClosestTo(650));
        assertEquals(5, multiRoute3.nodeClosestTo(800));
        assertEquals(6, multiRoute3.nodeClosestTo(1000));
        assertEquals(7, multiRoute3.nodeClosestTo(1200));
        assertEquals(7, multiRoute3.nodeClosestTo(1400));

        //multiRoute4
        assertEquals(1, multiRoute4.nodeClosestTo(100));
        assertEquals(1, multiRoute4.nodeClosestTo(200));
        assertEquals(2, multiRoute4.nodeClosestTo(650));
        assertEquals(3, multiRoute4.nodeClosestTo(700));
        assertEquals(4, multiRoute4.nodeClosestTo(1000));
        assertEquals(4, multiRoute4.nodeClosestTo(1500));
        assertEquals(6, multiRoute4.nodeClosestTo(1750));
        assertEquals(6, multiRoute4.nodeClosestTo(2100));

        //multiRoute5
        assertEquals(1, multiRoute5.nodeClosestTo(100));
        assertEquals(1, multiRoute5.nodeClosestTo(200));
        assertEquals(2, multiRoute5.nodeClosestTo(650));
        assertEquals(3, multiRoute5.nodeClosestTo(700));
        assertEquals(4, multiRoute5.nodeClosestTo(1000));
        assertEquals(4, multiRoute5.nodeClosestTo(1500));
        assertEquals(6, multiRoute5.nodeClosestTo(1750));
        assertEquals(6, multiRoute5.nodeClosestTo(2100));

    }

    @Test
    void NodeClosestToWorksOnEdgeValues() {
        create();

        //works on negative values
        assertEquals(0, multiRoute1.nodeClosestTo(-1));
        assertEquals(0, multiRoute2.nodeClosestTo(-1));
        assertEquals(3, multiRoute3.nodeClosestTo(-1));
        assertEquals(0, multiRoute4.nodeClosestTo(-1));
        assertEquals(0, multiRoute5.nodeClosestTo(-1));

        //works on values greater than length
        assertEquals(7, multiRoute1.nodeClosestTo(multiRoute1.length() + 1));
        assertEquals(3, multiRoute2.nodeClosestTo(multiRoute2.length() + 1));
        assertEquals(7, multiRoute3.nodeClosestTo(multiRoute3.length() + 1));
        assertEquals(7, multiRoute4.nodeClosestTo(multiRoute4.length() + 1));
        assertEquals(7, multiRoute5.nodeClosestTo(multiRoute5.length() + 1));

        //works on values equal to 0
        assertEquals(0, multiRoute1.nodeClosestTo(0));
        assertEquals(0, multiRoute2.nodeClosestTo(0));
        assertEquals(3, multiRoute3.nodeClosestTo(0));
        assertEquals(0, multiRoute4.nodeClosestTo(0));
        assertEquals(0, multiRoute5.nodeClosestTo(0));

        //works on values equal to length
        assertEquals(7, multiRoute1.nodeClosestTo(multiRoute1.length()));
        assertEquals(3, multiRoute2.nodeClosestTo(multiRoute2.length()));
        assertEquals(7, multiRoute3.nodeClosestTo(multiRoute3.length()));
        assertEquals(7, multiRoute4.nodeClosestTo(multiRoute4.length()));
        assertEquals(7, multiRoute5.nodeClosestTo(multiRoute5.length()));
    }

    @Test
    void PointClosestToWorksOnValuesOutsideOfSegments() {
        create();

        //multiRoute1
        assertEquals(new RoutePoint(new PointCh(2535151.33161, 1152151.3316100002), 40.0669471060211, 23.862506270602033), multiRoute1.pointClosestTo(new PointCh(2535168.20495, 1152134.45827)));
        assertEquals(new RoutePoint(new PointCh(2535284.984775, 1152284.984775), 229.0810657028165, 41.96730365141696), multiRoute1.pointClosestTo(new PointCh(2535314.66014,1152255.30941)));
        assertEquals(new RoutePoint(new PointCh(2535423.259410883, 1152289.1976374576), 410.0722098832407, 26.526615400166015), multiRoute1.pointClosestTo(new PointCh(2535407.8589,1152267.59936)));
        assertEquals(new RoutePoint(new PointCh(2535532.0362687283, 1152317.1419089804), 827.7924046784461, 22.58244776137388), multiRoute1.pointClosestTo(new PointCh(2535518.46841,1152335.19406)));
        assertEquals(new RoutePoint(new PointCh(2535703.020791555, 1152388.6124594295), 1020.4352685523834, 25.541510684196023), multiRoute1.pointClosestTo(new PointCh(2535700.76928,1152414.05454)));
        assertEquals(new RoutePoint(new PointCh(2535797.894931484, 1152346.4929341371), 1685.477458553404, 24.148620127378198), multiRoute1.pointClosestTo(new PointCh(2535816.4996,1152331.09741)));
        assertEquals(new RoutePoint(new PointCh(2535709.192834078, 1152239.292630635), 1824.6175577146926, 26.286132011657273), multiRoute1.pointClosestTo(new PointCh(2535729.44582,1152222.53622)));

        //multiRoute2
        assertEquals(new RoutePoint(new PointCh(2535151.33161, 1152151.3316100002), 40.0669471060211, 23.862506270602033), multiRoute2.pointClosestTo(new PointCh(2535168.20495, 1152134.45827)));
        assertEquals(new RoutePoint(new PointCh(2535284.984775, 1152284.984775), 229.0810657028165, 41.96730365141696), multiRoute2.pointClosestTo(new PointCh(2535314.66014,1152255.30941)));
        assertEquals(new RoutePoint(new PointCh(2535423.259410883, 1152289.1976374576), 410.0722098832407, 26.526615400166015), multiRoute2.pointClosestTo(new PointCh(2535407.8589,1152267.59936)));

        //multiRoute3
        assertEquals(new RoutePoint(new PointCh(2535532.0362687283, 1152317.1419089804), 90.11420709481865, 22.58244776137388), multiRoute3.pointClosestTo(new PointCh(2535518.46841,1152335.19406)));
        assertEquals(new RoutePoint(new PointCh(2535703.020791555, 1152388.6124594295), 282.75707096875595, 25.541510684196023), multiRoute3.pointClosestTo(new PointCh(2535700.76928,1152414.05454)));
        assertEquals(new RoutePoint(new PointCh(2535797.894931484, 1152346.4929341371), 947.7992609697766, 24.148620127378198), multiRoute3.pointClosestTo(new PointCh(2535816.4996,1152331.09741)));
        assertEquals(new RoutePoint(new PointCh(2535709.192834078, 1152239.292630635), 1086.939360131065, 26.286132011657273), multiRoute3.pointClosestTo(new PointCh(2535729.44582,1152222.53622)));

        //multiRoute4
        assertEquals(new RoutePoint(new PointCh(2535151.33161, 1152151.3316100002), 40.0669471060211, 23.862506270602033), multiRoute4.pointClosestTo(new PointCh(2535168.20495, 1152134.45827)));
        assertEquals(new RoutePoint(new PointCh(2535284.984775, 1152284.984775), 229.0810657028165, 41.96730365141696), multiRoute4.pointClosestTo(new PointCh(2535314.66014,1152255.30941)));
        assertEquals(new RoutePoint(new PointCh(2535423.259410883, 1152289.1976374576), 410.0722098832407, 26.526615400166015), multiRoute4.pointClosestTo(new PointCh(2535407.8589,1152267.59936)));
        assertEquals(new RoutePoint(new PointCh(2535532.0362687283, 1152317.1419089804), 827.7924046784461, 22.58244776137388), multiRoute4.pointClosestTo(new PointCh(2535518.46841,1152335.19406)));
        assertEquals(new RoutePoint(new PointCh(2535703.020791555, 1152388.6124594295), 1020.4352685523834, 25.541510684196023), multiRoute4.pointClosestTo(new PointCh(2535700.76928,1152414.05454)));
        assertEquals(new RoutePoint(new PointCh(2535797.894931484, 1152346.4929341371), 1685.477458553404, 24.148620127378198), multiRoute4.pointClosestTo(new PointCh(2535816.4996,1152331.09741)));
        assertEquals(new RoutePoint(new PointCh(2535709.192834078, 1152239.292630635), 1824.6175577146926, 26.286132011657273), multiRoute4.pointClosestTo(new PointCh(2535729.44582,1152222.53622)));

        //multiRoute5
        assertEquals(new RoutePoint(new PointCh(2535151.33161, 1152151.3316100002), 40.0669471060211, 23.862506270602033), multiRoute5.pointClosestTo(new PointCh(2535168.20495, 1152134.45827)));
        assertEquals(new RoutePoint(new PointCh(2535284.984775, 1152284.984775), 229.0810657028165, 41.96730365141696), multiRoute5.pointClosestTo(new PointCh(2535314.66014,1152255.30941)));
        assertEquals(new RoutePoint(new PointCh(2535423.259410883, 1152289.1976374576), 410.0722098832407, 26.526615400166015), multiRoute5.pointClosestTo(new PointCh(2535407.8589,1152267.59936)));
        assertEquals(new RoutePoint(new PointCh(2535532.0362687283, 1152317.1419089804), 827.7924046784461, 22.58244776137388), multiRoute5.pointClosestTo(new PointCh(2535518.46841,1152335.19406)));
        assertEquals(new RoutePoint(new PointCh(2535703.020791555, 1152388.6124594295), 1020.4352685523834, 25.541510684196023), multiRoute5.pointClosestTo(new PointCh(2535700.76928,1152414.05454)));
        assertEquals(new RoutePoint(new PointCh(2535797.894931484, 1152346.4929341371), 1685.4774585534042, 24.148620127378198), multiRoute5.pointClosestTo(new PointCh(2535816.4996,1152331.09741)));
        assertEquals(new RoutePoint(new PointCh(2535709.192834078, 1152239.292630635), 1824.6175577146926, 26.286132011657273), multiRoute5.pointClosestTo(new PointCh(2535729.44582,1152222.53622)));
    }

    @Test
    void PointClosestToWorksForValuesOnSegments() {
        create();

        //multiRoute1
        assertEquals(new RoutePoint(point11, 0, 0), multiRoute1.pointClosestTo(point11));
        assertEquals(new RoutePoint(point21, 156.97770542341354, 0), multiRoute1.pointClosestTo(point21));
        assertEquals(new RoutePoint(point31, 313.9554108468271, 0), multiRoute1.pointClosestTo(point31));
        assertEquals(new RoutePoint(point41, 455.19633975909386, 0), multiRoute1.pointClosestTo(point41));
        assertEquals(new RoutePoint(point51, 934.078299416595, 0), multiRoute1.pointClosestTo(point51));
        assertEquals(new RoutePoint(point61, 1160.961529294874, 0), multiRoute1.pointClosestTo(point61));
        assertEquals(new RoutePoint(point7, 1764.7753002570983, 0), multiRoute1.pointClosestTo(point7));
        assertEquals(new RoutePoint(point8, 1908.062269657502, 0), multiRoute1.pointClosestTo(point8));

        //multiRoute2
        assertEquals(new RoutePoint(point11, 0, 0), multiRoute2.pointClosestTo(point11));
        assertEquals(new RoutePoint(point21, 156.97770542341354, 0), multiRoute2.pointClosestTo(point21));
        assertEquals(new RoutePoint(point31, 313.9554108468271, 0), multiRoute2.pointClosestTo(point31));
        assertEquals(new RoutePoint(point41, 455.19633975909386, 0), multiRoute2.pointClosestTo(point41));

        //multiRoute3
        assertEquals(new RoutePoint(point41, 0, 0), multiRoute3.pointClosestTo(point41));
        assertEquals(new RoutePoint(point51, 196.4001018329675, 0), multiRoute3.pointClosestTo(point51));
        assertEquals(new RoutePoint(point61, 423.2833317112465, 0), multiRoute3.pointClosestTo(point61));
        assertEquals(new RoutePoint(point7, 1027.0971026734708, 0), multiRoute3.pointClosestTo(point7));
        assertEquals(new RoutePoint(point8, 1170.3840720738744, 0), multiRoute3.pointClosestTo(point8));

        //multiRoute4
        assertEquals(new RoutePoint(point11, 0, 0), multiRoute4.pointClosestTo(point11));
        assertEquals(new RoutePoint(point21, 156.97770542341354, 0), multiRoute4.pointClosestTo(point21));
        assertEquals(new RoutePoint(point31, 313.9554108468271, 0), multiRoute4.pointClosestTo(point31));
        assertEquals(new RoutePoint(point41, 455.19633975909386, 0), multiRoute4.pointClosestTo(point41));
        assertEquals(new RoutePoint(point51, 934.078299416595, 0), multiRoute4.pointClosestTo(point51));
        assertEquals(new RoutePoint(point61, 1160.961529294874, 0), multiRoute4.pointClosestTo(point61));
        assertEquals(new RoutePoint(point7, 1764.7753002570983, 0), multiRoute4.pointClosestTo(point7));
        assertEquals(new RoutePoint(point8, 1908.062269657502, 0), multiRoute4.pointClosestTo(point8));

        //multiRoute5
        assertEquals(new RoutePoint(point11, 0, 0), multiRoute5.pointClosestTo(point11));
        assertEquals(new RoutePoint(point21, 156.97770542341354, 0), multiRoute5.pointClosestTo(point21));
        assertEquals(new RoutePoint(point31, 313.9554108468271, 0), multiRoute5.pointClosestTo(point31));
        assertEquals(new RoutePoint(point41, 455.19633975909386, 0), multiRoute5.pointClosestTo(point41));
        assertEquals(new RoutePoint(point51, 934.078299416595, 0), multiRoute5.pointClosestTo(point51));
        assertEquals(new RoutePoint(point61, 1160.961529294874, 0), multiRoute5.pointClosestTo(point61));
        assertEquals(new RoutePoint(point7, 1764.7753002570983, 0), multiRoute5.pointClosestTo(point7));
        assertEquals(new RoutePoint(point8, 1908.062269657502, 0), multiRoute5.pointClosestTo(point8));
    }

    PointCh point11 = new PointCh(2595000,1200000);
    PointCh point21 = new PointCh(2600000,1204000);
    PointCh point31 = new PointCh(2603000,1202000);
    PointCh point41 = new PointCh(2605000,1203000);
    PointCh point51 = new PointCh(2609000,1199000);
    PointCh point61 = new PointCh(2609500, 1198500);

    float[] samples1 = new float[]{200, 240, 230, 360,380};
    DoubleUnaryOperator profile11 = Functions.sampled(samples1, point11.distanceTo(point21) );
    Edge edge11 = new Edge(0, 3, point11, point21, point11.distanceTo(point21), profile11);
    float[] samples2 = new float[]{380,360, 340, 350,320,350};
    DoubleUnaryOperator profile21 = Functions.sampled(samples2, point21.distanceTo(point31));
    Edge edge21 = new Edge(3, 8, point21, point31, point21.distanceTo(point31), profile21);
    float[] samples3 = new float[]{350, 320, 300, 280,270,250};
    DoubleUnaryOperator profile31 = Functions.sampled(samples3, point31.distanceTo(point41));
    Edge edge31 = new Edge(8,10, point31, point41, point31.distanceTo(point41), profile31);
    float[] samples4 = new float[]{250, 300, 330, 360,380,400};
    DoubleUnaryOperator profile41 = Functions.sampled(samples4, point41.distanceTo(point51));
    Edge edge41 = new Edge(10, 18, point41, point51, point41.distanceTo(point51), profile41);
    // Edge edge5 = new Edge(18, 5,point5 ,point6, point5.distanceTo(point6),x ->Float.NaN);

    float[] samplesReverse4 = new float[]{400, 380, 360, 330,300,250};
    DoubleUnaryOperator profileReverse4 = Functions.sampled(samplesReverse4, point41.distanceTo(point51));
    Edge edgeReverse4 = new Edge(18, 10, point51, point41, point41.distanceTo(point51),profileReverse4);
    float[] samplesReverse3 = new float[]{250, 270, 280, 300,320,350};
    DoubleUnaryOperator profileReverse3 = Functions.sampled(samplesReverse3, point31.distanceTo(point41));
    Edge edgeReverse3 = new Edge(10,8, point41, point31, point31.distanceTo(point41),profileReverse3);
    float[] samplesReverse2 = new float[]{350,320, 350, 340, 360,380};
    DoubleUnaryOperator profileReverse2 = Functions.sampled(samplesReverse2, point21.distanceTo(point31));
    Edge edgeReverse2 = new Edge(8, 3, point31, point21, point21.distanceTo(point31), profileReverse2);
    float[] samplesReverse1 = new float[]{380, 360, 230, 240,200};
    DoubleUnaryOperator profileReverse1 = Functions.sampled(samplesReverse1, point11.distanceTo(point21) );
    Edge edgeReverse1 = new Edge(3, 0, point21, point11, point11.distanceTo(point21), profileReverse1);

    List<Edge> edges11 = new ArrayList<>();
    List<Edge> edges21 = new ArrayList<>();
    List<Edge> edges31 = new ArrayList<>();
    List<Edge> edges41 = new ArrayList<>();

    @Test
    public void indexOfSegmentAtNestedMultiRoutes(){
        edges11.add(edge11);
        edges11.add(edge21);
        SingleRoute route1 = new SingleRoute(edges11);

        edges21.add(edge31);
        edges21.add(edge41);
        SingleRoute route2 = new SingleRoute(edges21);

        edges31.add(edgeReverse4);
        edges31.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges31);

        edges41.add(edgeReverse2);
        edges41.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges41);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);
        //La méthode fonctionne-t-elle pour les cas limites ?
        assertEquals(3,multiRouteSupreme.indexOfSegmentAt(multiRouteSupreme.length()));
        assertEquals(0,multiRouteSupreme.indexOfSegmentAt(0));
        assertEquals(3,multiRouteSupreme.indexOfSegmentAt(30795));
        //Fonctionne-elle pour des valeurs pratiques ?
        assertEquals(0,multiRouteSupreme.indexOfSegmentAt(5000));
        assertEquals(2,multiRouteSupreme.indexOfSegmentAt(20902));
        //Voyons si vous avez ramené la position entre 0 et la longueur totale.
        assertEquals(0,multiRouteSupreme.indexOfSegmentAt(-4));
        assertEquals(3,multiRouteSupreme.indexOfSegmentAt(multiRouteSupreme.length()+42));

    }
    @Test
    public void indexOfSegmentAtOnlySingleRoutes(){
        edges11.add(edge11);
        edges11.add(edge21);
        SingleRoute route1 = new SingleRoute(edges11);

        edges21.add(edge31);
        edges21.add(edge41);
        SingleRoute route2 = new SingleRoute(edges21);

        edges31.add(edgeReverse4);
        edges31.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges31);
        List<Route> routes = new ArrayList<>();
        routes.add(route1);
        routes.add(route2);
        routes.add(route3);

        MultiRoute bRoute = new MultiRoute(routes);

        assertEquals(1, bRoute.indexOfSegmentAt(13465));
        assertEquals(0, bRoute.indexOfSegmentAt(0));
        assertEquals(2, bRoute.indexOfSegmentAt(21402));
        assertEquals(2, bRoute.indexOfSegmentAt(bRoute.length()));
    }
    @Test
    public void indexOfSegmentAtInceptionMultiRoutes(){
        edges11.add(edge11);
        edges11.add(edge21);
        SingleRoute route1 = new SingleRoute(edges11);

        edges21.add(edge31);
        edges21.add(edge41);
        SingleRoute route2 = new SingleRoute(edges21);

        edges31.add(edgeReverse4);
        edges31.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges31);

        edges41.add(edgeReverse2);
        edges41.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges41);

        List<Route> routes1 = new ArrayList<>();

        routes1.add(route1);
        routes1.add(route2);

        List<Route> routes2 = new ArrayList<>();

        routes2.add(route3);
        routes2.add(route4);

        MultiRoute lucidRoute1 = new MultiRoute(routes1);
        MultiRoute lucidRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(lucidRoute1);
        multiRoutes.add(lucidRoute2);

        MultiRoute paradoxicalSleep = new MultiRoute(multiRoutes);

        List<Route> multisRoutes = new ArrayList<>();
        multisRoutes.add(paradoxicalSleep);

        MultiRoute inceptionRoute = new MultiRoute(multisRoutes);

        assertEquals(0,inceptionRoute.indexOfSegmentAt(0));
        assertEquals(3,inceptionRoute.indexOfSegmentAt(30795));
        assertEquals(3,inceptionRoute.indexOfSegmentAt(inceptionRoute.length()));
        assertEquals(0,inceptionRoute.indexOfSegmentAt(5000));
        assertEquals(0,inceptionRoute.indexOfSegmentAt(-4));
        assertEquals(3,inceptionRoute.indexOfSegmentAt(inceptionRoute.length()+42));
        assertEquals(2,inceptionRoute.indexOfSegmentAt(20902));
    }
    @Test
    public void indexOfSegmentAtMixAndTwist(){
        edges11.add(edge11);
        edges11.add(edge21);
        SingleRoute route1 = new SingleRoute(edges11);

        edges21.add(edge31);
        edges21.add(edge41);
        SingleRoute route2 = new SingleRoute(edges21);

        edges31.add(edgeReverse4);
        edges31.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges31);

        edges41.add(edgeReverse2);
        edges41.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges41);

        List<Route> routes1 = new ArrayList<>();

        routes1.add(route1);
        routes1.add(route2);

        MultiRoute mixRoute = new MultiRoute(routes1);

        List<Route> routes2 = new ArrayList<>();
        routes2.add(mixRoute);
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute twistRoute = new MultiRoute(routes2);

        assertEquals(0,twistRoute.indexOfSegmentAt(0));
        //False
        System.out.println("route1: " + route1.length());
        System.out.println("route2: " + route2.length());
        System.out.println("route3: " + route3.length());
        System.out.println("route4: " + route4.length());
        assertEquals(3,twistRoute.indexOfSegmentAt(30795));
        //System.out.println();
        assertEquals(3,twistRoute.indexOfSegmentAt(twistRoute.length()));
        assertEquals(0,twistRoute.indexOfSegmentAt(5000));
        assertEquals(0,twistRoute.indexOfSegmentAt(-4));
        assertEquals(3,twistRoute.indexOfSegmentAt(twistRoute.length()+42));
        assertEquals(2,twistRoute.indexOfSegmentAt(20902));
    }
    @Test
    public void lengthTest(){
        edges11.add(edge11);
        edges11.add(edge21);
        SingleRoute route1 = new SingleRoute(edges11);

        edges21.add(edge31);
        edges21.add(edge41);
        SingleRoute route2 = new SingleRoute(edges21);

        edges31.add(edgeReverse4);
        edges31.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges31);

        edges41.add(edgeReverse2);
        edges41.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges41);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);

        assertEquals(route1.length()+route2.length()+route3.length()+route4.length(),multiRouteSupreme.length());
    }
    @Test
    public void edgesTest(){
        edges11.add(edge11);
        edges11.add(edge21);
        SingleRoute route1 = new SingleRoute(edges11);

        edges21.add(edge31);
        edges21.add(edge41);
        SingleRoute route2 = new SingleRoute(edges21);

        edges31.add(edgeReverse4);
        edges31.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges31);

        edges41.add(edgeReverse2);
        edges41.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges41);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);
        List<Edge> edges = List.of(edge11, edge21, edge31, edge41,edgeReverse4,edgeReverse3,edgeReverse2,edgeReverse1);
        assertEquals(edges,multiRouteSupreme.edges());
    }
    @Test
    public void pointsTest(){
        edges11.add(edge11);
        edges11.add(edge21);
        SingleRoute route1 = new SingleRoute(edges11);

        edges21.add(edge31);
        edges21.add(edge41);
        SingleRoute route2 = new SingleRoute(edges21);

        edges31.add(edgeReverse4);
        edges31.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges31);

        edges41.add(edgeReverse2);
        edges41.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges41);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);

        List<PointCh> points = List.of(point11, point21, point31, point41, point51, point41, point31, point21, point11);

        assertEquals(points,multiRouteSupreme.points());
    }
    @Test
    public void elevationAtTest(){
        edges11.add(edge11);
        edges11.add(edge21);
        SingleRoute route1 = new SingleRoute(edges11);

        edges21.add(edge31);
        edges21.add(edge41);
        SingleRoute route2 = new SingleRoute(edges21);

        edges31.add(edgeReverse4);
        edges31.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges31);

        edges41.add(edgeReverse2);
        edges41.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges41);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);

        assertEquals(route3.elevationAt(18302-route1.length()-route2.length()), multiRouteSupreme.elevationAt(18302),1e-6);
        assertEquals(route1.elevationAt(0), multiRouteSupreme.elevationAt(0));
        assertEquals(route4.elevationAt(route4.length()), multiRouteSupreme.elevationAt(multiRouteSupreme.length()));
        assertEquals(route3.elevationAt(route3.length()), multiRouteSupreme.elevationAt(route1.length()+route2.length()+route3.length()));
        assertEquals(route1.elevationAt(route1.length()), multiRouteSupreme.elevationAt(route1.length()));
    }
    @Test
    public void pointAtTest(){
        edges11.add(edge11);
        edges11.add(edge21);
        SingleRoute route1 = new SingleRoute(edges11);

        edges21.add(edge31);
        edges21.add(edge41);
        SingleRoute route2 = new SingleRoute(edges21);

        edges31.add(edgeReverse4);
        edges31.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges31);

        edges41.add(edgeReverse2);
        edges41.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges41);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);

        assertEquals(route3.pointAt(18302-route1.length()-route2.length()), multiRouteSupreme.pointAt(18302));
        assertEquals(route1.pointAt(0), multiRouteSupreme.pointAt(0));
        assertEquals(route4.pointAt(route4.length()), multiRouteSupreme.pointAt(multiRouteSupreme.length()));
    }
    @Test
    public void nodeClosestToTest(){
        edges11.add(edge11);
        edges11.add(edge21);
        SingleRoute route1 = new SingleRoute(edges11);

        edges21.add(edge31);
        edges21.add(edge41);
        SingleRoute route2 = new SingleRoute(edges21);

        edges31.add(edgeReverse4);
        edges31.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges31);

        edges41.add(edgeReverse2);
        edges41.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges41);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);

        assertEquals(18, multiRouteSupreme.nodeClosestTo(18302));
        assertEquals(8, multiRouteSupreme.nodeClosestTo(25000));
        assertEquals(0, multiRouteSupreme.nodeClosestTo(0));
        assertEquals(0, multiRouteSupreme.nodeClosestTo(multiRouteSupreme.length()));
    }
    @Test
    public void pointClosestToTest(){

        edges11.add(edge11);
        edges11.add(edge21);
        SingleRoute route1 = new SingleRoute(edges11);

        edges21.add(edge31);
        edges21.add(edge41);
        SingleRoute route2 = new SingleRoute(edges21);

        edges31.add(edgeReverse4);
        edges31.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges31);

        edges41.add(edgeReverse2);
        edges41.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges41);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);


        RoutePoint routePoint1 = new RoutePoint(point51,route1.length()+route2.length(),0);
        assertEquals(routePoint1, multiRouteSupreme.pointClosestTo(point51));

        PointCh expectedPoint = new PointCh(2609500,1199500);
        RoutePoint routePoint2 = new RoutePoint(point51,route1.length()+route2.length(),expectedPoint.distanceTo(point51));
        assertEquals(routePoint2,multiRouteSupreme.pointClosestTo(expectedPoint));

        //c'est pas toi c'est moi
        //envoyez moi des colis c'est le PointCh de ma maison (Clement sans accent parce que c'est William qui ecrit)
        PointCh exPoint = new PointCh(2534471, 1154885);
        RoutePoint routePoint3 = new RoutePoint(point11, 0,exPoint.distanceTo(point11));
        assertEquals(routePoint3, multiRouteSupreme.pointClosestTo(exPoint));



        List<Route> singleRoutes = new ArrayList<>();
        SingleRoute singleRoute1 = new SingleRoute(List.of(edge11));
        SingleRoute singleRoute2 = new SingleRoute(List.of(edge21));
        SingleRoute singleRoute3 = new SingleRoute(List.of(edge31));
        SingleRoute singleRoute4 = new SingleRoute(List.of(edge41));
        singleRoutes.add(singleRoute1);
        singleRoutes.add(singleRoute2);
        singleRoutes.add(singleRoute3);
        singleRoutes.add(singleRoute4);
        MultiRoute cRoute = new MultiRoute(singleRoutes);

        //Si celui là marche vous êtes chauds (Oui il y a des accents donc c'est l'autre loustic)
        PointCh middlePoint = new PointCh(2604723, 1193147);
        RoutePoint routePoint4 = new RoutePoint(point51, route1.length()+route2.length(),middlePoint.distanceTo(point51));
        //Si le test ne fonctionne pas pour vous, décommenter ces prints pourrait vous aider.
        //System.out.println(middlePoint.distanceTo(point1));
        //System.out.println(middlePoint.distanceTo(point2));
        //System.out.println(middlePoint.distanceTo(point3));
        //System.out.println(middlePoint.distanceTo(point4));
        //System.out.println(middlePoint.distanceTo(point5));
        assertEquals(routePoint4, cRoute.pointClosestTo(middlePoint));
    }
}
