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

    //PIAZZA @293
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

    /**
     *
     * @param basePath
     * @return
     * @throws IOException
     */
    public Graph loadFrom(Path basePath) throws IOException {

       IntBuffer nodesBuffer = fileBuffer(basePath, "nodes.bin").asIntBuffer();
       ByteBuffer sectorsBuffer = fileBuffer(basePath, "sectors.bin");
       ByteBuffer edgesBuffer = fileBuffer(basePath, "edges.bin");
       IntBuffer profileIdsBuffer = fileBuffer(basePath, "profile_ids.bin").asIntBuffer();
       ShortBuffer elevationsBuffer = fileBuffer(basePath, "elevations.bin").asShortBuffer();
       LongBuffer attributesBuffer = fileBuffer(basePath, "attributes.bin").asLongBuffer();

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
              double distance = point.squaredDistanceTo(new PointCh(nodes.nodeE(j), nodes.nodeN(j)));
              if(distance < minDistance){
                  minDistance = distance;
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


    /**
     *
     * @param basePath
     * @param string
     * @return
     * @throws IOException
     */
    public ByteBuffer fileBuffer(Path basePath, String string) throws IOException {
        Path stringPath = basePath.resolve(string);
        ByteBuffer stringBuffer;
        try(FileChannel channel = FileChannel.open(stringPath)){
            stringBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
        return stringBuffer;
    }
}
