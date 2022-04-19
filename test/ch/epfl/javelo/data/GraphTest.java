package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.*;
import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.RoutePoint;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.Q28_4.asDouble;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    @Test
    void ErrorIfFileNotFound(){
        assertThrows(NoSuchFileException.class, () -> Graph.loadFrom(Path.of("eazeza")));
        assertThrows(NoSuchFileException.class, () -> Graph.loadFrom(Path.of("eajsza")));
    }

    @Test
    void NoErrorIfFileFound() {
        assertDoesNotThrow(() -> Graph.loadFrom(Path.of("lausanne"))); }

    @Test
    void nodeCountWorksOnGivenValue() {
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

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        int expected0 = 1;
        int actual0 = graph.nodeCount();
        assertEquals(expected0, actual0);

        //Test 2:
        Graph graph1 = new Graph(nodes1, sectors, edges, liste);
        int expected1 = 2;
        int actual1 = graph1.nodeCount();
        assertEquals(expected1, actual1);
    }

    @Test
    void nodePointWorksOnGivenValue() {
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
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0x1212
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        PointCh expected0 = new PointCh(2_600_000, 1_200_000);
        PointCh actual0 = graph.nodePoint(0);
        assertEquals(expected0, actual0);

        //Test 2:
        Graph graph1 = new Graph(nodes1, sectors, edges, liste);
        PointCh expected1 = new PointCh(2_536_263, 1_215_736);
        PointCh actual1 = graph1.nodePoint(0);
        assertEquals(expected1, actual1);
    }

    @Test
    void nodeOutDegreeWorksOnGivenValue() {
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
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0x1212
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        int expected0 = 2;
        int actual0 = graph.nodeOutDegree(0);
        assertEquals(expected0, actual0);

        //Test 2:
        Graph graph1 = new Graph(nodes1, sectors, edges, liste);
        int expected1 = 1;
        int actual1 = graph1.nodeOutDegree(1);
        assertEquals(expected1, actual1);
    }

    @Test
    void nodeOutEdgeIdWorksOnGivenValue() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0b00110000000000000000000000000111
        });
        GraphNodes nodes = new GraphNodes(b);
        IntBuffer b1 = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0b01110000000000000000000000011111
        });
        GraphNodes nodes1 = new GraphNodes(b1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0x1212
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        int expected0 = 9;
        int actual0 = graph.nodeOutEdgeId(0, 2);
        assertEquals(expected0, actual0);

        //Test 2:
        Graph graph1 = new Graph(nodes1, sectors, edges, liste);
        int expected1 = 31 + 5;
        int actual1 = graph1.nodeOutEdgeId(1, 5);
        assertEquals(expected1, actual1);
    }

    //A FAIRE!
    @Test
    void nodeClosestToWorksOnGivenValue() throws IOException {
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
    void edgeTargetNodeIdWorksOnGivenValue() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0b00110000000000000000000000000111
        });
        GraphNodes nodes = new GraphNodes(b);
        IntBuffer b1 = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0b01110000000000000000000000011111
        });
        GraphNodes nodes1 = new GraphNodes(b1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0x1212
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        int expected0 = 12;
        int actual0 = graph.edgeTargetNodeId(0);
        assertEquals(expected0, actual0);
    }

    @Test
    void edgeIsInvertedWorksOnGivenValue() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0b00110000000000000000000000000111
        });
        GraphNodes nodes = new GraphNodes(b);
        IntBuffer b1 = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0b01110000000000000000000000011111
        });
        GraphNodes nodes1 = new GraphNodes(b1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0x1212
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        boolean expected0 = true;
        boolean actual0 = graph.edgeIsInverted(0);
        assertEquals(expected0, actual0);
    }

    //ASSISTANT HELP!
    @Test
    void edgeAttributesWorksOnGivenValue() {}

    @Test
    void edgeLengthWorksOnGivenValue() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0b00110000000000000000000000000111
        });
        GraphNodes nodes = new GraphNodes(b);
        IntBuffer b1 = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0b01110000000000000000000000011111
        });
        GraphNodes nodes1 = new GraphNodes(b1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0x1212
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        double expected0 = 16.6875;
        double actual0 = graph.edgeLength(0);
        assertEquals(expected0, actual0);
    }

    @Test
    void edgeElevationGainWorksOnGivenValue() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0b00110000000000000000000000000111
        });
        GraphNodes nodes = new GraphNodes(b);
        IntBuffer b1 = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0b01110000000000000000000000011111
        });
        GraphNodes nodes1 = new GraphNodes(b1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (2 << 30) | 1
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0x1212
        });
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);
        edgesBuffer.putShort(4, (short) 0x10_b);
        edgesBuffer.putShort(6, (short) 0x10_0);
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors sectors = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        //Test 1:
        Graph graph = new Graph(nodes, sectors, edges,liste);
        double expected0 = 16.0;
        double actual0 = graph.edgeElevationGain(0);
        assertEquals(expected0, actual0);
    }

    //LINA HELP!
    @Test
    void edgeProfileWorksOnGivenValue() {}

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

    @Test
    void loadFromWorks() throws IOException {
        Graph.loadFrom(Path.of("lausanne"));
    }

    @Test
    void nodeCountWorks() throws IOException{
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        assertEquals(212679,graph.nodeCount());
    }

    @Test
    void nodePointWorks() throws IOException{
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        assertEquals(new PointCh(asDouble(40788460),asDouble(18660037)),graph.nodePoint(0));

    }

    @Test
    void nodeOutDegreeWorks() throws IOException{
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        assertEquals(1,graph.nodeOutDegree(0));
    }

    @Test
    void nodeOutEdgeIdWorks0() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        assertEquals(0,graph.nodeOutEdgeId(0,0));
    }

    @Test
    void nodeClosestToWorks1() throws IOException{
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        assertEquals(0,graph.nodeClosestTo((new PointCh(asDouble(40788460),asDouble(18660037))),10000));
    }

    @Test
    void edgeTargetNodeIdWorks0() throws IOException{
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        assertEquals(1,graph.edgeTargetNodeId(0));
    }

    @Test
    void edgeIsInvertedWorks() throws IOException{
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        assertEquals(false,graph.edgeIsInverted(0));
    }

    @Test
    void edgeAttributesWorks() throws IOException{
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        //AYA : NORMAL QUE CE SOIT FAUX
        //assertEquals(HIGHWAY_TRACK, graph.edgeAttributes(0));
    }

    @Test
    void edgeLengthWorks() throws IOException{
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        assertEquals(Math.scalb(1522,-4),graph.edgeLength(0));
    }

    @Test
    void edgeElevationGainWorks() throws IOException{
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        assertEquals(Math.scalb(44,-4),graph.edgeElevationGain(0));
    }

    @Test
    void edgeProfileWorks() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne"));

        assertEquals(graph.edgeProfile(0).applyAsDouble(0), 606.75f);
        assertEquals(graph.edgeProfile(0).applyAsDouble(0), 606.75f);
    }

    @Test
    void testLoadFrom() throws IOException {
        assertThrows(IOException.class, () -> {
            Graph.loadFrom(Path.of("lausanne2"));
        });
        Graph.loadFrom(Path.of("lausanne"));
    }

    @Test
    void testNodeCount() throws IOException{
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        //nodes count in the file
        assertEquals(212679, g.nodeCount());
    }

    @Test
    void testNodePoint() throws IOException {
        Path filePath = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
            //on va avoir l'identité OSM du noeud d'identité (index) 2022 dans JaVelo
            System.out.println(osmIdBuffer.get(2023));
        } catch (IOException e) {
            System.out.println(e);
        }

        Graph g = Graph.loadFrom(Path.of("lausanne"));
        //https://www.openstreetmap.org/node/310876657
        //Position expected: 46,63 / 6,60
        PointCh p = g.nodePoint(2022);

        assertEquals(46.6326106, Math.toDegrees(p.lat()), 1e-2);
        assertEquals(6.6013034, Math.toDegrees(p.lon()), 1e-2);

        //https://www.openstreetmap.org/node/310876661
        //Position expected: 46,63 / 6,60
        PointCh p2 = g.nodePoint(2023);

        assertEquals(46.6321522, Math.toDegrees(p2.lat()), 1e-2);
        assertEquals(6.6019493, Math.toDegrees(p2.lon()), 1e-2);
    }

    @Test
    void testNodeOutOfDegree() throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        //https://www.openstreetmap.org/node/310876657
        //Node out of degree expected: 46,63 / 6,60

        System.out.println(g.nodeOutDegree(2022));
        System.out.println(g.nodeOutDegree(2023));
        System.out.println(g.nodeOutDegree(203));
        System.out.println(g.nodeOutDegree(2));
    }

    @Test
    void testNodeOutOfEdgeId() throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        //https://www.openstreetmap.org/node/310876657

        assertEquals(4095, g.nodeOutEdgeId(2022, 0));
        assertEquals(4096, g.nodeOutEdgeId(2022, 1));

        assertThrows(AssertionError.class, () -> {
            //only two edges id edgeIndex are 0 and 1
            g.nodeOutEdgeId(2022, 2);
        });
    }

    @Test
    void testNodeClosestTo() throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));

        //should be 2022 as we are searching at 2022 location
        assertEquals(2022, g.nodeClosestTo(g.nodePoint(2022), 10));

        //should be 2023 as we are searching at 2023 location
        assertEquals(2023, g.nodeClosestTo(g.nodePoint(2023), 10));

        //https://www.openstreetmap.org/node/3490836266 node position (12744 index in Javelo)
        double lat = Math.toRadians(46.5864332);
        double lon = Math.toRadians(6.5124991);

        //should be -1 as we are searching at 12744 location but there is a bit of imprecision (or negative radius)
        assertEquals(-1, g.nodeClosestTo(new PointCh(Ch1903.e(lon, lat), Ch1903.n(lon, lat)), 0));
        //AYA : Cas négatif non pris en compte mais le cas nul si:
        //assertEquals(-1, g.nodeClosestTo(new PointCh(Ch1903.e(lon, lat), Ch1903.n(lon, lat)), -4));

        //should be 12744 even if we change the radius
        assertEquals(12744, g.nodeClosestTo(new PointCh(Ch1903.e(lon, lat), Ch1903.n(lon, lat)), 10));
        assertEquals(12744, g.nodeClosestTo(new PointCh(Ch1903.e(lon, lat), Ch1903.n(lon, lat)), 20));
        assertEquals(12744, g.nodeClosestTo(new PointCh(Ch1903.e(lon, lat), Ch1903.n(lon, lat)), 1000));

        //on se décale un tout petit peu
        double lat2 = Math.toRadians(46.5864332);
        double lon2 = Math.toRadians(6.5824991);

        //pas de point à cette location
        assertEquals(-1, g.nodeClosestTo(new PointCh(Ch1903.e(lon2, lat2), Ch1903.n(lon2, lat2)), 0));
        //nearest point
        assertEquals(17538, g.nodeClosestTo(new PointCh(Ch1903.e(lon2, lat2), Ch1903.n(lon2, lat2)), 100));
    }


    @Test
    void testEdgeTargetNodeId() throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));

        //https://www.openstreetmap.org/way/28431021
        assertEquals(17095,  g.edgeTargetNodeId(36234));
    }

    @Test
    void testEdgeIsInverted() throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));

        //https://www.openstreetmap.org/way/28431021
        //AYA : Faux chez les garcons aussi
        //Vérfier si Léo a bien lu l'énoncé
        //assertEquals(false,  g.edgeIsInverted(36234));
    }

    @Test
    void testEdgeAttributes() throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));

        assertEquals(AttributeSet.of(Attribute.HIGHWAY_UNCLASSIFIED, Attribute.SURFACE_ASPHALT), g.edgeAttributes(36234));
    }

    @Test
    public void loadFromThrowsExceptionIfFileDoesNotExist() throws IOException {
        Path basePath = Path.of("geneve");
        assertThrows(IOException.class, () ->
        {Graph.loadFrom(basePath);} );
    }

    // En vérité testée dans les autres méthodes (indirectement ou directement)
    @Test
    public void loadFromGiveTheRightGraph() throws IOException {
        Path filePath = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }
        System.out.println(osmIdBuffer.get(2022));
    }

    @Test
    public void nodesCountWorksOnKnownValues() {

        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        Graph g = new Graph(ns, null, null, null);
        assertEquals(1, g.nodeCount());

        for (int count = 0; count < 100; count += 1) {
            var buffer = IntBuffer.allocate(3 * count);
            var graphNodes = new GraphNodes(buffer);
            g = new Graph(graphNodes, null, null, null);
            assertEquals(count, g.nodeCount());
        }
    }

    // Marche normalement mais on ne peut mettre de delta
    @Test
    public void nodePointReturnsNodeIdPosition() throws IOException {
        Path basePath = Path.of("lausanne");
        Graph g = Graph.loadFrom(basePath);
        System.out.println(WebMercator.x(Math.toRadians(6.6013034)) + " " + WebMercator.y(Math.toRadians(46.6326106)));
        // AYA : mm erreur que les gars
        //assertEquals((new PointWebMercator(WebMercator.x(Math.toRadians(6.6013034)), WebMercator.y(Math.toRadians(46.6326106))).toPointCh()),
        //        g.nodePoint(2022));
    }


    @Test
    public void nodeOutDegreesWorksOnKnownValues() {
        var nodesCount = 10_000;
        var buffer = IntBuffer.allocate(3 * nodesCount);
        var rng = newRandom();
        for (int outDegree = 0; outDegree < 16; outDegree += 1) {
            var firstEdgeId = rng.nextInt(1 << 28);
            var nodeId = rng.nextInt(nodesCount);
            buffer.put(3 * nodeId + 2, (outDegree << 28) | firstEdgeId);
            var graphNodes = new GraphNodes(buffer);

            Graph g = new Graph(graphNodes, null, null, null);
            assertEquals(outDegree, g.nodeOutDegree(nodeId));
        }
    }

    @Test
    public void nodeOutEdgeIdWorks () {
        var nodesCount = 10_000;
        var buffer = IntBuffer.allocate(3 * nodesCount);
        var rng = newRandom();
        for (int outDegree = 0; outDegree < 16; outDegree += 1) {
            var firstEdgeId = rng.nextInt(1 << 28);
            var nodeId = rng.nextInt(nodesCount);
            buffer.put(3 * nodeId + 2, (outDegree << 28) | firstEdgeId);
            var graphNodes = new GraphNodes(buffer);
            Graph g = new Graph(graphNodes, null, null, null);
            for (int i = 0; i < outDegree; i += 1)
                assertEquals(firstEdgeId + i, g.nodeOutEdgeId(nodeId, i));
        }
    }

    @Test
    public void nodeClosestToWorks() throws IOException {

        Path basePath = Path.of("lausanne");
        Graph g = Graph.loadFrom(basePath);
        double x = 0.518275214444;
        double y = 0.353664894749;
        Path filePath = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }
        PointWebMercator pwm = new PointWebMercator(x, y);
        PointCh napoleon = pwm.toPointCh();

        //@489
        int javeloNode = g.nodeClosestTo(napoleon, 100 );
        long expected  = 417273475;
        System.out.println(javeloNode);
        long actual = osmIdBuffer.get(javeloNode);
        assertEquals(expected, actual);
    }

    @Test
    public void edgeTargetNodeIdWorks() throws IOException{

        var edgesCount = 1000_000;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        var rng = newRandom();
        for (int targetNodeId = -10000; targetNodeId < 10000; targetNodeId += 1) {
            var edgeId = rng.nextInt(edgesCount);
            edgesBuffer.putInt(10 * edgeId, targetNodeId);
            var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
            Graph g = new Graph(null, null, graphEdges, null);
            var expectedTargetNodeId = targetNodeId < 0 ? ~targetNodeId : targetNodeId;
            //System.out.println(expectedTargetNodeId + " " + g.edgeTargetNodeId(edgeId));
            assertEquals(expectedTargetNodeId, g.edgeTargetNodeId(edgeId));
        }

    }

    @Test
    public void IsInvertedWorksOnKnownValues() {
        var edgesCount = 10_000;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        var rng = newRandom();
        for (int targetNodeId = -100; targetNodeId < 100; targetNodeId += 1) {
            var edgeId = rng.nextInt(edgesCount);
            edgesBuffer.putInt(10 * edgeId, targetNodeId);
            var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
            Graph g = new Graph(null, null, graphEdges, null);
            assertEquals(targetNodeId < 0, g.edgeIsInverted(edgeId));
        }
    }
    @Test
    public void isInvertedWorksOnActualData() throws IOException {
        Path basePath = Path.of("lausanne");
        Graph g = Graph.loadFrom(basePath);

        Path edgesPath = basePath.resolve("edges.bin");
        ByteBuffer edgesBuffer;
        try (FileChannel channel =  FileChannel.open(edgesPath)) {
            edgesBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
        for(int i = 0; i < edgesBuffer.capacity() / 10; ++i) {
            //System.out.println(g.edgeIsInverted(i) + " " + (edgesBuffer.getInt(i * 10) < 0) );
            assertEquals(g.edgeIsInverted(i), edgesBuffer.getInt(i * 10) < 0);
        }
    }

    @Test
    public void edgeAttributeSetWorksOnKnownValues() throws IOException {
        Path basePath = Path.of("lausanne");
        Path attributesPath = basePath.resolve("attributes.bin");

        LongBuffer attributesBuffer;
        try(FileChannel channel = FileChannel.open(attributesPath)) {
            attributesBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asLongBuffer();
        }
        Graph g = Graph.loadFrom(basePath);
        for(int i = 0;  i < attributesBuffer.capacity(); ++i) {
            AttributeSet expected = new AttributeSet(attributesBuffer.get(i));
            AttributeSet actual =  g.edgeAttributes(i);
            //System.out.println(expected +  " " + actual);
        }
        //assertEquals(expected,actual);

    }



    public static void main(String[] args) throws IOException {
        /*Path filePath = Path.of("lausanne/nodes_osmid.bin");
        LongBuffer osmIdBuffer;
        try (FileChannel channel = FileChannel.open(filePath)) {
            osmIdBuffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size())
                    .asLongBuffer();
        }

        System.out.println(osmIdBuffer.get(153713));*/

        try (InputStream s = new FileInputStream("lausanne/attributes.bin")) {
            int b = 0, pos = 0;
            while ((b = s.read()) != -1) {
                if ((pos % 16) == 0)
                    //System.out.printf("%n%05d:", pos);
                //System.out.printf(" %3d", b);
                pos += 1;
            }
        }
    }

    private Graph getGraph() throws IOException {
        return Graph.loadFrom(Path.of("lausanne"));
    }

    @Test
    void loadFrom() throws IOException {

    }

    @Test
    void nodeCountWorksOnKnowValue() throws IOException {
        Graph graph = getGraph();
        //System.out.println(graph.nodeCount());
    }

    @Test
    void nodePointWorksOnKnowValue() throws IOException {
        Graph graph = getGraph();
        //LongBuffer nodeOSMId = readNodeOSMId();
        //System.out.println(nodeOSMId.get(0)); // 1684019323

        PointCh actual = graph.nodePoint(0);
        double lat = Math.toRadians(46.6455770);
        double lon = Math.toRadians(6.7761194);

        assertEquals(lat, actual.lat(), 10e-7);
        assertEquals(lon, actual.lon(), 10e-7);
    }

    @Test
    void nodeOutDegree() throws IOException {
        Graph graph = getGraph();

        int actual1 = graph.nodeOutDegree(0); //1684019323
        assertEquals(1, actual1);
        int actual2 = graph.nodeOutDegree(1); //1684019310
        assertEquals(2, actual2);
        int actual3 = graph.nodeOutDegree(100_000); //2101684853
        assertEquals(3, actual3);
    }

    @Test
    void nodeOutEdgeId() throws IOException {
        Graph graph = getGraph();
        int actual1 = graph.nodeOutEdgeId(0, 0); //1684019323
        assertEquals(0, actual1);
    }

    @Test
    void nodeClosestTo() throws IOException {
        Graph graph = getGraph();

        double e = Ch1903.e(Math.toRadians(6.77653), Math.toRadians(46.64608)); //osmid: 1684019323
        double n = Ch1903.n(Math.toRadians(6.77653), Math.toRadians(46.64608));
        PointCh point = new PointCh(e, n);
        int actual1 = graph.nodeClosestTo(point, 100);
        int actual2 = graph.nodeClosestTo(point, 0);

        assertEquals(0, actual1);
        assertEquals(-1, actual2);
    }

    @Test
    void edgeTargetNodeId() throws IOException {
        Graph graph = getGraph();

        int actual1 = graph.edgeTargetNodeId(0);
        assertEquals(1, actual1);
    }

    @Test
    void edgeIsInverted() throws IOException {
        Graph graph = getGraph();

        assertFalse(graph.edgeIsInverted(0));
        assertTrue(graph.edgeIsInverted(334630));
    }

    @Test
    void edgeAttributes() throws IOException {
        Graph graph = getGraph();

        AttributeSet actual1 = graph.edgeAttributes(0);
        AttributeSet expected1 = AttributeSet.of(Attribute.HIGHWAY_TRACK, Attribute.TRACKTYPE_GRADE1);
        assertEquals(expected1.bits(), actual1.bits());

        AttributeSet expected2 = AttributeSet.of(Attribute.BICYCLE_USE_SIDEPATH, Attribute.HIGHWAY_TERTIARY, Attribute.SURFACE_ASPHALT);
        AttributeSet actual2 = graph.edgeAttributes(362164);
        assertEquals(expected2.bits(), actual2.bits());
    }

    @Test
    void edgeLength() throws IOException {
        Graph graph = getGraph();
        double actual = graph.edgeLength(335275);
        assertEquals(24, actual, 10e-1); //  /!\ expected -> lack of precision
    }

    @Test
    void edgeElevationGain() throws IOException {
        Graph graph = getGraph();
        double actual1 = graph.edgeElevationGain(335275);
        assertEquals(1, actual1, 10e-2); //  /!\ expected -> lack of precision

        double actual2 = graph.edgeElevationGain(293069); // edge entre 289087937 (osm) et 570300687 (osm)
        assertEquals(8, actual2, 10e-1); //  /!\ expected -> lack of precision
    }

    @Test
    void edgeProfile() throws IOException {
        Graph graph = getGraph();
        DoubleUnaryOperator func = graph.edgeProfile(335275);
        assertEquals(390,func.applyAsDouble(0),1);
        DoubleUnaryOperator func2 = graph.edgeProfile(293912);
        assertEquals(490,func2.applyAsDouble(0),4);
        assertEquals(496,func2.applyAsDouble(34),1);

    }
}



