package org.santifa.hfts.core.metric;

import org.aksw.gerbil.transfer.nif.Document;
import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;
import org.santifa.hfts.core.NifDataset;
import org.santifa.hfts.core.nif.ExtendedNif;
import org.santifa.hfts.core.nif.MetaNamedEntity;
import org.santifa.hfts.core.utils.DictionaryConnector;

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

    private DictionaryConnector connector;

    private boolean flush = false;

    public Diversity(DictionaryConnector connector, boolean flush) {
        this.connector = connector;
        this.flush = flush;
    }

    public Diversity(Path entityFile, Path surfaceFormFile, boolean flush) throws IOException {
        this.connector = new DictionaryConnector(entityFile, surfaceFormFile);
        this.flush = flush;
    }

    public static Diversity getDefaultDiversity() {
        try {
            return new Diversity(Paths.get("..", "data", "ambiguity_e"),
                    Paths.get("..", "data", "ambiguity_sf"), true);
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
        if (flush) {
            connector.flush();
        }
        return dataset;
    }

    @Override
    public NifDataset calculateMicro(NifDataset dataset) {
        double microDivE = 0.0;
        double microDivSf = 0.0;

        /* count all surface forms and entities present in the data set */
        for (Document d : dataset.getDocuments()) {
            List<MetaNamedEntity> meanings = d.getMarkings(MetaNamedEntity.class);
            HashMap<String, List<String>> knownEntities = new HashMap<>();
            HashMap<String, List<String>> knownSurfaceForms = new HashMap<>();

            for (MetaNamedEntity meaning : meanings) {
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
                try {
                    if (connector.getEntityMappping().containsKey(entity)) {
                        averageMacroEntityDiversity += (double) knownEntities.get(entity).size() / (double) connector.getEntityMappping().get(entity);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            microDivE += averageMacroEntityDiversity;

            double averageMacroSurfaceFormDiversity = 0.0;
            for (String surfaceForm : knownSurfaceForms.keySet()) {
                try {
                    if (connector.getSfMapping().containsKey(surfaceForm)) {
                       averageMacroSurfaceFormDiversity += (double) knownSurfaceForms.get(surfaceForm).size() / (double) connector.getSfMapping().get(surfaceForm);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            microDivSf += averageMacroSurfaceFormDiversity;
        }

        if (!dataset.getDocuments().isEmpty()) {
            dataset.getMetaInformations().put(ExtendedNif.microDiversityE, String.valueOf(microDivE / (double) dataset.getDocuments().size()));
            dataset.getMetaInformations().put(ExtendedNif.microDiversitySF, String.valueOf(microDivSf/ (double) dataset.getDocuments().size()));
        } else {
            dataset.getMetaInformations().put(ExtendedNif.microDiversityE, String.valueOf(microDivE));
            dataset.getMetaInformations().put(ExtendedNif.microDiversitySF, String.valueOf(microDivSf));
        }

        Logger.debug("Macro diversity of entities for {} is {}", dataset.getName(), dataset.getMetaInformations().get(ExtendedNif.microDiversityE));
        Logger.debug("Macro diversity of surface forms for {} is {}", dataset.getName(), dataset.getMetaInformations().get(ExtendedNif.microDiversitySF));
        return dataset;
    }

    @Override
    public NifDataset calculateMacro(NifDataset dataset) {
        HashMap<String, List<String>> knownEntities = new HashMap<>();
        HashMap<String, List<String>> knownSurfaceForms = new HashMap<>();

        /* count all surface forms and entities present in the data set */
        for (Document d : dataset.getDocuments()) {
            List<MetaNamedEntity> meanings = d.getMarkings(MetaNamedEntity.class);

            for (MetaNamedEntity meaning : meanings) {
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
            try {
                if (connector.getEntityMappping().containsKey(entity)) {
                    averageMacroEntityDiversity += (double) knownEntities.get(entity).size() / (double) connector.getEntityMappping().get(entity);
                    counter++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (counter != 0) {
            resultE = averageMacroEntityDiversity / (double) counter;
            dataset.getMetaInformations().put(ExtendedNif.macroDiversityE, String.valueOf(resultE));
        } else {
            dataset.getMetaInformations().put(ExtendedNif.macroDiversityE, String.valueOf(resultE));
        }

        counter = 0;
        double resultSf = 0.0;
        double averageMacroSurfaceFormDiversity = 0.0;
        for (String surfaceForm : knownSurfaceForms.keySet()) {
            try {
                if (connector.getSfMapping().containsKey(surfaceForm)) {
                    averageMacroSurfaceFormDiversity += (double) knownSurfaceForms.get(surfaceForm).size() / (double) connector.getSfMapping().get(surfaceForm);
                    counter++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (counter != 0) {
            resultSf = averageMacroSurfaceFormDiversity/ (double) counter;
            dataset.getMetaInformations().put(ExtendedNif.macroDiversitySF, String.valueOf(resultSf));
        } else {
            dataset.getMetaInformations().put(ExtendedNif.macroDiversitySF, String.valueOf(resultSf));
        }
        Logger.debug("Macro diversity of entities for {} is {}", dataset.getName(), dataset.getMetaInformations().get(ExtendedNif.macroDiversityE));
        Logger.debug("Macro diversity of surface forms for {} is {}", dataset.getName(), dataset.getMetaInformations().get(ExtendedNif.macroDiversitySF));
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
