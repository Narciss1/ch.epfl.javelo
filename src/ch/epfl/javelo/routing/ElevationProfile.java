package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;
import java.util.function.DoubleUnaryOperator;

public final class ElevationProfile {

    private final double length;
    private final float[] elevationSamples;

    /** Constructs a new ElevationProfile
     *
     * @param length the length of the edge
     * @param elevationSamples the array containing the elevations for the samples of the edge
     */
    public ElevationProfile(double length, float[] elevationSamples){
        Preconditions.checkArgument(length > 0 && elevationSamples.length >= 2);
        this.length = length;
        this.elevationSamples = elevationSamples.clone(); //Est-ce que  mettre ça et les finals suffit ??
    }

    /** Gives the length for the edge
     *
     * @return the edge's length
     */
    public double length(){
        return length;
    }

    /** creates statistics concerning the array of the edge's samples' elevation
     *
     * @param elevationSamples the array containing the samples's heights
     * @return the statistics created based on the array given
     */
    private DoubleSummaryStatistics constructStatistics(float[] elevationSamples){
        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        for (int i = 0; i < elevationSamples.length; ++i){
            s.accept(elevationSamples[i]); //Prblm si float -> double ?
        }
        return s;
    }

    /** Gives the minimal elevation in the array
     *
     * @return the least high elevation in the edge
     */
    public double minElevation(){
        return constructStatistics(elevationSamples).getMin();
    }

    /** Gives the maximal elevation in the edge
     *
     * @return the highest elevation in the edge
     */
    public double maxElevation(){
        return constructStatistics(elevationSamples).getMax();
    }

    /** Gives the sum of all the ascents of the edge
     *
     * @return the total ascent in the edge
     */
    public double totalAscent(){
        double ascent = 0;
        for (int i = 1; i < elevationSamples.length; ++i){
            if (elevationSamples[i-1] < elevationSamples[i]){
                ascent = ascent + (elevationSamples[i] - elevationSamples[i-1]);
            }
        }
        return ascent;
    }

    /** Gives the sum of all the descents of the edge
     *
     * @return the total descent in the edge
     */
    public double totalDescent(){
        double descent = 0;
        for (int i = 1; i < elevationSamples.length; ++i){
            if (elevationSamples[i-1] > elevationSamples[i]){
                descent = descent + (elevationSamples[i-1] - elevationSamples[i]);
            }
        }
        return descent;
    }

    /** Gives the elevation at a certain position given
     *
     * @param position the position we want to give the elevation at
     * @return a double that is the elevation at the given position
     */
    public double elevationAt(double position){
        DoubleUnaryOperator graph = Functions.sampled(elevationSamples, length - 1);
        return graph.applyAsDouble(position);
    }

}
