package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.data.*;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EdgeTest {

    @Test
     void EdgeIsFine(){
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


        float[] type3Array = new float[]{
                384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,
        };

       ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
               0,0,0,12,0,10});
       GraphSectors sectors1 = new GraphSectors(buffer);

       List<AttributeSet> liste = new ArrayList<>();

       Graph graph1 = new Graph(graphNodes1, sectors1, edges1, liste);

       Edge edge1 = Edge.of(graph1, 0, 0, 1);

       PointCh pointToTest = new PointCh(2600000, 1085000);

        System.out.println(edge1.positionClosestTo(pointToTest));
    }


    @Test
    void HorizontalEdgeWorksCorrectlyForLimits(){
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
        Edge edge1 = new Edge(0, 3, fromPoint, toPoint, 10, squared);

        assertEquals(0,edge1.positionClosestTo(fromPoint));
        assertEquals(10, edge1.positionClosestTo(toPoint));


        assertEquals(fromPoint, edge1.pointAt(0));
        assertEquals(toPoint, edge1.pointAt(10));
        assertEquals(new PointCh(2485015, 1076000), edge1.pointAt(5));

        assertEquals(9,edge1.elevationAt(3));
        Edge edge2 = new Edge(0, 3, fromPoint, toPoint, 10, profile);
        assertEquals(384.75f,edge2.elevationAt(0));

    }

    @Test
    void AnyEdgeWorksCorrectly(){
        PointCh fromPoint = new PointCh(2485010, 1076000);
        PointCh toPoint = new PointCh(2485020, 1076010);
        PointCh pointToTest = new PointCh(2485015, 1076005);
        DoubleUnaryOperator function = new DoubleUnaryOperator() {
            @Override
            public double applyAsDouble(double operand) {
                return Math.pow(operand, 2);
            }
        };
        Edge edge1 = new Edge(0, 3, fromPoint, toPoint, 10, function);
        assertEquals(pointToTest, edge1.pointAt(5));
    }


    @Test
    void testPositionClosestTo() {
        PointCh fromPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        assertEquals(3, edge.positionClosestTo(new PointCh(SwissBounds.MIN_E + 3, SwissBounds.MIN_N + 2)));
        assertEquals(7, edge.positionClosestTo(new PointCh(SwissBounds.MIN_E + 7, SwissBounds.MIN_N + 2)));
    }

    @Test
    void testPointAt() {
        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        float[] profileTab = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge = new Edge(0, 1, fromPoint, toPoint, 5, profile);

        assertEquals(new PointCh(SwissBounds.MIN_E + 2, SwissBounds.MIN_N), edge.pointAt(3));
        assertEquals(new PointCh(SwissBounds.MIN_E + 7, SwissBounds.MIN_N), edge.pointAt(-2));
        assertEquals(new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N), edge.pointAt(0));
        assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), edge.pointAt(5));

        //AYA : Exception returned chez les garcons too:
        //assertEquals(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), edge.pointAt(10));
    }

    @Test
    void testElevationAt() {
        PointCh fromPoint = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        float[] profileTab = {502f, 500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile = Functions.sampled(profileTab, 5);
        Edge edge = new Edge(0, 1, fromPoint, toPoint, 5, profile);


        assertEquals(502f, edge.elevationAt(0));
        assertEquals(502f, edge.elevationAt(-1));
        assertEquals(500f, edge.elevationAt(1));
        assertEquals(505f, edge.elevationAt(2));
        assertEquals(510f, edge.elevationAt(3));
        assertEquals(505f, edge.elevationAt(4));
        assertEquals(507.5f, edge.elevationAt(4.5));
        assertEquals(510f, edge.elevationAt(5));
        assertEquals(510f, edge.elevationAt(6));
    }

    @Test
    void testOf() throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        Edge e = Edge.of(g, 0, 0, g.edgeTargetNodeId(0));
    }

    @Test
    public void positionClosestToWorks() throws IOException {
        PointCh p1 = new PointCh(2_533_132, 1_152_206);
        PointCh p2 = new PointCh( 2_533_513.610, 1_152_248.664);
        PointCh p3 = new PointCh( 2_533_232.407, 1_152_164.508);
        Edge e = new Edge(0,0, p1, p2, 384.2, null);
        double expected = 0;
        assertEquals(expected, e.positionClosestTo(p1));
        assertEquals(94.14, e.positionClosestTo(p3), 3);
    }

    @Test
    public void pointAtWorks() {
        PointCh p1 = new PointCh(2_533_132, 1_152_206);
        PointCh p2 = new PointCh( 2_533_513.610, 1_152_248.664);
        Edge e = new Edge(0,0, p1, p2, 384.2, null);
        assertEquals((new PointCh( 2_533_232.407, 1_152_164.508)).e(), (e.pointAt(101)).e(), 10);
        assertEquals((new PointCh( 2_533_232.407, 1_152_164.508)).n(), (e.pointAt(101)).n(), 100);
    }

}
