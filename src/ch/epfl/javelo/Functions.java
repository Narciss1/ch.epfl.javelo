package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * Contains methods allowing us to create objects representing mathematical functions
 from the real numbers to the real numbers.
 * @author Lina Sadgal (342075)
 */

public final class Functions
{

    private Functions (){ }

    /**
     * creates a DoubleUnaryOperator of type Constant using y which is the image of all the antecedents
     * @param y the image we want the constant function to give
     * @return a new Object of type Constant
     */
    public static DoubleUnaryOperator constant(double y){
        return new Constant(y);
    }

    /**
     * creates a DoubleUnaryOperator of type Sampled using an array samples containing some images
     * and the maximum value xMax for which the image figures in samples
     * @throws IllegalArgumentException if the array samples contains less than 2 elements,
     * or xMax is negative.
     * @param samples an array that contains the images of some values
     * @param xMax the last antecedent for which we have the image in samples
     * @return a new Object of type Sampled
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax){
        Preconditions.checkArgument(samples.length >= 2 && xMax > 0);
        return new Sampled(samples, xMax);
        }


    /**
     * allows us to create a constant function
     */
    private static record Constant(double constantValue) implements DoubleUnaryOperator {

        /**
         *  creates a DoubleUnaryOperator of type Sampled using an array samples containing some
         *  images and the maximum value xMax for which the image figures in samples
         * @param x the antecedent
         * @return the image of x
         */
        @Override
        public double applyAsDouble(double x){
            return constantValue;
        }

    }

    /**
     * allows us to create a function from an array of distant samples
     */

    private final static class Sampled implements DoubleUnaryOperator{

        private final float[] samples;
        private final double xMax;

        /**
         * allows us to create a function from an array of samples
         * @param samples the array of samples
         * @param xMax the last antecedent for which we have the image in samples
         */
        public Sampled (float[] samples, double xMax){
            this.samples = samples.clone();
            this.xMax = xMax;
        }

        /**
         * gives the image of x from the array samples using interpolation, or returns the
         * image of 0 if x is smaller than 0, or the image of xMax if x is bigger than xMax
         * @param x the antecedent
         * @return the image of x
         */
        @Override
        public double applyAsDouble(double x){
            //The gap is the distance between two antecedents which image is in the array samples
            double gap = xMax / (samples.length - 1);
            double floorValue = Math.floor(x / gap);
            if (x > 0 && x < xMax) {
                return Math2.interpolate(samples[(int)(floorValue)],
                        samples[(int)(floorValue) + 1] ,
                        (x - floorValue * gap) / gap );
            }
            else if (x <= 0){
                return samples[0];
            }
            return samples[samples.length - 1];
        }
    }

}
