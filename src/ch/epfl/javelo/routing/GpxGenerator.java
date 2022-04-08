package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.Ch1903;
import org.w3c.dom.Element;

import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class GpxGenerator {

    private GpxGenerator() {}

    public static org.w3c.dom.Document createGpx(Route route, ElevationProfile profile) {
        org.w3c.dom.Document doc = newDocument();

        Element root = doc
                .createElementNS("http://www.topografix.com/GPX/1/1",
                        "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        double position = 0;

        for (int i = 0; i < route.points().size(); ++i){

            Element rtept = doc.createElement("rtept");
            rtept.setAttribute("lat", String.valueOf(Ch1903.lat(route.points().get(i).e(),
                    route.points().get(i).e())));
            rtept.setAttribute("lon", String.valueOf(Ch1903.lon(route.points().get(i).e(),
                    route.points().get(i).e())));
            rte.appendChild(rtept);

            Element ele = doc.createElement("ele");
            rtept.appendChild(ele);
            ele.setTextContent(String.valueOf(profile.elevationAt(position)));

            position += route.edges().get(i).length();

        }

        return doc;
    }


    public static Document writeGpx(String fileName, Route route, ElevationProfile profile) {
        return null;
    }

    private static org.w3c.dom.Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e); // Should never happen
        }
    }
}
