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
        int previousIndex = 0;
        double length = 0;
        double newPosition = clamp(0, position, this.length());
        for(int i = 0; i < segments.size(); ++i){
            newPosition -= length;
            length = segments.get(i).length();
            if(rightRange(newPosition, length)) {
                return previousIndex + segments.get(i).indexOfSegmentAt(newPosition);
            }
            previousIndex += segments.get(i).indexOfSegmentAt(length) + 1;
        }
        //Tester pour mieux comprendre
        return previousIndex - 1;
    }

    /**
     * Determines the length of the itinerary
     * @return the length of the itinerary in meters
     */
    @Override
    public double length() {
        double length = 0;
        for (int i = 0; i < segments.size(); ++i) {
            length += segments.get(i).length();
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
        for (int i = 0; i < segments.size(); ++i) {
            edges.addAll(segments.get(i).edges());
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
        //Is set a better choice?
        List<PointCh> points = new ArrayList<>();
        for (int i = 0; i < segments.size(); ++i) {
            //if(points.isEmpty()){
                //points.addAll(segments.get(i).points());
                for(PointCh point : segments.get(i).points()){
                    if(!points.contains(point)){
                        points.add(point);
                    }
                }
           /* } else {
                points.remove(points.size() - 1);
                points.addAll(segments.get(i).points());
            }*/
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
        double newPosition = clamp(0, position, this.length());
        double length = 0;
        for(int i = 0; i < segments.size(); ++i){
            newPosition -= length;
            length = segments.get(i).length();
            if(rightRange(newPosition, length)) {
                return segments.get(i).pointAt(newPosition);
            }
        }
        return segments.get(segments.size() - 1).pointAt(newPosition);
    }

    /**
     * Determines the altitude at a given position along the itinerary
     * @param position a given position on the itinerary
     * @return the altitude at a given position along the itinerary,
     * which can be NaN if the edge containing this position has no profile
     */
    @Override
    public double elevationAt(double position) {
        double newPosition = clamp(0, position, this.length());
        double length = 0;
        for(int i = 0; i < segments.size(); ++i){
            newPosition -= length;
            length = segments.get(i).length();
            if(rightRange(newPosition, length)) {
                return segments.get(i).elevationAt(newPosition);
            }
        }
        return segments.get(segments.size() - 1).elevationAt(newPosition);
    }

    /**
     * Determines the closest node to a given position on the itinerary
     * @param position a certain position on the itinerary
     * @return the identity of the node belonging to the itinerary and located closest
     * to the given position
     */
    @Override
    public int nodeClosestTo(double position) {
        double newPosition = clamp(0, position, this.length());
        double length = 0;
        for(int i = 0; i < segments.size(); ++i){
            newPosition -= length;
            length = segments.get(i).length();
            if(rightRange(newPosition, length)) {
                return segments.get(i).nodeClosestTo(newPosition);
            }
        }
        return segments.get(segments.size() - 1).nodeClosestTo(newPosition);
    }

    /**
     * Determines the closest point to a given reference point on the itinerary
     * @param point a reference point
     * @return the point on the itinerary that is closest to the given reference point
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint closestPoint = RoutePoint.NONE;
        double segmentsLength = 0d;
        for (int i = 0; i < segments.size(); ++i) {
            RoutePoint newClosePoint = segments.get(i).pointClosestTo(point);
            closestPoint = closestPoint.min(newClosePoint.withPositionShiftedBy(segmentsLength));
            segmentsLength += segments.get(i).length();
        }
        return closestPoint;
    }

    /**
     * Determines if a position is in a given range or not
     * @param position a given position
     * @param length a given length
     * @return true if the position's value is between 0 and the length and false otherwise
     */
    private boolean rightRange(double position, double length){
        if(position >= 0 && position <= length){
            return true;
        }
        return false;
    }
}
