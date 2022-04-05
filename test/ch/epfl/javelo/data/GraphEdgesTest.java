package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.List;


import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

public class GraphEdgesTest {


    IntBuffer profileIds = IntBuffer.wrap(new int[]{
            // Type : 0. Index du premier échantillon : 1.
            (2 << 30) | 1
    });

    ShortBuffer elevations = ShortBuffer.wrap(new short[]{
            (short) 0,
//            (short) 0x180C, (short) 0xFEFF,
//            (short) 0xFFFE, (short) 0xF000 //TypeIn3
            (short) 0x180C, (short) 0x1212 //Type2
    });


    //PROF TESTS

    @Test
    void isInvertedWorks() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertTrue(edges.isInverted(0));
    }

    @Test
    void nodeTargetWorks() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertEquals(12, edges.targetNodeId(0));
    }

    @Test
    void lengthWorks() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertEquals(16.6875, edges.length(0));
    }

    @Test
    void elevationGainWorks() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertEquals(16.0, edges.elevationGain(0));
    }

//    @Test
//    void typeOfProfileWorks(){
//        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
//        // Sens : inversé. Nœud destination : 12.
//        edgesBuffer.putInt(0, ~12);
//        // Longueur : 0x10.b m (= 16.6875 m)
//        edgesBuffer.putShort(4, (short) 0x10_b);
//        // Dénivelé : 0x10.0 m (= 16.0 m)
//        edgesBuffer.putShort(6, (short) 0x10_0);
//        // Identité de l'ensemble d'attributs OSM : 2022
//        edgesBuffer.putShort(8, (short) 2022);
//        GraphEdges edges =
//                new GraphEdges(edgesBuffer, profileIds, elevations);
//        assertEquals(2,edges.typeOfProfile(0));
//    }

    @Test
    void attributeIndexWorks() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertEquals(2022, edges.attributesIndex(0));
    }

    @Test
    void profileSamplesWorksType2NotInverted() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        //Changement - = 4m.
        edgesBuffer.putShort(4, (short) 0x4_0);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        float[] type3InvertedArray = new float[]{
                384.0625f,
                384.125f, 384.25f, 384.3125f, 384.375f,
                384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
        };
        float[] type2Array = new float[]{
                384.75f, 385.875f, 387f
        }; //0x0180 - 0x1212
        assertArrayEquals(type2Array, edges.profileSamples(0));
    }

    @Test
    void profileWorksWithType3Inverted(){
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
            (short) 0x180C, (short) 0xFEFF,
            (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        float[] type3InvertedArray = new float[]{
                384.0625f,
                384.125f, 384.25f, 384.3125f, 384.375f,
                384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
        };
        assertArrayEquals(type3InvertedArray, edges.profileSamples(0));
    }

    @Test
    void profileWorksWithType3NotInverted(){
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        float[] type3Array = new float[]{
                384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,
        };

        assertArrayEquals(type3Array, edges.profileSamples(0));
    }

    @Test
    void profileWorksWithType0(){

        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        //Longueur : 4m
        edgesBuffer.putShort(4, (short) 0x4_0);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (0 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0, (short) 0x1801, (short) 0x180C, (short) 0x180E //TypeIn0
        });
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        float[] type0Array = new float[0];
        assertArrayEquals(type0Array, edges.profileSamples(0));
    }


    @Test
    void profileWorksWithType1Inverted(){

        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        //Longueur : 4m
        edgesBuffer.putShort(4, (short) 0x4_0);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (1 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0, (short) 0x1801, (short) 0x180C, (short) 0x180E //TypeIn1
        });
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        float[] type1InvertedArray = new float[]{
                384.875f, 384.75f, 384.0625f
        };
        for (int i = 0; i < edges.profileSamples(0).length; ++i){
            System.out.println(edges.profileSamples(0)[i]);
        }
        assertArrayEquals(type1InvertedArray, edges.profileSamples(0));
    }


    //Added
    @Test
    public void eazeaze()
    {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
// Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short)((short) 0x10_b / 4f));
// Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
// Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        int type = 1;
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (type << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) (0x180C + 1),
                (short) (0x180C + 2), (short) (0x180C + 1)
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        assertTrue(edges.isInverted(0));
        assertEquals(12, edges.targetNodeId(0));
        //assertEquals(16.6875 / 2, edges.length(0), 1);


        assertEquals(16.0, edges.elevationGain(0));

        assertEquals(2022, edges.attributesIndex(0));

        float[] expectedSamples = new float[]{
                (float) ((short) (0x180C + 1) / 16.0), (float) ((short) (0x180C + 2)/ 16.0), (float) ((short) (0x180C + 1) / 16.0), (float) (0x180C / 16.0)

        };
        float[] result = edges.profileSamples(0);
        System.out.println(Arrays.toString(result));
        assertArrayEquals(expectedSamples, result);
    }

    @Test
    public void eazeaze2()
    {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
// Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short)((short) 0x10_b / 2f));
// Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
// Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        int type = 2;
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (type << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xFF00
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        assertTrue(edges.isInverted(0));
        assertEquals(12, edges.targetNodeId(0));
        assertEquals(16.6875 / 2, edges.length(0), 1);


        assertEquals(16.0, edges.elevationGain(0));

        assertEquals(2022, edges.attributesIndex(0));

        float[] expectedSamples = new float[]{
                (float)(384.75 - 2*0.125 - 3/16.0), (float)(384.75 - 2*0.125 - 2/16.0), (float)(384.75 - 0.125 - 2/16.0), (float)(384.75 - 0.125 - 1/16.0), (float)(384.75 - 0.125), 384.75f
        };
        float[] result = edges.profileSamples(0);
        System.out.println(Arrays.toString(result));
        assertArrayEquals(expectedSamples, result);
    }





    @Test
    void graphEdgesWorksOnGivenExample() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF, (short) 0xFFFE, (short) 0xF000
        });

        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        assertTrue(edges.isInverted(0));
        assertEquals(12, edges.targetNodeId(0));
        assertEquals(16.6875, edges.length(0));
        assertEquals(16.0, edges.elevationGain(0));
        assertTrue(edges.hasProfile(0));
        assertEquals(2022, edges.attributesIndex(0));
        float[] expectedSamples = new float[]{
                384.0625f, 384.125f, 384.25f, 384.3125f, 384.375f,
                384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
        };
        assertArrayEquals(expectedSamples, edges.profileSamples(0));
    }

    @Test
    void graphEdgesIsInvertedWorksForPlusMinus100() {
        var edgesCount = 10_000;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        var rng = newRandom();
        for (int targetNodeId = -100; targetNodeId < 100; targetNodeId += 1) {
            var edgeId = rng.nextInt(edgesCount);
            edgesBuffer.putInt(10 * edgeId, targetNodeId);
            var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
            assertEquals(targetNodeId < 0, graphEdges.isInverted(edgeId));
        }
    }

    @Test
    void graphEdgesTargetNodeIdWorksForPlusMinus100() {
        var edgesCount = 10_000;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        var rng = newRandom();
        for (int targetNodeId = -100; targetNodeId < 100; targetNodeId += 1) {
            var edgeId = rng.nextInt(edgesCount);
            edgesBuffer.putInt(10 * edgeId, targetNodeId);
            var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
            var expectedTargetNodeId = targetNodeId < 0 ? ~targetNodeId : targetNodeId;
            assertEquals(expectedTargetNodeId, graphEdges.targetNodeId(edgeId));
        }
    }

    @Test
    void graphEdgesLengthWorksOnRandomValues() {
        var edgesCount = 10_000;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var edgeId = rng.nextInt(edgesCount);
            var length = rng.nextDouble(1 << 12);
            var length_q12_4 = (int) Math.scalb(length, 4);
            length = Math.scalb((double) length_q12_4, -4);
            edgesBuffer.putShort(10 * edgeId + 4, (short) length_q12_4);
            var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
            assertEquals(length, graphEdges.length(edgeId));
        }
    }

    @Test
    void graphEdgesElevationGainWorksOnRandomValues() {
        var edgesCount = 10_000;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var edgeId = rng.nextInt(edgesCount);
            var elevationGain = rng.nextDouble(1 << 12);
            var elevationGain_q12_4 = (int) Math.scalb(elevationGain, 4);
            elevationGain = Math.scalb((double) elevationGain_q12_4, -4);
            edgesBuffer.putShort(10 * edgeId + 6, (short) elevationGain_q12_4);
            var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
            assertEquals(elevationGain, graphEdges.elevationGain(edgeId));
        }
    }

    @Test
    void graphEdgesHasProfileWorks() {
        var edgesCount = 10_000;
        var elevationsCount = 25_000;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(elevationsCount);
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            for (int profileType = 0; profileType < 4; profileType += 1) {
                var edgeId = rng.nextInt(edgesCount);
                var firstSampleIndex = rng.nextInt(elevationsCount);
                profileIds.put(edgeId, (profileType << 30) | firstSampleIndex);
                var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
                assertEquals(profileType != 0, graphEdges.hasProfile(edgeId));
            }
        }
    }

    @Test
    void graphEdgesProfileSamplesWorksForType0() {
        var edgesCount = 10_000;
        var elevationsCount = 25_000;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(elevationsCount);
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var edgeId = rng.nextInt(edgesCount);
            var firstSampleIndex = rng.nextInt(elevationsCount);
            profileIds.put(edgeId, firstSampleIndex);
            var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
            assertArrayEquals(new float[0], graphEdges.profileSamples(edgeId));
        }
    }

    @Test
    void graphEdgesProfileSamplesWorksForType1() {
        var elevationsCount = 500;
        var edgesBuffer = ByteBuffer.allocate(10);
        var profileIds = IntBuffer.allocate(1);
        var elevations = ShortBuffer.allocate(elevationsCount);
        var rng = newRandom();
        for (int i = 0; i < elevationsCount; i += 1)
            elevations.put(i, (short) rng.nextInt(1 << 16));
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var inverted = rng.nextBoolean();
            var sampleCount = rng.nextInt(2, 100);
            var firstSampleIndex = rng.nextInt(elevationsCount - sampleCount);
            var edgeLength_q28_4 = (2 * (sampleCount - 1)) << 4;
            edgesBuffer.putInt(0, inverted ? ~0 : 0);
            edgesBuffer.putShort(4, (short) edgeLength_q28_4);
            profileIds.put(0, (1 << 30) | firstSampleIndex);
            var expectedSamples = new float[sampleCount];
            for (int j = 0; j < sampleCount; j += 1) {
                var elevation = Math.scalb(Short.toUnsignedInt(elevations.get(firstSampleIndex + j)), -4);
                if (inverted)
                    expectedSamples[sampleCount - 1 - j] = elevation;
                else
                    expectedSamples[j] = elevation;
            }
            var graphEdges = new GraphEdges(edgesBuffer.asReadOnlyBuffer(), profileIds.asReadOnlyBuffer(), elevations.asReadOnlyBuffer());
            assertArrayEquals(expectedSamples, graphEdges.profileSamples(0));
        }
    }

    @Test
    void graphEdgesProfileSamplesWorksForType2() {
        List<TestCase> samples = List.of(
                new TestCase(
                        new short[]{0x2a2d, 0x0201},
                        new float[]{674.812500f, 674.937500f, 675.000000f}),
                new TestCase(
                        new short[]{0x2036, 0x01e0, (short) 0xd200},
                        new float[]{515.375000f, 515.437500f, 513.437500f, 510.562500f}),
                new TestCase(
                        new short[]{0x2022, 0x0103, 0x090c},
                        new float[]{514.125000f, 514.187500f, 514.375000f, 514.937500f, 515.687500f}),
                new TestCase(
                        new short[]{0x204d, (short) 0xf2f9, 0x0209, (short) 0xfa00},
                        new float[]{516.812500f, 515.937500f, 515.500000f, 515.625000f, 516.187500f, 515.812500f}),
                new TestCase(
                        new short[]{0x19c8, (short) 0xfefe, (short) 0xfeff, (short) 0xff13},
                        new float[]{412.500000f, 412.375000f, 412.250000f, 412.125000f, 412.062500f, 412.000000f, 413.187500f}),
                new TestCase(
                        new short[]{0x1776, 0x0100, (short) 0xfff3, (short) 0xe800, 0x0100},
                        new float[]{375.375000f, 375.437500f, 375.437500f, 375.375000f, 374.562500f, 373.062500f, 373.062500f, 373.125000f}));

        var edgesBuffer = ByteBuffer.allocate(10);
        var profileIds = IntBuffer.wrap(new int[]{2 << 30}).asReadOnlyBuffer();
        var elevations = ShortBuffer.allocate(20);
        for (TestCase testCase : samples) {
            var sampleCount = testCase.uncompressed().length;
            var edgeLength_q28_4 = (2 * (sampleCount - 1)) << 4;
            elevations.put(0, testCase.compressed());
            edgesBuffer.putShort(4, (short) edgeLength_q28_4);
            var graphEdges = new GraphEdges(edgesBuffer.asReadOnlyBuffer(), profileIds, elevations.asReadOnlyBuffer());

            // Straight
            edgesBuffer.putInt(0, 0);
            assertArrayEquals(testCase.uncompressed(), graphEdges.profileSamples(0));

            // Inverted
            edgesBuffer.putInt(0, ~0);
            assertArrayEquals(testCase.uncompressedInverted(), graphEdges.profileSamples(0));
        }
    }

    @Test
    void graphEdgesProfileSamplesWorksForType3() {
        List<TestCase> samples = List.of(
                new TestCase(
                        new short[]{0x2a0f, (short) 0xeff0},
                        new float[]{672.937500f, 672.812500f, 672.750000f, 672.687500f}),
                new TestCase(
                        new short[]{0x2a3e, (short) 0xefef},
                        new float[]{675.875000f, 675.750000f, 675.687500f, 675.562500f, 675.500000f}),
                new TestCase(
                        new short[]{0x2a13, 0x1121, 0x1000},
                        new float[]{673.187500f, 673.250000f, 673.312500f, 673.437500f, 673.500000f, 673.562500f}),
                new TestCase(
                        new short[]{0x2a8b, 0x2121, 0x2200},
                        new float[]{680.687500f, 680.812500f, 680.875000f, 681.000000f, 681.062500f, 681.187500f, 681.312500f}),
                new TestCase(
                        new short[]{0x2a49, (short) 0xefef, (short) 0xeef0},
                        new float[]{676.562500f, 676.437500f, 676.375000f, 676.250000f, 676.187500f, 676.062500f, 675.937500f, 675.875000f}));

        var edgesBuffer = ByteBuffer.allocate(10);
        var profileIds = IntBuffer.wrap(new int[]{3 << 30}).asReadOnlyBuffer();
        var elevations = ShortBuffer.allocate(20);
        for (TestCase testCase : samples) {
            var sampleCount = testCase.uncompressed().length;
            var edgeLength_q28_4 = (2 * (sampleCount - 1)) << 4;
            elevations.put(0, testCase.compressed());
            edgesBuffer.putShort(4, (short) edgeLength_q28_4);
            var graphEdges = new GraphEdges(edgesBuffer.asReadOnlyBuffer(), profileIds, elevations.asReadOnlyBuffer());

            // Straight
            edgesBuffer.putInt(0, 0);
            assertArrayEquals(testCase.uncompressed(), graphEdges.profileSamples(0));

            // Inverted
            edgesBuffer.putInt(0, ~0);
            assertArrayEquals(testCase.uncompressedInverted(), graphEdges.profileSamples(0));
        }
    }

    private record TestCase(short[] compressed, float[] uncompressed) {
        public float[] uncompressedInverted() {
            float[] array = uncompressed();
            var inverted = Arrays.copyOf(array, array.length);
            for (int i = 0, j = inverted.length - 1; i < j; i += 1, j -= 1) {
                var t = inverted[i];
                inverted[i] = inverted[j];
                inverted[j] = t;
            }
            return inverted;
        }
    }

    @Test
    void graphEdgesAttributesIndexWorksOnRandomValues() {
        var edgesCount = 10_000;
        var edgesBuffer = ByteBuffer.allocate(10 * edgesCount);
        var profileIds = IntBuffer.allocate(edgesCount);
        var elevations = ShortBuffer.allocate(10);
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var edgeId = rng.nextInt(edgesCount);
            var attributesIndex = rng.nextInt(1 << 16);
            edgesBuffer.putShort(10 * edgeId + 8, (short) attributesIndex);
            var graphEdges = new GraphEdges(edgesBuffer, profileIds, elevations);
            assertEquals(attributesIndex, graphEdges.attributesIndex(edgeId));
        }
    }

}



