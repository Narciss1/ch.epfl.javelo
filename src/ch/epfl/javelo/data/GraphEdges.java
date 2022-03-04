package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static java.lang.Short.toUnsignedInt;

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {

    private static final int OFFSET_NODE = 0;
    private static final int OFFSET_LENGTH = OFFSET_NODE + 4;
    private static final int OFFSET_ELEVATION = OFFSET_LENGTH + 2;
    private static final int OFFSET_ATTRIBUTES = OFFSET_ELEVATION + 2;
    private static final int EDGES_INTS = OFFSET_ATTRIBUTES + 2;

    private static final int OFFSET_PROFILE = 0;
    private static final int PROFILES_INTS = OFFSET_PROFILE + 1;

    public boolean isInverted(int edgeId){
        return edgesBuffer.getInt(EDGES_INTS * edgeId + OFFSET_NODE) < 0;
    }

    public int targetNodeId(int edgeId){
        if (! isInverted(edgeId)){
            return Bits.extractUnsigned(edgesBuffer.getInt(EDGES_INTS * edgeId + OFFSET_NODE),
                    0, 31);
        } else {
            return Bits.extractUnsigned(~edgesBuffer.getInt(EDGES_INTS * edgeId + OFFSET_NODE),
                    0, 31);
        }
    }

    public double length(int edgeId){
        return Q28_4.asDouble(toUnsignedInt(edgesBuffer.getShort(EDGES_INTS * edgeId + OFFSET_LENGTH)));
    }

    public double elevationGain(int edgeId){
        return Q28_4.asDouble(toUnsignedInt(edgesBuffer.getShort(EDGES_INTS * edgeId + OFFSET_ELEVATION)));
    }

    public boolean hasProfile(int edgeId){
        return Bits.extractUnsigned(profileIds.get(PROFILES_INTS * edgeId), 30, 2) != 0;
    }


    //Je tiens à préciser que je n'ai pas encore traité le cas
    //où l'arête est inversé, je ne sais pas trop comment procéder pour ça. :(.
    //Doute entre 2 manières, donc attente d'une clarification sur piazza

    public float[] profileSamples(int edgeId){
        int samplesNumber = 1 + Math2.ceilDiv(toUnsignedInt(edgesBuffer.getShort(
                EDGES_INTS * edgeId + OFFSET_LENGTH)),
                Q28_4.ofInt(2));
        float[] profileSamples = new float[samplesNumber];
        if (!hasProfile(edgeId)){
            return profileSamples;  //Le pb est que lui n'est pas vide, il contient des 0. (PIAZZA).
        }else{
            int firstSampleId = Bits.extractUnsigned(profileIds.get(PROFILES_INTS * edgeId + OFFSET_PROFILE),
                    0, 30);
            //Stockage du 1er échantillon dans le tableau.
            profileSamples[0] = Q28_4.asFloat(Q28_4.ofInt(toUnsignedInt(elevations.get(firstSampleId))));
            if (Bits.extractUnsigned(profileIds.get(PROFILES_INTS * edgeId + OFFSET_PROFILE),
                    30, 2) == 1) {
                for (int i = 1; i <= samplesNumber; ++i) {
                    profileSamples[i] = Q28_4.asFloat(Q28_4.ofInt(toUnsignedInt(elevations.get(firstSampleId + i))));
                }
                return profileSamples;
            }
            if (Bits.extractUnsigned(profileIds.get(PROFILES_INTS * edgeId + OFFSET_PROFILE),
                    30, 2) == 2){
                boolean shouldTakeHalf = false;
                double numberOfShorts = samplesNumber / 2;
                for (int i = 1; i<= numberOfShorts; ++i){
                    profileSamples[i * 2 - 1] = profileSamples[i * 2 - 2] +
                            Q28_4.asFloat(Q28_4.ofInt(Bits.extractSigned(toUnsignedInt(elevations.get(firstSampleId + i)),
                                   7,8 )));
                    profileSamples[i * 2] = profileSamples[i * 2 - 1] +
                            Q28_4.asFloat(Q28_4.ofInt(Bits.extractSigned(toUnsignedInt(elevations.get(firstSampleId + i)),
                                    0,8 )));
                }
                if (numberOfShorts % 2 != 0){
                    profileSamples[samplesNumber - 1] =
                            Q28_4.asFloat(Q28_4.ofInt(Bits.extractSigned(toUnsignedInt(elevations.get(firstSampleId +
                                    (int)numberOfShorts + 1)), 7, 8)));
                }
            }
            }

    }

    public int attributesIndex(int edgeId){
        return EDGES_INTS * edgeId + OFFSET_ATTRIBUTES;
    }

}
