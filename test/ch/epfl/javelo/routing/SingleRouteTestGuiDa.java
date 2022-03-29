package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SingleRouteTestGuiDa {
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
    public void elevationAtTest()
    {
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
                new Edge(0, 1, pointCreator(0, 0), pointCreator(2, 0), 16, Functions.sampled(new float[]{5,7, 1, 0, 4}, 16)),
                new Edge(1, 2, pointCreator(2, 0), pointCreator(2, 2), 4, Functions.sampled(new float[]{4,0}, 4))
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
    public void nodeClosestToTest()
    {
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

    private PointCh pointCreator(int e, int n)
    {
        return new PointCh(SwissBounds.MIN_E + e, SwissBounds.MIN_N + n);
    }
}
