package org.santifa.htfs.core;

import com.hp.hpl.jena.rdf.model.Property;
import org.aksw.gerbil.io.nif.impl.TurtleNIFParser;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.pmw.tinylog.Logger;
import org.santifa.htfs.core.utils.ExtendedTurtleNifWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A {@link NifDataset} is an extension to the common
 * {@link Document} which is normally used for NIF documents.
 * <br/>
 * The data set contains additional meta information
 * which are calculated by some {@link org.santifa.htfs.core.metric.Metric}.
 *
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class NifDataset {

    private String name;

    private List<Document> documents;

    private List<Marking> markings;

    private HashMap<Property, String> metaInformations;

    private boolean changed = false;

    /**
     * Instantiates a new Nif dataset.
     *
     * @param name the name of the data set
     * @param data the path to the data file
     * @throws IOException the io exception if no file is found
     */
    public NifDataset(String name, Path data) throws IOException {
        this.name = name;
        this.documents = parse(Files.newInputStream(data));
        this.markings = referenceMarkings(documents);
        this.metaInformations = new HashMap<>();
    }

    /**
     * Instantiates a new Nif dataset.
     *
     * @param name the name of the data set
     * @param data the data as string
     */
    NifDataset(String name, String data) {
        this.name = name;
        this.documents = parse(new ByteArrayInputStream(data.getBytes(Charset.forName("UTF-8"))));
        this.markings = referenceMarkings(documents);
        this.metaInformations = new HashMap<>();
    }

    private List<Marking> referenceMarkings(List<Document> docs) {
        List<Marking> markings = new ArrayList<>();
        for (Document doc : docs) {
            markings.addAll(doc.getMarkings());
        }
        return markings;
    }

    /**
     * Gets name of the data set.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the nif {@link Document}.
     * <br/>
     * Do not edit the documents with this method.
     *
     * @return the documents
     */
    public List<Document> getDocuments() {
        return documents;
    }

    /**
     * Due to the internal usage a cross reference to the
     * {@link Marking}s of a document is stored. So use this
     * method if the documents get changed.
     *
     * @return the list
     */
    public List<Document> changeDocuments() {
        changed = true;
        return documents;
    }

    /**
     * Gets all markings used in all documents within
     * a data set.
     *
     * @return the markings
     */
    public List<Marking> getMarkings() {
        if (changed) {
            markings = referenceMarkings(documents);
        }
        return markings;
    }

    public HashMap<Property, String> getMetaInformations() {
        return metaInformations;
    }

    private List<Document> parse(InputStream data) {
        Logger.debug("Parsing {} dataset", name);
        TurtleNIFParser parser = new TurtleNIFParser();
        return parser.parseNIF(data);
    }

    /**
     * Write the data set to some {@link OutputStream} in turtle format.
     *
     * @param os the ooutputstream
     */
    public void write(OutputStream os) {
        ExtendedTurtleNifWriter writer = new ExtendedTurtleNifWriter();
        writer.writeNIF(this, os);
    }

    /**
     * Write the data set to a string in turtle format.
     *
     * @return the data set as string
     */
    public String write() {
        ExtendedTurtleNifWriter writer = new ExtendedTurtleNifWriter();
        return writer.writeNIF(this);
    }

    @Override
    public String toString() {
        return "NifDataset{" +
                "name='" + name + '\'' +
                ", documents=" + documents +
                ", markings=" + markings +
                '}';
    }
}
