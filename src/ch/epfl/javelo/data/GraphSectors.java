package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Short.toUnsignedInt;
import static javax.swing.UIManager.getInt;

public record GraphSectors(ByteBuffer buffer) {

    //0)Est-ce qu'on peut use simplement une collision physique entre carré et rectangles?
    //1)Est-ce que les constantes évoluent en unité de bits ou vraiment en octet 8 par 8?
    private static final int OFFSET_Index = 0;
    private static final int OFFSET_Number = OFFSET_Index + Integer.BYTES;
    private static final int SECTOR_INTS = OFFSET_Number + Short.BYTES;

    public List<Sector> sectorsInArea(PointCh center, double distance) {
        ArrayList<Sector> sectorInArea = new ArrayList<Sector>();
        //2)Est-ce qu'on peut définir les limites du carré ainsi?
        double eRight = center.e() + 2 * distance;
        double eLeft = center.e() - 2 * distance;
        double nUp = center.n() + 2 * distance;
        double nDown = center.n() - 2 * distance;
        for (int i = 0; i < buffer.capacity(); i++) {
            //3)Est-ce que ces deux variables sont correctes?
            int index = buffer.getInt(buffer.get(i) + OFFSET_Index);
            int number = toUnsignedInt(buffer().getShort(buffer.get(i) + OFFSET_Number));
            //4)Est-ce qu'on peut prendre les startNodeId et endNodeId directement? Ou est-ce qu'on doit définir ces méthodes dans le record?
            //5)Choisir de comparer leur e et n aux limites définies via des checkArgument() est une bonne idée?
            //6)Comment on peut ajouter au tableau des Sector?
        }
        return sectorInArea;
    }
    //7)Quel droit d'accès?
    //8)Laisser vide ou censés add things?
    record Sector(int startNodeId, int endNodeId) {
    }
}
