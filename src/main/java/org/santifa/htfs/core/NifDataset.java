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

    private double notAnnotatedDocs = 0.0;

    private double macroDensity = 0.0;

    private double averageMacroDiversityOfEntities = 0.0;

    private double averageMacroDiversityOfSurfaceForms = 0.0;

    private double averageMacroAmbiguityOfEntities = 0.0;

    private double averageMacroAmbiguityOfSurfaceForms = 0.0;

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

    /**
     * Sets not annotated docs metric.
     *
     * @param notAnnotatedDocs the not annotated docs
     */
    public void setNotAnnotatedDocs(double notAnnotatedDocs) {
        this.notAnnotatedDocs = notAnnotatedDocs;
    }

    /**
     * Gets not annotated docs metric.
     *
     * @return the not annotated docs
     */
    public double getNotAnnotatedDocs() {
        return notAnnotatedDocs;
    }

    /**
     * Gets macro density metric.
     *
     * @return the macro density
     */
    public double getMacroDensity() {
        return macroDensity;
    }

    /**
     * Sets macro density metric.
     *
     * @param macroDensity the macro density
     */
    public void setMacroDensity(double macroDensity) {
        this.macroDensity = macroDensity;
    }

    /**
     * Gets average macro diversity of entities metric.
     *
     * @return the average macro diversity of entities
     */
    public double getAverageMacroDiversityOfEntities() {
        return averageMacroDiversityOfEntities;
    }

    /**
     * Sets average macro diversity of entities metric.
     *
     * @param averageMacroDiversityOfEntities the average macro diversity of entities
     */
    public void setAverageMacroDiversityOfEntities(double averageMacroDiversityOfEntities) {
        this.averageMacroDiversityOfEntities = averageMacroDiversityOfEntities;
    }

    /**
     * Gets average macro diversity of surface forms metric.
     *
     * @return the average macro diversity of surface forms
     */
    public double getAverageMacroDiversityOfSurfaceForms() {
        return averageMacroDiversityOfSurfaceForms;
    }

    /**
     * Sets average macro diversity of surface forms metric.
     *
     * @param averageDiversityOfSurfaceForms the average diversity of surface forms
     */
    public void setAverageMacroDiversityOfSurfaceForms(double averageDiversityOfSurfaceForms) {
        this.averageMacroDiversityOfSurfaceForms = averageDiversityOfSurfaceForms;
    }

    /**
     * Gets average macro ambiguity of entities metric.
     *
     * @return the average macro ambiguity of entities
     */
    public double getAverageMacroAmbiguityOfEntities() {
        return averageMacroAmbiguityOfEntities;
    }

    /**
     * Sets average macro ambiguity of entities metric.
     *
     * @param averageMacroAmbiguityOfEntities the average macro ambiguity of entities
     */
    public void setAverageMacroAmbiguityOfEntities(double averageMacroAmbiguityOfEntities) {
        this.averageMacroAmbiguityOfEntities = averageMacroAmbiguityOfEntities;
    }

    /**
     * Gets average macro ambiguity of surface forms metric.
     *
     * @return the average macro ambiguity of surface forms
     */
    public double getAverageMacroAmbiguityOfSurfaceForms() {
        return averageMacroAmbiguityOfSurfaceForms;
    }

    /**
     * Sets average macro ambiguity of surface forms metric.
     *
     * @param averageMacroAmbiguityOfSurfaceForms the average macro ambiguity of surfaceforms
     */
    public void setAverageMacroAmbiguityOfSurfaceForms(double averageMacroAmbiguityOfSurfaceForms) {
        this.averageMacroAmbiguityOfSurfaceForms = averageMacroAmbiguityOfSurfaceForms;
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
                ", notAnnotatedDocs=" + notAnnotatedDocs +
                ", macroDensity=" + macroDensity +
                ", averageMacroDiversityOfEntities=" + averageMacroDiversityOfEntities +
                ", averageMacroDiversityOfSurfaceForms=" + averageMacroDiversityOfSurfaceForms +
                ", averageMacroAmbiguityOfEntities=" + averageMacroAmbiguityOfEntities +
                ", averageMacroAmbiguityOfSurfaceForms=" + averageMacroAmbiguityOfSurfaceForms +
                ", changed=" + changed +
                '}';
    }
}
