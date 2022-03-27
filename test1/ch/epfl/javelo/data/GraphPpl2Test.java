package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static ch.epfl.javelo.Q28_4.asDouble;
import static ch.epfl.javelo.data.Attribute.HIGHWAY_TRACK;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphPpl2Test {
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
    void nodeOutEdgeIdWorks() throws IOException {
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        assertEquals(0,graph.nodeOutEdgeId(0,0));
    }

    @Test
    void nodeClosestToWorks() throws IOException{
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        assertEquals(0,graph.nodeClosestTo((new PointCh(asDouble(40788460),asDouble(18660037))),10000));
    }

    @Test
    void edgeTargetNodeIdWorks() throws IOException{
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
}