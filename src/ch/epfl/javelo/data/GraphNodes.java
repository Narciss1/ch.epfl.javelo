package ch.epfl.javelo.data;

import java.nio.IntBuffer;

import static ch.epfl.javelo.Bits.extractUnsigned;
import static ch.epfl.javelo.Q28_4.asDouble;

public record GraphNodes(IntBuffer buffer) {

    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;

    public int count(){
        return buffer.capacity() / NODE_INTS;
    }

    public double nodeE(int nodeId){
        return asDouble(buffer.get(NODE_INTS * nodeId + OFFSET_E));  }

    public double nodeN(int nodeId){
        return asDouble(buffer.get(NODE_INTS * nodeId + OFFSET_N));
    }

    public int outDegree(int nodeId){
        return extractUnsigned(buffer.get(NODE_INTS * nodeId + OFFSET_OUT_EDGES), 28, 4);
    }

    public int edgeId(int nodeId, int edgeIndex){
        assert 0 <= edgeIndex && edgeIndex < outDegree(nodeId);
        return extractUnsigned(buffer.get(NODE_INTS * nodeId + OFFSET_OUT_EDGES),0, 28)
                + edgeIndex;
    }
}
