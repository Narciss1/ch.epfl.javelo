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



    public float[] profileSamples(int edgeId) {

        if (!hasProfile(edgeId)) {
            return new float[0];

        } else {
            int samplesNumber = 1 + Math2.ceilDiv(edgesBuffer.getShort(
                            EDGES_INTS * edgeId + OFFSET_LENGTH),
                    Q28_4.ofInt(2));
            float[] profileSamples = new float[samplesNumber];
            int firstSampleId = Bits.extractUnsigned(profileIds.get(PROFILES_INTS * edgeId + OFFSET_PROFILE),
                    0, 30);
            //Stockage du 1er échantillon dans le tableau.
            profileSamples[0] = Q28_4.asFloat(elevations.get(firstSampleId));
            if (typeOfProfile(edgeId) == 1) {
                for (int i = 1; i < samplesNumber; ++i) {
                    profileSamples[i] = Q28_4.asFloat(elevations.get(firstSampleId + i));
                }
            }

            else if (typeOfProfile(edgeId) == 2){
                int numberOfShorts = (samplesNumber - 1) / 2;
                for (int i = 1; i<= numberOfShorts; ++i){
                    profileSamples[i * 2 - 1] = profileSamples[i * 2 - 2] +
                            Q28_4.asFloat(Bits.extractSigned(elevations.get(firstSampleId + i), 8, 8));
                    profileSamples[i * 2] = profileSamples[i * 2 - 1] +
                            Q28_4.asFloat(Bits.extractSigned(elevations.get(firstSampleId + i),
                                    0,8 ));
                }
                if ((samplesNumber - 1) % 2 != 0){
                    profileSamples[samplesNumber - 1] = profileSamples [samplesNumber - 2] +
                            Q28_4.asFloat(Bits.extractSigned(elevations.get(firstSampleId +
                                    numberOfShorts + 1), 8, 8));
                }

            } else {
                int numberOfShorts = (samplesNumber - 1) / 4;
                for (int i = 1; i <= numberOfShorts; ++i){
                    profileSamples[i * 4 - 3] = profileSamples[i * 4 - 4] +
                            Q28_4.asFloat(Bits.extractSigned(elevations.get(firstSampleId + i),
                                    12,4));
                    profileSamples[i * 4 - 2] = profileSamples[i * 4 - 3] +
                            Q28_4.asFloat(Bits.extractSigned(elevations.get(firstSampleId + i),
                                    8,4));
                    profileSamples[i * 4 - 1] = profileSamples[i * 4 - 2] +
                            Q28_4.asFloat(Bits.extractSigned(elevations.get(firstSampleId + i),
                                    4,4));
                    profileSamples[i * 4] = profileSamples[i * 4 - 1] +
                            Q28_4.asFloat(Bits.extractSigned(elevations.get(firstSampleId + i),
                                    0,4));
                }
                int samplesLeft = (samplesNumber-1) % 4;
                int counting = 0;
                while (samplesLeft != 0){
                    profileSamples[samplesNumber - samplesLeft] = profileSamples[samplesNumber - samplesLeft - 1] +
                            Q28_4.asFloat(Bits.extractSigned(elevations.get
                                    (firstSampleId + Math2.ceilDiv(samplesNumber, 4)), 12 - counting,4));
                    samplesLeft = samplesLeft - 1;
                    counting = counting + 4;
                }
            }
            if (!isInverted(edgeId)){
                return profileSamples;
            } else {
                return inverse(profileSamples);
            }
        }
    }

    //created method _ public and static for tests
    public static float[] inverse(float[] toInverse){
        int i = 0;
        int j = toInverse.length - 1;
        while (i < j){
            float toKeep = toInverse[i];
            toInverse[i] = toInverse[j];
            toInverse[j] = toKeep;
            i = i + 1;
            j = j - 1;
        }
        return toInverse;
    }

    //CreatedMethod. Public juste le tps de la tester.
    public int typeOfProfile(int edgeId){
        return (Bits.extractUnsigned(profileIds.get(PROFILES_INTS * edgeId + OFFSET_PROFILE),
                30, 2));
    }


    //Peut-être rajouter une 3e methode qui fait le asFloat et tt le calcul chiant là lourd.

    public int attributesIndex(int edgeId){
        return edgesBuffer.getShort(EDGES_INTS * edgeId + OFFSET_ATTRIBUTES);
    }

}
