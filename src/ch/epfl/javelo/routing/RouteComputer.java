package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.*;

public final class RouteComputer {

    private final Graph graph;
    private final CostFunction costFunction;


    //Faut-il rajouter des trucs à ce constructeur pour immuabilité ?
    public RouteComputer(Graph graph, CostFunction costFunction){
        this.graph = graph;
        this.costFunction = costFunction;
    }


    /** calculates the best route between two nodes.
     *
     * @param startNodeId the node we start the itinerary from
     * @param endNodeId   the node we end the itinerary in
     * @return  the best itinerary for a bike between the two nodes
     */
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
        Arrays.fill(distances,Float.POSITIVE_INFINITY);
        distances[startNodeId] = 0;
        int[] predecessors = new int[graph.nodeCount()];

        PriorityQueue<WeightedNode> exploring = new PriorityQueue<>();
        exploring.add(new WeightedNode(startNodeId, distances[startNodeId]
                //+ distanceBetweenNodes(startNodeId, endNodeId)
        ));

        while (!exploring.isEmpty()) {

            int currentNode = exploring.remove().nodeId;

            if (currentNode == endNodeId) {
                Route route = constructRoute(predecessors, startNodeId, endNodeId);
                System.out.println("Distance : " + route.length());
                return constructRoute(predecessors, startNodeId, endNodeId);
            }

            if (distances[currentNode] != Float.NEGATIVE_INFINITY){
                for (int i = 0; i < graph.nodeOutDegree(currentNode); ++i){
                    int edgeId = graph.nodeOutEdgeId(currentNode, i);
                    int targetNodeId = graph.edgeTargetNodeId(edgeId);
                    float potentialDistance = (float) (distances[currentNode] +
                            graph.edgeLength(edgeId) * costFunction.costFactor(currentNode, edgeId));
                    if (potentialDistance < distances[targetNodeId]) {
                        distances[targetNodeId] = potentialDistance;
                        predecessors[targetNodeId] = currentNode;
                        exploring.add(new WeightedNode(targetNodeId, distances[targetNodeId]
                                //+ distanceBetweenNodes(targetNodeId, endNodeId)
                        ));
                    }
                }
                distances[currentNode] = Float.NEGATIVE_INFINITY;
            }
        }
        return null;
    }

    /** Constructs the Route by finding its edges using predecessors array.
     *
     * @param predecessors
     * @param startNodeId
     * @param endNodeId
     * @return
     */
    private Route constructRoute(int[] predecessors, int startNodeId, int endNodeId){
        List<Edge> edgesForRoute = new ArrayList<>();
        int currentNode = endNodeId;
        int edgeId;
        while (currentNode != startNodeId){
            int i = 0;
            while (graph.edgeTargetNodeId(graph.nodeOutEdgeId(predecessors[currentNode], i)) != currentNode){
                ++i;
            }
            edgeId = graph.nodeOutEdgeId(predecessors[currentNode], i);
            edgesForRoute.add(Edge.of(graph, edgeId, predecessors[currentNode], currentNode));
            currentNode = predecessors[currentNode];
        }

        System.out.println("Edges number : " + edgesForRoute.size());
        Collections.reverse(edgesForRoute);
        return new SingleRoute(edgesForRoute);
    }



    //On retourne un double ou un float ?
    //Est-ce mieux de mettre le gros calcul comme ça ou d'abord de stocker les
    //PointCh qlq part ?

    /** Calculates the distance between a poten
     *
     * @param targetNodeId
     * @param endNodeId
     * @return the distance between
     */
    private float distanceBetweenNodes(int targetNodeId, int endNodeId) {
        return (float) graph.nodePoint(targetNodeId).distanceTo(graph.nodePoint(endNodeId));
    }

}