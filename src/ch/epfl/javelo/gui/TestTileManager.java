package ch.epfl.javelo.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.nio.file.Path;

    public final class TestTileManager extends Application {
        public static void main(String[] args) {
            launch(args);
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
            TileManager tm = new TileManager(
                    Path.of("."), "tile.openstreetmap.org");
            for (int i = 1; i <= 6; ++i){
                Image tileImage = tm.imageForTileAt(
                        new TileManager.TileId(12, i, i));
            }
            //Image tileImage = tm.imageForTileAt(
//                    new TileManager.TileId(17, 10, 20));
                   //new TileManager.TileId(19, 271720, 185430));
            Platform.exit();
        }


    }


