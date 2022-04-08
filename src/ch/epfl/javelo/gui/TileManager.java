package ch.epfl.javelo.gui;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import javafx.scene.image.Image;


public final class TileManager {

    private Path basePath;
    private String server;
    private LinkedHashMap<TileId, javafx.scene.image.Image> cacheMemory =
            new LinkedHashMap(100, 0.75f, true);

    private static int CACHE_MEMORY_CAPACITY = 100;

    public record TileId(int zoomLevel, int indexX, int indexY) {
        public static boolean isValid(int zoomLevel, int indexX, int indexY) {
            return true;//If the tileId is valid; range of attributes?
            //Ne pas limiter le zoom here, juste verifier sil est positif ou null
        }
    }

    public TileManager(Path basePath, String server) {
        this.basePath = basePath;
        this.server = server;

    }

    public Image imageForTile(TileId tileId) throws IOException {
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

        //Creat directories:
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
            if(cacheMemory.size() == CACHE_MEMORY_CAPACITY) {
                Iterator<TileId> iterator = cacheMemory.keySet().iterator();
                cacheMemory.remove(iterator.next());
            }
            cacheMemory.put(tileId, image);
            return image;
        }
    }
}
