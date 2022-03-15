package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RouteTest {

    @Test
    void withPositionShiftedByWorksOnGivenValue(){
        //Shift by 2000
        RoutePoint point0 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint actual0 = point0.withPositionShiftedBy(2000);
        RoutePoint expected0 = new RoutePoint(new PointCh(2800000, 1080000), 14839, 1000);
        assertEquals(expected0, actual0);

        //Shift by -2000
        RoutePoint point1 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint actual1 = point1.withPositionShiftedBy(-2000);
        RoutePoint expected1 = new RoutePoint(new PointCh(2800000, 1080000), 10839, 1000);
        assertEquals(expected1, actual1);

        //Shift by 0
        RoutePoint point2 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint actual2 = point2.withPositionShiftedBy(0);
        RoutePoint expected2 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        assertEquals(expected2, actual2);
    }

    @Test
    void minWorksOnGivenValue(){
        //That plus grand
        RoutePoint pointThis0 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint pointThat0 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 500);
        RoutePoint actual0 = pointThis0.min(pointThat0);
        RoutePoint expected0 = pointThat0;
        assertEquals(expected0, actual0);

        //Egalité
        RoutePoint pointThis1 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint pointThat1 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint actual1 = pointThis1.min(pointThat1);
        RoutePoint expected1 = pointThis1;
        assertEquals(expected1, actual1);

        //This plus grand
        RoutePoint pointThis2 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 500);
        RoutePoint pointThat2 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint actual2 = pointThis2.min(pointThat2);
        RoutePoint expected2 = pointThis2;
        assertEquals(expected2, actual2);
    }
}
