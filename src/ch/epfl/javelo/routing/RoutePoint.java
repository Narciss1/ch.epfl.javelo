package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import static java.lang.Double.POSITIVE_INFINITY;

public record RoutePoint(PointCh point, double position, double distanceToReference) {
    //QUESTION 1: Ainsi le point est nul?
    /**
     * A constant representing a non-existent point
     */
    public static final RoutePoint NONE = new RoutePoint(null, Double.NaN, POSITIVE_INFINITY);

    /**
     * Finds a point identical to the receiver (this), but whose position is shifted by the given
     * difference
     * @param positionDifference a given position difference, which can be positive or negative
     * @return a point identical to the receiver but whose position has been shifted
     *
     */
    //PIAZZA @296
    public RoutePoint withPositionShiftedBy(double positionDifference) {
        return new RoutePoint(this.point, this.position + positionDifference,
                this.distanceToReference);
    }

    //PIAZZA @297
    /*public RoutePoint min(RoutePoint that) {

    }

    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference) {

    }*/
}

