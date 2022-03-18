package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.Edge;
import ch.epfl.javelo.routing.ElevationProfile;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertEquals(new PointCh(2539399.27250234,1164288.767963147), actualEdge.pointAt(100));
    }

    @Test
    void elevationAtWorks(){
        Edge actualEdge = Edge.of(actual1, 1000, 2345, 5436);
        assertEquals(841.125, actualEdge.atElevation(100));
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
      //  assertEquals(5.429999947547913, el.totalAscent());
        assertEquals(3.5800000429153442, el.totalDescent());

        assertEquals(4.384999930858613, el.elevationAt(1));
        assertEquals(2.25F, el.elevationAt(-5));
        assertEquals(4.1F, el.elevationAt(200));

    }
}
