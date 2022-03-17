package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static ch.epfl.javelo.Math2.clamp;
import static ch.epfl.javelo.projection.PointWebMercator.ofPointCh;
import static ch.epfl.javelo.projection.SwissBounds.*;
import static java.lang.Short.toUnsignedInt;
import static javax.swing.UIManager.getInt;

public record GraphSectors(ByteBuffer buffer) {

    /**
     * index corresponding to the last byte of the sector's first node
     */
    private static final int OFFSET_Index = 0;
    /**
     *  index corresponding to the last byte of the number of nodes in a sector
     */
    private static final int OFFSET_Number = OFFSET_Index + Integer.BYTES;
    /**
     * number of bytes representing a sector
     */
    private static final int SECTOR_INTS = OFFSET_Number + Short.BYTES;

    /**
     * Lists all sectors having an intersection with a given square
     * @param center of the square
     * @param distance separating the center of the square and its sides
     * @return the list of all sectors having an intersection with the square
     *             centered at the given point and with a side equal
     *                     to twice the given distance
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {

        ArrayList<Sector> sectorInArea = new ArrayList<Sector>();

       /* double xMinInter = clamp(MIN_E, center.e() - distance, MAX_E - 1);
        double xMaxInter = clamp(MIN_E, center.e() + distance, MAX_E - 1);
        double yMinInter = clamp(MIN_N, center.n() - distance, MAX_N - 1);
        double yMaxInter = clamp(MIN_N, center.n() + distance, MAX_N - 1);

        int xMin = (int) ((xMinInter - MIN_E)*128 / WIDTH);
        int xMax = (int) ((xMaxInter - MIN_E)*128 / WIDTH);
        int yMin = (int) ((yMinInter - MIN_N)*128 / HEIGHT);
        int yMax = (int) ((yMaxInter - MIN_N)*128 / HEIGHT);*/

        double xMinInter = center.e() - distance;
        double xMaxInter = center.e() + distance;
        double yMinInter = center.n() - distance;
        double yMaxInter = center.n() + distance;

        int xMin = clamp(0, (int) ((xMinInter - MIN_E)*128 / WIDTH), 127);
        int xMax = clamp(0, (int) ((xMaxInter - MIN_E)*128 / WIDTH), 127);
        int yMin = clamp(0, (int) ((yMinInter - MIN_N)*128 / HEIGHT), 127);
        int yMax = clamp(0, (int) ((yMaxInter - MIN_N)*128 / HEIGHT), 127);

        int indexLeftDown = xMin + 128*yMin;
        int indexLeftUp = xMin + 128*yMax;
        int indexRightDown = xMax + 128*yMin;
        int largeur = indexRightDown - indexLeftDown + 1;

        for(int i = indexLeftDown; i <= indexLeftUp; i += 128){
            for(int j = 0; j < largeur; ++j){
                int indexStartNode = buffer.getInt((i + j)*SECTOR_INTS + OFFSET_Index);
                int numberNodes = toUnsignedInt(buffer().getShort((i + j)*SECTOR_INTS + OFFSET_Number));
                sectorInArea.add(new Sector(indexStartNode, indexStartNode + numberNodes));
            }
        }
        return sectorInArea;
    }

    /**
     * Represents a sector defined by the identity of its first node, which is its index
     *   startNodeId and the identity of the node located just after the last
     *     node in the sector, which is its index endNodeId.
     */
    public record Sector(int startNodeId, int endNodeId) {
    }
}