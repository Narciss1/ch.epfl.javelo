package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.Ch1903;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class GraphTestHassan {

    private Graph getGraph() throws IOException {
        return Graph.loadFrom(Path.of("lausanne"));
    }

    @Test
    void loadFrom() throws IOException {

    }

    @Test
    void nodeCountWorksOnKnowValue() throws IOException {
        Graph graph = getGraph();
        System.out.println(graph.nodeCount());
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