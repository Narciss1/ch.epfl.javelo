package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Locale;

/**
 * Represents a route generator in GPX format
 * @author Hamane Aya (345565)
 * @author Sadgal Lina (342075)
 */
public class GpxGenerator {

    /**
     * Constructor
     */
    private GpxGenerator() {}

    /**
     * Creates a document containing the itinerary information
     * @param route the itinerary for which we want a GPX document
     * @param profile the route's profile
     * @return the created document
     */
    public static Document createGpx(Route route, ElevationProfile profile) {
        Document doc = newDocument();
        Element root = doc.createElementNS(
                "http://www.topografix.com/GPX/1/1",
                "gpx");

        doc.appendChild(root);
        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 " +
                        "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        Iterator<PointCh> iPoints = route.points().iterator();
        Iterator<Edge> iEdges = route.edges().iterator();

        double position = 0;

        while (iPoints.hasNext()) {
            PointCh currentPoint = iPoints.next();
            Element rtept = doc.createElement("rtept");
            rtept.setAttribute(
                    "lat",
                    String.format(Locale.ROOT,
                            "%.5f", Math.toDegrees(currentPoint.lat())));
            rtept.setAttribute(
                    "lon",
                    String.format(Locale.ROOT,
                            "%.5f", Math.toDegrees(currentPoint.lon())));
            rte.appendChild(rtept);

            Element ele = doc.createElement("ele");
            rtept.appendChild(ele);
            ele.setTextContent(
                    String.format(Locale.ROOT, "%.2f",
                            profile.elevationAt(position)));
            if (iEdges.hasNext()) {
                System.out.println(position);
                position += iEdges.next().length();
            }
        }
        return doc;
    }

    /**
     * Writes a GPX document in a file
     * @param fileName the file name
     * @param route the itinerary for which we want a GPX document
     * @param profile the route's profile
     * @throws IOException if an I/O error occurs
     */
    public static void writeGpx(String fileName, Route route, ElevationProfile profile)
            throws IOException {
        Document doc = createGpx(route, profile);
        File file = new File(fileName);
        Writer w = Files.newBufferedWriter(file.toPath());
        try {
            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(w));
        } catch (TransformerException e) {
            throw new Error(e); // Should never happen
        }
    }

    /**
     * Creates a new document
     * @return a document
     */
    private static Document newDocument() {
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
