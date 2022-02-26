package ch.epfl.javelo;


import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.function.DoubleUnaryOperator;
import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
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
}
