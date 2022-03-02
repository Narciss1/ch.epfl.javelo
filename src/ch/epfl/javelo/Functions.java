package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

public final class Functions
{

    private Functions (){ }

    public static DoubleUnaryOperator constant(double y){
        return new Constant(y);
    }

    public static DoubleUnaryOperator sampled(float[] samples, double xMax){
        Preconditions.checkArgument(samples.length >= 2);
        Preconditions.checkArgument(xMax > 0);
        return new Sampled(samples, xMax);
        }


    private static record Constant(double constantValue) implements DoubleUnaryOperator {


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

        @Override
        public double applyAsDouble(double x){
            double gap = xMax / (samples.length - 1);
            double floorValue = Math.floor(x / gap);
            if (x > 0 && x < xMax) {
                return Math2.interpolate(samples[(int)(floorValue)],
                        samples[(int)(floorValue)+1] ,
                        (x - floorValue* gap) / gap );
            }
            else if (x <= 0){
                return samples[0];
            }
            return samples[samples.length-1];
        }
    }


}
