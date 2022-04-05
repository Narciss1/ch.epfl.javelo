package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;

import static java.lang.Float.isNaN;
import static java.util.Arrays.fill;

public final class ElevationProfileComputer {

    private ElevationProfileComputer(){}

    /**
     * constructs the elevation profile for a route.
     * @throws IllegalArgumentException if the argument maxStepLength is negative
     * @param route  the route we want to create an elevation profile for
     * @param maxStepLength the maximum gap that could be between two samples
     * @return a new instance of type ElevationProfile
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength){
        Preconditions.checkArgument(maxStepLength > 0);
        int samplesNumber = ((int)Math.ceil(route.length() / maxStepLength)) + 1;
        double gap = route.length() / (samplesNumber - 1);
        float[] elevationSamples = new float[samplesNumber];
        for (int i = 0; i < samplesNumber; ++i ){
            elevationSamples[i] = (float) route.elevationAt(i*gap);
        }

        //AVANT
//        fillBeginningAndEnd(elevationSamples);
//        interpolateElevation(elevationSamples);

        //APRES
        if (!fillBeginningAndEnd(elevationSamples)){
            interpolateElevation(elevationSamples);
        }
        return new ElevationProfile(route.length(), elevationSamples);
    }


    /**
     * takes an array and fill it with 0's if it only contains NaN values, or fill it in the
     * beginning with the first not NaN value and in the end with the last not NaN value.
     * @param elevationSamples the array we want to fill
     * @return true if the array originally contained only NaN values, false otherwise.
     */
    private static boolean fillBeginningAndEnd(float[] elevationSamples){
        int firstNotNan = 0;
        while( firstNotNan < elevationSamples.length && isNaN(elevationSamples[firstNotNan])){
            ++firstNotNan;
        }
        if (firstNotNan == elevationSamples.length){
            Arrays.fill(elevationSamples, 0);
//            for (int i = 0; i < elevationSamples.length; ++i){
//                elevationSamples[i] = 0;
//            }
            return true;
        } else {
            fill(elevationSamples, 0, firstNotNan, elevationSamples[firstNotNan]);
            int lastNotNan = elevationSamples.length - 1;
            while ( lastNotNan > 0 && isNaN(elevationSamples[lastNotNan])){
                --lastNotNan;
                //lastNotNan = lastNotNan - 1;
            }
            fill(elevationSamples, lastNotNan, elevationSamples.length, elevationSamples[lastNotNan]);
        }
        return false;
    }


    //C'est dramatique si je laisse mes 2 boucles for au lieu de foreach ? Je les trouve plus agréables
    //a la lecture et en plus we can do the jump
    /**
     * looks for NaN values in the array and replaces the NaN with an interpolated value
     * @param elevationSamples the array we want to interpolate onto.
     */
    private static void interpolateElevation(float[] elevationSamples){
        int counting = 0;
        for (int i = 1; i < elevationSamples.length - 1; ++i){
            if (isNaN(elevationSamples[i])) {
                ++counting;

                //J'ai l'impression que ce truc de k en condition est useless that's why je l'ai enlevé.
                //Pck techniquement on va ms atteindre la fin, grâce au fill qui a été fait au préalable.
                //en gros y avait : k < length du tableau.
                int k = i + 1;
                while (isNaN(elevationSamples[k])){
                    ++counting;
                    ++k;
                }
                for (int j = 0; j < counting; ++j) {
                    elevationSamples[i + j] = (float) Math2.interpolate(elevationSamples[i - 1],
                            elevationSamples[i + counting], (1.0+j) / (counting + 1));
                }
            }
            //Here we are sure that all the NaN values in between the values we used to interpolate
            //are not NaN anymore, so we don't need to go through them in the loop.
            i = i + counting;
            counting = 0;
        }
    }


}
