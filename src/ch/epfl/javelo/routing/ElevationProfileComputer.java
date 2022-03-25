package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;



import static java.lang.Float.isNaN;
import static java.util.Arrays.fill;

public final class ElevationProfileComputer {

    private ElevationProfileComputer(){}

    public static ElevationProfile elevationProfile(Route route, double maxStepLength){
        Preconditions.checkArgument(maxStepLength > 0);
        int samplesNumber = ((int)Math.ceil(route.length() / maxStepLength)) + 1;
        double gap = route.length() / (samplesNumber - 1);
        float[] elevationSamples = new float[samplesNumber];
        for (int i = 0; i < samplesNumber; ++i ){
            elevationSamples[i] = (float) route.elevationAt(i*gap);
        }
        elevationSamples = fillBeginningAndEnd(elevationSamples);
        elevationSamples = interpolateElevation(elevationSamples);
        return new ElevationProfile(route.length(), elevationSamples);
    }



    //Ces classes sont momentanément publiques pour se faire testées.

    public static float[] fillBeginningAndEnd(float[] elevationSamples){
        int firstNotNan = 0;
        while( firstNotNan < elevationSamples.length && isNaN(elevationSamples[firstNotNan])){
            ++firstNotNan;
        }
        if (firstNotNan == elevationSamples.length){
            for (int i = 0; i < elevationSamples.length; ++i){
                elevationSamples[i] = 0;
            }
            return elevationSamples;
        }
        fill(elevationSamples, 0, firstNotNan, elevationSamples[firstNotNan]);
        int lastNotNan = elevationSamples.length - 1;
        while ( lastNotNan > 0 && isNaN(elevationSamples[lastNotNan])){
            lastNotNan = lastNotNan - 1;
        }
        fill(elevationSamples, lastNotNan, elevationSamples.length, elevationSamples[lastNotNan]);
        return elevationSamples;
    }

    public static float[] interpolateElevation(float[] elevationSamples){
        int counting = 0;
        for (int i = 1; i < elevationSamples.length - 1; ++i){
            if (isNaN(elevationSamples[i])) {
                ++counting;
                int k = i + 1;
                while (isNaN(elevationSamples[k]) && k < elevationSamples.length){
                    ++counting;
                    k = k + 1;

                }
                for (int j = 0; j < counting; ++j) {
                    elevationSamples[i + j] = (float) Math2.interpolate(elevationSamples[i - 1],
                            elevationSamples[i + counting], (1.0+j) / (counting + 1));
                }
            }
            //ici, d'après moi, on n'a pas besoin de changer le i, mais je le fais qd mm
            //pr une question d'optimisation.
            i = i + counting;
            counting = 0;
        }
        return elevationSamples;
    }


}
