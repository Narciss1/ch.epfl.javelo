package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;



public class main {

    public static void main(String[] args) throws IOException {

        Graph g = Graph.loadFrom(Path.of("ch_west"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        long t0 = System.nanoTime();
        //Route r = rc.bestRouteBetween(2046000, 2654240);
        //Route r2 = rc.bestRouteBetween(1956050, 2414240);
        Route r3 = rc.bestRouteBetween(2100000, 2150110);
        //Route r = rc.bestRouteBetween(159049, 117669);
        KmlPrinter.write("javelo.kml", r3);
        System.out.printf("Itinéraire calculé en %d ms\n",
                (System.nanoTime() - t0) / 1_000_000);
    }

}
