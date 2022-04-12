package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.Ch1903;
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
import java.util.Locale;

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
            rtept.setAttribute("lat", String.format(Locale.ROOT, "%.5f", Math.toDegrees(route.points().get(i).lat())));
            rtept.setAttribute("lon", String.format(Locale.ROOT, "%.5f", Math.toDegrees(route.points().get(i).lon())));
            rte.appendChild(rtept);

            Element ele = doc.createElement("ele");
            rtept.appendChild(ele);
            ele.setTextContent(String.format(Locale.ROOT, "%.2f", Math.toDegrees(route.points().get(i).lon())));

            if (i != route.points().size()- 1){
                position += route.edges().get(i).length();
            }

        }

        return doc;
    }


    public static Document writeGpx(String fileName, Route route, ElevationProfile profile)
    throws IOException {

        org.w3c.dom.Document doc = createGpx(route, profile);
        File file = new File(fileName);
        Writer w = Files.newBufferedWriter(file.toPath());

        try {
            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(w));
        } catch (TransformerException e) {
            throw new Error(e); // Should never happen
        }
        return doc;
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
