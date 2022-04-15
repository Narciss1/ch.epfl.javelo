package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoutePointTest {

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
        //That plus petit
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

        //This plus petit
        RoutePoint pointThis2 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 500);
        RoutePoint pointThat2 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint actual2 = pointThis2.min(pointThat2);
        RoutePoint expected2 = pointThis2;
        assertEquals(expected2, actual2);
    }

    @Test
    void min2WorksOnGivenValue(){
        //ThatDistanceToReference plus petit
        RoutePoint pointThis0 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint actual0 = pointThis0.min(new PointCh(2800000, 1080000), 12839, 999);
        RoutePoint expected0 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 999);
        assertEquals(expected0, actual0);

        //Egalité
        RoutePoint pointThis1 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint actual1 = pointThis1.min(new PointCh(2800000, 1080000), 12839, 1000);
        RoutePoint expected1 = pointThis1;
        assertEquals(expected1, actual1);

        //This plus petit
        RoutePoint pointThis2 = new RoutePoint(new PointCh(2800000, 1080000), 12839, 500);
        RoutePoint actual2 = pointThis2.min(new PointCh(2800000, 1080000), 12839, 5002);
        RoutePoint expected2 = pointThis2;
        assertEquals(expected2, actual2);
    }

    @Test
    void withPositionShiftedBybasicTest()
    {
        RoutePoint rp = new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 10, 100);
        RoutePoint result = new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 110, 100);

        RoutePoint shifted = rp.withPositionShiftedBy(100);
        assertEquals(result.point().e(), shifted.point().e());
        assertEquals(result.point().n(), shifted.point().n());
        assertEquals(result.distanceToReference(), shifted.distanceToReference());
        assertEquals(result.position(), shifted.position());
    }

    @Test
    void MinTests()
    {
        RoutePoint rp = new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 10, 50);
        RoutePoint result = new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 110, 10);

        RoutePoint found = rp.min(result);
        assertEquals(result.point().e(), found.point().e());
        assertEquals(result.point().n(), found.point().n());
        assertEquals(result.distanceToReference(), found.distanceToReference());
        assertEquals(result.position(), found.position());
    }

    @Test
    void MinTestsNotCreated()
    {
        RoutePoint rp = new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 10, 50);

        RoutePoint found = rp.min(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 110, 100);
        assertEquals(rp.point().e(), found.point().e());
        assertEquals(rp.point().n(), found.point().n());
        assertEquals(rp.distanceToReference(), found.distanceToReference());
        assertEquals(rp.position(), found.position());
    }

    @Test
    void MinTestsCreated()
    {
        RoutePoint rp = new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 10, 50);
        RoutePoint result = new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 110, 10);

        RoutePoint found = rp.min(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 110, 10);
        assertEquals(result.point().e(), found.point().e());
        assertEquals(result.point().n(), found.point().n());
        assertEquals(result.distanceToReference(), found.distanceToReference());
        assertEquals(result.position(), found.position());
    }

    @Test
    public void withPositionShiftedByReturnsShiftedPointPosition() {
        RoutePoint base = new RoutePoint(new PointCh(2_485_000, 1_075_000), 1500, 3000);
        RoutePoint test = new RoutePoint(new PointCh(2_485_000, 1_075_000), 1800, 3000);
        assertEquals(test, base.withPositionShiftedBy(300));
        RoutePoint base2 = new RoutePoint(new PointCh(2_485_000, 1_075_000), 1500, 3000);
        RoutePoint test2 = new RoutePoint(new PointCh(2_485_000, 1_075_000), 1200, 3000);
        assertEquals(test2, base2.withPositionShiftedBy(-300));
    }

    @Test
    public void minReturnsThisIfItsDistanceToReferenceIsBelowOrEqualToThat() {
        RoutePoint base = new RoutePoint(new PointCh(2_485_000, 1_075_000), 1500, 3000);
        RoutePoint test = new RoutePoint(new PointCh(2_485_000, 1_075_093), 1800, 3000);
        assertEquals(base, base.min(test));
        RoutePoint base2 = new RoutePoint(new PointCh(2_485_000, 1_075_000), 1500, 2999.99);
        RoutePoint test2 = new RoutePoint(new PointCh(2_485_000, 1_075_030), 1800, 3000);
        assertEquals(base, base.min(test));
    }

    @Test
    public void minReturnsThatifThisDistanceToRefIsGreater() {
        RoutePoint base = new RoutePoint(new PointCh(2_485_000, 1_075_000), 1500, 3000);
        RoutePoint test = new RoutePoint(new PointCh(2_485_000, 1_075_023), 1800, 2978);
        assertEquals(test, base.min(test));
    }

    @Test
    public void minReturnsThisIfDistanceToRefIsBelowOrEqualToThatDistanceToRef() {
        RoutePoint base = new RoutePoint(new PointCh(2_485_000, 1_075_000), 1500, 3000);
        assertEquals(base, base.min(new PointCh(2_485_000, 1_075_023), 1800, 3000));
        RoutePoint base2 = new RoutePoint(new PointCh(2_485_000, 1_075_000), 1500, 2745);
        assertEquals(base2, base2.min(new PointCh(2_485_000, 1_075_023), 1800, 3000));
    }

    @Test
    public void minReturnsNewInstanceOfRoutePointWithGivenArgumentsIfThisDistanceToRefIsGreater() {
        RoutePoint base = new RoutePoint(new PointCh(2_485_000, 1_075_000), 1500, 3000);
        RoutePoint test = new RoutePoint(new PointCh(2_485_000, 1_075_023), 1800, 2999.984029348);
        assertEquals(test, base.min(new PointCh(2_485_000, 1_075_023), 1800, 2999.984029348));
    }

    PointCh p = new PointCh(2537737.9706338, 1152665.627210865);
    RoutePoint rP = new RoutePoint(p, 176, 116);

    @Test
    void withPositionShiftedByWorks(){
        RoutePoint rPs = rP.withPositionShiftedBy(5);
        //AYA : Meme erreur que les garcons
        //NORMAL
        // assertEquals(rPs, new RoutePoint(p, 181, 116.3));

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
