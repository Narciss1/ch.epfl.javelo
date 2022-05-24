package ch.epfl.javelo.gui;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import ch.epfl.javelo.Preconditions;
import javafx.scene.image.Image;

/**
 * Represents the manager of OSM tiles
 * @author Hamane Aya (345565)
 * @author Sadgal Lina (342075)
 */
public final class TileManager {

    private final Path basePath;
    private final String server;
    private final LinkedHashMap<TileId, Image> cacheMemory;

    /**
     * Represents the cache memory capacity
     */
     private final static int CACHE_MEMORY_CAPACITY = 100;
    /**
     * Value of load factor
     */
    private final static float LOAD_FACTOR = 0.75f;
    private final static String PNG = ".png";
    private final static String USER = "User-Agent";
    private final static String JAVELO = "JaVelo";
    private final static String HTTPS = "https://";
    /**
     * Constructor
     * @param basePath the path of the directory containing the disk cache
     * @param server the tiles' server's name
     */
    public TileManager(Path basePath, String server) {
        this.basePath = basePath;
        this.server = server;
        cacheMemory = new LinkedHashMap<>(CACHE_MEMORY_CAPACITY, LOAD_FACTOR, true);
    }

    /**
     * Represents the identity of an OSM tile
     */
    public record TileId(int zoomLevel, int indexX, int indexY) {

        /**
         * Constructor
         * @throws IllegalArgumentException if the index X or the index Y of the tile are not valid
         * @param zoomLevel the zoom level of the tile
         * @param indexX index X of the tile
         * @param indexY index Y of the tile
         */
        public TileId {
            Preconditions.checkArgument(isValid(zoomLevel, indexX, indexY));
        }

        /**
         * Checks if the index X and Y of a tile are valid index according to the zoom level
         * @param zoomLevel the zoom level of the tile
         * @param indexX index X of a potential tile
         * @param indexY index Y of a potential tile
         * @return true if the index X and Y of a potential tile are valid index according to the zoom level,
         * false if one or both of them are not
         */
        public static boolean isValid(int zoomLevel, int indexX, int indexY) {
            int limit = (int)Math.pow(2, zoomLevel) - 1;
            return (indexX <= limit && indexY <= limit && zoomLevel >= 0);
        }
    }

    /**
     * Gives us the image corresponding to the given tile
     * @param tileId the identity of the tile whose image we want
     * @throws IOException if an I/O error occurs
     * @return the image corresponding to the given tile
     */
    public Image imageForTileAt(TileId tileId) throws IOException {
        //Search in the cacheMemory
        if(cacheMemory.containsKey(tileId)) {
            return cacheMemory.get(tileId);
        }

        Path pathImage = basePath.resolve(String.valueOf(server))
                .resolve(String.valueOf(tileId.zoomLevel))
                .resolve(String.valueOf(tileId.indexX))
                .resolve(tileId.indexY + PNG);
        if (Files.exists(pathImage)) {
            return imageInCacheMemory(pathImage, tileId);
        }

        String s = HTTPS + server + '/' + tileId.zoomLevel + '/' + tileId.indexX
                   + '/' + tileId.indexY + PNG;
        URL u = new URL(s);
        URLConnection c = u.openConnection();
        c.setRequestProperty(USER, JAVELO);

        Path directoryPath = basePath.resolve(String.valueOf(server))
                .resolve(String.valueOf(tileId.zoomLevel))
                .resolve(String.valueOf(tileId.indexX));
        Files.createDirectories(directoryPath);

        try(InputStream i = c.getInputStream();
            OutputStream o = new FileOutputStream(pathImage.toFile())) {
            i.transferTo(o);
        }
        return imageInCacheMemory(pathImage, tileId);
    }

    /**
     * Checks if the memory cache is full, add directly the image to it if not, or removes
     * the one that has been charged for the longest one to add the new image
     * @param pathImage the path of the directory containing the image
     * @param tileId the identity of the tile we want the image for
     * @throws IOException if an I/O error occurs
     * @return the image corresponding to the tile
     */
    private Image imageInCacheMemory(Path pathImage, TileId tileId) throws IOException {
        try(InputStream i = new FileInputStream(pathImage.toFile())) {
            Image image = new Image(i);
            if(cacheMemory.size() == CACHE_MEMORY_CAPACITY) {
                Iterator<TileId> iterator = cacheMemory.keySet().iterator();
                cacheMemory.remove(iterator.next());
            }
            cacheMemory.put(tileId, image);
            return image;
        }
    }
}
