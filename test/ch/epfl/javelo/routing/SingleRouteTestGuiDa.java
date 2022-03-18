package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

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
}
