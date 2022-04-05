package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import static java.lang.Double.POSITIVE_INFINITY;

public record RoutePoint(PointCh point, double position, double distanceToReference) {

    /**
     * Constant representing a non-existent point
     */
    public static final RoutePoint NONE = new RoutePoint(null, Double.NaN, POSITIVE_INFINITY);

    /**
     * Finds a point identical to the receiver (this), but whose position is shifted by the given
     * difference
     * @param positionDifference position difference, which can be positive or negative
     * @return point identical to the receiver but whose position has been shifted
     */
    public RoutePoint withPositionShiftedBy(double positionDifference) {
        return new RoutePoint(this.point, this.position + positionDifference,
                this.distanceToReference);
    }

    /**
     * Determines the point of the itinerary whose distance to the reference is the smallest
     * @param that point of the itinerary
     * @return point of the itinerary "this" if its distance to the reference is less than
     * or equal to the point "that"'s distance to the reference, and the point "that" otherwise
     */
    public RoutePoint min(RoutePoint that) {
        if(this.distanceToReference <= that.distanceToReference){
            return this;
        }
        return that;
    }

    /**
     * Gives the point of the itinerary "this" if its distance to the reference is less than
     * or equal to thatDistanceToReference, and otherwise a new RoutePoint instance
     * @param thatPoint point of the itinerary
     * @param thatPosition position of the given point along the route, in meters
     * @param thatDistanceToReference distance in meters between the point and the reference
     * @return point of the itinerary "this" if its distance to the reference is less than
     * or equal to thatDistanceToReference, and otherwise a new RoutePoint instance whose attributes
     * are the arguments passed to the method min
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference) {
        if(this.distanceToReference <= thatDistanceToReference){
            return this;
        }
        return new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
    }
}

