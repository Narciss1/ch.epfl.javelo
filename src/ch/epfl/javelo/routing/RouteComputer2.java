package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.data.GraphEdges;
import ch.epfl.javelo.data.GraphNodes;

import java.util.*;

public class RouteComputer2 {

    Graph graph;
    CostFunction costFunction;

    RouteComputer2(Graph graph, CostFunction costFunction){
        this.graph = graph;
        this.costFunction = costFunction;
    }

    private List<Edge> findEdgefromNodes (Graph graph, int[] predecesseur, int startingNodeId, int endingNodeId) {

        List<Edge> listEdges = new ArrayList<>();
        int nodeId = endingNodeId;
        int edgeId;
        while (nodeId != startingNodeId) {
//            for (int i = 0; i < graph.nodeOutDegree(predecesseur[nodeId]); i++) {
//                edgeId = graph.nodeOutEdgeId(predecesseur[nodeId], i);
//                if (graph.edgeTargetNodeId(edgeId) == nodeId) {
//                    listEdges.add(Edge.of(graph, edgeId,  predecesseur[nodeId], nodeId));
//                    //System.out.println(edgeId);
//                    break;
//                }
//            }

            int i = 0;
            while (graph.edgeTargetNodeId(graph.nodeOutEdgeId(predecesseur[nodeId], i)) != nodeId){
                ++i;
            }
            edgeId = graph.nodeOutEdgeId(predecesseur[nodeId], i);
            //System.out.println(edgeId);
            listEdges.add(Edge.of(graph, edgeId, predecesseur[nodeId], nodeId));
            nodeId = predecesseur[nodeId];
        }

        Collections.reverse(listEdges);
        return listEdges;
    }

    Route bestRouteBetween(int startNodeId, int endNodeId){

        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        Preconditions.checkArgument(startNodeId!=endNodeId);

        //Start of the method sorta
        float[] distance = new float[graph.nodeCount()];
        int[] predecesseur = new int[graph.nodeCount()];
        PriorityQueue<WeightedNode> en_exploration = new PriorityQueue<>();

        Arrays.fill(distance, Float.POSITIVE_INFINITY);
        Arrays.fill(predecesseur, 0);

        distance[startNodeId] = 0;
        WeightedNode firstNode = new WeightedNode(startNodeId, distance[startNodeId]);
        en_exploration.add(firstNode);

        while (!en_exploration.isEmpty()) {
            WeightedNode N = en_exploration.remove();
            if (N.nodeId == endNodeId) {
                SingleRoute r = new SingleRoute(findEdgefromNodes(graph, predecesseur, startNodeId, endNodeId));
                //System.out.println("length: " + r.length());
                //System.out.println("nÂºnodes: " + findEdgefromNodes(graph, predecesseur, startNodeId, endNodeId).size());
                return r;
            }

            //For each edge A out of Node N
            for (int i = 0; i < graph.nodeOutDegree(N.nodeId); i++) {
                int N_PrimeId = graph.edgeTargetNodeId(graph.nodeOutEdgeId(N.nodeId, i));
                double d = distance[N.nodeId] + costFunction.costFactor(N.nodeId, graph.nodeOutEdgeId(N.nodeId, i))*graph.edgeLength(graph.nodeOutEdgeId(N.nodeId, i));

                if (d < distance[N_PrimeId]) {
                    distance[N_PrimeId] = (float) d;
                    predecesseur[N_PrimeId] = N.nodeId;
                    en_exploration.add(new WeightedNode(N_PrimeId, distance[N_PrimeId]));
                }
            }
        }
        return null;
    }

}

