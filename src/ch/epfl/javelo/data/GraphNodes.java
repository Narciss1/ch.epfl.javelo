package ch.epfl.javelo.data;

import java.nio.IntBuffer;
import static ch.epfl.javelo.Bits.extractUnsigned;
import static ch.epfl.javelo.Q28_4.asDouble;

/**
 * Represents the array of all the nodes in Javelo
 * @author Lina Sadgal (342075)
 * @author Aya Hamane (...)
 */
public record GraphNodes(IntBuffer buffer) {

    /**
     * index corresponding to the east coordinate of a node
     */
    private static final int OFFSET_E = 0;
    /**
     * index corresponding to the north coordinate of a node
     */
    private static final int OFFSET_N = OFFSET_E + 1;
    /**
     * index corresponding to the number of edges of a node and the identity of the first one
     */
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    /**
     * number of values representing a sector
     */
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    private static final int OUT_DEGREE_START = 28;

    private static final int OUT_DEGREE_LENGTH = 4;

    private static final int EDGE_ID_LENGTH = 28;

    /**
     * Calculates the number of nodes contained in the buffer
     * @return total number of nodes
     */
    public int count() {
        return buffer.capacity() / NODE_INTS;
    }

    /**
     * Calculates the number of edges exiting the given identity node
     * @param nodeId identity of a node
     * @return number of edges exiting the node
     */
    public int outDegree(int nodeId) {
        return extractUnsigned(buffer.get(NODE_INTS*nodeId + OFFSET_OUT_EDGES),
                OUT_DEGREE_START, OUT_DEGREE_LENGTH);
    }

    /**
     * Calculates the identity of the edgeIndex-th edge coming out of the identity node
     * @param nodeId node's identity
     * @param edgeIndex index of an edge
     * @return identity of an edge
     */
    public int edgeId(int nodeId, int edgeIndex) {
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        return extractUnsigned(buffer.get(NODE_INTS*nodeId + OFFSET_OUT_EDGES),
                0, EDGE_ID_LENGTH) + edgeIndex;
    }

    /**
     * Calculates the east coordinate of the given identity node
     * @param nodeId identity of a node
     * @return east coordinate of the node
     */
    public double nodeE(int nodeId) {
        return asDouble(buffer.get(NODE_INTS*nodeId + OFFSET_E));
    }

    /**
     * Calculates the north coordinate of the given identity node
     * @param nodeId identity of a node
     * @return north coordinate of the node
     */
    public double nodeN(int nodeId) {
        return asDouble(buffer.get(NODE_INTS*nodeId + OFFSET_N));
    }
}
