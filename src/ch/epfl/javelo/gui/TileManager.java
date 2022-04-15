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


public final class TileManager {

    private Path basePath;
    private String server;
    private LinkedHashMap<TileId, javafx.scene.image.Image> cacheMemory =
            new LinkedHashMap(100, 0.75f, true);

    private static int CACHE_MEMORY_CAPACITY = 100;

    public TileManager(Path basePath, String server) {
        this.basePath = basePath;
        this.server = server;
    }

    public record TileId(int zoomLevel, int indexX, int indexY) {

        public TileId {
            Preconditions.checkArgument(isValid(zoomLevel, indexX, indexY));
        }

        public static boolean isValid(int zoomLevel, int indexX, int indexY) {
            int limit = (int)Math.pow(2,zoomLevel) - 1;
            return (indexX <= limit && indexY <= limit);
        }

        //Faudra l'enlever mais
        public String toString(){
            return String.valueOf(zoomLevel) + String.valueOf(indexX) + String.valueOf(indexY);
        }

    }

    public Image imageForTileAt(TileId tileId) throws IOException {
        //Search in cacheMemory:
        if(cacheMemory.containsKey(tileId)){
            return cacheMemory.get(tileId);
        }

        Path pathImage = basePath.resolve(String.valueOf(tileId.zoomLevel)).resolve(String.valueOf(tileId.indexX))
                .resolve(tileId.indexY + ".png");
        if (Files.exists(pathImage)){
           return imageInCacheMemory(pathImage, tileId);
        }

        String s = "https://" + server + '/' + tileId.zoomLevel + '/' + tileId.indexX
                + '/' + tileId.indexY + ".png";
        URL u = new URL(s);
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "JaVelo");

        //Create directories:
        Path directoryPath = basePath.resolve(String.valueOf(tileId.zoomLevel)).
                resolve(String.valueOf(tileId.indexX));
        Files.createDirectories(directoryPath);

        try(InputStream i = c.getInputStream(); OutputStream o = new FileOutputStream(pathImage.toFile())){
            i.transferTo(o);
        }
        return imageInCacheMemory(pathImage, tileId);
    }

    public Image imageInCacheMemory(Path pathImage, TileId tileId) throws IOException {
        try(InputStream i = new FileInputStream(pathImage.toFile())){
            javafx.scene.image.Image image = new javafx.scene.image.Image(i);
            //System.out.println(cacheMemory.keySet());
            if(cacheMemory.size() == CACHE_MEMORY_CAPACITY) {
                //System.out.println("Je suis rentré");
                Iterator<TileId> iterator = cacheMemory.keySet().iterator();
                cacheMemory.remove(iterator.next());
                //System.out.println(cacheMemory.keySet());
            }
            cacheMemory.put(tileId, image);
            System.out.println(cacheMemory.keySet());
            return image;
        }
    }
}
