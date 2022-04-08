package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

/**
 * Represents an edge of the itinerary
 * @author Lina Sadgal
 */

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint, double length,
                   DoubleUnaryOperator profile) {

    /**
     *creates a new Edge
     * @param graph the edge's belongs to
     * @param edgeId the edge's identity
     * @param fromNodeId the identity of the first node in the
     * @param toNodeId
     * @return a new Edge
     */
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId){
        return new Edge(fromNodeId, toNodeId, graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId),
                graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    /**
     * Gives the closest position according to the edge to the PointCh given
     * @param point the PointCh we want to give the closest position in the edge to
     * @return the double corresponding closest position along the edge to the given PointCh
     */
    public double positionClosestTo(PointCh point){
        return Math2.projectionLength(fromPoint.e(), fromPoint.n(), toPoint.e(),
                toPoint.n(), point.e(), point.n());
    }

    /**
     * Gives the PointCh located in the given position in the edge
     * @param position the given position we want to find the PointCh in.
     * @return the PointCh in the given position in the edge.
     */
    public PointCh pointAt(double position){
        //The percentage of the edge at which we find the position
        double percentage = position / length;
        double y = Math2.interpolate(fromPoint.n(), toPoint.n(), percentage);
        double x = Math2.interpolate(fromPoint.e(), toPoint.e(), percentage);
        return new PointCh(x, y);
    }

    /**
     * Gives the elevation for a given position in meters.
     * @param position given position for the height
     * @return the double corresponding to the height in meters corresponding to the position given,
     * on the edge.
     */
    public double elevationAt(double position){
        return profile.applyAsDouble(position);
    }

}
