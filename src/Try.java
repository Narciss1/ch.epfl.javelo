import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Try {

    public static void main(String[] args) throws IOException {
                Graph g = Graph.loadFrom(Path.of("lausanne"));
                CostFunction cf = new CityBikeCF(g);
                RouteComputer rc = new RouteComputer(g, cf);
                Route r = rc.bestRouteBetween(159049, 117669);
                KmlPrinter.write("javelo.kml", r);
            }
        }
