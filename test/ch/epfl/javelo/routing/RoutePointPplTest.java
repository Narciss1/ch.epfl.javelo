package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoutePointPplTest {
    PointCh p = new PointCh(2537737.9706338, 1152665.627210865);
    RoutePoint rP = new RoutePoint(p, 176, 116);

    @Test
    void withPositionShiftedByWorks(){
        RoutePoint rPs = rP.withPositionShiftedBy(5);
      //  assertEquals(rPs, new RoutePoint(p, 181, 116.3));

    }
    @Test
    void minWorks(){
        RoutePoint routePoint = new RoutePoint(p, 176, 118);
        RoutePoint routePoint2 = new RoutePoint(p, 176, 110);
        assertEquals(rP.min(routePoint), rP);
        assertEquals(rP.min(routePoint2), routePoint2);
    }
    @Test
    void min2Works(){
        assertEquals(rP.min(p, 5, 120), rP);
        assertEquals(rP.min(p, 5, 115), new RoutePoint(p, 5, 115));
    }

}