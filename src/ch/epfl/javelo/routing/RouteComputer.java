package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.HashSet;

import static java.util.Arrays.fill;

public final class RouteComputer {

    private final Graph graph;
    private final CostFunction costFunction;


    //Faut-il rajouter des trucs à ce constructeur pour immuabilité ?
    public RouteComputer(Graph graph, CostFunction costFunction){
        this.graph = graph;
        this.costFunction = costFunction;
    }

    public Route bestRouteBetween(int startNodeId, int endNodeId) {
        Preconditions.checkArgument(startNodeId != endNodeId);
        double[] distances = new double[graph.nodeCount()];
        distances[0] = 0;
        double[] predecessors = new double[graph.nodeCount()];
        fill(distances, 1, distances.length, Double.POSITIVE_INFINITY);
        fill(predecessors, 0, predecessors.length, -1);
        HashSet<Integer> explorating = new HashSet<>();
        explorating.add(startNodeId);
        while (!explorating.isEmpty()) {
            int currentNode = currentNode(distances, explorating);
            if (currentNode == endNodeId) {
                //Faut retourner la Route avec les bonnes edges. (Piazza) ToDO.
            } else { //else à tej vu qu'on mettre return plus haut.
                for (int i = 0; i < graph.nodeOutDegree(currentNode); ++i){
                    int targetNode = graph.edgeTargetNodeId(graph.nodeOutEdgeId(currentNode, i));
                    double potentialDistance = graph.edgeLength(graph.nodeOutEdgeId(currentNode, i));
                    if (potentialDistance < distances[targetNode]){
                        distances[targetNode] = potentialDistance;
                        predecessors[targetNode] = currentNode;
                        explorating.add(targetNode);
                    }
                }
            }
        }
        return null;
    }

    private int currentNode (double[] distances, HashSet<Integer> explorating){
        double distanceToKeep = Double.POSITIVE_INFINITY;
        int nodeToKeep = 0;
        for (Integer node : explorating){
            if (distances[node] < distanceToKeep){
                nodeToKeep = node;
            }
        }
        return nodeToKeep;
    }

}
