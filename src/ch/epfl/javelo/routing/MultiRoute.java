package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.javelo.Math2.clamp;

public final class MultiRoute implements Route{

    private final List<Route> segments;

    public MultiRoute(List<Route> segments) {
        this.segments = List.copyOf(segments);
    }

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
        return previousIndex + segments.get(segments.size() - 1).indexOfSegmentAt(newPosition);
    }

    @Override
    public double length() {
        double length = 0;
        for (int i = 0; i < segments.size(); ++i) {
            length += segments.get(i).length();
        }
        return length;
    }

    @Override
    public List<Edge> edges() {
        List<Edge> edges= new ArrayList<Edge>();
        for (int i = 0; i < segments.size(); ++i) {
            edges.addAll(segments.get(i).edges());
        }
        return edges;
    }

    @Override
    public List<PointCh> points() {
        List<PointCh> points = new ArrayList<>();
        for (int i = 0; i < segments.size(); ++i) {
            if(points.isEmpty()){
                points.addAll(segments.get(i).points());
            } else {
                points.remove(points.size() - 1);
                points.addAll(segments.get(i).points());
            }
        }
        return points;
    }

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

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint closestPoint = RoutePoint.NONE;
        for (int i = 0; i < segments.size(); ++i) {
            RoutePoint newClosePoint = segments.get(i).pointClosestTo(point);
            closestPoint = closestPoint.min(newClosePoint);
        }
        return closestPoint;
    }

    /**
     *
     * @param position
     * @param length
     * @return
     */
    private boolean rightRange(double position, double length){
        if(position >= 0 && position <= length){
            return true;
        }
        return false;
    }
}