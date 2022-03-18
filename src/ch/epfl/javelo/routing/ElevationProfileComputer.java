package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import static java.lang.Float.isNaN;
import static java.util.Arrays.fill;

public final class ElevationProfileComputer {

    private ElevationProfileComputer(){}

    public static ElevationProfile elevationProfile(Route route, double maxStepLength){
        Preconditions.checkArgument(maxStepLength > 0);
        int samplesNumber = (int)Math.ceil(route.length() / maxStepLength) + 1;
        double gap = route.length() / (samplesNumber - 1);
        float[] elevationSamples = new float[samplesNumber];
        for (int i = 0; i < samplesNumber; ++i ){
            elevationSamples[i] = (float) route.elevationAt(i*gap);
        }
        elevationSamples = fillBeginningAndEnd(elevationSamples);

        //Ce truc est freaking long ms c un assistant qui m'a dit de faire comme ça
        //alors que moi l'idée que j'avais (et qui est fausse d'après lui) fait 3 lignes.
        //Si c vrmt ce qu'il a dit, mettre ça dans une méthode auxiliaire. A voir.
        int counting = 0;
        for (int i = 1; i < elevationSamples.length - 1; ++i){
            if (isNaN(elevationSamples[i])) {
                ++counting;
                for (int j = i; j < elevationSamples.length - 1; ++j) {
                    if (isNaN(elevationSamples[j])) {
                        ++counting;
                    }
                }
                for (int j = 0; j <= counting; ++j) {
                    elevationSamples[i + j] = (float) Math2.interpolate(elevationSamples[i - 1],
                            elevationSamples[i + 1], 1.0 / (counting + 1));
                }
            }
            //ici, d'après moi, on n'a pas besoin de changer le i, mais je le fais qd mm
            //pr une question d'optimisation.
            i = i + counting;
            counting = 0;
        }
        return new ElevationProfile(route.length(), elevationSamples);
    }


    private static float[] fillBeginningAndEnd(float[] elevationSamples){
        int firstNotNan = elevationSamples.length;  //Pr donner un indice non atteignable
        boolean keepLooking = true;
        for (int i = 0; i < elevationSamples.length; ++i){
            if (! isNaN(elevationSamples[i]) && keepLooking){
                firstNotNan = i;
                keepLooking = false;
            }
        }
        if (firstNotNan == elevationSamples.length){    //Peut être tej d'après Assistant.
            for (int i = 0; i < elevationSamples.length; ++i){
                elevationSamples[i] = 0;
            }
            return elevationSamples;
        }
        fill(elevationSamples, 0, firstNotNan, elevationSamples[firstNotNan]);
        int lastNotNan = elevationSamples.length;
        keepLooking = true;
        for (int i = elevationSamples.length - 1; i >= 0; i = i - 1){
            if (! isNaN(elevationSamples[i]) && keepLooking ){
                lastNotNan = i;
                keepLooking = false;
            }
        }
        fill(elevationSamples, lastNotNan, elevationSamples.length - 1, elevationSamples[lastNotNan]);
        return elevationSamples;
    }

}
