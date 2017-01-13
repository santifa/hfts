package org.santifa.htfs.core.metric;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.vocabulary.NIF;
import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;
import org.santifa.htfs.core.NifDataset;
import org.santifa.htfs.core.utils.DictionaryConnector;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ratzeputz on 30.12.16.
 */
public class Diversity implements Metric {

    public static final Property microDiversityE = ResourceFactory.createProperty(NIF.getURI(), "microDiversity");
    public static final Property microDiversitySF = ResourceFactory.createProperty(NIF.getURI(), "microDiversity");
    public static final Property macroDiversityE = ResourceFactory.createProperty(NIF.getURI(), "macroDiversity");
    public static final Property macroDiversitySF = ResourceFactory.createProperty(NIF.getURI(), "macroDiversity");

    DictionaryConnector connector;

    public Diversity(DictionaryConnector connector) {
        this.connector = connector;
    }

    public Diversity(Path entityFile, Path surfaceFormFile) throws IOException {
        this.connector = new DictionaryConnector(entityFile, surfaceFormFile);
    }

    public static Diversity getDefaultDiversity() {
        try {
            return new Diversity(Paths.get("..", "data", "ambiguity_e"),
                    Paths.get("..", "data", "ambiguity_sf"));
        } catch (IOException e) {
            Logger.error("Failed to load internal entity file {} and surface form file {} with {}",
                    "../data/ambiguity_e", "../data/ambiguity_sf", e);
        }
        return null;
    }

    @Override
    public NifDataset calculate(NifDataset dataset) {
        dataset = calculateMacro(dataset);
        dataset = calculateMicro(dataset);
        return dataset;
    }

    @Override
    public NifDataset calculateMicro(NifDataset dataset) {
        double microDivE = 0.0;
        double microDivSf = 0.0;

        /* count all surface forms and entities present in the data set */
        for (Document d : dataset.getDocuments()) {
            List<MeaningSpan> meanings = d.getMarkings(MeaningSpan.class);
            HashMap<String, List<String>> knownEntities = new HashMap<>();
            HashMap<String, List<String>> knownSurfaceForms = new HashMap<>();

            for (MeaningSpan meaning : meanings) {
                String s = getEntityName(meaning.getUri());
                String sf = StringUtils.substring(d.getText(), meaning.getStartPosition(),
                        meaning.getStartPosition() + meaning.getLength()).toLowerCase();

               /* add a known surface form to known entities */
                if (knownEntities.containsKey(s)) {
                    if (!knownEntities.get(s).contains(sf)) {
                        knownEntities.get(s).add(sf);
                    }
                } else {
                    knownEntities.put(s, new ArrayList<>());
                    knownEntities.get(s).add(sf);

                }

                /* add entities to known surface forms */
                if (knownSurfaceForms.containsKey(sf)) {
                    if (knownSurfaceForms.get(sf).contains(s)) {
                        knownSurfaceForms.get(sf).add(s);
                    }
                } else {
                    knownSurfaceForms.put(sf, new ArrayList<>());
                    knownSurfaceForms.get(sf).add(s);
                }
            }

            double averageMacroEntityDiversity = 0.0;
            for (String entity : knownEntities.keySet()) {
                if (connector.getEntityMappping().containsKey(entity)) {
                    averageMacroEntityDiversity += (double) knownEntities.get(entity).size() / (double) connector.getEntityMappping().get(entity);
                }
            }
            microDivE += averageMacroEntityDiversity;

            double averageMacroSurfaceFormDiversity = 0.0;
            for (String surfaceForm : knownSurfaceForms.keySet()) {
                if (connector.getSfMapping().containsKey(surfaceForm)) {
                   averageMacroSurfaceFormDiversity += (double) knownSurfaceForms.get(surfaceForm).size() / (double) connector.getSfMapping().get(surfaceForm);
                }
            }
            microDivSf += averageMacroSurfaceFormDiversity;
        }

        if (!dataset.getDocuments().isEmpty()) {
            dataset.getMetaInformations().put(microDiversityE, String.valueOf(microDivE / (double) dataset.getDocuments().size()));
            dataset.getMetaInformations().put(microDiversitySF, String.valueOf(microDivSf/ (double) dataset.getDocuments().size()));
        } else {
            dataset.getMetaInformations().put(microDiversityE, String.valueOf(microDivE));
            dataset.getMetaInformations().put(microDiversitySF, String.valueOf(microDivSf));
        }

        Logger.debug("Macro diversity of entities for {} is {}", dataset.getName(), dataset);
        Logger.debug("Macro diversity of surface forms for {} is {}", dataset.getName(), dataset);
        return dataset;
    }

    @Override
    public NifDataset calculateMacro(NifDataset dataset) {
        HashMap<String, List<String>> knownEntities = new HashMap<>();
        HashMap<String, List<String>> knownSurfaceForms = new HashMap<>();

        /* count all surface forms and entities present in the data set */
        for (Document d : dataset.getDocuments()) {
            List<MeaningSpan> meanings = d.getMarkings(MeaningSpan.class);

            for (MeaningSpan meaning : meanings) {
                String s = getEntityName(meaning.getUri());
                String sf = StringUtils.substring(d.getText(), meaning.getStartPosition(),
                        meaning.getStartPosition() + meaning.getLength()).toLowerCase();

                /* add a known surface form to known entities */
                if (knownEntities.containsKey(s)) {
                    if (!knownEntities.get(s).contains(sf)) {
                        knownEntities.get(s).add(sf);
                    }
                } else {
                    knownEntities.put(s, new ArrayList<>());
                    knownEntities.get(s).add(sf);

                }

                /* add entities to known surface forms */
                if (knownSurfaceForms.containsKey(sf)) {
                    if (knownSurfaceForms.get(sf).contains(s)) {
                        knownSurfaceForms.get(sf).add(s);
                    }
                } else {
                    knownSurfaceForms.put(sf, new ArrayList<>());
                    knownSurfaceForms.get(sf).add(s);
                }
            }
        }

        int counter = 0;
        double resultE = 0.0;
        double averageMacroEntityDiversity = 0.0;
        for (String entity : knownEntities.keySet()) {
            if (connector.getEntityMappping().containsKey(entity)) {
                averageMacroEntityDiversity += (double) knownEntities.get(entity).size() / (double) connector.getEntityMappping().get(entity);
                counter++;
            }
        }

        if (counter != 0) {
            resultE = averageMacroEntityDiversity / (double) counter;
            dataset.getMetaInformations().put(macroDiversityE, String.valueOf(resultE));
        } else {
            dataset.getMetaInformations().put(macroDiversityE, String.valueOf(resultE));
        }

        counter = 0;
        double resultSf = 0.0;
        double averageMacroSurfaceFormDiversity = 0.0;
        for (String surfaceForm : knownSurfaceForms.keySet()) {
            if (connector.getSfMapping().containsKey(surfaceForm)) {
                averageMacroSurfaceFormDiversity += (double) knownSurfaceForms.get(surfaceForm).size() / (double) connector.getSfMapping().get(surfaceForm);
                counter++;
            }
        }

        if (counter != 0) {
            resultSf = averageMacroSurfaceFormDiversity/ (double) counter;
            dataset.getMetaInformations().put(macroDiversitySF, String.valueOf(resultSf));
        } else {
            dataset.getMetaInformations().put(macroDiversitySF, String.valueOf(resultSf));
        }
        Logger.debug("Macro diversity of entities for {} is {}", dataset.getName(), resultE);
        Logger.debug("Macro diversity of surface forms for {} is {}", dataset.getName(), resultSf);
        return dataset;
    }

    private String getEntityName(String s) {
        if (StringUtils.contains(s, "sentence-")) {
            return StringUtils.substringAfterLast(s, "sentence-");
        } else {
            return StringUtils.substringAfterLast(s, "/");
        }
    }
}
