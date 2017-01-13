package org.santifa.hfts.core.metric;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.vocabulary.NIF;
import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;
import org.santifa.hfts.core.NifDataset;
import org.santifa.hfts.core.utils.DictionaryConnector;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by ratzeputz on 30.12.16.
 */
public class Ambiguity implements Metric {

    public static final Property microAmbiguityE = ResourceFactory.createProperty(NIF.getURI(), "microAmbiguityEntities");

    public static final Property microAmbiguitySF = ResourceFactory.createProperty(NIF.getURI(), "microAmbiguitySurfaceForms");

    public static final Property macroAmbiguityE = ResourceFactory.createProperty(NIF.getURI(), "macroAmbiguityEntities");

    public static final Property macroAmbiguitySF = ResourceFactory.createProperty(NIF.getURI(), "macroAmbiguitySurfaceForms");

    DictionaryConnector connector;

    public Ambiguity(DictionaryConnector connector) {
        this.connector = connector;
    }

    public Ambiguity(Path entityFile, Path surfaceFormFile) throws IOException {
        this.connector = new DictionaryConnector(entityFile, surfaceFormFile);
    }

    public static Ambiguity getDefaultAmbiguity() {
        try {
            return new Ambiguity(Paths.get("..", "data", "ambiguity_e"), Paths.get("..", "data", "ambiguity_sf"));
        } catch (IOException e) {
            Logger.error("Failed to load internal entity file {} and surface form file {} with {}",
                    "../data/ambiguity_e", "../data/ambiguity_sf", e);
        }
        return null;
    }

    @Override
    public NifDataset calculate(NifDataset dataset) {
        dataset = calculateMicro(dataset);
        dataset = calculateMacro(dataset);
       return dataset;
    }

    @Override
    public NifDataset calculateMicro(NifDataset dataset) {
        double entities = 0.0;
        double surfaceForms = 0.0;

        for (Document d : dataset.getDocuments()) {
            int ambiguityEntities = 0;
            int ambiguitySf = 0;
            List<MeaningSpan> meanings = d.getMarkings(MeaningSpan.class);

            /* add every annotation ambiguity and increase number of stored ambiguities */
            for (MeaningSpan meaning : meanings) {
                String s = getEntityName(meaning.getUri());
                String sf = StringUtils.substring(d.getText(), meaning.getStartPosition(),
                        meaning.getStartPosition() + meaning.getLength()).toLowerCase();
                sf = StringUtils.replace(sf, "_", " ");

                if (connector.getEntityMappping().containsKey(s)) {
                    ambiguityEntities += connector.getEntityMappping().get(s);
                }


                if (connector.getSfMapping().containsKey(sf)) {
                    ambiguitySf += connector.getSfMapping().get(sf);
                }
            }
            if (meanings.size() != 0) {
                entities += (double) ambiguityEntities / (double) meanings.size();
                surfaceForms += (double) ambiguitySf / (double) meanings.size();
            }
        }

        double resultMicroEntities = 0.0;
        if (!dataset.getDocuments().isEmpty()) {
            resultMicroEntities = entities / (double) dataset.getDocuments().size();
        }
        dataset.getMetaInformations().put(microAmbiguityE, String.valueOf(resultMicroEntities));

        double resultMicroSurfaceForms = 0.0;
        if (!dataset.getDocuments().isEmpty()) {
            resultMicroSurfaceForms = surfaceForms / (double) dataset.getDocuments().size();
        }
        dataset.getMetaInformations().put(microAmbiguitySF, String.valueOf(resultMicroSurfaceForms));

        Logger.debug("Macro ambiguity of entities for {} is {}", dataset.getName(), resultMicroEntities);
        Logger.debug("Macro ambiguity of surface forms for {} is {}", dataset.getName(), resultMicroSurfaceForms);
        return dataset;
    }

    @Override
    public NifDataset calculateMacro(NifDataset dataset) {
        int ambiguityEntities = 0;
        int ambiguitySf = 0;
        int counter = 0;

        for (Document d : dataset.getDocuments()) {
            List<MeaningSpan> meanings = d.getMarkings(MeaningSpan.class);

            /* add every annotation ambiguity and increase number of stored ambiguities */
            for (MeaningSpan meaning : meanings) {
                String s = getEntityName(meaning.getUri());
                String sf = StringUtils.substring(d.getText(), meaning.getStartPosition(),
                        meaning.getStartPosition() + meaning.getLength()).toLowerCase();
                sf = StringUtils.replace(sf, "_", " ");

                if (connector.getEntityMappping().containsKey(s)) {
                    ambiguityEntities += connector.getEntityMappping().get(s);
                }


                if (connector.getSfMapping().containsKey(sf)) {
                    ambiguitySf += connector.getSfMapping().get(sf);
                }
            }
            counter += meanings.size();
        }

        double resultMacroEntities = 0.0;
        if (counter != 0.0) {
            resultMacroEntities = (double) ambiguityEntities / (double) counter;
        }
        dataset.getMetaInformations().put(macroAmbiguityE, String.valueOf(resultMacroEntities));

        double resultMacroSurfaceForms = 0.0;
        if (counter != 0.0) {
            resultMacroSurfaceForms = (double) ambiguitySf / (double) counter;
        }
        dataset.getMetaInformations().put(macroAmbiguitySF, String.valueOf(resultMacroSurfaceForms));

        Logger.debug("Macro ambiguity of entities for {} is {}", dataset.getName(), resultMacroEntities);
        Logger.debug("Macro ambiguity of surface forms for {} is {}", dataset.getName(), resultMacroSurfaceForms);
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
