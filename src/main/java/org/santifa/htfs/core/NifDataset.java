package org.santifa.htfs.core;

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
import java.util.List;

/**
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class NifDataset {

    private String name;

    private List<Document> documents;

    private List<Marking> markings;

    private double notAnnotatedDocs = 0.0;

    private double macroDensity = 0.0;

    private double averageMacroDiversityOfEntities = 0.0;

    private double averageMacroDiversityOfSurfaceforms = 0.0;

    private double averageMacroAmbiguityOfEntities = 0.0;

    private double averageMacroAmbiguityOfSurfaceforms = 0.0;

    private boolean changed = false;

    public NifDataset(String name, Path data) throws IOException {
        this.name = name;
        this.documents = parse(Files.newInputStream(data));
        this.markings = referenceMarkings(documents);
    }

    NifDataset(String name, String data) {
        this.name = name;
        this.documents = parse(new ByteArrayInputStream(data.getBytes(Charset.forName("UTF-8"))));
        this.markings = referenceMarkings(documents);
    }

    private List<Marking> referenceMarkings(List<Document> docs) {
        List<Marking> markings = new ArrayList<>();
        for (Document doc : docs) {
            markings.addAll(doc.getMarkings());
        }
        return markings;
    }

    public String getName() {
        return name;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public List<Document> changeDocuments() {
        changed = true;
        return documents;
    }

    public List<Marking> getMarkings() {
        if (changed) {
            markings = referenceMarkings(documents);
        }
        return markings;
    }

    public void setNotAnnotatedDocs(double notAnnotatedDocs) {
        this.notAnnotatedDocs = notAnnotatedDocs;
    }

    public double getNotAnnotatedDocs() {
        return notAnnotatedDocs;
    }

    public double getMacroDensity() {
        return macroDensity;
    }

    public void setMacroDensity(double macroDensity) {
        this.macroDensity = macroDensity;
    }

    public double getAverageMacroDiversityOfEntities() {
        return averageMacroDiversityOfEntities;
    }

    public void setAverageMacroDiversityOfEntities(double averageMacroDiversityOfEntities) {
        this.averageMacroDiversityOfEntities = averageMacroDiversityOfEntities;
    }

    public double getAverageMacroDiversityOfSurfaceforms() {
        return averageMacroDiversityOfSurfaceforms;
    }

    public void setAverageMacroDiversityOfSurfaceforms(double averageDiversityOfSurfaceforms) {
        this.averageMacroDiversityOfSurfaceforms = averageDiversityOfSurfaceforms;
    }

    public double getAverageMacroAmbiguityOfEntities() {
        return averageMacroAmbiguityOfEntities;
    }

    public void setAverageMacroAmbiguityOfEntities(double averageMacroAmbiguityOfEntities) {
        this.averageMacroAmbiguityOfEntities = averageMacroAmbiguityOfEntities;
    }

    public double getAverageMacroAmbiguityOfSurfaceforms() {
        return averageMacroAmbiguityOfSurfaceforms;
    }

    public void setAverageMacroAmbiguityOfSurfaceforms(double averageMacroAmbiguityOfSurfaceforms) {
        this.averageMacroAmbiguityOfSurfaceforms = averageMacroAmbiguityOfSurfaceforms;
    }

    private List<Document> parse(InputStream data) {
        Logger.debug("Parsing {} dataset", name);
        TurtleNIFParser parser = new TurtleNIFParser();
        return parser.parseNIF(data);
    }

    public void write(OutputStream os) {
        ExtendedTurtleNifWriter writer = new ExtendedTurtleNifWriter();
        writer.writeNIF(this, os);
    }

    public String write() {
        ExtendedTurtleNifWriter writer = new ExtendedTurtleNifWriter();
        return writer.writeNIF(this);
    }
}
