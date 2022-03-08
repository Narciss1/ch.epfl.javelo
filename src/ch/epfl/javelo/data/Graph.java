package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public final class Graph {

    //QUESTION 1: suffisant de mettre final pour la classe et ses attributs quand elle est immuable ?
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

    //QUESTION 2: ADD METHODE AUXILIAIRE CAUSE THIS IS DU CODE DUPLIQUE ?
    //QUESTION 3: Shouldnt add: Path filePath = Path.of("lausanne/nodes_osmid.bin"); ?
    //QUESTION 4: Est-ce qu'il lève bien l'exception seul ?
    // PIAZZA @264
   public Graph loadFrom(Path basePath) throws IOException {

        Path nodesPath = basePath.resolve("nodes.bin");
        IntBuffer nodesBuffer;
        try (FileChannel channel = FileChannel.open(nodesPath)) {
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
        ByteBuffer attributesBuffer;
        try (FileChannel channel = FileChannel.open(attributesPath)) {
            attributesBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
        return new Graph(new GraphNodes(nodesBuffer), new GraphSectors(sectorsBuffer),
                new GraphEdges(edgesBuffer, profileIdsBuffer, elevationsBuffer),  );
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

   // PIAZZA @273
   // qui retourne l'identité du nœud se trouvant le plus proche du point donné,
   // à la distance maximale donnée (en mètres), ou -1 si aucun nœud ne correspond à ces critères
   /*public int nodeClosestTo(PointCh point, double searchDistance) {
       List<GraphSectors.Sector> closeSectors = sectors.sectorsInArea(point, searchDistance);
       for( int i = 0; i < closeSectors.size(); i++){
          int j = point.squaredDistanceTo(closeSectors.get(i).startNodeId());
       }
   }*/

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

   // PIAZZA @268
   // public AttributeSet edgeAttributes(int edgeId){return}
   // qui retourne l'ensemble des attributs OSm attachés à l'arête d'identité donnée

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
        //QUESTION 5: Est-ce que c'est exactement que la meme chose que la méthode de edges ?
    }

    //A LINA DE FINIR CETTE METHODE!
    /*
    public DoubleUnaryOperator edgeProfile(int edgeId){
        if(edges.hasProfile(edgeId)){
            Sampled(profileSamples(edgeId), xMax);
        } else {
           return Double.NaN();
        }
    }*/
}
