package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static ch.epfl.javelo.projection.PointWebMercator.ofPointCh;
import static ch.epfl.javelo.projection.SwissBounds.MIN_E;
import static ch.epfl.javelo.projection.SwissBounds.MIN_N;
import static java.lang.Short.toUnsignedInt;
import static javax.swing.UIManager.getInt;

public record GraphSectors(ByteBuffer buffer) {

    private static final int OFFSET_Index = 0;
    private static final int OFFSET_Number = OFFSET_Index + Integer.BYTES;
    private static final int SECTOR_INTS = OFFSET_Number + Short.BYTES;

    public List<Sector> sectorsInArea(PointCh center, double distance) {

        ArrayList<Sector> sectorInArea = new ArrayList<Sector>();

        int xMin = (int) (center.getE() - distance - MIN_E)*128 / 349000;
        int xMax = (int) (center.getE() + distance - MIN_E)*128 / 349000;
        int yMin = (int) (center.getN() - distance - MIN_N)*128 / 221000;
        int yMax = (int) (center.getN() + distance - MIN_N)*128 / 221000;

        int indexLeftDown = xMin + 128*yMin;
        int indexLeftUp = xMin + 128*yMax;
        int indexRightDown = xMax + 128*yMin;
        int largeur = indexRightDown - indexLeftDown + 1;

        for(int i = indexLeftDown; i <= indexLeftUp; i += 128){
            for(int j = 0; j < largeur; ++j){
                int indexStartNode = buffer.getInt((i + j)*SECTOR_INTS + OFFSET_Index);
                int numberNodes = toUnsignedInt(buffer().getShort((i + j)*SECTOR_INTS + OFFSET_Number));
                sectorInArea.add(new Sector(indexStartNode, indexStartNode + numberNodes - 1));
                //System.out.println(i+j);
            }
        }
        return sectorInArea;
    }
    public record Sector(int startNodeId, int endNodeId) {
    }
}