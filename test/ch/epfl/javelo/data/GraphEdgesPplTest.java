package ch.epfl.javelo.data;

import ch.epfl.javelo.Q28_4;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.junit.jupiter.api.Assertions.*;

public class GraphEdgesPplTest {

    //TestsLeo

    @Test
    void defaultTest() {
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
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        assertEquals(true, edges.isInverted(0));
        assertEquals(12, edges.targetNodeId(0));
        assertEquals(16.6875, edges.length(0));
        assertEquals(16.0, edges.elevationGain(0));
        assertEquals(2022, edges.attributesIndex(0));
        assertEquals(true, edges.hasProfile(0));
        float[] expectedSamples = new float[]{
                384.0625f, 384.125f, 384.25f, 384.3125f, 384.375f,
                384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
        };
        assertArrayEquals(expectedSamples, edges.profileSamples(0));
    }

    @Test
    void testIsInverted() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(60);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);

        edgesBuffer.putInt(10, 12);
        edgesBuffer.putInt(20, 1);
        edgesBuffer.putInt(30, 0);
        edgesBuffer.putInt(40, ~20);
        edgesBuffer.putInt(50, ~1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });

        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        assertEquals(true, edges.isInverted(0));
        assertEquals(false, edges.isInverted(1));
        assertEquals(false, edges.isInverted(2));
        assertEquals(false, edges.isInverted(3));
        assertEquals(true, edges.isInverted(4));
        assertEquals(true, edges.isInverted(5));
    }

    @Test
    void testTargetNodeId() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(60);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);

        edgesBuffer.putInt(10, 12);
        edgesBuffer.putInt(20, 1);
        edgesBuffer.putInt(30, 0);
        edgesBuffer.putInt(40, ~20);
        edgesBuffer.putInt(50, ~1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });

        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        assertEquals(12, edges.targetNodeId(0));
        assertEquals(12, edges.targetNodeId(1));
        assertEquals(1, edges.targetNodeId(2));
        assertEquals(0, edges.targetNodeId(3));
        assertEquals(20, edges.targetNodeId(4));
        assertEquals(1, edges.targetNodeId(5));
    }

    @Test
    void testLength() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(70);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);

        edgesBuffer.putShort(14, (short) 0);
        //16m
        edgesBuffer.putShort(24, (short) 0x10_0);
        //384,75m
        edgesBuffer.putShort(34, (short) 0x180C);
        edgesBuffer.putShort(44, (short) 28);
        edgesBuffer.putShort(54, (short) 40);
        //we shouldn't expect a negative length, will be interpreted as an unsigned short
        edgesBuffer.putShort(64, (short) -2);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });

        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        assertEquals(16.6875, edges.length(0));
        assertEquals(0, edges.length(1));
        assertEquals(16, edges.length(2));
        assertEquals(384.75, edges.length(3));
        assertEquals(Q28_4.asFloat(28), edges.length(4));
        assertEquals(Q28_4.asFloat(40), edges.length(5));
        //should never happen but it works
        assertEquals(Q28_4.asFloat(Short.toUnsignedInt((short) -2)), edges.length(6));
    }

    @Test
    void checkElevationLength() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(60);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);

        edgesBuffer.putShort(16, (short) 0);
        //-2 will be interpreted as unsigned short so we shouldn't expect a negative elevation
        edgesBuffer.putShort(26, (short) -2);
        edgesBuffer.putShort(36, (short) 30);
        edgesBuffer.putShort(46, (short) 100);
        edgesBuffer.putShort(56, (short) 1);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });

        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        assertEquals(16.0, edges.elevationGain(0));
        assertEquals(0, edges.elevationGain(1));
        assertEquals(Q28_4.asFloat(Short.toUnsignedInt((short) -2)), edges.elevationGain(2));
        assertEquals(Q28_4.asFloat(30), edges.elevationGain(3));
        assertEquals(Q28_4.asFloat(100), edges.elevationGain(4));
        assertEquals(Q28_4.asFloat(1), edges.elevationGain(5));
    }

    @Test
    void testHasProfile() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(60);
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
                (3 << 30) | 1, (0 << 30) | 1, (1 << 30) | 1, (2 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });

        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);


        assertEquals(true, edges.hasProfile(0));
        assertEquals(false, edges.hasProfile(1));
        assertEquals(true, edges.hasProfile(2));
        assertEquals(true, edges.hasProfile(3));
    }

    @Test
    void testAttributesIndex() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(60);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);

        edgesBuffer.putShort(18, (short) 100);
        edgesBuffer.putShort(28, (short) 1);
        edgesBuffer.putShort(38, (short) 0);
        //we shouldn't expect a negative attribute index, will be interpreted as an unsigned short
        edgesBuffer.putShort(48, (short) -2);
        edgesBuffer.putShort(58, (short) 10);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        assertEquals(2022, edges.attributesIndex(0));
        assertEquals(100, edges.attributesIndex(1));
        assertEquals(1, edges.attributesIndex(2));
        assertEquals(0, edges.attributesIndex(3));
        assertEquals(Short.toUnsignedInt((short) -2), edges.attributesIndex(4));
        assertEquals(10, edges.attributesIndex(5));
    }

    @Test
    void testProfileSamples() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(40);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
        // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);

        // Longueur: 6.25
        edgesBuffer.putShort(24, (short) 100);

        // Longueur: 6.25 -> 5 samples -> 16 bits + 4 * 8 bits = 16 + 32 = 48 bits = 3 shorts
        edgesBuffer.putShort(34, (short) 100);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (3 << 30) | 1, (0 << 30) | 1, (1 << 30) | 5, (2 << 30) | 10
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0, (short) 0x180C, (short) 0xFEFF, (short) 0xFFFE, (short) 0xF000,
                (short) 10, (short) 20, (short) 30, (short) 40, (short) 50,
                (short) 100, (short) 0, (short) 1
        });

        GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);

        assertEquals(true, edges.hasProfile(0));
        float[] expectedSamples = new float[]{
                384.0625f, 384.125f, 384.25f, 384.3125f, 384.375f,
                384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
        };
        assertArrayEquals(expectedSamples, edges.profileSamples(0));

        assertArrayEquals(new float[]{}, edges.profileSamples(1));

        float[] expectedSamples2 = new float[]{
                Q28_4.asFloat(10), Q28_4.asFloat(20), Q28_4.asFloat(30), Q28_4.asFloat(40), Q28_4.asFloat(50)
        };
        assertArrayEquals(expectedSamples2, edges.profileSamples(2));

        float[] expectedSamples3 = new float[]{
                Q28_4.asFloat(100), Q28_4.asFloat(100), Q28_4.asFloat(100), Q28_4.asFloat(100), Q28_4.asFloat(101)
        };
        assertArrayEquals(expectedSamples3, edges.profileSamples(3));
    }

    @Test
    public void testsDuProf(){
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
// Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, ~12);
// Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
// Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
// Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 3. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });

        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);

        assertTrue(edges.isInverted(0));
        assertEquals(12, edges.targetNodeId(0));
        assertEquals(16.6875, edges.length(0));
        assertEquals(16.0, edges.elevationGain(0));
        assertEquals(2022, edges.attributesIndex(0));
        float[] expectedSamples = new float[]{
                384.0625f, 384.125f, 384.25f, 384.3125f, 384.375f,
                384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
        };
        assertArrayEquals(expectedSamples, edges.profileSamples(0));
    }


    @Test
    public void profileSamplesType1(){
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);
// Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
// Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
// Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 1. Index du premier échantillon : 0.
                (1 << 30) | 0
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short)0b0001100000000001 , (short)0b0001100000000010 ,
                (short)0b0001100000000100 , (short)0b0001100000000101 ,
                (short)0b0001100000000110 , (short)0b0001100000000111 ,
                (short)0b0001100000001000 , (short)0b0001100000001001 ,
                (short)0b0001100000001011 , (short)0b0001100000001100

        });
        float[] expectedSamples = new float[]{
                384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f
        };
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertArrayEquals(expectedSamples, edges.profileSamples(0));
    }
    @Test
    public void profileSamplesType2(){
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);
// Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
// Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
// Identité de l'ensemble d'attributs OSM : 2022
        edgesBuffer.putShort(8, (short) 2022);

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 2. Index du premier échantillon : 0.
                (2 << 30) | 0
        });
        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0x180C, (short) 0xDFDE,
                (short) 0xDFDF, (short) 0xEFEF,
                (short) 0xCFBE, (short) 0xEF00

        });
        float[] expectedSamples = new float[]{
                366.0625f, 367.125f, 371.25f, 374.3125f, 375.375f,
                376.4375f, 378.5f, 380.5625f, 382.6875f, 384.75f
        };
        GraphEdges edges =
                new GraphEdges(edgesBuffer, profileIds, elevations);
        assertArrayEquals(expectedSamples, edges.profileSamples(0));
    }
}
