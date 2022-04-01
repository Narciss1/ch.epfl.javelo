package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;
import jdk.swing.interop.SwingInterOpUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ch.epfl.javelo.Math2.clamp;

public final class MultiRoute implements Route{

    private final List<Route> segments;

    /**
     * Constructor
     * @param segments a given list of routes
     */
    public MultiRoute(List<Route> segments) {
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
    }

    /**
     * Determines the index of an itinerary segment
     * @param position a given position on the itinerary
     * @return the index of the itinerary segment containing the given position
     */
    @Override
    public int indexOfSegmentAt(double position) {
        position = clamp(0, position, length());
        int previousIndex = 0;
        double length = 0;
        for(Route route: segments){
            position -= length;
            length = route.length();
            if(rightRange(position, length)) {
                return previousIndex + route.indexOfSegmentAt(position);
            }
            previousIndex += route.indexOfSegmentAt(length) + 1;
        }
        return previousIndex - 1;
    }

    /**
     * Determines the length of the itinerary
     * @return the length of the itinerary in meters
     */
    @Override
    public double length() {
        double length = 0;
        for (Route route: segments) {
            length += route.length();
        }
        return length;
    }

    /**
     * Makes a list of the totality of the edges of the itinerary
     * @return the totality of the edges of the itinerary
     */
    @Override
    public List<Edge> edges() {
        List<Edge> edges= new ArrayList<Edge>();
        for (Route route: segments) {
            edges.addAll(route.edges());
        }
        return edges;
    }

    /**
     * Makes a list of all the points located at the extremities of the edges of the itinerary,
     * without duplicates
     * @return the lists of the totality of those points
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> points = new ArrayList<>();
        for (Route route: segments) {
            if (!points.isEmpty()) {
                points.remove(points.size() - 1);
            }
            points.addAll(route.points());
        }
        return points;
    }

    /**
     * Determines the point PointCh at a given position along the itinerary
     * @param position a given position along the itinerary
     * @return the point at a given position along the itinerary
     */
    @Override
    public PointCh pointAt(double position) {
        position = clamp(0, position, this.length());
        double length = 0;
        for(Route route: segments) {
            position -= length;
            length = route.length();
            if(rightRange(position, length)) {
                return route.pointAt(position);
            }
        }
        return segments.get(segments.size() - 1).pointAt(position);
    }

    /**
     * Determines the altitude at a given position along the itinerary
     * @param position a given position on the itinerary
     * @return the altitude at a given position along the itinerary,
     * which can be NaN if the edge containing this position has no profile
     */
    @Override
    public double elevationAt(double position) {
        position = clamp(0, position, this.length());
        double length = 0;
        for(Route route: segments){
            position -= length;
            length = route.length();
            if(rightRange(position, length)) {
                return route.elevationAt(position);
            }
        }
        return segments.get(segments.size() - 1).elevationAt(position);
    }

    /**
     * Determines the closest node to a given position on the itinerary
     * @param position a certain position on the itinerary
     * @return the identity of the node belonging to the itinerary and located closest
     * to the given position
     */
    @Override
    public int nodeClosestTo(double position) {
        position = clamp(0, position, this.length());
        double length = 0;
        for(Route route: segments){
            position -= length;
            length = route.length();
            if(rightRange(position, length)) {
                return route.nodeClosestTo(position);
            }
        }
        return segments.get(segments.size() - 1).nodeClosestTo(position);
    }

    /**
     * Determines the closest point to a given reference point on the itinerary
     * @param point a reference point
     * @return the point on the itinerary that is closest to the given reference point
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint closestPoint = RoutePoint.NONE;
        double segmentsLength = 0;
        for (Route route: segments) {
            RoutePoint newClosePoint = route.pointClosestTo(point);
            closestPoint = closestPoint.min(newClosePoint.withPositionShiftedBy(segmentsLength));
            segmentsLength += route.length();
        }
        return closestPoint;
    }

    /**
     * Determines if a position is in a given range or not
     * @param position a given position
     * @param length a given length
     * @return true if the position's value is between 0 and the length and false otherwise
     */
    private boolean rightRange(double position, double length) { return position >= 0 && position <= length;}
}
