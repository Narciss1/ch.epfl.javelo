package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.*;

import static java.lang.Short.toUnsignedInt;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphSectorsTest {

    @Test
    void methodsGraphSectorsWorksGivenTest() {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20});
        GraphSectors ns = new GraphSectors(buffer);
        int idStartNode = buffer.getInt(0);
        int idEndNode = idStartNode + toUnsignedInt(buffer.getShort(4));
        GraphSectors.Sector sector = new GraphSectors.Sector(idStartNode,idEndNode);
        List<GraphSectors.Sector> expected0 = new ArrayList<GraphSectors.Sector>();
        expected0.add(sector);
        List<GraphSectors.Sector> actual0 = ns.sectorsInArea(new PointCh(2485100, 1075100), 100);
        assertEquals(expected0, actual0);

        ByteBuffer buffer1 = ByteBuffer.wrap(new byte[]{
                0,0,0,16,0,20,0,0,0,21,0,12});
        GraphSectors ns1 = new GraphSectors(buffer1);
        int idEndNode1 = buffer1.getInt(6) + toUnsignedInt(buffer1.getShort(10));
        GraphSectors.Sector sector1 = new GraphSectors.Sector(buffer1.getInt(6),idEndNode1);
        List<GraphSectors.Sector> expected1 = new ArrayList<GraphSectors.Sector>();
        expected1.add(sector1);
        List<GraphSectors.Sector> actual1 = ns1.sectorsInArea(new PointCh(2488050, 1076050), 50);
        assertEquals(expected1, actual1);

        //ns.sectorsInArea(new PointCh(2505000, 1085000), 5000);
    }
}
