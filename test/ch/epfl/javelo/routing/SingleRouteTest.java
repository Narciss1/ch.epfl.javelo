package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public class SingleRouteTest {

    @Test
    public void rightPositionWorks(){
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(1 ,3 , new PointCh(10000,20000), new PointCh(10000,2000), 3.5,
        DoubleUnaryOperator profile));
        SingleRoute singleRoute = new SingleRoute(edges);
        double position = -1;
        singleRoute.rightPosition(position);

    }
}
