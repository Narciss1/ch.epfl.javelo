package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;


import static java.lang.Short.toUnsignedInt;

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {

    /**
     * index corresponding to identity of the starting node of the edge
     */
    private static final int OFFSET_NODE = 0;
    /**
     * index corresponding to the length of the edge
     */
    private static final int OFFSET_LENGTH = OFFSET_NODE + 4;
    /**
     * index corresponding to the total elevation gain of the edge
     */
    private static final int OFFSET_ELEVATION = OFFSET_LENGTH + 2;
    /**
     * index corresponding to the attributes of the edge
     */
    private static final int OFFSET_ATTRIBUTES = OFFSET_ELEVATION + 2;
    /**
     * number of values representing a single edge
     */
    private static final int EDGES_INTS = OFFSET_ATTRIBUTES + 2;

    /**
     * index representing the index of type of profile and identity of thr first sample for the edge
     */
    private static final int OFFSET_PROFILE_ID = 0;
    /**
     * number of values representing an edge
     */
    private static final int PROFILES_INTS = OFFSET_PROFILE_ID + 1;


    /** checks if an edge is inverted compared to the OSM way
     *
     * @param edgeId the edge's (we want to check) identity
     * @return true if the edge is inverted compared to the OSM way
     */
    public boolean isInverted(int edgeId){
        return edgesBuffer.getInt(EDGES_INTS * edgeId + OFFSET_NODE) < 0;
    }

    /** gives us the integer corresponding to destination node's identity
     *
     * @param edgeId the edge's identity
     * @return the identity of the destination node for an edge
     */
    public int targetNodeId(int edgeId){
        if (! isInverted(edgeId)){
            return Bits.extractUnsigned(edgesBuffer.getInt(EDGES_INTS * edgeId + OFFSET_NODE),
                    0, 31);
        } else {
            return Bits.extractUnsigned(~edgesBuffer.getInt(EDGES_INTS * edgeId + OFFSET_NODE),
                    0, 31);
        }
    }

    /** gives us the double corresponding to the edge's length in meters.
     *
     * @param edgeId
     * @return the length of the edge
     */
    public double length(int edgeId){
        return Q28_4.asDouble(toUnsignedInt(edgesBuffer.getShort(EDGES_INTS * edgeId + OFFSET_LENGTH)));
    }


    /** gives us the double corresponding to the edge's total elevation gain.
     *
     * @param edgeId the edge's identity
     * @return the total elevation gain of the edge
     */
    public double elevationGain(int edgeId){
        return Q28_4.asDouble(toUnsignedInt(edgesBuffer.getShort(EDGES_INTS * edgeId + OFFSET_ELEVATION)));
    }

    /** checks if an edge has a profile of type 1, 2 or 3, which means it has a profile.
     *
     * @param edgeId the edge's identity
     * @return true if the edge has a profile
     */
    public boolean hasProfile(int edgeId){
        return Bits.extractUnsigned(profileIds.get(PROFILES_INTS * edgeId + OFFSET_PROFILE_ID), 30, 2) != 0;
    }


    /** Stores in an array the profile samples of an edge that can be of any type. The array
     * is empty if the edge has no profile.
     *
     * @param edgeId the edge's identity
     * @return an array containing the profile samples of the edge.
     */
    public float[] profileSamples(int edgeId) {

        if (!hasProfile(edgeId)) {
            return new float[0];

        } else {
            int samplesNumber = 1 + Math2.ceilDiv(Short.toUnsignedInt(edgesBuffer.getShort(
                            EDGES_INTS * edgeId + OFFSET_LENGTH)),
                    Q28_4.ofInt(2));
            float[] profileSamples = new float[samplesNumber];
            int firstSampleId = Bits.extractUnsigned(profileIds.get(PROFILES_INTS * edgeId + OFFSET_PROFILE_ID),
                    0, 30);
            if (typeOfProfile(edgeId) == 1) {
                for (int i = 0; i < samplesNumber; ++i) {
                    profileSamples[i] = Q28_4.asFloat(Short.toUnsignedInt(elevations.get(firstSampleId + i)));
                }
            }

            else if (typeOfProfile(edgeId) == 2){
                profileSamples[0] = Q28_4.asFloat(elevations.get(firstSampleId));   //Ici pas besoin de Unsigned??
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
                profileSamples[0] = Q28_4.asFloat(elevations.get(firstSampleId));
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


    //Faut vrmt passer par cet algo ?
    /** takes an array of float values and inverses its values
     *
     * @param toInverse the array we want to inverse the values for
     * @return an array with the inversed values of the one given as an argument
     */
    private static float[] inverse(float[] toInverse){
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

    /** gives us the integer corresponding to the edge's profile.
     *
     * @param edgeId the edge's identity
     * @return the profile type of the edge
     */
    private int typeOfProfile(int edgeId){
        return (Bits.extractUnsigned(profileIds.get(PROFILES_INTS * edgeId + OFFSET_PROFILE_ID),
                30, 2));
    }


    /**
     *
     * @param edgeId the edge's identity
     * @return
     */
    public int attributesIndex(int edgeId){
        return Short.toUnsignedInt(edgesBuffer.getShort(EDGES_INTS * edgeId + OFFSET_ATTRIBUTES));
    }

}
