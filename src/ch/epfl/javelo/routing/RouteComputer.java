package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.*;

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

        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Double.compare(this.distance, that.distance);
            }
        }

        float[] distances = new float[graph.nodeCount()];
        int[] predecessors = new int[graph.nodeCount()];// Quand tu vas passer sur toutes la suisse les
        //10 millions de nodes à mettre dans le tableau ça va etre complique, guigui a dit de revoir ça, tu peux faire mieu :/
        fill(distances, 0, distances.length, Float.POSITIVE_INFINITY);
        distances[startNodeId] = 0;

        PriorityQueue<WeightedNode> exploring = new PriorityQueue<>();
        exploring.add(new WeightedNode(startNodeId, distances[startNodeId]));

        HashSet<Integer> explorating = new HashSet<>();
        explorating.add(startNodeId);

        while (!exploring.isEmpty()) {

            int currentNode = currentNode(distances, explorating);
            explorating.remove(currentNode);

            int currentNod = exploring.remove().nodeId;

            if (currentNod == endNodeId) {
                return constructRoute(predecessors, startNodeId, endNodeId);
            }

            for (int i = 0; i < graph.nodeOutDegree(currentNode); ++i){
                int targetNode = graph.edgeTargetNodeId(graph.nodeOutEdgeId(currentNode, i));
                float potentialDistance = (float) (distances[currentNode] +
                        graph.edgeLength(graph.nodeOutEdgeId(currentNode, i)) *
                        costFunction.costFactor(currentNode, graph.nodeOutEdgeId(currentNode, i)));
                if (potentialDistance < distances[targetNode]) {
                    distances[targetNode] = potentialDistance;
                    predecessors[targetNode] = currentNode;
                    explorating.add(targetNode);
                    exploring.add(new WeightedNode(targetNode, potentialDistance));
                }
            }
        }
        return null;
    }

    private Route constructRoute(int[] predecessors, int startNodeId, int endNodeId){
        List<Edge> edgesForRoute = new ArrayList<>();
        int currentNode = endNodeId;
        while (currentNode != startNodeId){
            int edgeId = -1;
            for (int i = 0; i < graph.nodeOutDegree(predecessors[currentNode]); ++i){
                if (graph.edgeTargetNodeId(graph.nodeOutEdgeId(predecessors[currentNode], i)) == currentNode){
                    edgeId = graph.nodeOutEdgeId(currentNode, i);
                }
            }
            edgesForRoute.add(Edge.of(graph, edgeId, predecessors[currentNode], currentNode));
            currentNode = predecessors[currentNode];
        }
        Collections.reverse(edgesForRoute);
        return new SingleRoute(edgesForRoute);
    }

    private int currentNode (float[] distances, HashSet<Integer> explorating){
        double distanceToKeep = Double.POSITIVE_INFINITY;
        int nodeToKeep = 0;
        for (Integer node : explorating){
            if (distances[node] <= distanceToKeep){
                distanceToKeep = distances[node];
                nodeToKeep = node;
            }
        }
        return nodeToKeep;
    }

}
