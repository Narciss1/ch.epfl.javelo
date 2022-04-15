package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.data.*;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.Math2.clamp;
import static java.lang.Double.isNaN;
import static java.lang.Float.NaN;
import static java.lang.Math.sqrt;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SingleRouteTest {
    // LES EDGES DE CES TEST NE SONT PAS COLLE!! :/
    @Test
    public void indexOfSegmentAt1() {
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges edges1 =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0, 0, 0, 12, 0, 10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, edges1, liste);
        Edge edge1 = Edge.of(graph1, 0, 0, 1);

        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        SingleRoute singleRoute = new SingleRoute(edges);
        int expected = 0;
        int actual0 = singleRoute.indexOfSegmentAt(4.5);
        //TEST 1:
        assertEquals(expected, actual0);
        int actual1 = singleRoute.indexOfSegmentAt(16);
        //TEST 2:
        assertEquals(expected, actual1);
    }

    @Test
    public void lengthWorks() {
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges graphEdges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0, 0, 0, 12, 0, 10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, graphEdges, liste);
        Edge edge0 = Edge.of(graph1, 0, 0, 1);

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        float[] profileTab = {502f, 500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        List<Edge> edges0 = new ArrayList<>();
        edges0.add(edge0);
        SingleRoute singleRoute0 = new SingleRoute(edges0);
        double expected0 = 16.6875;
        double actual0 = singleRoute0.length();
        //TEST 1:
        assertEquals(expected0, actual0);
        List<Edge> edges1 = new ArrayList<>();
        edges1.add(edge0);
        edges1.add(edge1);
        SingleRoute singleRoute1 = new SingleRoute(edges1);
        double expected1 = 21.6875;
        double actual1 = singleRoute1.length();
        //TEST 2:
        assertEquals(expected1, actual1);
    }

    @Test
    public void edgesWorks4() {
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges graphEdges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0, 0, 0, 12, 0, 10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, graphEdges, liste);
        Edge edge0 = Edge.of(graph1, 0, 0, 1);

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        float[] profileTab = {502f, 500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        List<Edge> edges0 = new ArrayList<>();
        edges0.add(edge0);
        SingleRoute singleRoute0 = new SingleRoute(edges0);
        List<Edge> expected0 = edges0;
        List<Edge> actual0 = singleRoute0.edges();
        //TEST 1:
        assertEquals(expected0, actual0);
        List<Edge> edges1 = new ArrayList<>();
        edges1.add(edge0);
        edges1.add(edge1);
        SingleRoute singleRoute1 = new SingleRoute(edges1);
        List<Edge> expected1 = edges1;
        List<Edge> actual1 = singleRoute1.edges();
        //TEST 2:
        assertEquals(expected1, actual1);
    }

    @Test
    public void pointsWorks() {
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges graphEdges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0, 0, 0, 12, 0, 10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, graphEdges, liste);
        Edge edge0 = Edge.of(graph1, 0, 0, 1);

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        float[] profileTab = {502f, 500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        List<Edge> edges0 = new ArrayList<>();
        edges0.add(edge0);
        SingleRoute singleRoute0 = new SingleRoute(edges0);
        List<PointCh> expected0 = new ArrayList<>();
        expected0.add(edge0.fromPoint());
        expected0.add(edge0.toPoint());
        List<PointCh> actual0 = singleRoute0.points();
        //TEST 1:
        assertEquals(expected0, actual0);
        List<Edge> edges1 = new ArrayList<>();
        edges1.add(edge0);
        edges1.add(edge1);
        SingleRoute singleRoute1 = new SingleRoute(edges1);
        List<PointCh> expected1 = new ArrayList<>();
        expected1.add(edge0.fromPoint());
        expected1.add(edge1.fromPoint());
        expected1.add(edge1.toPoint());
        List<PointCh> actual1 = singleRoute1.points();
        //TEST 2:
        assertEquals(expected1, actual1);
    }

    @Test
    public void pointAtWorks() {
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges graphEdges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0, 0, 0, 12, 0, 10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, graphEdges, liste);
        Edge edge0 = Edge.of(graph1, 0, 0, 1);

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        float[] profileTab = {502f, 500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        List<Edge> edges0 = new ArrayList<>();
        edges0.add(edge0);
        SingleRoute singleRoute0 = new SingleRoute(edges0);
        PointCh expected0 = edge0.pointAt(4);
        PointCh actual0 = singleRoute0.pointAt(4);
        //TEST 1:
        assertEquals(expected0, actual0);
        List<Edge> edges1 = new ArrayList<>();
        edges1.add(edge0);
        edges1.add(edge1);
        SingleRoute singleRoute1 = new SingleRoute(edges1);
        PointCh expected1 = edge1.pointAt(4);
        PointCh actual1 = singleRoute1.pointAt(20.6875);
        //TEST 2:
        assertEquals(expected1, actual1);
        PointCh expected2 = edge1.pointAt(5);
        PointCh actual2 = singleRoute1.pointAt(21.6875);
        //TEST 3:
        assertEquals(expected2, actual2);
        PointCh expected3 = edge0.pointAt(0);
        PointCh actual3 = singleRoute1.pointAt(0);
        //TEST 4:
        assertEquals(expected3, actual3);
    }

    @Test
    public void elevationAtWorks() {
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges graphEdges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0, 0, 0, 12, 0, 10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, graphEdges, liste);
        Edge edge0 = Edge.of(graph1, 0, 0, 1);

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        float[] profileTab = {502f, 500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        List<Edge> edges0 = new ArrayList<>();
        edges0.add(edge0);
        SingleRoute singleRoute0 = new SingleRoute(edges0);
        double expected0 = edge0.elevationAt(4);
        double actual0 = singleRoute0.elevationAt(4);
        //TEST 1:
        assertEquals(expected0, actual0);
        List<Edge> edges1 = new ArrayList<>();
        edges1.add(edge0);
        edges1.add(edge1);
        SingleRoute singleRoute1 = new SingleRoute(edges1);
        double expected1 = edge1.elevationAt(4);
        double actual1 = singleRoute1.elevationAt(20.6875);
        //TEST 2:
        assertEquals(expected1, actual1);
        double expected2 = edge1.elevationAt(5);
        double actual2 = singleRoute1.elevationAt(21.6875);
        //TEST 3:
        assertEquals(expected2, actual2);
        double expected3 = edge0.elevationAt(0);
        double actual3 = singleRoute1.elevationAt(0);
        //TEST 4:
        assertEquals(expected3, actual3);
    }

    @Test
    public void nodeClosestToWorks() {
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges graphEdges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0, 0, 0, 12, 0, 10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, graphEdges, liste);
        Edge edge0 = Edge.of(graph1, 0, 0, 1);

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        float[] profileTab = {502f, 500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        List<Edge> edges0 = new ArrayList<>();
        edges0.add(edge0);
        SingleRoute singleRoute0 = new SingleRoute(edges0);
        int expected0 = edge0.fromNodeId();
        int actual0 = singleRoute0.nodeClosestTo(4);
        //TEST 1:
        assertEquals(expected0, actual0);
        List<Edge> edges1 = new ArrayList<>();
        edges1.add(edge0);
        edges1.add(edge1);
        SingleRoute singleRoute1 = new SingleRoute(edges1);
        int expected1 = edge1.toNodeId();
        int actual1 = singleRoute1.nodeClosestTo(20.6875);
        //TEST 2:
        assertEquals(expected1, actual1);
        int expected2 = edge1.toNodeId();
        int actual2 = singleRoute1.nodeClosestTo(21.6875);
        //TEST 3:
        assertEquals(expected2, actual2);
        int expected3 = edge0.fromNodeId();
        int actual3 = singleRoute1.nodeClosestTo(0);
        //TEST 4:
        assertEquals(expected3, actual3);
    }

    @Test
    public void rightPositionWorks() {
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges graphEdges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0, 0, 0, 12, 0, 10});
        GraphSectors sectors1 = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph1 = new Graph(graphNodes1, sectors1, graphEdges, liste);
        Edge edge1 = Edge.of(graph1, 0, 0, 1);

        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        SingleRoute singleRoute = new SingleRoute(edges);
        double position = 100000000;
        //TEST 1: Should return 16.6875
        //singleRoute.rightPosition(position);
        double position1 = -100000000;
        //TEST 2: Should return 0.0
        //singleRoute.rightPosition(position1);
    }

    /* */
    /**
     * Changes the value of the position to 0 if it is a negative value and to
     * the length of the itinerary if it is greater than that
     *
     * @param position a given position on the itinerary
     *//*
    public void rightPosition(double position) {
        if (position < 0) { position = 0;}
        if (position > length()) { position = length();}
        System.out.println(position);
    }*/

    SingleRoute sr1 = new SingleRoute(new ArrayList<>(Arrays.asList(

            new Edge(0, 0, null, null, 10, null),

            new Edge(0, 0, null, null, 10, null),

            new Edge(0, 0, null, null, 10, null),

            new Edge(0, 0, null, null, 10, null),

            new Edge(0, 0, null, null, 10, null),

            new Edge(0, 0, null, null, 10, null),

            new Edge(0, 0, null, null, 10, null),

            new Edge(0, 0, null, null, 10, null),

            new Edge(0, 0, null, null, 10, null),

            new Edge(0, 0, null, null, 10, null),

            new Edge(0, 0, null, null, 10, null),

            new Edge(0, 0, null, null, 10, null)


    )));


    SingleRoute sr2 = new SingleRoute(new ArrayList<>(Arrays.asList(

            new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), 10, null),

            new Edge(1, 2, new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N), 10, null)


    )));


    @Test
    void TestindexOfSegmentAt() {

        assertEquals(0, sr1.indexOfSegmentAt(1000000000));

    }


    @Test
    void Testlength() {

        assertEquals(120, sr1.length());

    }


    @Test
    void Testedges() {

        assertEquals(12, sr1.edges().size());

        assertThrows(UnsupportedOperationException.class, () -> sr1.edges().add(null));

        assertThrows(UnsupportedOperationException.class, () -> sr1.edges().remove(null));

    }

    @Test
    void Testpoints() {

        assertEquals(13, sr1.points().size());

    }

    @Test
    void TestpointAt() {

        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N).toString(), sr2.pointAt(0).toString());

        assertEquals(new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N).toString(), sr2.pointAt(10).toString());

        assertEquals(new PointCh(SwissBounds.MIN_E + 15, SwissBounds.MIN_N).toString(), sr2.pointAt(15).toString());

        assertEquals(new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N).toString(), sr2.pointAt(20).toString());

    }

    @Test
    public void elevationAtTest1() {
        List<Edge> edgeList = new ArrayList<>(List.of(
                new Edge(0, 1, pointCreator(0, 0), pointCreator(2, 0), 2, Functions.constant(0)),
                new Edge(1, 2, pointCreator(2, 0), pointCreator(2, 2), Math.sqrt(2), Functions.constant(0))
        ));
        SingleRoute sr = new SingleRoute(edgeList);

        Assertions.assertEquals(0, sr.elevationAt(0));
        Assertions.assertEquals(0, sr.elevationAt(2));
        Assertions.assertEquals(0, sr.elevationAt(3));
        Assertions.assertEquals(0, sr.elevationAt(4));
        Assertions.assertEquals(0, sr.elevationAt(10));
        Assertions.assertEquals(0, sr.elevationAt(0.3));
        Assertions.assertEquals(0, sr.elevationAt(0.4));
        Assertions.assertEquals(0, sr.elevationAt(0.5));
        for (int i = 0; i < 10000000; i++) {
            Assertions.assertEquals(0, sr.elevationAt(new Random().nextDouble(1000000)));
        }
    }

    @Test
    public void elevationAtNotConstantTest() {
        List<Edge> edgeList = new ArrayList<>(List.of(
                new Edge(0, 1, pointCreator(0, 0), pointCreator(2, 0), 16, Functions.sampled(new float[]{5, 7, 1, 0, 4}, 16)),
                new Edge(1, 2, pointCreator(2, 0), pointCreator(2, 2), 4, Functions.sampled(new float[]{4, 0}, 4))
        ));
        SingleRoute sr = new SingleRoute(edgeList);

        Assertions.assertEquals(4, sr.elevationAt(6));
        Assertions.assertEquals(1, sr.elevationAt(8));
        Assertions.assertEquals(5, sr.elevationAt(0));
        Assertions.assertEquals(0, sr.elevationAt(20));
        Assertions.assertEquals(4, sr.elevationAt(16));
        Assertions.assertEquals(2, sr.elevationAt(18));
    }

    @Test
    public void nodeClosestToTest() {
        List<Edge> edgeList = new ArrayList<>(List.of(
                new Edge(0, 1, pointCreator(0, 0), pointCreator(2, 0), 2, null),
                new Edge(1, 2, pointCreator(2, 0), pointCreator(2, 2), Math.sqrt(2), null)
        ));
        SingleRoute sr = new SingleRoute(edgeList);

        Assertions.assertEquals(0, sr.nodeClosestTo(0));
        Assertions.assertEquals(1, sr.nodeClosestTo(1.5));
        Assertions.assertEquals(0, sr.nodeClosestTo(0.9));
        Assertions.assertEquals(1, sr.nodeClosestTo(2.3));
        Assertions.assertEquals(2, sr.nodeClosestTo(3));
        Assertions.assertEquals(2, sr.nodeClosestTo(4));
        Assertions.assertEquals(2, sr.nodeClosestTo(3));
    }

    @Test
    public void pointClosestToTest() {
        List<Edge> edgeList = new ArrayList<>(List.of(
                new Edge(0, 0, pointCreator(0, 0), pointCreator(2, 0), 2, null),
                new Edge(0, 0, pointCreator(2, 0), pointCreator(2, 2), Math.sqrt(2), null)
        ));
        SingleRoute sr = new SingleRoute(edgeList);

        Assertions.assertEquals(pointCreator(1, 0).toString(), sr.pointClosestTo(pointCreator(1, 1)).point().toString());
        Assertions.assertEquals(pointCreator(0, 0).toString(), sr.pointClosestTo(pointCreator(0, 0)).point().toString());
        Assertions.assertEquals(pointCreator(2, 2).toString(), sr.pointClosestTo(pointCreator(2, 2)).point().toString());
        Assertions.assertEquals(pointCreator(2, 0).toString(), sr.pointClosestTo(pointCreator(2, 0)).point().toString());
        Assertions.assertEquals(pointCreator(2, 2).toString(), sr.pointClosestTo(pointCreator(1000, 1000)).point().toString());

    }

    private PointCh pointCreator(int e, int n) {
        return new PointCh(SwissBounds.MIN_E + e, SwissBounds.MIN_N + n);
    }

    @Test
    void testInitializeSingleRoute(){
        List<Edge> l = new ArrayList<Edge>();

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        l.add(edge1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab2 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab, 5);
        Edge edge2 = new Edge(1, 2, fromPoint2, toPoint2, 5, profile2);

        l.add(edge2);

        SingleRoute s = new SingleRoute(l);

        assertThrows(IllegalArgumentException.class, () -> {
            new SingleRoute(new ArrayList<Edge>());
        });
    }

    @Test
    void testLengthAndEdges(){
        List<Edge> l = new ArrayList<Edge>();
        List<PointCh> pointsList = new ArrayList<PointCh>();

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        //on ajoute pas le toPoint car l'edge2 fromPoint est ce point
        pointsList.add(fromPoint);
        l.add(edge1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab2 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab, 5);
        Edge edge2 = new Edge(1, 2, fromPoint2, toPoint2, 5, profile2);

        l.add(edge2);

        pointsList.add(fromPoint2);
        pointsList.add(toPoint2);

        SingleRoute s = new SingleRoute(l);
        assertEquals(10, s.length());
        assertEquals(l, s.edges());
        assertEquals(pointsList, s.points());


        PointCh fromPoint3 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        PointCh toPoint3 = new PointCh(SwissBounds.MIN_E + 15, SwissBounds.MIN_N);
        float[] profileTab3 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile3 = Functions.sampled(profileTab, 5);
        Edge edge3 = new Edge(2, 1, fromPoint3, toPoint3, 5, profile3);

        l.add(edge3);
        //le fromPoint a deja été ajouté du coup (endPoint de l'edge 2)
        //pointsList.add(fromPoint3);
        pointsList.add(toPoint3);

        //length of s shouldn't have changed even if l was modified
        assertEquals(10, s.length());

        SingleRoute s2 = new SingleRoute(l);
        assertEquals(15, s2.length());
        assertEquals(l, s2.edges());
        assertEquals(pointsList, s2.points());


        //Test pointAt()

        for(int i = 0; i < 15; i++){
            assertEquals(new PointCh(SwissBounds.MIN_E + i, SwissBounds.MIN_N), s2.pointAt(i));
        }

        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), s2.pointAt(-2));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), s2.pointAt(-1));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), s2.pointAt(0));
        assertEquals(new PointCh(SwissBounds.MIN_E + 15, SwissBounds.MIN_N), s2.pointAt(15));
        assertEquals(new PointCh(SwissBounds.MIN_E + 15, SwissBounds.MIN_N), s2.pointAt(16));
        assertEquals(new PointCh(SwissBounds.MIN_E + 15, SwissBounds.MIN_N), s2.pointAt(20));
        assertEquals(new PointCh(SwissBounds.MIN_E + 15, SwissBounds.MIN_N), s2.pointAt(30));
    }


    @Test
    void testElevationAt(){
        List<Edge> l = new ArrayList<Edge>();

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab = {500f, 505f, 510f, 515f, 520f, 525f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        l.add(edge1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab2 = {525f, 530f, 535f, 540f, 545f, 550f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab2, 5);
        Edge edge2 = new Edge(1, 2, fromPoint2, toPoint2, 5, profile2);

        l.add(edge2);

        SingleRoute s = new SingleRoute(l);

        for(int i = 0; i < 10; i++){
            assertEquals(500 + 5*i, s.elevationAt(i));
        }

        assertEquals(500, s.elevationAt(0));
        assertEquals(500, s.elevationAt(-2));
        assertEquals(500, s.elevationAt(-10));
        assertEquals(550, s.elevationAt(10));
        assertEquals(550, s.elevationAt(11));


        assertEquals(550, s.elevationAt(14));
        assertEquals(550, s.elevationAt(100000));

        PointCh fromPoint3 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        PointCh toPoint3 = new PointCh(SwissBounds.MIN_E + 15, SwissBounds.MIN_N);
        float[] profileTab3 = new float[]{};
        DoubleUnaryOperator profile3 =Functions.constant(Double.NaN);
        Edge edge3 = new Edge(2, 1, fromPoint3, toPoint3, 5, profile3);

        l.add(edge3);

        SingleRoute s2 = new SingleRoute(l);

        //edge3 length is 5 (10 - 15)
        for(double i = 10.0; i < 18; i += 0.1){
            assertEquals(Double.NaN, s2.elevationAt(i));
        }
    }

    @Test
    void testNodeClosestTo(){
        List<Edge> l = new ArrayList<Edge>();

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab = {500f, 505f, 510f, 515f, 520f, 525f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        l.add(edge1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab2 = {525f, 530f, 535f, 540f, 545f, 550f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab2, 5);
        Edge edge2 = new Edge(1, 2, fromPoint2, toPoint2, 5, profile2);

        l.add(edge2);

        SingleRoute s = new SingleRoute(l);

        assertEquals(2, s.nodeClosestTo(10));
        assertEquals(2, s.nodeClosestTo(10.1));
        assertEquals(2, s.nodeClosestTo(12));
        assertEquals(2, s.nodeClosestTo(20));
        assertEquals(2, s.nodeClosestTo(100));

        for(double i = 0; i < 10.0; i += 0.1){
            //System.out.println("i=" + i + " closest=" + s.nodeClosestTo(i));
            if(i <= 2.5){
                assertEquals(0, s.nodeClosestTo(i));
            }else if(i <= 7.5){
                assertEquals(1, s.nodeClosestTo(i));
            }else{
                assertEquals(2, s.nodeClosestTo(i));
            }
        }

        //according to the assistant, when we are at half way we should chose left node
        assertEquals(0, s.nodeClosestTo(2.5));
        assertEquals(1, s.nodeClosestTo(7.5));

        assertEquals(0, s.nodeClosestTo(-1));
        assertEquals(0, s.nodeClosestTo(0));
        assertEquals(0, s.nodeClosestTo(-6));
        assertEquals(0, s.nodeClosestTo(-1000));
    }

    @Test
    void testNearestPoint(){
        List<Edge> l = new ArrayList<Edge>();

        PointCh fromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab = {500f, 505f, 510f, 515f, 520f, 525f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge1 = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        l.add(edge1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab2 = {525f, 530f, 535f, 540f, 545f, 550f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab2, 5);
        Edge edge2 = new Edge(1, 2, fromPoint2, toPoint2, 5, profile2);

        l.add(edge2);

        SingleRoute s = new SingleRoute(l);

        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 1, SwissBounds.MIN_N), 1, 1), s.pointClosestTo(new PointCh(SwissBounds.MIN_E + 1, SwissBounds.MIN_N + 1)));
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 2, SwissBounds.MIN_N), 2, 10), s.pointClosestTo(new PointCh(SwissBounds.MIN_E + 2, SwissBounds.MIN_N + 10)));

        //Math.sqrt(104) car distanceToReferee au carré = 2*2 + 10*10
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), 10, Math.sqrt(104)), s.pointClosestTo(new PointCh(SwissBounds.MIN_E + 12, SwissBounds.MIN_N + 10)));

        //testing with point already on the edge
        for(int i = 0; i <= 50; i++){
            if(i <= 10){
                assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + i, SwissBounds.MIN_N), i, 0), s.pointClosestTo(new PointCh(SwissBounds.MIN_E + i, SwissBounds.MIN_N)));
            }else{
                assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), 10, i - 10), s.pointClosestTo(new PointCh(SwissBounds.MIN_E + i, SwissBounds.MIN_N)));
            }
        }

        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N), 5, 5), s.pointClosestTo(new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N + 5)));
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), 10, 10), s.pointClosestTo(new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N)));
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 0, 3), s.pointClosestTo(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 3)));
    }


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
    void pointAtWorks1() {
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
    void edgesWorks1() {
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

    private List<Edge> edges = new ArrayList<>();
    private List<PointCh> points = new ArrayList<>();
    SingleRoute route = route();

    private SingleRoute route() {
        float[] samples1 = new float[]{200, 100, 150, 250,300};
        DoubleUnaryOperator profile1 = Functions.sampled(samples1, 100 * sqrt(2));
        Edge edge1 = new Edge(0, 4,
                new PointCh(2500100, 1100100), new PointCh(2500200, 1100200),
                100 * sqrt(2), profile1);
        points1.add(new PointCh(2500100, 1100100));

        float[] samples2 = new float[]{0, 200, 100, 400,0};
        PointCh from2 = new PointCh(2500200, 1100200);
        PointCh to2 = new PointCh(2500000, 1100000);
        DoubleUnaryOperator profile2 = Functions.sampled(samples2, from2.distanceTo(to2));
        Edge edge2 = new Edge(4, 7,
                from2, to2, from2.distanceTo(to2), profile2);
        points1.add(from2);
        points1.add(to2);

        float[] samples3 = new float[]{300, 100, 200, 100,200};
        PointCh from3 = new PointCh(2500000, 1100000);
        PointCh to3 = new PointCh(2500400, 1100400);
        DoubleUnaryOperator profile3 = Functions.sampled(samples3, from3.distanceTo(to3));
        Edge edge3 = new Edge(7, 5,
                from3, to3, from3.distanceTo(to3), profile3);
        points1.add(to3);

        edges1.add(edge1);
        edges1.add(edge2);
        edges1.add(edge3);
        SingleRoute route1 = new SingleRoute(edges1);
        return route1;
    }


    @Test
    void indexOfSegmentAtTest() {
        assertEquals(0, route.indexOfSegmentAt(100000));
    }

    @Test
    void lengthTest() {
        assertEquals((100 + 200 + 400) * sqrt(2), route.length());
    }

    @Test
    void edgesTest() {
        assertIterableEquals(edges1, route.edges());
    }

    @Test
    void pointsTest() {
        assertEquals(points1, route.points());
    }

    //PLUSIEURS TESTS QUI NE PASSENT PAS
    @Test
    void pointAtTest() {
        PointCh expected = new PointCh(2500175, 1100175);
        PointCh actual = route.pointAt(75 * sqrt(2));
        assertEquals(expected.e(), actual.e());
        assertEquals(expected.n(), actual.n());
        for (int i = 100; i <= 300; i += 5) {
            int p = i - 100;
            expected = new PointCh(2500200 - p, 1100200 - p);
            actual = route.pointAt(i * sqrt(2));
            assertEquals(expected.e(), actual.e());
            assertEquals(expected.n(), actual.n());
        }
        for (int i = 300; i <= 700; i += 5) {
            int p = i - 300;
            expected = new PointCh(2500000 + p, 1100000 + p);
            actual = route.pointAt(i * sqrt(2));
            assertEquals(expected.e(), actual.e());
            assertEquals(expected.n(), actual.n());
        }

        // can the position ecceed the lenght? what to do in that case
        expected = new PointCh(2500500, 1100500);
        actual = route.pointAt(800 * sqrt(2));
        //System.out.println(800 * sqrt(2));
        //assertEquals(expected.e(), actual.e());
        //assertEquals(expected.n(), actual.n());

        expected = new PointCh(2500100, 1100100);
        actual = route.pointAt(0);
        assertEquals(expected.e(), actual.e());
        assertEquals(expected.n(), actual.n());

        expected = new PointCh(2500000, 1100000);
        actual = route.pointAt(-100 * sqrt(2));
        System.out.println(-100 * sqrt(2));
        //assertEquals(expected.e(), actual.e());
        //assertEquals(expected.n(), actual.n());

        expected = new PointCh(2500700, 1100700);
        actual = route.pointAt(1000 * sqrt(2));
        System.out.println(1000 * sqrt(2));
        //assertEquals(expected.e(), actual.e());
        //assertEquals(expected.n(), actual.n());

    }

    @Test
    void elevationAtTest() throws IOException {
        List<Edge> edges2=new ArrayList<>();
        DoubleUnaryOperator profile2= Functions.constant(Double.NaN);
        edges2.add(new Edge(0,2,
                new PointCh(2500700, 1100700),new PointCh(2500500, 1100500),
                200*sqrt(2),profile2));
        SingleRoute route2=new SingleRoute(edges2);
        assertEquals(Double.NaN,route2.elevationAt(100));
        assertEquals(Double.NaN,route2.elevationAt(0));
        assertEquals(Double.NaN,route2.elevationAt(200));


        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        List<Edge> edges3=new ArrayList<>();
        //Pont Charles Bessières Lausanne
        edges3.add(new Edge(129480,129415,graph.nodePoint(129480),graph.nodePoint(129415),83.3,graph.edgeProfile(276838)));
        SingleRoute route3=new SingleRoute(edges3);

        assertEquals(Double.NaN,route3.elevationAt(50));
        assertEquals(Double.NaN,route3.elevationAt(0));
        assertEquals(Double.NaN,route3.elevationAt(-10));
        assertEquals(Double.NaN,route3.elevationAt(100));


        double expected = 250;
        double actual = route.elevationAt(75 * sqrt(2));
        assertEquals(expected, actual);

        expected = 200;
        actual = route.elevationAt(-25 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 200;
        actual = route.elevationAt(-1);
        assertEquals(expected, actual,10e-6);

        expected = 150;
        actual = route.elevationAt(50 * sqrt(2));
        assertEquals(expected, actual);

        expected = 200.0;
        actual = route.elevationAt(62.5 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 275;
        actual = route.elevationAt(87.5 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 287.5;
        actual = route.elevationAt(93.75 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 300;
        actual = route.elevationAt(99.999999999 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 0;
        actual = route.elevationAt(100 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 100;
        actual = route.elevationAt(125 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 150;
        actual = route.elevationAt(137.5 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 150;
        actual = route.elevationAt(175 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 100;
        actual = route.elevationAt(200 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 200;
        actual = route.elevationAt(275 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 300;
        actual = route.elevationAt(300 * sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 200;
        actual = route.elevationAt( 350* sqrt(2));
        assertEquals(expected, actual,10e-6);

        expected = 200;
        actual = route.elevationAt( 1000* sqrt(2));
        assertEquals(expected, actual,10e-6);
    }

    @Test
    void nodeClosestToTest1() {

        int expected = 0;
        int actual = route.nodeClosestTo(25 * sqrt(2));
        assertEquals(expected, actual);

        expected = 0;
        actual = route.nodeClosestTo(-25 * sqrt(2));
        assertEquals(expected, actual);

        expected = 0;
        actual = route.nodeClosestTo(-1);
        assertEquals(expected, actual);

        expected = 0;
        actual = route.nodeClosestTo(50 * sqrt(2));
        assertEquals(expected, actual);

        expected = 4;
        actual = route.nodeClosestTo(62.5 * sqrt(2));
        assertEquals(expected, actual);

        expected = 4;
        actual = route.nodeClosestTo(87.5 * sqrt(2));
        assertEquals(expected, actual);

        expected = 4;
        actual = route.nodeClosestTo(93.75 * sqrt(2));
        assertEquals(expected, actual);

        expected = 4;
        actual = route.nodeClosestTo(99.999999999 * sqrt(2));
        assertEquals(expected, actual);

        expected = 4;
        actual = route.nodeClosestTo(100 * sqrt(2));
        assertEquals(expected, actual);

        expected = 4;
        actual = route.nodeClosestTo(125 * sqrt(2));
        assertEquals(expected, actual);

        expected = 4;
        actual = route.nodeClosestTo(137.5 * sqrt(2));
        assertEquals(expected, actual);

        expected = 4;
        actual = route.nodeClosestTo(175 * sqrt(2));
        assertEquals(expected, actual);

        expected = 4;
        actual = route.nodeClosestTo(200 * sqrt(2));
        assertEquals(expected, actual);

        expected = 7;
        actual = route.nodeClosestTo(275 * sqrt(2));
        assertEquals(expected, actual);

        expected = 7;
        actual = route.nodeClosestTo(300 * sqrt(2));
        assertEquals(expected, actual);

        expected = 7;
        actual = route.nodeClosestTo( 350* sqrt(2));
        assertEquals(expected, actual);

        expected = 5;
        actual = route.nodeClosestTo( 1000* sqrt(2));
        assertEquals(expected, actual);

        expected = 5;
        actual = route.nodeClosestTo( 675* sqrt(2));
        assertEquals(expected, actual);
    }

    //PLUSIEURS TESTS QUI NE PASSENT PAS
    //@Test
   /* void pointClosestToTest() {
        RoutePoint expected = new RoutePoint(new PointCh(2500100, 1100100), 0, 0);
        RoutePoint actual = route.pointClosestTo(new PointCh(2500100, 1100100));
        assertEquals(expected, actual);

        expected = new RoutePoint(new PointCh(2500150, 1100150), 50*sqrt(2), 50*sqrt(2));
        actual = route.pointClosestTo(new PointCh(2500200, 1100100));
        assertEquals(expected, actual);

        expected = new RoutePoint(new PointCh(2500050, 1100050), 100*sqrt(2)+150*sqrt(2), 0);
        actual = route.pointClosestTo(new PointCh(2500050, 1100050));
        assertEquals(expected.point(), actual.point());
        assertEquals(expected.position(), actual.position(),10e-6);
        assertEquals(expected.distanceToReference(), actual.distanceToReference());

        expected = new RoutePoint(new PointCh(2500175, 1100175), 75*sqrt(2), 25*sqrt(2));
        actual = route.pointClosestTo(new PointCh(2500150, 1100200));
        assertEquals(expected.point(), actual.point());
        assertEquals(expected.position(), actual.position(),10e-6);
        assertEquals(expected.distanceToReference(), actual.distanceToReference());

        expected = new RoutePoint(new PointCh(2500200, 1100200), 100*sqrt(2), 0);
        actual = route.pointClosestTo(new PointCh(2500200, 1100200));
        assertEquals(expected.point(), actual.point());
        assertEquals(expected.position(), actual.position(),10e-6);
        assertEquals(expected.distanceToReference(), actual.distanceToReference());

        expected = new RoutePoint(new PointCh(2500100, 1100100), 0, 100*sqrt(2));
        actual = route.pointClosestTo(new PointCh(2500200, 1100000));
        assertEquals(expected.point(), actual.point());
        assertEquals(expected.position(), actual.position(),10e-6);
        assertEquals(expected.distanceToReference(), actual.distanceToReference());

        expected = new RoutePoint(new PointCh(2500005, 1100005), 100*sqrt(2)+195*sqrt(2), 5*sqrt(2));
        actual = route.pointClosestTo(new PointCh(2500000, 1100010));
        assertEquals(expected.point(), actual.point());
        assertEquals(expected.position(), actual.position(),10e-6);
        assertEquals(expected.distanceToReference(), actual.distanceToReference());

        expected = new RoutePoint(new PointCh(2500010, 1100010), 100*sqrt(2)+190*sqrt(2), 10*sqrt(2));
        actual = route.pointClosestTo(new PointCh(2500020, 1100000));
        assertEquals(expected.point(), actual.point());
        assertEquals(expected.position(), actual.position(),10e-6);
        assertEquals(expected.distanceToReference(), actual.distanceToReference());

        expected = new RoutePoint(new PointCh(2500300, 1100300), 300*sqrt(2)+300*sqrt(2), 100*sqrt(2));
        actual = route.pointClosestTo(new PointCh(2500200, 1100400));
        assertEquals(expected.point(), actual.point());
        assertEquals(expected.position(), actual.position(),10e-6);
        assertEquals(expected.distanceToReference(), actual.distanceToReference());

        expected = new RoutePoint(new PointCh(2500400, 1100400), 700*sqrt(2), 200*sqrt(2));
        actual = route.pointClosestTo(new PointCh(2500600, 1100600));
        assertEquals(expected.point(), actual.point());
        assertEquals(expected.position(), actual.position(),10e-6);
        assertEquals(expected.distanceToReference(), actual.distanceToReference());

        expected = new RoutePoint(new PointCh(2500400, 1100400), 700*sqrt(2), 200);
        actual = route.pointClosestTo(new PointCh(2500600, 1100400));
        assertEquals(expected.point(), actual.point());
        assertEquals(expected.position(), actual.position(),10e-6);
        assertEquals(expected.distanceToReference(), actual.distanceToReference());

        expected = new RoutePoint(new PointCh(2500400, 1100400), 700*sqrt(2), new PointCh(2500400, 1100400).distanceTo(new PointCh(2500600, 1100500)));
        actual = route.pointClosestTo(new PointCh(2500600, 1100500));
        assertEquals(expected.point(), actual.point());
        assertEquals(expected.position(), actual.position(),10e-6);
        assertEquals(expected.distanceToReference(), actual.distanceToReference());
    }*/

    //Mimi

    PointCh p0 = new PointCh(2600100, 1200400);
    PointCh p1 = new PointCh(2600123, 1200456);
    PointCh p2 = new PointCh(2600456, 1200789);
    PointCh p3 = new PointCh(2600789, 1200123);
    PointCh p4 = new PointCh(2601000, 1201000);

    Edge myEdge1 = new Edge(1, 2, p1, p2, p1.distanceTo(p2), x -> Double.NaN);
    Edge myEdge2 = new Edge(2, 3, p2, p3, p2.distanceTo(p3), x -> Math.sin(x));
    Edge edgeBeforeRoute = new Edge(3, 4, p0, p1, p0.distanceTo(p1), x -> Double.NaN);



    /*public SingleRoute ourRoute() {

        List<Edge> edges = new ArrayList<>();
        edges.add(myEdge1);
        edges.add(myEdge2);

        return new SingleRoute(edges);
    }

    @Test
    void indexOfSegmentAtMimi() {
        int expected = 0;
        int actual1 = ourRoute().indexOfSegmentAt(23456);
        int actual2 = ourRoute().indexOfSegmentAt(2343456);
        assertEquals(expected, actual1, "ATTENTION LÀÀÀÀ OH");
        assertEquals(expected, actual2, "ATTENTION LÀÀÀÀ OH");

    }

    @Test
    void constructorThrowsRightExceptionMimi() {
        List<Edge> edgesVide = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> {
            SingleRoute route1 = new SingleRoute(edgesVide);
        });
    }

    @Test
    void lengthMimi() {
        double expected = 1215.5437527776;
        double actual = ourRoute().length();
        assertEquals(expected,actual,Math.pow(10,-5));
    }

    *//*@Test
    void edges() {
        PointCh p1 = new PointCh(2600123, 1200456);
        PointCh p2 = new PointCh(2600456, 1200789);
        PointCh p3 = new PointCh(2600789, 1200123);
        PointCh p4 = new PointCh(2601000, 1201000);

        Edge edge1 = new Edge(1, 2, p1, p2, p1.distanceTo(p2), x -> Double.NaN);
        Edge edge2 = new Edge(3, 4, p3, p4, p3.distanceTo(p4), x -> Double.NaN);
        List<Edge> expected = new ArrayList<>();
        expected.add(edge1);
        expected.add(edge2);

        List<Edge> actual = ourRoute().edges();
        for (int i = 0; i < actual.size(); i++) {
            System.out.println(expected.get(i));
            System.out.println(actual.get(i));
            assertEquals(expected.get(i),actual.get(i));
        }
    }*//*

    @Test
    void pointsMimi() {
        List<PointCh> expected = new ArrayList<>();
        expected.add(p1);
        expected.add(p2);
        expected.add(p3);

        assertEquals(expected, ourRoute().points(), "lolilol c faux");

    }

    @Test
    void pointAtMimi() {

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
    void elevationAtMimi() {
        SingleRoute r = ourRoute();

        double actual1 = r.elevationAt(myEdge1.length()/2d);
        double actual2 = r.elevationAt(myEdge1.length()+(myEdge2.length()/3d));
        //System.out.println(myEdge2.elevationAt(myEdge2.length()/3d));

        assertTrue(Double.isNaN(actual1));
        assertEquals(Math.sin(myEdge2.length()/3d), actual2, Math.pow(10, -5));
    }

    @Test
    void nodeClosestToMimi() {
        // limit points (at the edges' extremity)
        assertEquals(1, ourRoute().nodeClosestTo(0));
        assertEquals(2, ourRoute().nodeClosestTo(myEdge1.length()));
        assertEquals(3, ourRoute().nodeClosestTo(ourRoute().length()));
        // testing intermediate points :
    }

    @Test
    public void pointAtShit() {
        Edge e0 = new Edge(0,0,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 100),100, null);
        Edge e1 = new Edge(0,0,e0.pointAt(100),e0.pointAt(200),100, null);
        System.out.println(e0.pointAt(100));
        System.out.println(e0.pointAt(200));
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        SingleRoute s = new SingleRoute(l);
        PointCh pointCh = e0.pointAt(200);
        System.out.println(e0.pointAt(200));
        PointCh p = pointCh;
        assertEquals(p.e(), s.pointAt(200).e(), 3);
        assertEquals(p.n(), s.pointAt(200).n(), 3);
    }*/

    @Test
    public void pointAtWorks4() {
        double[] a = new double[]{0, 5800, 8100, 9200, 11_400, 13_100};
        Edge e0 = new Edge(0,0,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 199, SwissBounds.MIN_N + 199),5800, null);
        Edge e1 = new Edge(0,0,e0.pointAt(100),e0.pointAt(200),2300, null);
        Edge e2 = new Edge(0,0,e1.pointAt(200),e1.pointAt(300),1100, null);
        Edge e3 = new Edge(0,0,e2.pointAt(300),e2.pointAt(400),2200, null);
        Edge e4 = new Edge(0,0,e3.pointAt(400),e3.pointAt(500),1700, null);
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        PointCh p = e2.pointAt(10_000);
        assertEquals(p.e(), s.pointAt(10_000).e(), 3);
        assertEquals(p.n(), s.pointAt(10_000).n(), 3);
    }

    @Test
    public void pointAtWorks2() {
        double[] a = new double[]{0, 5800, 8100, 9200, 11_400, 13_100};
        Edge e0 = new Edge(0,0,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 199, SwissBounds.MIN_N + 199),5800, null);
        Edge e1 = new Edge(0,0,e0.pointAt(100),e0.pointAt(200),2300, null);
        Edge e2 = new Edge(0,0,e1.pointAt(200),e1.pointAt(300),1100, null);
        Edge e3 = new Edge(0,0,e2.pointAt(300),e2.pointAt(400),2200, null);
        Edge e4 = new Edge(0,0,e3.pointAt(400),e3.pointAt(500),1700, null);
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        PointCh p = e2.pointAt(1);
        assertEquals(p.e(), s.pointAt(1).e(), 4);
        assertEquals(p.n(), s.pointAt(1).n(), 4);
    }

    @Test
    public void elevationAtReturnsNaNIfNoProfile(){
        double[] a = new double[]{0, 5800, 8100, 9200, 11_400, 13_100};
        Edge e0 = new Edge(0,0,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 199, SwissBounds.MIN_N + 199),5800,
                Functions.constant(Double.NaN));
        Edge e1 = new Edge(0,0,e0.pointAt(100),e0.pointAt(200),2300,
                Functions.constant(Double.NaN));
        Edge e2 = new Edge(0,0,e1.pointAt(200),e1.pointAt(300),1100,
                Functions.constant(Double.NaN));
        Edge e3 = new Edge(0,0,e2.pointAt(300),e2.pointAt(400),2200,
                Functions.constant(Double.NaN));
        Edge e4 = new Edge(0,0,e3.pointAt(400),e3.pointAt(500),1700,
                Functions.constant(Double.NaN));
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        PointCh p = e2.pointAt(10_000);
        assertTrue(isNaN(s.elevationAt(10_000)));
    }

    @Test
    public void elevationAtReturnsAltitudeAtGivenPosition() {
        double[] a = new double[]{0, 5800, 8100, 9200, 11_400, 13_100};
        Edge e0 = new Edge(0, 0, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 199, SwissBounds.MIN_N + 199), 5800,
                Functions.constant(4589));
        Edge e1 = new Edge(0, 0, e0.pointAt(100), e0.pointAt(200), 2300,
                Functions.constant(234));
        Edge e2 = new Edge(0, 0, e1.pointAt(200), e1.pointAt(300), 1100,
                Functions.constant(643));
        Edge e3 = new Edge(0, 0, e2.pointAt(300), e2.pointAt(400), 2200,
                Functions.constant(0));
        Edge e4 = new Edge(0, 0, e3.pointAt(400), e3.pointAt(500), 1700,
                Functions.constant(2.3));
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        PointCh p = e2.pointAt(10_000);
        double expected = 234;
        assertEquals(expected, s.elevationAt(5_900));
    }

    @Test
    public void elevationAtWorksWithNonConstantAltitudesOnInterval() {

        float[] elevationSamples = new float[]{384.75f, 384.6875f, 384.5625f, 384.5f};
        Edge e0 = new Edge(0,1,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 199, SwissBounds.MIN_N + 199),5800,
                Functions.constant(Double.NaN));
        Edge e1 = new Edge(1,2,e0.pointAt(100),e0.pointAt(200),2300,
                Functions.sampled(elevationSamples, 2300));
        Edge e2 = new Edge(2,3,e1.pointAt(200),e1.pointAt(300),1100,
                Functions.constant(Double.NaN));
        Edge e3 = new Edge(4,5,e2.pointAt(300),e2.pointAt(400),2200,
                Functions.constant(Double.NaN));
        Edge e4 = new Edge(5,6,e3.pointAt(400),e3.pointAt(500),1700,
                Functions.constant(Double.NaN));
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        float expected = 384.7418f;
        assertEquals(expected, s.elevationAt(5900), 1e-3);
    }

    @Test
    public void pointClosestToWorksOnTrivialPoint() {
        PointCh p = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        RoutePoint rp = new RoutePoint(p, 0, 0);
        Edge e0 = new Edge(0,1,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 1, SwissBounds.MIN_N + 1),5800,
                Functions.constant(Double.NaN));
        Edge e1 = new Edge(1,2,e0.pointAt(100),e0.pointAt(200),2300,
                Functions.constant(Double.NaN));
        Edge e2 = new Edge(2,3,e1.pointAt(200),e1.pointAt(300),1100,
                Functions.constant(Double.NaN));
        Edge e3 = new Edge(4,5,e2.pointAt(300),e2.pointAt(400),2200,
                Functions.constant(Double.NaN));
        Edge e4 = new Edge(5,6,e3.pointAt(400),e3.pointAt(500),1700,
                Functions.constant(Double.NaN));
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        assertEquals(rp, s.pointClosestTo(p));
    }

    @Test
    public void pointClosestToWorksOnHorizontalItinerary() {
        PointCh p = new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N + 300);
        PointCh pp = new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N);

        RoutePoint rp = new RoutePoint(pp, 0, p.distanceTo(pp));

        Edge e0 = new Edge(0,1,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N),1000,
                Functions.constant(Double.NaN));
        Edge e1 = new Edge(1,2,new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 200, SwissBounds.MIN_N),1000,
                Functions.constant(Double.NaN));
        Edge e2 = new Edge(2,3,new PointCh(SwissBounds.MIN_E + 200, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N),1000,
                Functions.constant(Double.NaN));
        Edge e3 = new Edge(4,5,new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 400, SwissBounds.MIN_N),1000,
                Functions.constant(Double.NaN));
        Edge e4 = new Edge(5,6,new PointCh(SwissBounds.MIN_E + 400, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N),1000,
                Functions.constant(Double.NaN));
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        assertEquals(rp, s.pointClosestTo(p));
    }

    @Test
    public void pointClosestToWorksOnDiagonalItinerary1() {
        PointCh p = new PointCh(SwissBounds.MIN_E + 250, SwissBounds.MIN_N + 350);
        PointCh pp = new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N + 300);

        RoutePoint rp = new RoutePoint(pp, 0, p.distanceTo(pp));

        Edge e0 = new Edge(0,1,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 100),1000,
                Functions.constant(Double.NaN));
        Edge e1 = new Edge(1,2,new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 100),
                new PointCh(SwissBounds.MIN_E + 200, SwissBounds.MIN_N + 200),1000,
                Functions.constant(Double.NaN));
        Edge e2 = new Edge(2,3,new PointCh(SwissBounds.MIN_E + 200, SwissBounds.MIN_N + 200),
                new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N + 300),1000,
                Functions.constant(Double.NaN));
        Edge e3 = new Edge(4,5,new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N + 300),
                new PointCh(SwissBounds.MIN_E + 400, SwissBounds.MIN_N + 400),1000,
                Functions.constant(Double.NaN));
        Edge e4 = new Edge(5,6,new PointCh(SwissBounds.MIN_E + 400, SwissBounds.MIN_N + 400),
                new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 500),1000,
                Functions.constant(Double.NaN));
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        assertEquals(rp, s.pointClosestTo(p));
    }

    @Test
    public void pointClosestToWorksOnDiagonalItinerary2() {
        PointCh p = new PointCh(SwissBounds.MIN_E + 220, SwissBounds.MIN_N + 350);
        PointCh pp = new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N + 300);

        RoutePoint rp = new RoutePoint(pp, 424.24, p.distanceTo(pp));

        System.out.println((new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N).distanceTo(pp)));

        Edge e0 = new Edge(0,1,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 100),1000,
                Functions.constant(Double.NaN));
        Edge e1 = new Edge(1,2,new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 100),
                new PointCh(SwissBounds.MIN_E + 200, SwissBounds.MIN_N + 200),1000,
                Functions.constant(Double.NaN));
        Edge e2 = new Edge(2,3,new PointCh(SwissBounds.MIN_E + 200, SwissBounds.MIN_N + 200),
                new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N + 300),1000,
                Functions.constant(Double.NaN));
        Edge e3 = new Edge(4,5,new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N + 300),
                new PointCh(SwissBounds.MIN_E + 400, SwissBounds.MIN_N + 400),1000,
                Functions.constant(Double.NaN));
        Edge e4 = new Edge(5,6,new PointCh(SwissBounds.MIN_E + 400, SwissBounds.MIN_N + 400),
                new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 500),1000,
                Functions.constant(Double.NaN));
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        System.out.println(s.pointClosestTo(p).point());
        assertEquals(rp.point(), s.pointClosestTo(p).point());
        //Test ne passe pas:
        assertEquals(rp.position(), s.pointClosestTo(p).position(), 1e-2);
        assertEquals(rp.distanceToReference(), s.pointClosestTo(p).distanceToReference());
    }

    //Last test added that i didnt run
    /*@Test
    public void pointClosestToWorksOnDiagonalItinerary2() {
        PointCh p = new PointCh(SwissBounds.MIN_E + 220, SwissBounds.MIN_N + 350);
        PointCh pp = new PointCh(SwissBounds.MIN_E + 285, SwissBounds.MIN_N + 285);

        RoutePoint rp = new RoutePoint(pp, 403, 92);

        System.out.println((new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N).distanceTo(pp)));

        Edge e0 = new Edge(0,1,new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 100),100sqrt(2), Functions.constant(Double.NaN));
        Edge e1 = new Edge(1,2,new PointCh(SwissBounds.MIN_E + 100, SwissBounds.MIN_N + 100), new PointCh(SwissBounds.MIN_E + 200, SwissBounds.MIN_N + 200),100sqrt(2), Functions.constant(Double.NaN));
        Edge e2 = new Edge(2,3,new PointCh(SwissBounds.MIN_E + 200, SwissBounds.MIN_N + 200),
                new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N + 300),100sqrt(2),
                Functions.constant(Double.NaN));
        Edge e3 = new Edge(4,5,new PointCh(SwissBounds.MIN_E + 300, SwissBounds.MIN_N + 300),
                new PointCh(SwissBounds.MIN_E + 400, SwissBounds.MIN_N + 400),100sqrt(2),
                Functions.constant(Double.NaN));
        Edge e4 = new Edge(5,6,new PointCh(SwissBounds.MIN_E + 400, SwissBounds.MIN_N + 400),
                new PointCh(SwissBounds.MIN_E + 500, SwissBounds.MIN_N + 500),100*sqrt(2),
                Functions.constant(Double.NaN));
        List<Edge> l = new ArrayList<>();
        l.add(e0);
        l.add(e1);
        l.add(e2);
        l.add(e3);
        l.add(e4);
        SingleRoute s = new SingleRoute(l);
        assertEquals(rp.point(), s.pointClosestTo(p).point());
        assertEquals(rp.position(), s.pointClosestTo(p).position(), 1);
        assertEquals(rp.distanceToReference(), s.pointClosestTo(p).distanceToReference(), 1);
    }*/

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

    List<Edge> edges1;
    List<PointCh> points1;
    SingleRoute singleRoute;

    private void create() {

        points1 = new ArrayList<>();
        points1.add(point1);
        points1.add(point2);
        points1.add(point3);
        points1.add(point4);
        points1.add(point5);
        points1.add(point6);

        edges1 = new ArrayList<>();
        edges1.add(edge1);
        edges1.add(edge2);
        edges1.add(edge3);
        edges1.add(edge4);
        edges1.add(edge5);

        singleRoute = new SingleRoute(edges1);
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

        assertTrue(expectedEdges.equals(edges1));
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

        assertTrue(expectedPoints.equals(points1));
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
        // assertEquals(route4, singleRoute.pointClosestTo(singleRoute.pointAt(555)));
        assertEquals(route5, singleRoute.pointClosestTo(singleRoute.pointAt(614)));
        assertEquals(route6, singleRoute.pointClosestTo(singleRoute.pointAt(800)));

    }


    // tests mimi et marwa ->

    PointCh p01 = new PointCh(2600100, 1200400);
    PointCh p11 = new PointCh(2600123, 1200456);
    PointCh p21 = new PointCh(2600456, 1200789);
    PointCh p31 = new PointCh(2600789, 1200123);
    PointCh p41 = new PointCh(2601000, 1201000);

    Edge myEdge11 = new Edge(1, 2, p11, p21, p11.distanceTo(p21), x -> Double.NaN);
    Edge myEdge21 = new Edge(2, 3, p21, p31, p21.distanceTo(p31), x -> Math.sin(x));
    Edge edgeBeforeRoute1 =  new Edge(3, 4, p01, p11, p01.distanceTo(p11), x -> Double.NaN);

    private SingleRoute ourRoute() {

        List<Edge> edges = new ArrayList<>();
        edges.add(myEdge11);
        edges.add(myEdge21);

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
        expected.add(p11);
        expected.add(p21);
        expected.add(p31);

        assertEquals(expected, ourRoute().points(), "lolilol c faux");

    }

    @Test
    void pointAtMM() {

        PointCh pInter = myEdge11.pointAt(myEdge11.length()/2d); // expected intermediate point :
        PointCh actualInter = ourRoute().pointAt(myEdge11.length()/2d); // actual
        PointCh actual1 = ourRoute().pointAt(0); // the point at the beginning of our SingleRoute
        PointCh actual2 = ourRoute().pointAt(myEdge11.length()); //  the point between the first and second edges on our SingleRoute
        PointCh actual3 = ourRoute().pointAt(myEdge11.length() + myEdge21.length()); // the point at the edge of our SingleRoute
        PointCh actual3bis = ourRoute().pointAt(ourRoute().length()); // just another way to compute the point at the end of the route

        assertEquals(p11,actual1, "oh no…our code…it's broken");
        assertEquals(p21,actual2, "oh no…our code…it's broken");
        assertEquals(p31,actual3, "oh no…our code…it's broken");
        assertEquals(p31, actual3bis, "oh no…our code…it's broken");
        assertEquals(pInter, actualInter, "oh no…our code…it's broken");

    }

    @Test
    void pointAtWorksWithPointsBeyondRoute() {
        PointCh pInter = myEdge11.pointAt(myEdge11.length()/2d); // expected intermediate point :
        PointCh actualInter = ourRoute().pointAt(myEdge11.length()/2d); // actual
        PointCh actual1 = ourRoute().pointAt(0); // the point at the beginning of our SingleRoute
        PointCh actual2 = ourRoute().pointAt(myEdge11.length()); //  the point between the first and second edges on our SingleRoute
        PointCh actual3 = ourRoute().pointAt(myEdge11.length() + myEdge21.length()); // the point at the edge of our SingleRoute
        PointCh actual3bis = ourRoute().pointAt(ourRoute().length()); // just another way to compute the point at the end of the route

        assertEquals(p11,actual1, "oh no…our code…it's broken");
        assertEquals(p21,actual2, "oh no…our code…it's broken");
        assertEquals(p31,actual3, "oh no…our code…it's broken");
        assertEquals(p31, actual3bis, "oh no…our code…it's broken");
        assertEquals(pInter, actualInter, "oh no…our code…it's broken");
    }

    @Test
    void elevationAtMM() {
        SingleRoute r = ourRoute();

        double actual1 = r.elevationAt(myEdge11.length()/2d);
        double actual2 = r.elevationAt(myEdge11.length()+(myEdge21.length()/3d));
        //System.out.println(myEdge2.elevationAt(myEdge2.length()/3d));

        assertTrue(Double.isNaN(actual1));
        assertEquals(Math.sin(myEdge21.length()/3d), actual2, Math.pow(10, -5));
    }

    @Test
    void elevationAtWorksWithPointsBeyondRoute() {
        SingleRoute r = ourRoute();


        double actual1 = r.elevationAt(0); // edgeIndex = 0 :
        double actual2 = r.elevationAt(myEdge11.length()-1); //  positive edgeIndex on first edge
        double actual3 = r.elevationAt(myEdge11.length() + (myEdge21.length()/2d));
        double actual4 = r.elevationAt(r.length()+29); // position beyond the route
        double actual5 = r.elevationAt(edgeBeforeRoute1.length()/2d); // position before the route


        assertEquals(myEdge11.elevationAt(0), actual1, "oh no…our code…it's broken");
        assertTrue(Double.isNaN(actual2), "oh no…our code…it's broken");
        assertEquals(Math.sin(myEdge21.length()/2d), actual3, Math.pow(10, -5), "oh no…our code…it's broken");
        assertEquals(Math.sin(myEdge21.length()), actual4, "oh no…our code…it's broken");
        assertTrue(Double.isNaN(actual5));
    }

    @Test
    void nodeClosestToMM() {
        // limit points (at the edges' extremity)
        assertEquals(1, ourRoute().nodeClosestTo(0));
        assertEquals(2, ourRoute().nodeClosestTo(myEdge11.length()));
        assertEquals(3, ourRoute().nodeClosestTo(ourRoute().length()));

        // testing intermediate points :
        assertEquals(1, ourRoute().nodeClosestTo(myEdge11.length() - (2* myEdge11.length()/3d))); //  the first node should be the closest one
        assertEquals(3, ourRoute().nodeClosestTo(myEdge11.length() + myEdge21.length()-3));
    }

    private static final double DELTA = 1e-2;
    private static final SingleRoute HORIZONTAL_PATH = HorizontalPath(); // Constant north coordinate
    private static final SingleRoute VERTICAL_PATH = VerticalPath(); // Constant east coordinate
    private static final SingleRoute DIAGONAL_PATH = DiagonalPath(); //
    private static final SingleRoute ZEBRA_PATH = ZebraPath(); // Zebra path

    public static SingleRoute HorizontalPath() {
        PointCh point0 = new PointCh(2_532_700, 1_152_348);
        PointCh point1 = new PointCh(2_532_700 + 5800, 1_152_348);
        PointCh point2 = new PointCh(2_532_700 + 8100, 1_152_348);
        PointCh point3 = new PointCh(2_532_700 + 9200, 1_152_348);
        PointCh point4 = new PointCh(2_532_700 + 11400, 1_152_348);
        PointCh point5 = new PointCh(2_532_700 + 13100, 1_152_348);

        Edge edge0 = new Edge(0,1, point0, point1, 5800, DoubleUnaryOperator.identity());
        Edge edge1 = new Edge(1,2, point1, point2, 2300, DoubleUnaryOperator.identity());
        Edge edge2 = new Edge(2,3, point2, point3, 1100, DoubleUnaryOperator.identity());
        Edge edge3 = new Edge(3,4, point3, point4, 2200, DoubleUnaryOperator.identity());
        Edge edge4 = new Edge(4,5, point4, point5, 1700, DoubleUnaryOperator.identity());

        List<Edge> edgeList = new ArrayList<>(List.of(edge0, edge1, edge2, edge3, edge4));
        return new SingleRoute(edgeList);
    }

    public static SingleRoute VerticalPath() {
        PointCh point0 = new PointCh(2_532_705, 1_152_300);
        PointCh point1 = new PointCh(2_532_705, 1_152_300 + 5800);
        PointCh point2 = new PointCh(2_532_705, 1_152_300 + 8100);
        PointCh point3 = new PointCh(2_532_705, 1_152_300 + 9200);
        PointCh point4 = new PointCh(2_532_705, 1_152_300 + 11400);
        PointCh point5 = new PointCh(2_532_705, 1_152_300 + 13100);

        Edge edge0 = new Edge(0,1, point0, point1, 5800, DoubleUnaryOperator.identity());
        Edge edge1 = new Edge(1,2, point1, point2, 2300, DoubleUnaryOperator.identity());
        Edge edge2 = new Edge(2,3, point2, point3, 1100, DoubleUnaryOperator.identity());
        Edge edge3 = new Edge(3,4, point3, point4, 2200, DoubleUnaryOperator.identity());
        Edge edge4 = new Edge(4,5, point4, point5, 1700, DoubleUnaryOperator.identity());

        List<Edge> edgeList = new ArrayList<>(List.of(edge0, edge1, edge2, edge3, edge4));
        return new SingleRoute(edgeList);
    }

    public static SingleRoute DiagonalPath() {
        PointCh point0 = new PointCh(2_532_705, 1_152_348);
        PointCh point1 = new PointCh(2_532_715, 1_152_358);
        PointCh point2 = new PointCh(2_532_725, 1_152_368);
        PointCh point3 = new PointCh(2_532_735, 1_152_378);
        PointCh point4 = new PointCh(2_532_745, 1_152_388);
        PointCh point5 = new PointCh(2_532_755, 1_152_398);

        Edge edge0 = new Edge(0,1, point0, point1, Math.sqrt(200), DoubleUnaryOperator.identity());
        Edge edge1 = new Edge(1,2, point1, point2, Math.sqrt(200), DoubleUnaryOperator.identity());
        Edge edge2 = new Edge(2,3, point2, point3, Math.sqrt(200), DoubleUnaryOperator.identity());
        Edge edge3 = new Edge(3,4, point3, point4, Math.sqrt(200), DoubleUnaryOperator.identity());
        Edge edge4 = new Edge(4,5, point4, point5, Math.sqrt(200), DoubleUnaryOperator.identity());

        List<Edge> edgeList = new ArrayList<>(List.of(edge0, edge1, edge2, edge3, edge4));
        return new SingleRoute(edgeList);
    }


    public static SingleRoute ZebraPath() {
        PointCh point0 = new PointCh( 2485000, 1075000); //MIN E,N SwissBounds
        PointCh point1 = new PointCh(2485100, 1075000);
        PointCh point2 = new PointCh(2485100, 1075100);
        PointCh point3 = new PointCh(2485200, 1075100);
        PointCh point4 = new PointCh(2485200, 1075200);
        PointCh point5 = new PointCh(2485300, 1075200);

        Edge edge0 = new Edge(0, 1, point0, point1, 100, DoubleUnaryOperator.identity());
        Edge edge1 = new Edge(1, 2, point1, point2, 100, DoubleUnaryOperator.identity());
        Edge edge2 = new Edge(2, 3, point2, point3, 100, DoubleUnaryOperator.identity());
        Edge edge3 = new Edge(3, 4, point3, point4, 100, DoubleUnaryOperator.identity());
        Edge edge4 = new Edge(4, 5, point4, point5, 100, DoubleUnaryOperator.identity());

        List<Edge> edgeList = new ArrayList<>(List.of(edge0, edge1, edge2, edge3, edge4));
        return new SingleRoute(edgeList);
    }

    @Test
    void SingleRouteThrowsOnEmptyList() {
        List<Edge> emptyList = new ArrayList<>();
        assertThrows(
                IllegalArgumentException.class,
                () -> { new SingleRoute(emptyList); });
    }

    @Test
    void indexOfSegmentWorks() {
        int valH = HORIZONTAL_PATH.indexOfSegmentAt(42);
        int valV = VERTICAL_PATH.indexOfSegmentAt(24);
        int valD = DIAGONAL_PATH.indexOfSegmentAt(44);
        assertEquals(0, valH);
        assertEquals(0, valV);
        assertEquals(0, valD);
    }


    @Test
    void lengthHorizontalPathWorks() {
        double valH = HORIZONTAL_PATH.length();
        double valV = VERTICAL_PATH.length();
        double valD = DIAGONAL_PATH.length();
        assertEquals(13100, valH);
        assertEquals(13100, valV);
        assertEquals(Math.sqrt(200) * 5, valD);
    }

    @Test
    void edgesWorks() {
        assertEquals(HorizontalPath().edges(), HORIZONTAL_PATH.edges());
        assertEquals(VerticalPath().edges(), VERTICAL_PATH.edges());
        assertEquals(DiagonalPath().edges(), DIAGONAL_PATH.edges());
        //kind of stupid
    }

    @Test
    void pointsWorksHorizontal() {
        PointCh point0 = new PointCh(2_532_700, 1_152_348);
        PointCh point1 = new PointCh(2_532_700 + 5800, 1_152_348);
        PointCh point2 = new PointCh(2_532_700 + 8100, 1_152_348);
        PointCh point3 = new PointCh(2_532_700 + 9200, 1_152_348);
        PointCh point4 = new PointCh(2_532_700 + 11400, 1_152_348);
        PointCh point5 = new PointCh(2_532_700 + 13100, 1_152_348);

        List<PointCh> pointsList = new ArrayList<>(List.of(point0, point1, point2, point3, point4, point5));

        assertEquals(pointsList, HORIZONTAL_PATH.points());
    }

    @Test
    void pointsWorksVertical() {
        PointCh point0 = new PointCh(2_532_705, 1_152_300);
        PointCh point1 = new PointCh(2_532_705, 1_152_300 + 5800);
        PointCh point2 = new PointCh(2_532_705, 1_152_300 + 8100);
        PointCh point3 = new PointCh(2_532_705, 1_152_300 + 9200);
        PointCh point4 = new PointCh(2_532_705, 1_152_300 + 11400);
        PointCh point5 = new PointCh(2_532_705, 1_152_300 + 13100);

        List<PointCh> pointsList = new ArrayList<>(List.of(point0, point1, point2, point3, point4, point5));

        assertEquals(pointsList, VERTICAL_PATH.points());
    }

    @Test
    void pointsWorksDiagonal() {
        PointCh point0 = new PointCh(2_532_705, 1_152_348);
        PointCh point1 = new PointCh(2_532_715, 1_152_358);
        PointCh point2 = new PointCh(2_532_725, 1_152_368);
        PointCh point3 = new PointCh(2_532_735, 1_152_378);
        PointCh point4 = new PointCh(2_532_745, 1_152_388);
        PointCh point5 = new PointCh(2_532_755, 1_152_398);

        List<PointCh> pointsList = new ArrayList<>(List.of(point0, point1, point2, point3, point4, point5));

        assertEquals(pointsList, DIAGONAL_PATH.points());
    }

    @Test
    void pointAtWorks5() {
        PointCh pointOnHorizontalRoute = new PointCh(2_532_700 + 9200 + 800, 1152348.0);
        PointCh pointOnVerticalRoute = new PointCh(2_532_705, 1_152_300 + 9200 + 800);
        PointCh pointOnDiagonalRoute = new PointCh(2_532_755, 1_152_398);
        assertEquals(pointOnHorizontalRoute, HORIZONTAL_PATH.pointAt(10000));
        assertEquals(pointOnVerticalRoute, VERTICAL_PATH.pointAt(10000));
        assertEquals(pointOnDiagonalRoute, DIAGONAL_PATH.pointAt(10000));
    }



    @Test
    void elevationAtWorks3() {
        assertEquals(800, HORIZONTAL_PATH.elevationAt(10000));
        assertEquals(800, VERTICAL_PATH.elevationAt(10000));
        //assertEquals(10, DIAGONAL_PATH.elevationAt(20));
    }

    @Test
    void nodeClosestToWorks3() {
        assertEquals(3, HORIZONTAL_PATH.nodeClosestTo(10000));
        assertEquals(3, VERTICAL_PATH.nodeClosestTo(10000));
        assertEquals(5, DIAGONAL_PATH.nodeClosestTo(10000));
    }

    @Test
    void pointClosestToWorksOnKnownValues() {
        PointCh point3 = new PointCh(2_532_700 + 9200 + 2, 1_152_348);
        PointCh referencePoint3 = new PointCh(2_532_700 + 9200 + 2, 1_152_348);
        RoutePoint routePoint3 = new RoutePoint(point3, 9200 + 2, 0);

        RoutePoint actualRoutePoint = HORIZONTAL_PATH.pointClosestTo(referencePoint3);

        assertEquals(routePoint3.point(), actualRoutePoint.point());
        assertEquals(routePoint3.position(), actualRoutePoint.position());
        assertEquals(routePoint3.distanceToReference(), actualRoutePoint.distanceToReference(), DELTA);



        PointCh point5 = new PointCh(2_532_700 + 13100, 1_152_348);
        PointCh referencePoint5 = new PointCh(2_532_700 + 13100+ 2, 1_152_348);
        RoutePoint routePoint5 = new RoutePoint(point5, 13100, 2);

        RoutePoint actualRoutePoint1 = HORIZONTAL_PATH.pointClosestTo(referencePoint5);


        assertEquals(routePoint5.point(), actualRoutePoint1.point());
        assertEquals(routePoint5.position(), actualRoutePoint1.position());
        assertEquals(routePoint5.distanceToReference(), actualRoutePoint1.distanceToReference(), DELTA);




        PointCh point3V = new PointCh(2_532_705, 1_152_300 + 9200 + 2);
        PointCh referencePoint3V = new PointCh(2_532_705, 1_152_300 + 9200 + 2);
        RoutePoint routePoint3V = new RoutePoint(point3V, 9200 + 2, 0);

        RoutePoint actualRoutePoint3V = VERTICAL_PATH.pointClosestTo(referencePoint3V);

        assertEquals(routePoint3V.point(), actualRoutePoint3V.point());
        assertEquals(routePoint3V.position(), actualRoutePoint3V.position());
        assertEquals(routePoint3V.distanceToReference(), actualRoutePoint3V.distanceToReference(), DELTA);
    }

    @Test
    void testZebra() {
        PointCh pointZ = new PointCh(2485000, 1075000);
        PointCh referencePointZ = new PointCh(2485000, 1075030);
        RoutePoint routePointZ = new RoutePoint(pointZ, 0, 30);

        RoutePoint actualRoutePointZ = ZEBRA_PATH.pointClosestTo(referencePointZ);

        assertEquals(routePointZ.point(), actualRoutePointZ.point());
        assertEquals(routePointZ.position(), actualRoutePointZ.position());
        assertEquals(routePointZ.distanceToReference(), actualRoutePointZ.distanceToReference(), DELTA);
    }
}

