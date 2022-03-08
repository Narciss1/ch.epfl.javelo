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
    public RoutePoint withPositionShiftedBy(double positionDifference) {
        return new RoutePoint(this.point, this.position + positionDifference,
                this.distanceToReference);
    }

    /**
     *
     * @param that
     * @return
     */
    public RoutePoint min(RoutePoint that) {
        if(this.distanceToReference <= that.distanceToReference){
            return this;
        }
        return that;
    }

    /**
     *
     * @param thatPoint
     * @param thatPosition
     * @param thatDistanceToReference
     * @return
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference) {
        if(this.distanceToReference <= thatDistanceToReference){
            return this;
        }
        return new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
    }
}

