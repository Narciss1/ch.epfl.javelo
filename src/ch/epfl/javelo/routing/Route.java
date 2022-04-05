package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;

public interface Route {

    /**
     * Finds the index of a segment at a given position in meters
     * @param position position
     * @return index of a segment
     */
    public abstract int indexOfSegmentAt(double position);

    /**
     * Finds the node belonging to the itinerary and located closest to
     * a certain position
     * @param position position
     * @return identity of the node belonging to the itinerary and located closest to
     * the given position
     */
    public abstract int nodeClosestTo(double position);

    /**
     * Calculates the length of the itinerary in meters
     * @return length of the itinerary
     */
    public abstract double length();

    /**
     * Finds the altitude at the given position along the itinerary
     * @param position position
     * @return altitude at a given position along the itinerary
     */
    public abstract double elevationAt(double position);

    /**
     * Finds the point at the given position along the itinerary
     * @param position position
     * @return point at the given position along the itinerary
     */
    public abstract PointCh pointAt(double position);

    /**
     * Finds the point on the route that is closest to a given point
     * @param point reference point
     * @return point on the route that is closest to the given reference point
     */
    public abstract RoutePoint pointClosestTo(PointCh point);

    /**
     * Finds all the edges of the itinerary
     * @return list of the edges of the itinerary
     */
    public abstract List<Edge> edges();

    /**
     * Finds all the points located at the extremities of the edges of the itinerary
     * @return list of the points located at the extremities of the edges
     */
    public abstract List<PointCh> points();
}
