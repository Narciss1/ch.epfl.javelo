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

import static javax.swing.UIManager.get;

public final class Graph {

    //QUESTION: ADD METHODE AUXILIAIRE CAUSE THIS IS DU CODE DUPLIQUE ?
    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;

    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets){
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = attributeSets;
    }

   public Graph loadFrom(Path basePath) throws IOException {

       Path nodesPath = basePath.resolve("nodes.bin");
       IntBuffer nodesBuffer;
       try(FileChannel channel = FileChannel.open(nodesPath)){
           nodesBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asIntBuffer();
       }
       Path sectorsPath = basePath.resolve("sectors.bin");
       ByteBuffer sectorsBuffer;
       try (FileChannel channel = FileChannel.open(sectorsPath)) {
           sectorsBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
       }
       Path edgesPath = basePath.resolve("edges.bin");
       ByteBuffer edgesBuffer;
       try (FileChannel channel = FileChannel.open(edgesPath)) {
           edgesBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
       }
       Path profileIdsPath = basePath.resolve("profile_ids.bin");
       IntBuffer profileIdsBuffer;
       try (FileChannel channel = FileChannel.open(profileIdsPath)) {
           profileIdsBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asIntBuffer();
       }
       Path elevationsPath = basePath.resolve("elevations.bin");
       ShortBuffer elevationsBuffer;
       try (FileChannel channel = FileChannel.open(elevationsPath)) {
           elevationsBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asShortBuffer();
       }
       Path attributesPath = basePath.resolve("attributes.bin");
       LongBuffer attributesBuffer;
       try (FileChannel channel = FileChannel.open(attributesPath)) {
           attributesBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()).asLongBuffer();
       }
       List<AttributeSet> attributeSetsBuffer = new ArrayList<>();
       for(int i = 0; i < attributesBuffer.capacity(); i++) {
           attributeSetsBuffer.add(new AttributeSet(attributesBuffer.get(i)));
       }
       return new Graph(new GraphNodes(nodesBuffer), new GraphSectors(sectorsBuffer),
                new GraphEdges(edgesBuffer, profileIdsBuffer, elevationsBuffer), attributeSetsBuffer);
    }

    /**
     *
     * @return
     */
   public int nodeCount(){ return nodes.count(); }

    /**
     *
     * @param nodeId
     * @return
     */
   public PointCh nodePoint(int nodeId) {
       return new PointCh(nodes.nodeE(nodeId), nodes.nodeN(nodeId));}

    /**
     *
     * @param nodeId
     * @return
     */
   public int nodeOutDegree(int nodeId){
       return nodes.outDegree(nodeId);
   }

    /**
     *
     * @param nodeId
     * @param edgeIndex
     * @return
     */
   public int nodeOutEdgeId(int nodeId, int edgeIndex) {
       return nodes.edgeId(nodeId, edgeIndex);
   }

    /**
     *
     * @param point
     * @param searchDistance
     * @return
     */
   public int nodeClosestTo(PointCh point, double searchDistance) {
       double minDistance = Math.pow(searchDistance, 2);
       int closestNode = -1;
       List<GraphSectors.Sector> closeSectors = sectors.sectorsInArea(point, searchDistance);
       for(int i = 0; i < closeSectors.size(); i++){
          for(int j = closeSectors.get(i).startNodeId(); j < closeSectors.get(i).endNodeId(); j++){
              if(point.squaredDistanceTo(new PointCh(nodes.nodeE(j), nodes.nodeN(j))) < minDistance){
                  minDistance = point.squaredDistanceTo(new PointCh(nodes.nodeE(j), nodes.nodeN(j)));
                  closestNode = j;
              }
          }
       }
       return closestNode;
   }

    /**
     *
     * @param edgeId
     * @return
     */
    public int edgeTargetNodeId(int edgeId){
        return edges.targetNodeId(edgeId);
    }

    /**
     *
     * @param edgeId
     * @return
     */
    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }

    /**
     *
     * @param edgeId
     * @return
     */
   public AttributeSet edgeAttributes(int edgeId){
       return attributeSets.get(edges.attributesIndex(edgeId));
    }

    /**
     *
     * @param edgeId
     * @return
     */
    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }

    /**
     *
     * @param edgeId
     * @return
     */
    public double edgeElevationGain(int edgeId){
        return edges.elevationGain(edgeId);
    }

    /**
     *
     * @param edgeId
     * @return
     */
    public DoubleUnaryOperator edgeProfile(int edgeId){
        if(edges.hasProfile(edgeId)){
            return Functions.sampled(edges.profileSamples(edgeId), edges.length(edgeId));
        }
        return Functions.constant(Double.NaN);
    }
}
