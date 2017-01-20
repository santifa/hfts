package org.santifa.hfts.core.metric;

import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;
import org.santifa.hfts.core.NifDataset;
import org.santifa.hfts.core.nif.ExtendedNif;
import org.santifa.hfts.core.nif.MetaDocument;
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

    private DictionaryConnector connectorEntity;

    private DictionaryConnector connectorSf;

    private boolean flush = false;

    public Diversity(DictionaryConnector connectorEntity, DictionaryConnector connectorSf, boolean flush) {
        this.connectorEntity = connectorEntity;
        this.connectorSf = connectorSf;
        this.flush = flush;
    }

    public Diversity(Path entityFile, Path surfaceFormFile, boolean flush) throws IOException {
        this.connectorEntity = new DictionaryConnector(entityFile);
        this.connectorSf = new DictionaryConnector(surfaceFormFile);

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
        HashMap<String, List<String>> knownEntities = new HashMap<>();
        HashMap<String, List<String>> knownSurfaceForms = new HashMap<>();

        /* collect all possible surface forms and entities */
        for (MetaDocument d : dataset.getDocuments()) {
            for (MetaNamedEntity entity : d.getMarkings(MetaNamedEntity.class)) {
                String s = getEntityName(entity.getUri()).toLowerCase();
                String sf = StringUtils.substring(d.getText(), entity.getStartPosition(),
                        entity.getStartPosition() + entity.getLength()).toLowerCase();

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

        /* determine the diversity for every marking */
        double diversityEntities = 0.0;
        double diversitySurfaceForms = 0.0;
        for (MetaDocument d : dataset.getDocuments()) {
            for (MetaNamedEntity entity : d.getMarkings(MetaNamedEntity.class)) {
                String s = getEntityName(entity.getUri()).toLowerCase();
                String sf = StringUtils.substring(d.getText(), entity.getStartPosition(),
                        entity.getStartPosition() + entity.getLength()).toLowerCase();

                try {
                    /* check if we have a known entity in dict and dataset */
                    if (knownEntities.containsKey(s) && connectorEntity.getMapping().containsKey(s)) {
                        diversityEntities += knownEntities.get(s).size() / Double.valueOf(connectorEntity.getMapping().get(s));
                    }

                    /* check if we have a known surface form in dict and dataset */
                    if (knownSurfaceForms.containsKey(sf) && connectorSf.getMapping().containsKey(sf)) {
                        diversitySurfaceForms += knownSurfaceForms.get(sf).size() / Double.valueOf(connectorSf.getMapping().get(sf));
                    }

                } catch (IOException e) {
                    Logger.error("Failed to load connector. ", e);
                }
            }
        }

        double resultDiversityEntities = 0.0;
        double resultDiversitySurfaceForms = 0.0;
        if (!dataset.getMarkings().isEmpty()) {
            resultDiversityEntities = diversityEntities / (double) dataset.getMarkings().size();
            resultDiversitySurfaceForms = diversitySurfaceForms / (double) dataset.getMarkings().size();
        }

        dataset.getMetaInformations().put(ExtendedNif.diversityEntities, String.valueOf(resultDiversityEntities));
        dataset.getMetaInformations().put(ExtendedNif.diversitySurfaceForms, String.valueOf(resultDiversitySurfaceForms));
        Logger.debug("Macro diversity of entities for {} is {}", dataset.getName(), dataset.getMetaInformations().get(ExtendedNif.diversityEntities));
        Logger.debug("Macro diversity of surface forms for {} is {}", dataset.getName(), dataset.getMetaInformations().get(ExtendedNif.diversitySurfaceForms));

        /* flush if requested */
        if (flush) {
            connectorSf.flush();
            connectorEntity.flush();
        }
        return dataset;
    }

    @Override
    public NifDataset calculateMicro(NifDataset dataset) {
        return calculate(dataset);
    }

    @Override
    public NifDataset calculateMacro(NifDataset dataset) {
        return calculate(dataset);
    }

    private String getEntityName(String s) {
        if (StringUtils.contains(s, "sentence-")) {
            return StringUtils.substringAfterLast(s, "sentence-");
        } else {
            return StringUtils.substringAfterLast(s, "/");
        }
    }
}
