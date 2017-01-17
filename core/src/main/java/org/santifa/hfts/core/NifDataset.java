package org.santifa.hfts.core;

import com.hp.hpl.jena.rdf.model.Property;
import org.aksw.gerbil.io.nif.impl.TurtleNIFParser;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.pmw.tinylog.Logger;
import org.santifa.hfts.core.metric.Metric;
import org.santifa.hfts.core.nif.MetaDocument;
import org.santifa.hfts.core.nif.MetaNamedEntity;
import org.santifa.hfts.core.nif.writers.ExtendedTurtleNifWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * A {@link NifDataset} is an extension to the common
 * {@link Document} which is normally used for NIF documents.
 * <br/>
 * The data set contains additional meta information
 * which are calculated by some {@link Metric}.
 *
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class NifDataset {

    private String name;

    private List<MetaDocument> documents;

    private List<MetaNamedEntity> markings;

    private HashMap<Property, String> metaInformations;

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

    public void reload() {
        this.getMarkings().clear();
        referenceMarkings(this.getDocuments());
    }

    private List<MetaNamedEntity> referenceMarkings(List<MetaDocument> docs) {
        List<MetaNamedEntity> markings = new ArrayList<>();
        for (Document doc : docs) {
            markings.addAll(doc.getMarkings(MetaNamedEntity.class));
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
    public List<MetaDocument> getDocuments() {
        return documents;
    }

    /**
     * Gets all markings used in all documents within
     * a data set.
     *
     * @return the markings
     */
    public List<MetaNamedEntity> getMarkings() {
        return referenceMarkings(documents);
    }

    public HashMap<Property, String> getMetaInformations() {
        return metaInformations;
    }

    private List<MetaDocument> parse(InputStream data) {
        Logger.debug("Parsing {} dataset", name);
        TurtleNIFParser parser = new TurtleNIFParser();
        List<Document> docs = parser.parseNIF(data);
        List<MetaDocument> metaDocs = new ArrayList<>(docs.size());

        for (Document d : docs) {
            MetaDocument doc = new MetaDocument(d.getText(), d.getDocumentURI(), null);

            List<Marking> markings = new ArrayList<>();
            for (Marking m : d.getMarkings()) {
                /* for the start only handle meaning spans */
                if (m instanceof MeaningSpan) {
                    markings.add(new MetaNamedEntity(((MeaningSpan) m).getStartPosition(), ((MeaningSpan) m).getLength(),
                            ((MeaningSpan) m).getUris(), new HashSet<>()));
                } else {
                    /* preserve all other markings */
                    markings.add(m);
                }
            }
            doc.setMarkings(markings);
            metaDocs.add(doc);
        }
        //System.out.println(docs);
        return metaDocs;
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
