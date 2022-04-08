package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import static ch.epfl.javelo.Math2.clamp;
import static ch.epfl.javelo.projection.SwissBounds.*;
import static java.lang.Short.toUnsignedInt;

/**
 * Represents a table containing the 16384 sectors of JaVelo
 * Every GraphSectors had a buffer containing the value
 * of the attributes of all the sectors
 * @author Aya Hamane (345565)
 */
public record GraphSectors(ByteBuffer buffer) {

    /**
     * index corresponding to the last byte of the sector's first node
     */
    private static final int OFFSET_INDEX = 0;
    /**
     * index corresponding to the last byte of the number of nodes in a sector
     */
    private static final int OFFSET_NUMBER = OFFSET_INDEX + Integer.BYTES;
    /**
     * number of bytes representing a sector
     */
    private static final int SECTOR_INTS = OFFSET_NUMBER + Short.BYTES;
    /**
     * number of sectors on the side of the rectangle encompassing Switzerland
     */
    private static final int SECTOR_SIDE = 128;
    /**
     * number representing the limit of sectors that can be used
     */
    private static final int SECTOR_MAX = 127;

    /**
     * Lists all sectors having an intersection with a given square
     * @param center center of the square
     * @param distance distance separating the center of the square and its sides
     * @return list of all sectors having an intersection with the square
     *             centered at the given point and with a side equal
     *                     to twice the given distance
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {
        ArrayList<Sector> sectorInArea = new ArrayList<>();

        double xMinInter = center.e() - distance;
        double xMaxInter = center.e() + distance;
        double yMinInter = center.n() - distance;
        double yMaxInter = center.n() + distance;

        int xMin = clamp(0, (int) ((xMinInter - MIN_E)*SECTOR_SIDE / WIDTH), SECTOR_MAX);
        int xMax = clamp(0, (int) ((xMaxInter - MIN_E)*SECTOR_SIDE / WIDTH), SECTOR_MAX);
        int yMin = clamp(0, (int) ((yMinInter - MIN_N)*SECTOR_SIDE / HEIGHT), SECTOR_MAX);
        int yMax = clamp(0, (int) ((yMaxInter - MIN_N)*SECTOR_SIDE / HEIGHT), SECTOR_MAX);

        int indexLeftDown = xMin + SECTOR_SIDE*yMin;
        int indexLeftUp = xMin + SECTOR_SIDE*yMax;
        int indexRightDown = xMax + SECTOR_SIDE*yMin;
        int width = indexRightDown - indexLeftDown + 1;

        for(int i = indexLeftDown; i <= indexLeftUp; i += SECTOR_SIDE){
            for(int j = 0; j < width; ++j){
                int indexStartNode = buffer.getInt((i + j)*SECTOR_INTS + OFFSET_INDEX);
                int numberNodes = toUnsignedInt(buffer().getShort(
                        (i + j)*SECTOR_INTS + OFFSET_NUMBER));
                sectorInArea.add(new Sector(indexStartNode, indexStartNode + numberNodes));
            }
        }
        return sectorInArea;
    }

    /**
     * Represents a sector defined by the identity of its first node, which is its index
     *   startNodeId and the identity of the node located just after the last
     *      node in the sector, which is its index endNodeId.
     */
    public record Sector(int startNodeId, int endNodeId) {}
}