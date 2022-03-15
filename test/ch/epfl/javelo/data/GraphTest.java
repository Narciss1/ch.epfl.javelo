package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import ch.epfl.javelo.routing.ElevationProfile;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    @Test
    void ErrorIfFileNotFound(){
        assertThrows(NoSuchFileException.class, () -> Graph.loadFrom(Path.of("eazeza")));
    }

    @Test
    void NoErrorIfFileFound() {
        assertDoesNotThrow(() -> Graph.loadFrom(Path.of("lausanne"))); }

    @Test
    void nodeCountWorksGivenTest() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes nodes = new GraphNodes(b);
        IntBuffer b1 = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0x1_803_0925
        });
        GraphNodes nodes1 = new GraphNodes(b1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
//            (short) 0x180C, (short) 0xFEFF,
//            (short) 0xFFFE, (short) 0xF000 //TypeIn3
                (short) 0x180C, (short) 0x1212 //Type2
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);
        List<AttributeSet> liste = new ArrayList<>();
        Graph graph = new Graph(nodes, sectors, edges,liste);
        int expected0 = 1;
        int actual0 = graph.nodeCount();
        assertEquals(expected0, actual0);

        Graph graph1 = new Graph(nodes1, sectors, edges, liste);
        int expected1 = 2;
        int actual1 = graph1.nodeCount();
        assertEquals(expected1, actual1);
    }

    @Test
    void nodePointWorksOnGivenValues(){

    }

    @Test
    void NodeClosedToMINUSONE() throws IOException {
        assertEquals(-1, Graph.loadFrom(Path.of("lausanne")).nodeClosestTo(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 0));
    }

    @Test
    void NodeClosedTo0() throws IOException {
        assertEquals(194878, Graph.loadFrom(Path.of("lausanne")).nodeClosestTo(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 10000000));
    }

    @Test
    void NodeClosedToMAX() throws IOException {
        assertEquals(26, Graph.loadFrom(Path.of("lausanne")).nodeClosestTo(new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 10000000));
    }
    @Test
    void ThrowElevationProfile()
    {
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(-1, new float[]{1,2,3}));
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(0, new float[]{1,2,3}));
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(10, new float[]{1}));
        assertThrows(IllegalArgumentException.class, () -> new ElevationProfile(10, new float[]{}));
    }

    @Test
    void TestWithCalculationsActualFunction()
    {
        float[] values = new float[]{5,7,0,2,10};
        double length = 20;
        ElevationProfile ep = new ElevationProfile(length, values);
        assertEquals(20, ep.length());
        assertEquals(0, ep.minElevation());
        assertEquals(10, ep.maxElevation());
        assertEquals(12, ep.totalAscent());
        assertEquals(7, ep.totalDescent());
        assertEquals(6, ep.elevationAt(2.5));
        assertEquals(7, ep.elevationAt(5));
        assertEquals(1, ep.elevationAt(12.5));
        assertEquals(10, ep.elevationAt(21));
        assertEquals(5, ep.elevationAt(-1));
        assertEquals(0, ep.elevationAt(10));
    }

    @Test
    void TestWithCalculationsCst()
    {
        float[] values = new float[]{5,5};
        double length = 20;
        ElevationProfile ep = new ElevationProfile(length, values);
        assertEquals(20, ep.length());
        assertEquals(5, ep.minElevation());
        assertEquals(5, ep.maxElevation());
        assertEquals(0, ep.totalAscent());
        assertEquals(0, ep.totalDescent());
        assertEquals(5, ep.elevationAt(2.5));
        assertEquals(5, ep.elevationAt(5));
        assertEquals(5, ep.elevationAt(12.5));
        assertEquals(5, ep.elevationAt(21));
        assertEquals(5, ep.elevationAt(-1));
        assertEquals(5, ep.elevationAt(10));
    }

}
/*

    IntBuffer b = IntBuffer.wrap(new int[]{
            2_600_000 << 4,
            1_200_000 << 4,
            0x2_000_1234
    });
    GraphNodes nodes = new GraphNodes(b);

    IntBuffer profileIds = IntBuffer.wrap(new int[]{
            // Type : 0. Index du premier échantillon : 1.
            (2 << 30) | 1
    });
    ShortBuffer elevations = ShortBuffer.wrap(new short[]{
            (short) 0,
//            (short) 0x180C, (short) 0xFEFF,
//            (short) 0xFFFE, (short) 0xF000 //TypeIn3
            (short) 0x180C, (short) 0x1212 //Type2
    });
    ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
                // Longueur : 0x10.b m (= 16.6875 m)
                edgesBuffer.putShort(4, (short) 0x10_b);
                // Dénivelé : 0x10.0 m (= 16.0 m)
                edgesBuffer.putShort(6, (short) 0x10_0);
                // Identité de l'ensemble d'attributs OSM : 1
                edgesBuffer.putShort(8, (short) 2022);
                GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

                ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
                GraphSectors sectors = new GraphSectors(buffer);
                List<AttributeSet> liste = new ArrayList<>();
        Graph graph = new Graph(nodes, sectors, edges,liste);
*/
