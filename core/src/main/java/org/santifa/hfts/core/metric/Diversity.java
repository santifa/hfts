package org.santifa.hfts.core.metric;

import org.pmw.tinylog.Logger;
import org.santifa.hfts.core.NifDataset;
import org.santifa.hfts.core.nif.ExtendedNif;
import org.santifa.hfts.core.nif.MetaDocument;
import org.santifa.hfts.core.nif.MetaNamedEntity;
import org.santifa.hfts.core.utils.DictionaryConnector;
import org.santifa.hfts.core.utils.HftsHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ratzeputz on 30.12.16.
 */
public class Diversity implements Metric {

    private DictionaryConnector connectorEntity;

    private DictionaryConnector connectorSf;

    public Diversity(DictionaryConnector connectorEntity, DictionaryConnector connectorSf) {
        this.connectorEntity = connectorEntity;
        this.connectorSf = connectorSf;
    }

    public static Diversity getDefaultDiversity(int timeToLive) {
            return new Diversity(DictionaryConnector.getDefaultEntityConnector(timeToLive),
                    DictionaryConnector.getDefaultSFConnector(timeToLive));
    }

    @Override
    public NifDataset calculate(NifDataset dataset) {
        HashMap<String, List<String>> knownEntities = new HashMap<>();
        HashMap<String, List<String>> knownSurfaceForms = new HashMap<>();

        /* collect all possible surface forms and entities */
        for (MetaDocument d : dataset.getDocuments()) {
            for (MetaNamedEntity entity : d.getMarkings(MetaNamedEntity.class)) {
                String s = HftsHelper.getEntityName(entity.getUri()).toLowerCase();
                String sf = HftsHelper.getSurfaceForm(d.getText(), entity);

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
                int idx;
                String s = HftsHelper.getEntityName(entity.getUri()).toLowerCase();
                String sf = HftsHelper.getSurfaceForm(d.getText(), entity);

                /* check if we have a known entity in dict and dataset */
                if (knownEntities.containsKey(s) && ((idx = connectorEntity.contains(s)) != -1)) {
                    diversityEntities += (double) knownEntities.get(s).size() / Double.valueOf(connectorEntity.get(idx));
                }

                /* check if we have a known surface form in dict and dataset */
                if (knownSurfaceForms.containsKey(sf) && ((idx = connectorSf.contains(sf)) != -1)) {
                    diversitySurfaceForms += (double) knownSurfaceForms.get(sf).size() / Double.valueOf(connectorSf.get(idx));
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
        connectorSf.flush();
        connectorEntity.flush();
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
}
