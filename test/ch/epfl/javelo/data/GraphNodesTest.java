package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GraphNodesTest {

    @Test
    void methodsGraphSectorsWorksGivenTest() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(1, ns.count());
        assertEquals(2_600_000, ns.nodeE(0));
        assertEquals(1_200_000, ns.nodeN(0));
        assertEquals(2, ns.outDegree(0));
        assertEquals(0x1234, ns.edgeId(0, 0));
        assertEquals(0x1235, ns.edgeId(0, 1));
    }

    @Test
    void methodsGraphSectorsWorksAyaTest() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_536_263 << 4,
                1_215_736 << 4,
                0x2_918_1873,
                1_297_183 << 4,
                2_015_772 << 4,
                0x1_803_0925
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(2, ns.count());
        assertEquals(2_536_263, ns.nodeE(0));
        assertEquals(1_215_736, ns.nodeN(0));
        assertEquals(2, ns.outDegree(0));
        assertEquals(0x918_1873, ns.edgeId(0, 0));
        assertEquals(Integer.valueOf(Integer.toHexString(0x918_1873 + 1),16),ns.edgeId(0, 1));
        assertEquals(1_297_183, ns.nodeE(1));
        assertEquals(2_015_772, ns.nodeN(1));
        assertEquals(1, ns.outDegree(1));
        assertEquals(0x803_0925, ns.edgeId(1, 0));
        assertThrows(AssertionError.class, () -> {
            ns.edgeId(1, 1);
        });
    }

    @Test
    void methodsGraphSectorsWorksAyaTest2() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                8_501_018 << 4,
                9_005_704 << 4,
                0x6_000_1029,
                3_108_826 << 4,
                4_010_002 << 4,
                0x2_800_0015,
                3_108_826 << 4,
                4_010_002 << 4,
                0x9_800_0010
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(3, ns.count());
        assertEquals(8_501_018, ns.nodeE(0));
        assertEquals(9_005_704, ns.nodeN(0));
        assertEquals(6, ns.outDegree(0));
        assertEquals(0x1029, ns.edgeId(0, 0));
        assertEquals(Integer.valueOf(Integer.toHexString(0x1029 + 1), 16), ns.edgeId(0, 1));
        assertEquals(3_108_826, ns.nodeE(1));
        assertEquals(4_010_002, ns.nodeN(1));
        assertEquals(2, ns.outDegree(1));
        assertEquals(0x800_0015, ns.edgeId(1, 0));
        assertEquals(Integer.valueOf(Integer.toHexString(0x800_0015 + 1),16), ns.edgeId(1, 1));
        assertEquals(3_108_826, ns.nodeE(2));
        assertEquals(4_010_002, ns.nodeN(2));
        assertEquals(9, ns.outDegree(2));
        assertEquals(0x800_0010, ns.edgeId(2, 0));
        assertEquals(Integer.valueOf(Integer.toHexString(0x800_0010 + 1),16), ns.edgeId(2, 1));
    }

    //TestsLeo

    @Test
    public void givenTests(){
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(1, ns.count());
        assertEquals(2_600_000, ns.nodeE(0));
        assertEquals(1_200_000, ns.nodeN(0));
        assertEquals(2, ns.outDegree(0));
        assertEquals(0x1234, ns.edgeId(0, 0));
        assertEquals(0x1235, ns.edgeId(0, 1));
    }

    // Inutile de tester les cas limites liés à la limite de stockage de Q28.4, E et N ne dépassant jamais 3_000_000 < 2^22.
    @Test
    public void nodeEAndNodeNTests(){

        int[] bPrime = {0b00000000000000000000000000000100,
                0b00000000000000000000000000010000,
                0b01000000000000000000000000001101,
                0b00000000000000000000000000000000,
                0b00000000000000000000000001000000,
                0b10000000000000000000000000000111};

        IntBuffer b = IntBuffer.wrap(bPrime);
        GraphNodes gn = new GraphNodes(b);

        assertEquals(0.25, gn.nodeE(0));
        assertEquals(0, gn.nodeE(1));

        assertEquals(1, gn.nodeN(0));
        assertEquals(4, gn.nodeN(1));

    }

    @Test
    public void countTest(){
        int[] bPrime = {0b00000000000000000000000000000100,
                0b00000000000000000000000000010000,
                0b01000000000000000000000000001101,
                0b00000000000000000000000000000000,
                0b00000000000000000000000001000000,
                0b10000000000000000000000000000111};

        IntBuffer b = IntBuffer.wrap(bPrime);
        GraphNodes gn = new GraphNodes(b);

        assertEquals(2, gn.count());

    }

    @Test
    public void outDegreeTest(){

        int[] bPrime = {0b00000000000000000000000000000100,
                0b00000000000000000000000000010000,
                0b01000000000000000000000000001101,
                0b00000000000000000000000000000000,
                0b00000000000000000000000001000000,
                0b10000000000000000000000000000111};

        IntBuffer b = IntBuffer.wrap(bPrime);
        GraphNodes gn = new GraphNodes(b);

        assertEquals(4, gn.outDegree(0));
        assertEquals(8, gn.outDegree(1));
    }


    @Test
    public void edgeIdTest(){

        //Normal case
        int[] bPrime = {0b00000000000000000000000000000100,
                0b00000000000000000000000000010000,
                0b01000000000000000000000000001101,
                0b00000000000000000000000000000000,
                0b00000000000000000000000001000000,
                0b10000000000000000000000000000111};

        IntBuffer b = IntBuffer.wrap(bPrime);
        GraphNodes gn = new GraphNodes(b);

        assertEquals(13, gn.edgeId(0, 0));
        assertEquals(15, gn.edgeId(0, 2));
        assertEquals(12, gn.edgeId(1, 5));

        //What if the edgeIndex isn't valid ? Works fine.

        assertThrows(AssertionError.class, () -> {
            gn.edgeId(0, 4);
        });

        assertThrows(AssertionError.class, () -> {
            gn.edgeId(0, -8);
        });


    }
}