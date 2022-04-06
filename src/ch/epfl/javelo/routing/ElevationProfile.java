package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;
import java.util.function.DoubleUnaryOperator;

public final class ElevationProfile {

    private final double length;
    private final float[] elevationSamples;

    /**
     * Constructs a new ElevationProfile
     * @throws IllegalArgumentException if the length is negative or the array elevationSamples
     * contains less than 2 values.
     * @param length the length of the edge
     * @param elevationSamples the array containing the elevations for the samples of the edge
     */
    public ElevationProfile(double length, float[] elevationSamples){
        Preconditions.checkArgument(length > 0 && elevationSamples.length >= 2);
        this.length = length;
        this.elevationSamples = elevationSamples.clone();
    }

    /**
     * Gives the length for the edge
     * @return the edge's length
     */
    public double length(){
        return length;
    }

    /**
     * creates statistics concerning the array of the edge's elevation samples
     * @param elevationSamples the array containing the samples' heights
     * @return the statistics created based on the array given
     */
    private DoubleSummaryStatistics constructStatistics(float[] elevationSamples){
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (float i : elevationSamples){
            s.accept(i);
        }
        return s;
    }

    /**
     * Gives the minimal elevation in the array
     * @return the minimal elevation in the edge
     */
    public double minElevation(){
        return constructStatistics(elevationSamples).getMin();
    }

    /**
     * Gives the maximal elevation in the edge
     * @return the highest elevation in the edge
     */
    public double maxElevation(){
        return constructStatistics(elevationSamples).getMax();
    }

    /**
     * Gives the sum of all the ascents of the edge
     * @return the total ascent in the edge
     */
    public double totalAscent(){
        return totalAscentOrDescent(true);
    }

    /**
     * Gives the sum of all the descents of the edge
     * @return the total descent in the edge
     */
    public double totalDescent(){
        return totalAscentOrDescent(false);
    }

    /** calculates the total ascent if
     *
     * @param ascent argument that is true if and only if we are calculating the total ascent
     * @return
     */
    private double totalAscentOrDescent(boolean ascent){
        double total = 0;
        float previousElevation = elevationSamples[0];
        for (float elevation : elevationSamples){
            total = ascent && previousElevation < elevation ?
                    (total + (elevation - previousElevation))
                    : !ascent && previousElevation > elevation ?
                    (total + (previousElevation - elevation)) : total;
            previousElevation = elevation;
        }
        return total;
    }

    /**
     * Gives the elevation at a certain position given
     * @param position the position we want to give the elevation at
     * @return a double that is the elevation at the given position
     */
    public double elevationAt(double position){
        DoubleUnaryOperator graph = Functions.sampled(elevationSamples, length);
        return graph.applyAsDouble(position);
    }

}
