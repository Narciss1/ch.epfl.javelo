package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

public final class Functions
{

    private Functions (){ }

    public DoubleUnaryOperator constant(double y){
        return new Constant(y);
    }

    public DoubleUnaryOperator sampled(float[] samples, double xMax){
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

        private float[] samples;
        private double xMax;

        public Sampled (float[] samples, double xMax){
            this.samples = samples;
            this.xMax = xMax;
        }

        @Override
        public double applyAsDouble(double x){
            double x0 = 0;
            double x1 = 0;
            for (int i = 0; i < samples.length - 1; ++i){
                if (Math2.clamp(samples[i], x, samples[i+1]) == x){
                    x0 = samples[i];
                    x1 = samples[i+1];
                }
            }
            return Math2.interpolate(x0, x1,x);
        }


    }


}
