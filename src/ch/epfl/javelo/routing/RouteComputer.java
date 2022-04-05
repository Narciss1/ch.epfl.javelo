package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.*;

public final class RouteComputer {

    private final Graph graph;
    private final CostFunction costFunction;


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

        //It is better to have it stored here in order not to find the PointCh each time we
        //want to calculate the distance between a node and the one in the end.
        PointCh endNodePointCh = graph.nodePoint(endNodeId);

        PriorityQueue<WeightedNode> exploring = new PriorityQueue<>();
        exploring.add(new WeightedNode(startNodeId, distances[startNodeId]
                + (float) graph.nodePoint(startNodeId).distanceTo(endNodePointCh)
        ));


        while (!exploring.isEmpty()) {

            int currentNodeId = exploring.remove().nodeId;

            if (currentNodeId == endNodeId) {
                //Route route = constructRoute(predecessors, startNodeId, endNodeId);
                //System.out.println("Distance : " + route.length());
                return constructRoute(predecessors, startNodeId, endNodeId);
            }

            if (distances[currentNodeId] != Float.NEGATIVE_INFINITY){
                for (int i = 0; i < graph.nodeOutDegree(currentNodeId); ++i){
                    int edgeId = graph.nodeOutEdgeId(currentNodeId, i);
                    int targetNodeId = graph.edgeTargetNodeId(edgeId);
                    //Faut-il rajouter un check de negative Infinity a ce niveau ou pas la peine ?
                    float potentialDistance = (float) (distances[currentNodeId] +
                            graph.edgeLength(edgeId) * costFunction.costFactor(currentNodeId, edgeId));
                    if (potentialDistance < distances[targetNodeId]) {
                        distances[targetNodeId] = potentialDistance;
                        predecessors[targetNodeId] = currentNodeId;
                        exploring.add(new WeightedNode(targetNodeId, distances[targetNodeId]
                                + (float) graph.nodePoint(targetNodeId).distanceTo(endNodePointCh)
                        ));
                    }
                }
                distances[currentNodeId] = Float.NEGATIVE_INFINITY;
            }
        }
        return null;
    }

    /** Constructs the Route by finding its edges using predecessors array.
     *
     * @param predecessors the array containing a node's predecessor's identity, if it has one.
     * @param startNodeId  the identity of the first node of the itinerary
     * @param endNodeId    the identity of the last node of the itinerary
     * @return the Route corresponding to the itinerary
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
        //System.out.println("Edges number : " + edgesForRoute.size());
        Collections.reverse(edgesForRoute);
        return new SingleRoute(edgesForRoute);
    }



    /**
     * Calculates the straight distance between a node in the graph and the last node of
     * the itinerary
     * @param targetNodeId a node in the graph
     * @param endNodeId   the last node of the route
     * @return the distance between the two nodes given as parameters.
     */
    private float distanceBetweenNodes(int targetNodeId, int endNodeId) {
        return (float) graph.nodePoint(targetNodeId).distanceTo(graph.nodePoint(endNodeId));
    }

}