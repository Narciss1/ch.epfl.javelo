package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;
import java.util.ArrayList;
import java.util.List;
import static ch.epfl.javelo.Math2.clamp;
import static ch.epfl.javelo.Preconditions.checkArgument;
import static java.util.Collections.binarySearch;

/**
 * Represents a simple route, i.e. an itinerary that has no intermediate points
 * between its starting and ending points
 * Every simple route has a corresponding list of edges
 * @author Aya Hamane (345565)
 */
public final class SingleRoute implements Route {

    private final List<Edge> edges;
    private List<Double> positionAllNodes;

    /**
     * Constructor
     * @param edges list of edges
     * @throws IllegalArgumentException if the list of edges is empty
     */
    public SingleRoute(List<Edge> edges) {
        checkArgument(!edges.isEmpty());
        this.edges = List.copyOf(edges);
        positionAllNodes = new ArrayList<>();
        positionAllNodes = positionAllNodes();
    }

    /**
     * Determines the index of an itinerary segment
     * @param position position on the itinerary
     * @return index of the itinerary segment containing the given position,
     * which is always 0 in the case of a simple route
     */
    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    /**
     * Determines the closest node to a given position on the itinerary
     * @param position position on the itinerary
     * @return identity of the node belonging to the itinerary and located closest
     * to the given position
     */
    @Override
    public int nodeClosestTo(double position) {
        position = clamp(0, position, length());
        int nodeIndex = binarySearch(positionAllNodes, position);
        int edgeIndex = -nodeIndex - 2;
        if (nodeIndex < 0) {
            return closestNode(position, edgeIndex, positionAllNodes);
        }
        if (nodeIndex < edges.size()) {
            return edges.get(nodeIndex)
                    .fromNodeId();
        }
        return edges.get(nodeIndex - 1)
                .toNodeId();
    }

    /**
     * Determines the length of the itinerary
     * @return length of the itinerary in meters
     */
    @Override
    public double length() {
        double length = 0;
        for (Edge edge : edges) {
            length += edge.length();
        }
        return length;
    }

    /**
     * Determines the altitude at a given position along the itinerary
     * @param position position on the itinerary
     * @return altitude at a given position along the itinerary,
     * which can be NaN if the edge containing this position has no profile
     */
    @Override
    public double elevationAt(double position) {
        position = clamp(0, position, length());
        int nodeIndex = binarySearch(positionAllNodes, position);
        int edgeIndex = -nodeIndex - 2;
        if (nodeIndex < 0) {
            return edges.get(edgeIndex)
                    .elevationAt(position - positionAllNodes.get(edgeIndex));
        }
        if (nodeIndex < edges.size()) {
            return edges.get(nodeIndex)
                    .elevationAt(0);
        }
        double lastEdgeLength = edges.get(nodeIndex - 1)
                                 .length();
        return edges.get(nodeIndex - 1)
                .elevationAt(lastEdgeLength);
    }

    /**
     * Determines the point PointCh at a given position along the itinerary
     * @param position position along the itinerary
     * @return point at a given position along the itinerary
     */
    @Override
    public PointCh pointAt(double position) {
        position = clamp(0, position, length());
        int nodeIndex = binarySearch(positionAllNodes, position);
        int edgeIndex = -nodeIndex - 2;
        if(nodeIndex < 0) {
            return edges.get(edgeIndex)
                    .pointAt(position - positionAllNodes.get(edgeIndex));
        }
        return points().get(nodeIndex);
    }

    /**
     * Determines the closest point to a given reference point on the itinerary
     * @param point reference point
     * @return point on the itinerary that is the closest to the given reference point
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint closestPoint = RoutePoint.NONE;
        double edgesLength = 0;
        for (Edge edge : edges) {
            double newPosition = clamp(0, edge.positionClosestTo(point), edge.length());
            PointCh newPoint = edge.pointAt(newPosition);
            double distance = point.distanceTo(newPoint);
            closestPoint = closestPoint.min(newPoint, edgesLength + newPosition, distance);
            edgesLength += edge.length();
        }
        return closestPoint;
    }

    /**
     * Makes a list of the totality of the edges of the itinerary
     * @return list containing the totality of the edges of the itinerary
     */
    @Override
    public List<Edge> edges() {
        return edges;
    }

    /**
     * Makes a list of all the points located at the extremities of the edges of the itinerary
     * @return list containing the totality of those points
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> points = new ArrayList<>();
        for (Edge edge : edges) {
            points.add(edge.fromPoint());
        }
        PointCh lastPoint = edges.get(edges.size() - 1)
                             .toPoint();
        points.add(lastPoint);
        return points;
    }

    /**
     * Makes a list of the positions of all the nodes of an itinerary
     * @return list of the positions on the itinerary of all nodes
     */
    private List<Double> positionAllNodes() {
        double length = 0;
        positionAllNodes.add(length);
        for (Edge edge : edges) {
            length += edge.length();
            positionAllNodes.add(length);
        }
        return positionAllNodes;
    }

    /**
     * Determines which one of the first and last nodes of an edge is the closest to a certain position
     * @param position position
     * @param edgeIndex index of an edge of the itinerary
     * @param positionAllNodes list of the positions of all the itinerary's nodes
     * @return closest node to a given position on an edge
     */
    private int closestNode (double position, int edgeIndex, List<Double> positionAllNodes) {
        double firstDistance = position - positionAllNodes.get(edgeIndex);
        double secondDistance = positionAllNodes.get(edgeIndex + 1) - position;
        if(firstDistance <= secondDistance) {
            return edges.get(edgeIndex)
                    .fromNodeId();
        }
        return edges.get(edgeIndex)
                .toNodeId();
    }
}