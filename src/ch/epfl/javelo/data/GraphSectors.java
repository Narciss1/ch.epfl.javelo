package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static ch.epfl.javelo.projection.PointWebMercator.ofPointCh;
import static java.lang.Short.toUnsignedInt;
import static javax.swing.UIManager.getInt;

public record GraphSectors(ByteBuffer buffer) {

    private static final int OFFSET_Index = 0;
    private static final int OFFSET_Number = OFFSET_Index + Integer.BYTES;
    private static final int SECTOR_INTS = OFFSET_Number + Short.BYTES;

    public List<Sector> sectorsInArea(PointCh center, double distance) {

        ArrayList<Sector> sectorInArea = new ArrayList<Sector>();

        PointCh leftDown = new PointCh(center.getE() - distance, center.getN() - distance);
        PointCh rightDown = new PointCh(center.getE() + distance, center.getN() - distance);
        PointCh leftUp = new PointCh(center.getE() - distance, center.getN() + distance);
        PointCh rightUp = new PointCh(center.getE() + distance, center.getN() + distance);

        int xMin = (int) (leftDown.getE()/2730.0);
        int xMax = (int) (rightDown.getE()/2730.0);
        int yMin = (int) (leftDown.getN()/1730.0);
        int yMax = (int) (rightUp.getN()/1730.0);

        int indexLeftDown = xMin + 128*yMin;
        int indexLeftUp = xMin + 128*yMax;
        int indexRightDown = xMax + 128*yMin;
        int indexRightUp = xMax + 128*yMax;

        int largeur = indexRightDown - indexLeftDown + 1;

        for(int i = indexLeftDown; i <= indexRightUp; i += 128){
            for(int j = 0; j <= largeur - 1; ++j){
                int indexStartNode = buffer.getInt((i + j)*SECTOR_INTS + OFFSET_Index);
                int numberNodes = toUnsignedInt(buffer().getShort((i + j)*SECTOR_INTS + OFFSET_Number));
                int indexEndNode = indexStartNode + numberNodes - 1;
                sectorInArea.add(new Sector(indexStartNode, indexEndNode));
            }
        }
        return sectorInArea;
    }

    public record Sector(int startNodeId, int endNodeId) {

    }
}
