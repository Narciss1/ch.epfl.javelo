package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;
import java.util.function.DoubleUnaryOperator;

public class ElevationProfile {

    private double length;
    private float[] elevationSamples;

    public ElevationProfile(double length, float[] elevationSamples){
        Preconditions.checkArgument(length > 0 && elevationSamples.length >= 2);
        this.length = length;
        this.elevationSamples = elevationSamples;
    }

    public double length(){
        return length;
    }

    private DoubleSummaryStatistics constructStatistics(float[] elevationSamples){
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (int i = 0; i < elevationSamples.length; ++i){
            s.accept(elevationSamples[i]); //Prblm si float -> double ?
        }
        return s;
    }

    public double minElevation(){
        return constructStatistics(elevationSamples).getMin();
    }

    public double maxElevation(){
        return constructStatistics(elevationSamples).getMax();
    }

    public double totalAscent(){
        double ascent = 0;
        for (int i = 1; i < elevationSamples.length; ++i){
            if (elevationSamples[i-1] < elevationSamples[i]){
                ascent = ascent + (elevationSamples[i] - elevationSamples[i-1]);
            }
        }
        return ascent;
    }

    public double totalDescent(){
        double descent = 0;
        for (int i = 1; i < elevationSamples.length; ++i){
            if (elevationSamples[i-1] > elevationSamples[i]){
                descent = descent + (elevationSamples[i-1] - elevationSamples[i]);
            }
        }
        return descent;
    }

    public double ElevationAt(double position){
        DoubleUnaryOperator graph = Functions.sampled(elevationSamples, length);
        return graph.applyAsDouble(position);
    }

}
