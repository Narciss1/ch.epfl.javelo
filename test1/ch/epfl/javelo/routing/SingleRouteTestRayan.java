package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.data.*;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Path;
import java.security.Signature;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class SingleRouteTestRayan {

    @Test
    @Disabled
    void nodeClosestToForKnownValues() {
        // TODO: c pas super beau mais c des test dsl LOL
        PointCh fromPoint = new PointCh(2485010, 1076000);
        PointCh toPoint = new PointCh(2485020, 1076000);

        // profile for edge 1
        float[] type3Array = new float[]{
                384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,
        };
        DoubleUnaryOperator profile = Functions.sampled(type3Array, 10);

        // profile for edge 2
        float[] type3Array2 = new float[]{
                384f, 380f, 370f, 360f, 350f,
                340f, 330f, 320f, 310f, 300f,
        };

        DoubleUnaryOperator profile2 = Functions.sampled(type3Array2, 10);

        // profile for edge 3
        float[] type3Array3 = new float[]{
                300f, 314f, 312.12f, 313.13f, 314.764f,
        };
        DoubleUnaryOperator profile3 = Functions.sampled(type3Array3, 5);

        Edge edge1 = new Edge(0, 3, fromPoint, toPoint, 10, profile);
        Edge edge2 = new Edge(0, 3, fromPoint, toPoint, 10, profile2);
        Edge edge3 = new Edge(0, 3, fromPoint, toPoint, 5, profile3);

        ArrayList<Edge> testEdges = new ArrayList<>();
        testEdges.add(edge1);
        testEdges.add(edge2);
        testEdges.add(edge3);
        SingleRoute route = new SingleRoute(testEdges);

        assertEquals(0, route.nodeClosestTo(5d));
        assertEquals(1, route.nodeClosestTo(12d));
        assertEquals(2, route.nodeClosestTo(17d));
    }

    // TODO AGAIN MAYBE BULLSHIT FUCK THAT
    @Test
    @Disabled
    void nodeClosestToWorksForLimitValues() {
        // TODO: c pas super beau mais c des test dsl LOL
        PointCh fromPoint = new PointCh(2485010, 1076000);
        PointCh toPoint = new PointCh(2485020, 1076000);

        // profile for edge 1
        float[] type3Array = new float[]{
                384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,
        };
        DoubleUnaryOperator profile = Functions.sampled(type3Array, 10);

        // profile for edge 2
        float[] type3Array2 = new float[]{
                384f, 380f, 370f, 360f, 350f,
                340f, 330f, 320f, 310f, 300f,
        };

        DoubleUnaryOperator profile2 = Functions.sampled(type3Array2, 10);

        // profile for edge 3
        float[] type3Array3 = new float[]{
                300f, 314f, 312.12f, 313.13f, 314.764f,
        };
        DoubleUnaryOperator profile3 = Functions.sampled(type3Array3, 5);

        Edge edge1 = new Edge(0, 3, fromPoint, toPoint, 10, profile);
        Edge edge2 = new Edge(0, 3, fromPoint, toPoint, 10, profile2);
        Edge edge3 = new Edge(0, 3, fromPoint, toPoint, 5, profile3);

        ArrayList<Edge> testEdges = new ArrayList<>();
        testEdges.add(edge1);
        testEdges.add(edge2);
        testEdges.add(edge3);
        SingleRoute route = new SingleRoute(testEdges);

        assertEquals(0, route.nodeClosestTo(Double.MIN_VALUE));
        assertEquals(2, route.nodeClosestTo(Double.MAX_VALUE));
    }

    @Test
    void lengthWorksOnKnownValues() {
        // TODO: clean le code
        PointCh fromPoint = new PointCh(2485010, 1076000);
        PointCh toPoint = new PointCh(2485020, 1076000);
        float[] type3Array = new float[]{
                384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,
        };
        DoubleUnaryOperator profile = Functions.sampled(type3Array, 10);
        DoubleUnaryOperator squared = new DoubleUnaryOperator() {
            @Override
            public double applyAsDouble(double operand) {
                return Math.pow(operand, 2);
            }
        };

        Edge edge1 = new Edge(0, 3, fromPoint, toPoint, 10, profile); // has no profile
        Edge edge2 = new Edge(0, 3, fromPoint, toPoint, 10, squared); // has profile

        ArrayList<Edge> testEdges = new ArrayList<>();
        testEdges.add(edge1);
        testEdges.add(edge2);
        SingleRoute route = new SingleRoute(testEdges);

        System.out.print(route.length());
    }

    @Test
    void pointsListWorks() {
        // TODO: c pas super beau mais c des test dsl LOL
        PointCh fromPoint = new PointCh(2485010, 1076000);
        PointCh toPoint = new PointCh(2485020, 1076000);

        PointCh fromPoint2 = new PointCh(2485666, 1076666);
        PointCh toPoint2 = new PointCh(2485500, 1076500);

        // profile for edge 1
        float[] type3Array = new float[]{
                384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,
        };
        DoubleUnaryOperator profile = Functions.sampled(type3Array, 10);

        // profile for edge 2
        float[] type3Array2 = new float[]{
                384f, 380f, 370f, 360f, 350f,
                340f, 330f, 320f, 310f, 300f,
        };
        DoubleUnaryOperator profile2 = Functions.sampled(type3Array2, 10);

        Edge edge1 = new Edge(0, 3, fromPoint, toPoint, 10, profile);
        Edge edge2 = new Edge(0, 3, fromPoint2, toPoint2, 10, profile2);

        ArrayList<Edge> testEdges = new ArrayList<>();
        testEdges.add(edge1);
        testEdges.add(edge2);
        SingleRoute route = new SingleRoute(testEdges);

        List<PointCh> returnedPoints = route.points();

        assertEquals(fromPoint.e(), returnedPoints.get(0).e());
        assertEquals(fromPoint.n(), returnedPoints.get(0).n());
        assertEquals(fromPoint2.e(), returnedPoints.get(1).e());
        assertEquals(fromPoint2.n(), returnedPoints.get(1).n());
    }

    @Test
    void pointAtWorks() {
        // TODO: c pas super beau mais c des test dsl LOL
        PointCh fromPoint = new PointCh(2485010, 1076000);
        PointCh toPoint = new PointCh(2485020, 1076000);

        PointCh fromPoint2 = new PointCh(2485666, 1076666);
        PointCh toPoint2 = new PointCh(2485500, 1076500);

        // profile for edge 1
        float[] type3Array = new float[]{
                384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,
        };
        DoubleUnaryOperator profile = Functions.sampled(type3Array, 10);

        // profile for edge 2
        float[] type3Array2 = new float[]{
                384f, 380f, 370f, 360f, 350f,
                340f, 330f, 320f, 310f, 300f,
        };
        DoubleUnaryOperator profile2 = Functions.sampled(type3Array2, 10);

        Edge edge1 = new Edge(0, 3, fromPoint, toPoint, 10, profile);
        Edge edge2 = new Edge(0, 3, fromPoint2, toPoint2, 10, profile2);

        ArrayList<Edge> testEdges = new ArrayList<>();
        testEdges.add(edge1);
        testEdges.add(edge2);
        SingleRoute route = new SingleRoute(testEdges);

        assertEquals(2485666, route.pointAt(10d).e());
        assertEquals(2485015.0, route.pointAt(5d).e());
    }

    @Test
    void nodeClosestTo() {
    }

    @Test
    void pointClosestTo() {
    }

    @Test
    void elevationAtWorksForLimitValues() {
        // TODO: c pas super beau mais c des test dsl LOL
        PointCh fromPoint = new PointCh(2485010, 1076000);
        PointCh toPoint = new PointCh(2485020, 1076000);

        // profile for edge 1
        float[] type3Array = new float[]{
                384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,
        };
        DoubleUnaryOperator profile = Functions.sampled(type3Array, 10);

        // profile for edge 2
        float[] type3Array2 = new float[]{
                384f, 380f, 370f, 360f, 350f,
                340f, 330f, 320f, 310f, 300f,
        };
        DoubleUnaryOperator profile2 = Functions.sampled(type3Array2, 10);

        Edge edge1 = new Edge(0, 3, fromPoint, toPoint, 10, profile);
        Edge edge2 = new Edge(0, 3, fromPoint, toPoint, 10, profile2);

        ArrayList<Edge> testEdges = new ArrayList<>();
        testEdges.add(edge1);
        testEdges.add(edge2);
        SingleRoute route = new SingleRoute(testEdges);

        assertEquals( 300f, route.elevationAt(Double.MAX_VALUE));
        assertEquals( 384.75f, route.elevationAt(Double.MIN_VALUE));
    }

    @Test
    void elevationAtWorksForKnownValues() {
        // TODO: c pas super beau mais c des test dsl LOL
        PointCh fromPoint = new PointCh(2485010, 1076000);
        PointCh toPoint = new PointCh(2485020, 1076000);

        // profile for edge 1
        float[] type3Array = new float[]{
                384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,
        };
        DoubleUnaryOperator profile = Functions.sampled(type3Array, 10);

        // profile for edge 2
        float[] type3Array2 = new float[]{
                384f, 380f, 370f, 360f, 350f,
                340f, 330f, 320f, 310f, 300f,
        };

        DoubleUnaryOperator profile2 = Functions.sampled(type3Array2, 10);

        // profile for edge 3
        float[] type3Array3 = new float[]{
                300f, 314f, 312.12f, 313.13f, 314.764f,
        };
        DoubleUnaryOperator profile3 = Functions.sampled(type3Array3, 5);

        Edge edge1 = new Edge(0, 3, fromPoint, toPoint, 10, profile);
        Edge edge2 = new Edge(0, 3, fromPoint, toPoint, 10, profile2);
        Edge edge3 = new Edge(0, 3, fromPoint, toPoint, 5, profile3);

        ArrayList<Edge> testEdges = new ArrayList<>();
        testEdges.add(edge1);
        testEdges.add(edge2);
        testEdges.add(edge3);
        SingleRoute route = new SingleRoute(testEdges);

        assertEquals(route.elevationAt(10d), 384f);
        //todo loool
    }

    @Test
    void edgesWorks() {
        // TODO: c pas super beau mais c des test dsl LOL
        PointCh fromPoint = new PointCh(2485010, 1076000);
        PointCh toPoint = new PointCh(2485020, 1076000);

        // profile for edge 1
        float[] type3Array = new float[]{
                384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,
        };
        DoubleUnaryOperator profile = Functions.sampled(type3Array, 10);

        // profile for edge 2
        float[] type3Array2 = new float[]{
                384f, 380f, 370f, 360f, 350f,
                340f, 330f, 320f, 310f, 300f,
        };
        DoubleUnaryOperator profile2 = Functions.sampled(type3Array2, 10);

        Edge edge1 = new Edge(0, 3, fromPoint, toPoint, 10, profile);
        Edge edge2 = new Edge(0, 3, fromPoint, toPoint, 15, profile2);

        ArrayList<Edge> testEdges = new ArrayList<>();
        testEdges.add(edge1);
        testEdges.add(edge2);
        SingleRoute route = new SingleRoute(testEdges);

        List<Edge> edgesMethod = route.edges();

        // compares both list by comparing each edges length
        for (int i = 0; i < testEdges.size(); i++) {
            assertEquals(testEdges.get(i).length(), edgesMethod.get(i).length());
        }
    }


    // CYRIELLE TESTS



}