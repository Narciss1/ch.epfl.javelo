package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.binarySearch;

public final class SingleRoute implements Route {

    private List<Edge> edges;

    /**
     * Constructor
     *
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
     * @return
     */
    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
        //QUESTION 1: Il faut bien laisser le 0?
    }

    /**
     * @return
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
     * @return
     */
    @Override
    public List<Edge> edges() {
        return edges;
        //QUESTION 3: Faut bien return la liste de base sans modif?
    }

    /**
     * @return
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
     * @return
     */
    @Override
    public PointCh pointAt(double position) {
        rightPosition(position);
        List<Double> positionAllNodes = positionAllNodes(position);
        int nodeIndex = binarySearch(positionAllNodes, position);
        int edgeIndex = -nodeIndex - 2;
        if (nodeIndex >= 0) {
            return edges.get(nodeIndex).fromPoint();
        } else {
            return edges.get(edgeIndex).pointAt(position - positionAllNodes.get(edgeIndex));
        }
        //Vérifier si c'est la bonne logique et si la somme des length est correcte?
        //Piazza @479
    }

    /**
     * @param position
     * @return
     */
    @Override
    public double elevationAt(double position) {
        rightPosition(position);
        List<Double> positionAllNodes = positionAllNodes(position);
        int nodeIndex = binarySearch(positionAllNodes, position);
        int edgeIndex = -nodeIndex - 2;
        if (nodeIndex >= 0) {
            return edges.get(nodeIndex).elevationAt(0);
        } else {
            return edges.get(edgeIndex).elevationAt(position - positionAllNodes.get(edgeIndex));
        }
        //Vérifier si c'est la bonne logique et si la somme des length est correcte?
        //Comment peut-on savoir si l'arête contenant cette position n'a pas de profil?
        //Piazza @478
    }

    /**
     * @param position a given position
     * @return
     */
    @Override
    public int nodeClosestTo(double position) {
        rightPosition(position);
        List<Double> positionAllNodes = positionAllNodes(position);
        int nodeIndex = binarySearch(positionAllNodes, position);
        int edgeIndex = -nodeIndex - 2;
        if (nodeIndex >= 0) {
            return edges.get(nodeIndex).toNodeId();
        }
        return 0;
        //else {return edges.get(edgeIndex).pointAt(position - positionAllNodes.get(edgeIndex));}
        //Should just get un des deux nodeId sur les extrémités de l'arête or get d'autres?
        //Vérifier si c'est la bonne logique et si la somme des length est correcte?
        //Piazza @477
    }

    /**
     * @param point a reference point
     * @return
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        return null;
    }
    //Piazza @476

    /**
     *
     * @param pos
     */
    public void rightPosition(double pos) {
        if (pos < 0) { pos = 0;}
        if (pos > length()) { pos = length();}
    }

    /**
     *
     * @param position
     * @return
     */
    public List<Double> positionAllNodes(double position) {
        List<Double> positionAllNodes = new ArrayList<>();
        double length = 0;
        positionAllNodes.add(0.0);
        for (int i = 0; i < edges.size(); ++i) {
            length += edges.get(i).length();
            positionAllNodes.add(length);
        }
        return positionAllNodes;
    }
}