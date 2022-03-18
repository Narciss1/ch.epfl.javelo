package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.binarySearch;

public final class SingleRoute implements Route {

    private List<Edge> edges;

    /**
     * Constructor
     * @param edges a given list of edges
     */
    public SingleRoute(List<Edge> edges) {
        if (edges.isEmpty()) {
            throw new IllegalArgumentException();
        } else {
            this.edges = edges;
        }
    }

    /**
     * @param position a given position
     * @return the index of the route segment containing the given position,
     * which is always 0 in the case of a simple route
     */
    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
        //QUESTION 1: Il faut bien laisser le 0?
    }

    /**
     * @return the length of the itinerary in meters
     */
    @Override
    public double length() {
        double length = 0;
        for (int i = 0; i < edges.size(); ++i) {
            length += edges.get(i).length();
        }
        return length;
        //QUESTION 2: Correspond bien à la somme des longueurs de toutes les arêtes?
    }

    /**
     * Makes a list of the totality of the edges of the itinerary
     * @return the totality of the edges of the itinerary
     */
    @Override
    public List<Edge> edges() {
        return edges;
        //QUESTION 3: Faut bien return la liste de base sans modif?
    }

    /**
     * @return all the points located at the extremities of the edges of the itinerary
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> points = new ArrayList<>();
        for (int i = 0; i < edges.size(); ++i) {
            points.add(edges.get(i).fromPoint());
        }
        points.add(edges.get(edges.size() - 1).toPoint());
        return points;
        //QUESTION 4: Est-ce le bon ordre dans la liste?
    }

    /**
     * @param position a given position
     * @return the point at the given position along the itinerary
     */
    @Override
    public PointCh pointAt(double position) {
        rightPosition(position);
        List<Double> positionAllNodes = positionAllNodes();
        int nodeIndex = binarySearch(positionAllNodes, position);
        int edgeIndex = -nodeIndex - 2;
        if (nodeIndex >= 0) {
            return edges.get(nodeIndex).fromPoint();
        } else {
            return edges.get(edgeIndex).pointAt(position - positionAllNodes.get(edgeIndex));
        }
        //Vérifier si c'est la bonne logique?
    }

    /**
     * @param position a given position
     * @return  the altitude at the given position along the itinerary,
     * which can be NaN if the edge containing this position has no profile,
     */
    @Override
    public double elevationAt(double position) {
        rightPosition(position);
        List<Double> positionAllNodes = positionAllNodes();
        int nodeIndex = binarySearch(positionAllNodes, position);
        int edgeIndex = -nodeIndex - 2;
        if (nodeIndex >= 0) {
            return edges.get(nodeIndex).elevationAt(0);
        } else {
            return edges.get(edgeIndex).elevationAt(position - positionAllNodes.get(edgeIndex));
        }
        //Vérifier si c'est la bonne logique?
        //Est-ce que le fait que l'arête a un profil ou pas est déjà vérifié?
    }

    /**
     * @param position a given position
     * @return the identity of the node belonging to the itinerary and
     * located closest to the given position
     */
    @Override
    public int nodeClosestTo(double position) {
        rightPosition(position);
        List<Double> positionAllNodes = positionAllNodes();
        int nodeIndex = binarySearch(positionAllNodes, position);
        int edgeIndex = -nodeIndex - 2;
        if (nodeIndex >= 0) {
            return edges.get(nodeIndex).fromNodeId();
        } else {
            return closestNode(position, edgeIndex, positionAllNodes);
        }
        //Vérifier si c'est la bonne logique?
    }

    /**
     * @param point a reference point
     * @return the point on the itinerary that is closest to the given reference point
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        double closestPosition = edges.get(0).positionClosestTo(point);
        PointCh closestPoint = edges.get(0).pointAt(closestPosition);
        return new RoutePoint(closestPoint, closestPosition, point.distanceTo(closestPoint));
    }
    //Piazza @476

    /**
     * Changes the value of the position if it is a negative value or
     * if it is greater than the length of the itinerary
     * @param position a given position
     */
    public void rightPosition(double position) {
        if (position < 0) { position = 0;}
        if (position > length()) { position = length();}
    }
    //Est-ce que ça change bien la valeur?

    /**
     * Makes a list of the positions of all the nodes of an itinerary
     * @return a list of the positions of all nodes
     */
    public List<Double> positionAllNodes() {
        List<Double> positionAllNodes = new ArrayList<>();
        double length = 0;
        positionAllNodes.add(length);
        for (int i = 0; i < edges.size(); ++i) {
            length += edges.get(i).length();
            positionAllNodes.add(length);
        }
        return positionAllNodes;
    }

    /**
     * Determines which one the first and last nodes of the edge is the closest to a certain position
     * @param position a given position
     * @param edgeIndex the index of an edge of the itinerary
     * @param positionAllNodes the list of the positions of all nodes
     * @return the closest node to a given position on an edge
     */
    public int closestNode (double position, int edgeIndex, List<Double> positionAllNodes) {
        double firstDistance = position - positionAllNodes.get(edgeIndex);
        double secondDistance = positionAllNodes.get(edgeIndex + 1) - position;
        if(firstDistance <= secondDistance) {
            return edges.get(edgeIndex).fromNodeId();
        } else {
            return edges.get(edgeIndex).toNodeId();
        }
    }
}