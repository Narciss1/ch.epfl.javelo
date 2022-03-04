package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {

    private static final int OFFSET_NODE = 0;
    private static final int OFFSET_LENGTH = OFFSET_NODE + 4;
    private static final int OFFSET_ELEVATION = OFFSET_LENGTH + 2;
    private static final int OFFSET_ATTRIBUTES = OFFSET_ELEVATION + 2;
    private static final int EDGES_INTS = OFFSET_ATTRIBUTES + 2;
    //private static final int

    public boolean isInverted(int edgeId){
        return edgesBuffer.getInt(EDGES_INTS * edgeId + OFFSET_NODE) < 0;
    }

    public int targetNodeId(int edgeId){
        if (! isInverted(edgeId)){
            return Bits.extractSigned(edgesBuffer.getInt(EDGES_INTS * edgeId + OFFSET_NODE),
                    0, 31);
        } else {
            return Bits.extractSigned(~edgesBuffer.getInt(EDGES_INTS * edgeId + OFFSET_NODE),
                    0, 31);
        }
    }

    public double length(int edgeId){
        return edgesBuffer.getShort(EDGES_INTS * edgeId + OFFSET_LENGTH);
    }

    public double elevationGain(int edgeId){
        return edgesBuffer.getShort(EDGES_INTS * edgeId + OFFSET_ELEVATION);
    }

    public boolean hasProfile(int edgeId){

    }

}
