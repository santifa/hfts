package org.santifa.htfs.core.metric;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;
import org.santifa.htfs.core.utils.DictionaryConnector;
import org.santifa.htfs.core.NifDataset;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ratzeputz on 30.12.16.
 */
public class Diversity implements Metric {

    DictionaryConnector connector;

    public Diversity(DictionaryConnector connector) {
        this.connector = connector;
    }

    public Diversity(Path entityFile, Path surfaceFormFile) throws IOException {
        this.connector = new DictionaryConnector(entityFile, surfaceFormFile);
    }

    public static Diversity getDefaultDiversity() {
        try {
            return new Diversity(Paths.get(Diversity.class.getClass().getResource("/filter/ambiguity_e").toURI()),
                    Paths.get(Diversity.class.getClass().getResource("/filter/ambiguity_sf").toURI()));
        } catch (IOException | URISyntaxException e) {
            Logger.error("Failed to load internal entity file {} and surface form file {} with {}",
                    "/filter/ambiguity_e", "/filter/ambiguity_sf", e);
        }
        return null;
    }

    @Override
    public NifDataset calculate(NifDataset dataset) {
        HashMap<String, Integer> knownEntities = new HashMap<>();
        HashMap<String, Integer> knownSurfaceForms = new HashMap<>();

        /* count all surface forms and entities present in the data set */
        for (Document d : dataset.getDocuments()) {
            List<MeaningSpan> meanings = d.getMarkings(MeaningSpan.class);

            for (MeaningSpan meaning : meanings) {
                String s = getEntityName(meaning.getUri());
                String sf = StringUtils.substring(d.getText(), meaning.getStartPosition(),
                        meaning.getStartPosition() + meaning.getLength()).toLowerCase();

                if (knownEntities.containsKey(s)) {
                    knownEntities.put(s, knownEntities.get(s) + 1);
                } else {
                    knownEntities.put(s, 1);
                }

                if (knownSurfaceForms.containsKey(sf)) {
                    knownSurfaceForms.put(sf, knownSurfaceForms.get(sf) + 1);
                } else {
                    knownSurfaceForms.put(sf, 1);
                }
            }
        }

        int counter = 0;
        double averageMacroEntityDiversity = 0.0;
        for (String entity : knownEntities.keySet()) {
            if (connector.getEntityMappping().containsKey(entity)) {
                averageMacroEntityDiversity += (double) knownEntities.get(entity) / (double) connector.getEntityMappping().get(entity);
                counter++;
            }
        }
        if (counter != 0) {
            dataset.setAverageMacroDiversityOfEntities(averageMacroEntityDiversity / (double) counter);
        }

        counter = 0;
        double averageMacroSurfaceFormDiversity = 0.0;
        for (String surfaceForm : knownSurfaceForms.keySet()) {
            if (connector.getSfMapping().containsKey(surfaceForm)) {
                averageMacroSurfaceFormDiversity += (double) knownSurfaceForms.get(surfaceForm) / (double) connector.getSfMapping().get(surfaceForm);
                counter++;
            }
        }

        if (counter != 0) {
            dataset.setAverageMacroDiversityOfSurfaceForms(averageMacroSurfaceFormDiversity/ (double) counter);
        }
        Logger.debug("Macro diversity of entities for {} is {}", dataset.getName(), dataset.getAverageMacroDiversityOfEntities());
        Logger.debug("Macro diversity of surface forms for {} is {}", dataset.getName(), dataset.getAverageMacroDiversityOfSurfaceForms());
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
