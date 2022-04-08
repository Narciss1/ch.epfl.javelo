package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public final class Graph {

    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;

    /**
     * Gives a buffer of type ByteBuffer, whose content is that of a file in the directory
     * @param basePath path of the directory
     * @param string name of the file
     * @return buffer of type ByteBuffer
     */
    private static ByteBuffer fileBuffer(Path basePath, String string) throws IOException {
        Path stringPath = basePath.resolve(string);
        ByteBuffer stringBuffer;
        try(FileChannel channel = FileChannel.open(stringPath)) {
            stringBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
        return stringBuffer;
    }

    /**
     * Finds the graph obtained from the files located in a directory or throws
     * IOException in case of an input/output error
     * @param basePath path of the directory
     * @return graph obtained from the files located in the directory
     */
    public static Graph loadFrom(Path basePath) throws IOException {
        IntBuffer nodesBuffer = fileBuffer(basePath, "nodes.bin").asIntBuffer();
        ByteBuffer sectorsBuffer = fileBuffer(basePath, "sectors.bin");
        ByteBuffer edgesBuffer = fileBuffer(basePath, "edges.bin");
        IntBuffer profileIdsBuffer = fileBuffer(basePath, "profile_ids.bin").asIntBuffer();
        ShortBuffer elevationsBuffer = fileBuffer(basePath, "elevations.bin").asShortBuffer();
        LongBuffer attributesBuffer = fileBuffer(basePath, "attributes.bin").asLongBuffer();

        GraphNodes graphNodes = new GraphNodes(nodesBuffer);
        GraphSectors graphSectors = new GraphSectors(sectorsBuffer);
        GraphEdges graphEdges = new GraphEdges(edgesBuffer, profileIdsBuffer, elevationsBuffer);
        List<AttributeSet> attributeSetsBuffer = new ArrayList<>();
        for(int i = 0; i < attributesBuffer.capacity(); i++) {
            AttributeSet attributeSet = new AttributeSet(attributesBuffer.get(i));
            attributeSetsBuffer.add(attributeSet);
        }

        return new Graph(graphNodes, graphSectors, graphEdges, attributeSetsBuffer);
    }

    /**
     * Constructor
     * @param nodes array of all graph nodes
     * @param sectors array of all sectors
     * @param edges array of all graph edges
     * @param attributeSets list of all attribute sets
     */
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets){
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = List.copyOf(attributeSets);
    }

    /**
     * Calculates the total number of nodes in the graph
     * @return total number of nodes in the graph
     */
   public int nodeCount() {
       return nodes.count();
   }

    /**
     * Calculates the number of edges leaving a node
     * @param nodeId identity of a node
     * @return number of edges leaving the given identity node
     */
   public int nodeOutDegree(int nodeId) {
       return nodes.outDegree(nodeId);
   }

    /**
     * Calculates the identity of the edgeIndex-th edge outgoing from a node,
     * @param nodeId identity of a node
     * @param edgeIndex index of an edge
     * @return identity of the edgeIndex-th edge outgoing from the identity node nodeId
     */
   public int nodeOutEdgeId(int nodeId, int edgeIndex) {
       return nodes.edgeId(nodeId, edgeIndex);
   }

    /**
     * Finds the node closest to a given point at a certain distance
     * @param point point
     * @param searchDistance maximum distance in meters
     * @return identity of the node closest to the given point or -1 if no node
     * matches these criteria
     */
   public int nodeClosestTo(PointCh point, double searchDistance) {
       double minDistance = Math.pow(searchDistance, 2);
       int closestNode = -1;
       List<GraphSectors.Sector> closeSectors = sectors.sectorsInArea(point, searchDistance);
       for(GraphSectors.Sector sector : closeSectors){
          for(int j = sector.startNodeId(); j < sector.endNodeId(); j++){
              double distance = point.squaredDistanceTo(
                      new PointCh(nodes.nodeE(j), nodes.nodeN(j)));
              if(distance < minDistance){
                  minDistance = distance;
                  closestNode = j;
              }
          }
       }
       return closestNode;
   }

    /**
     * Calculates the identity of the destination node of an edge
     * @param edgeId identity of an edge
     * @return identity of the destination node of the given identity edge
     */
    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }

    /**
     * Calculates the length of an edge in meters
     * @param edgeId identity of an edge
     * @return length of the given identity edge
     */
    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }

    /**
     * Calculates the total elevation gain of an edge
     * @param edgeId identity of an edge
     * @return total elevation gain of the given identity edge
     */
    public double edgeElevationGain(int edgeId) {
        return edges.elevationGain(edgeId);
    }

    /**
     * @param edgeId identity of an edge
     * @return returns true iff the given identity edge goes in the opposite direction
     * of the OSM channel it comes from
     */
    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }

    /**
     * Calculates the position of a node
     * @param nodeId identity of a node
     * @return position of the given identity node
     */
    public PointCh nodePoint(int nodeId) {
        return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));
    }

    /**
     * Finds the set of OSM attributes attached to an edge
     * @param edgeId identity of an edge
     * @return set of OSM attributes attached to the given identity edge
     */
   public AttributeSet edgeAttributes(int edgeId) {
       int index = edges.attributesIndex(edgeId);
       return attributeSets.get(index);
   }

    /**
     * Calculates the longitudinal profile of an edge as a function
     * @param edgeId identity of an edge
     * @return longitudinal profile of the given identity edge, as a function. If the edge
     * has no profile, then this function must return Double.NaN for any argument.
     */
    public DoubleUnaryOperator edgeProfile(int edgeId){
        if(edges.hasProfile(edgeId)){
            return Functions.sampled(edges.profileSamples(edgeId), edges.length(edgeId));
        }
        return Functions.constant(Double.NaN);
    }
}
