package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;

import static java.lang.Float.isNaN;
import static java.util.Arrays.fill;

/**
 * Represents a profile calculator
 * @author Lina Sadgal (342075)
 */

public final class ElevationProfileComputer {

    private ElevationProfileComputer(){}

    /**
     * constructs the elevation profile for a route.
     * @throws IllegalArgumentException if the argument maxStepLength is negative
     * @param route  the route we want to create an elevation profile for
     * @param maxStepLength the maximum gap that could be between two samples
     * @return a new instance of type ElevationProfile
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {

        Preconditions.checkArgument(maxStepLength > 0);
        int samplesNumber = ((int)Math.ceil(route.length() / maxStepLength)) + 1;
        double gap = route.length() / (samplesNumber - 1);
        float[] elevationSamples = new float[samplesNumber];
        for (int i = 0; i < samplesNumber; ++i ){
            elevationSamples[i] = (float) route.elevationAt(i*gap);
        }

        //Since it is useless to do interpolations in the array if it originally contained
        //only NaN values and has been filled with zeros through the method fillBeginningAndEnd
        //we only use the method interpolateElevation if it was not the case.
        if (fillBeginningAndEnd(elevationSamples)){
            interpolateElevation(elevationSamples);
        }

        return new ElevationProfile(route.length(), elevationSamples);
    }

    /**
     * takes an array and fill it with 0's if it only contains NaN values, or fill it in the
     * beginning with the first not NaN value and in the end with the last not NaN value.
     * @param elevationSamples the array we want to fill
     * @return false if the array originally contained only NaN values and has
     * been filled with zeros, true otherwise.
     */
    private static boolean fillBeginningAndEnd(float[] elevationSamples){
        int firstNotNan = 0;
        while( firstNotNan < elevationSamples.length && isNaN(elevationSamples[firstNotNan])){
            ++firstNotNan;
        }
        if (firstNotNan == elevationSamples.length){
            Arrays.fill(elevationSamples, 0);
            return false;
        }
        fill(elevationSamples, 0, firstNotNan, elevationSamples[firstNotNan]);
        int lastNotNan = elevationSamples.length - 1;
        while ( lastNotNan > 0 && isNaN(elevationSamples[lastNotNan])){
            --lastNotNan;
        }
        fill(elevationSamples, lastNotNan, elevationSamples.length, elevationSamples[lastNotNan]);
        return true;
    }

    /**
     * looks for NaN values in the array and replaces the NaN with an interpolated value
     * @param elevationSamples the array we want to interpolate onto.
     */
    private static void interpolateElevation(float[] elevationSamples){
        int nanCounter = 0;
        for (int i = 1; i < elevationSamples.length - 1; ++i){
            if (isNaN(elevationSamples[i])) {
                ++nanCounter;
                int k = i + 1;
                while (isNaN(elevationSamples[k])){
                    ++nanCounter;
                    ++k;
                }
                for (int j = 0; j < nanCounter; ++j) {
                    elevationSamples[i + j] = (float) Math2.interpolate(elevationSamples[i - 1],
                            elevationSamples[i + nanCounter], (1.0+j) / (nanCounter + 1));
                }
            }
            //Here we are sure that all the NaN values in between the values we used to interpolate
            //are not NaN anymore, so we don't need to go through them in the loop.
            i = i + nanCounter;
            nanCounter = 0;
        }
    }

}
