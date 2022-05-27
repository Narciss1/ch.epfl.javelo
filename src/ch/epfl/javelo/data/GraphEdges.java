package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;


import static java.lang.Short.toUnsignedInt;

/**
 * Represents the array of all the edges in JaVelo
 * @author Lina Sadgal (342075)
 */

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

    /**
     * the number of bits a difference occupies in a short of BufferElevation
     * for an edge of type 2
     */
    private static final int DIFFERENCE_LENGTH_TYPE_2 = 8;
    /**
     * the number of bits a difference occupies in a short of BufferElevation
     * for an edge of type 3
     */
    private static final int DIFFERENCE_LENGTH_TYPE_3 = 4;

    /**
     * checks if an edge is inverted compared to the OSM way
     * @param edgeId the edge's (we want to check) identity
     * @return true if the edge is inverted compared to the OSM way
     */
    public boolean isInverted(int edgeId){
        return edgesBuffer.getInt(EDGES_INTS * edgeId + OFFSET_NODE) < 0;
    }

    /**
     * gives us the integer corresponding to destination node's identity
     * @param edgeId the edge's identity
     * @return the identity of the destination node for an edge
     */
    public int targetNodeId(int edgeId){
        if (! isInverted(edgeId)){
            return Bits.extractUnsigned(edgesBuffer.getInt(EDGES_INTS * edgeId + OFFSET_NODE),
                    0, Integer.SIZE - 1);
        }
        return Bits.extractUnsigned(~edgesBuffer.getInt(EDGES_INTS * edgeId + OFFSET_NODE),
                0, Integer.SIZE - 1);
    }

    /**
     * gives us the double corresponding to the edge's length in meters.
     * @param edgeId the identity of the edge we want the length for.
     * @return the length of the edge
     */
    public double length(int edgeId){
        return Q28_4.asDouble(toUnsignedInt(edgesBuffer.getShort(EDGES_INTS * edgeId
                + OFFSET_LENGTH)));
    }


    /**
     * gives us the double corresponding to the edge's total elevation gain.
     * @param edgeId the edge's identity
     * @return the total elevation gain of the edge
     */
    public double elevationGain(int edgeId){
        return Q28_4.asDouble(toUnsignedInt(edgesBuffer.getShort(EDGES_INTS * edgeId
                + OFFSET_ELEVATION)));
    }

    /**
     * checks if an edge has a profile of type 1, 2 or 3, which means it has a profile.
     * @param edgeId the edge's identity
     * @return true if the edge has a profile
     */
    public boolean hasProfile(int edgeId){
        return Bits.extractUnsigned(profileIds.get(PROFILES_INTS * edgeId + OFFSET_PROFILE_ID),
                30, 2) != 0;
    }

    /**
     *Stores in an array the profile samples of an edge that can be of any type. The array
     * is empty if the edge has no profile.
     * @param edgeId the edge's identity
     * @return an array containing the profile samples of the edge.
     */
    public float[] profileSamples(int edgeId) {
        if (!hasProfile(edgeId)) {
            return new float[0];
        }
        int samplesNumber = 1 + Math2.ceilDiv(Short.toUnsignedInt(edgesBuffer.getShort(
                        EDGES_INTS * edgeId + OFFSET_LENGTH)),
                Q28_4.ofInt(2));
        float[] profileSamples = new float[samplesNumber];
        int firstSampleId = Bits.extractUnsigned(profileIds.get
                        (PROFILES_INTS * edgeId + OFFSET_PROFILE_ID), 0, 30);
        //We put the first elevation, that is never compressed regardless of the type, in the array
        profileSamples[0] = Q28_4.asFloat(Short.toUnsignedInt(elevations.get(firstSampleId)));
        switch(typeOfProfile(edgeId)){
            case 1 :
                for (int i = 1; i < samplesNumber; ++i) {
                    profileSamples[i] = Q28_4.asFloat(Short.toUnsignedInt
                            (elevations.get(firstSampleId + i)));
                }
                break;
            case 2 :
                profileSamplesThroughDifferences(DIFFERENCE_LENGTH_TYPE_2,
                        profileSamples, firstSampleId);
                break;
            case 3 :
                profileSamplesThroughDifferences(DIFFERENCE_LENGTH_TYPE_3,
                        profileSamples, firstSampleId);
                break;
        }
            if (!isInverted(edgeId)){
                return profileSamples;
            }
            return reverse(profileSamples);
    }

    /**
     * This method fills the array profileSamples with the elevations.
     * @param lengthOfDifferences  the number of bits of the difference in a Short.
     * @param profileSamples  the array of samples to fill
     * @param firstSampleId   the identity of the first elevation in the ElevationBuffer
     */
    private void profileSamplesThroughDifferences(int lengthOfDifferences, float[] profileSamples,
                                                  int firstSampleId){

        //The value of the start of the first difference (8 for type 2 and 12 for type 3).
        //This value does not change during the execution of the method to let us know it is
        //high time to go to the next level.
        int firstStartValue = -lengthOfDifferences + Short.SIZE;

        //The value of the start of the first difference (8 for type 2 and 12 for type 3).
        //This value changes during the execution of the method
        int startValue = -lengthOfDifferences + Short.SIZE;

        //The switcher, 8 for type 2 and 12 for type 3, allows us, through a modulo operation
        //to change the start value to give as an argument to extractedSigned
        int switcher = -lengthOfDifferences + Short.SIZE;

        //It is equal to one because the first elevation has already been added.
        int samplesTaken = 1;
        int shortNumber = 1;
        while (samplesTaken < profileSamples.length){
            profileSamples[samplesTaken] = profileSamples[samplesTaken - 1]
                    + Q28_4.asFloat(Bits.extractSigned(elevations.get(firstSampleId + shortNumber),
                    startValue, lengthOfDifferences));
            startValue = (startValue + switcher) % Short.SIZE;
            ++samplesTaken;
            //The condition is valid when it is time to switch to the next short
            if (startValue == firstStartValue){
                ++shortNumber;
            }
        }
    }

    /**
     * takes an array of float values and inverses its values
     * @param toReverse the array we want to inverse the values for
     * @return an array with the inverse values of the one given as an argument
     */
    private static float[] reverse(float[] toReverse){
        int i = 0;
        int j = toReverse.length - 1;
        while (i < j){
            float toKeep = toReverse[i];
            toReverse[i] = toReverse[j];
            toReverse[j] = toKeep;
            i = i + 1;
            j = j - 1;
        }
        return toReverse;
    }

    /**
     * gives us the integer corresponding to the edge's profile.
     * @param edgeId the edge's identity
     * @return the profile type of the edge
     */
    private int typeOfProfile(int edgeId){
        return (Bits.extractUnsigned(profileIds.get(PROFILES_INTS * edgeId + OFFSET_PROFILE_ID),
                30, 2));
    }

    /**
     * gives us the identity of all the attributes related to the edge which identity is given
     * @param edgeId the edge's identity
     * @return the identity of all the attributes related to the edge
     */
    public int attributesIndex(int edgeId){
        return Short.toUnsignedInt(edgesBuffer.getShort(EDGES_INTS * edgeId + OFFSET_ATTRIBUTES));
    }

}
