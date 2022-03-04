package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

public final class Functions
{

    private Functions (){ }

    /** creates a DoubleUnaryOperator of type Constant using y which is the image of all the antecedents
     *
     * @param y the image we want the constant function to give
     * @return a new Object of type Constant
     */
    public static DoubleUnaryOperator constant(double y){
        return new Constant(y);
    }


    /** creates a DoubleUnaryOperator of type Sampled using an array samples containing some images
     * and the maximum value x for which the image figures in samples
     *
     * @param samples an array that contains the images of some values
     * @param xMax the last antecedent for which we have the image in samples
     * @return an new Object of type Sampled
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax){
        Preconditions.checkArgument(samples.length >= 2);
        Preconditions.checkArgument(xMax > 0);
        return new Sampled(samples, xMax);
        }


    private static record Constant(double constantValue) implements DoubleUnaryOperator {


        /** applies the constant function on x and returns its image
         *
         * @param x the antecedent
         * @return the image of x
         */
        @Override
        public double applyAsDouble(double x){
            return constantValue;
        }

    }

    private final static class Sampled implements DoubleUnaryOperator{

        private final float[] samples;
        private final double xMax;

        public Sampled (float[] samples, double xMax){
            this.samples = samples.clone();
            this.xMax = xMax;
        }

        /** gives the image of x from the array samples if its image is there or calculate its
         * image using interpolation
         *
         * @param x the antecedent
         * @return the image of x
         */
        @Override
        public double applyAsDouble(double x){
            //The gap is the distance between two antecents which image is in the array samples
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
