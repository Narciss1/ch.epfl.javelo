package ch.epfl.javelo.gui;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.LinkedHashMap;

public final class TileManager {
    //Should add them en attributs right?
    private Path directory;
    private String server;
    //Verify if that's how it should be initialized?
    private LinkedHashMap<TileId, Image> memory = new LinkedHashMap(100, 0.75f, true);

    public record TileId(int zoomLevel, int indexX, int indexY) {
        //private or public record?
        public static boolean isValid(int zoomLevel, int indexX, int indexY){
            return true;//If the tileId is valid; range of attributes?
            //Ne pas limiter le zoom here, juste verifier sil est positif ou null
        }
    }

    public TileManager(Path directory, String server) {
        //Make sure that it is a string
        this.directory = directory;
        this.server = server;

    }

    public Image imageForTile(TileId tileId) throws IOException {
        //Should créer l'image here avec le flot de sortie pour la mettre en argument?
        if(memory.containsValue(memory.get(tileId))){
            return memory.get(tileId);
        }
        //Le cache disque se trouve dans le director: comment y chercher l'image?


        StringBuilder s = new StringBuilder();
        //Is this how am supposed to write the URL?
        s.append("https://" + server + '/' + tileId.zoomLevel + '/' + tileId.indexX
                + '/' + tileId.indexY + ".png");
        //Is it the right version à import pour URL?
        URL u = new URL(s.toString());
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "JaVelo");
        //C'est le bon try?
        try(InputStream i = c.getInputStream()){
            //How to get le flot de sortie du cache disque
            OutputStream o = new FileOutputStream(server);
            i.transferTo(o);
        }
        //Reconvertir le flot de sortie ou take flot that we already have?
        InputStream j = c.getInputStream();
        //Image image = new Image(j);
        //memory.put(tileId, image);
        //return image;
        return null;
    }

}
