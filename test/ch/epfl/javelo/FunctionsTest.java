package ch.epfl.javelo;


import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.function.DoubleUnaryOperator;

import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

public class FunctionsTest {

    @Test
    void constantWorksCorrectly() {
        DoubleUnaryOperator cst = Functions.constant(37.98);
        var rng = newRandom();
        for (var i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i += 1) {
            var x = rng.nextInt(1000);
            assertEquals(37.98, cst.applyAsDouble(x));
        }
    }

    @Test
    void sampledThrowsExceptionForLength(){
        float[] samples = {1};
        assertThrows(IllegalArgumentException.class, () ->{
            Functions.sampled(samples,5);
        });
    }


    @Test
    void sampledThrowsExceptionForMax(){
        float[] samples = {1,2,3};
        var rng = newRandom();
        for (var i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i += 1) {
//            var xMax = rng.nextInt(1000);
//            xMax = xMax - 1000;
            assertThrows(IllegalArgumentException.class, () ->{
                Functions.sampled(samples,-7);
            });
        }
    }

    @Test
    void sampledWorksFor2ValuesInSamples(){
        //Linear
        float[] samples = {0,2};
        //var rng = newRandom();
        DoubleUnaryOperator linear = Functions.sampled(samples,2);
        //for (var i = 0; i < RANDOM_ITERATIONS; i += 1){
            //var testValue = rng.nextDouble(0, 2);
            assertEquals(1.889,linear.applyAsDouble(1.889));
        }
    //}


    @Test
    void sampledWorksForAHugeArray(){
        float[] samples = {2, 0, 5, 6, 0, 0.5f, 7};
        float[] samplesTest = {2,0, 5};
        DoubleUnaryOperator fct = Functions.sampled(samples, 6);
        assertEquals(3.85,fct.applyAsDouble(1.77));
    }

    @Test
    public void sampledWorksOnKnownSamplesRAY() {
        float[] samples = new float[] {1.0f, 0.5f, 3.0f};
        double xMax = 2.0;
        DoubleUnaryOperator f = Functions.sampled(samples, xMax);
        assertEquals(1.0, f.applyAsDouble(-1));
        assertEquals(3.0, f.applyAsDouble(3));
        assertEquals(1.75, f.applyAsDouble(1.5));
    }

    @Test
    void sampledTestRAY2(){
        float[] samples = {12,100,6,8};
        double xMax = 12;
        DoubleUnaryOperator test = Functions.sampled(samples, xMax);
        assertEquals(Math2.interpolate(100,6, 0.25) ,test.applyAsDouble(5));
        assertEquals(12,test.applyAsDouble(-67));
        assertEquals(8,test.applyAsDouble(78));
    }

    @Test
    void sampled() {

        assertEquals(8.55,
                Functions.sampled(new float[]{10, 8 ,6, 9, 11, 12}, 10).applyAsDouble(5.7));
    }

    //S

    @Test
    void sampledToThrowExceptions(){
        assertThrows(IllegalArgumentException.class, () -> {
            float[] a = {1};
            var actual1 = Functions.sampled(a, 1.0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            float[] a = {2, 4, 6};
            var actual1 = Functions.sampled(a, -1.0);
        });
    }

    @Test
    void constantToWorkOnValidValues(){
        var actual1 = Functions.constant(1);
        var expected1 = 1;
        assertEquals(expected1, actual1.applyAsDouble(2));
    }

    @Test
    void sampledToWorkOnValidValues(){
        float[] a = {0, 2, 4, 6, 8, 10};
        var actual1 = Functions.sampled(a, 3.5);
        var expected1 = 0;
        assertEquals(expected1, actual1.applyAsDouble(0));

    }

}
