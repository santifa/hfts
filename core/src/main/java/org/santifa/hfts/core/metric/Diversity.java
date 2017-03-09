package org.santifa.hfts.core.metric;

import org.pmw.tinylog.Logger;
import org.santifa.hfts.core.HftsDataset;
import org.santifa.hfts.core.nif.HftsOnt;
import org.santifa.hfts.core.nif.MetaDocument;
import org.santifa.hfts.core.nif.MetaNamedEntity;
import org.santifa.hfts.core.utils.AmbiguityDictionary;
import org.santifa.hfts.core.utils.Dictionary;
import org.santifa.hfts.core.utils.HftsHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class Diversity implements Metric {

    private Dictionary<Integer> connectorEntity;

    private Dictionary<Integer> connectorSf;

    public Diversity(Dictionary<Integer> connectorEntity, Dictionary<Integer> connectorSf) {
        this.connectorEntity = connectorEntity;
        this.connectorSf = connectorSf;
    }

    public static Diversity getDefaultDiversity() {
            return new Diversity(AmbiguityDictionary.getDefaultEntityConnector(),
                    AmbiguityDictionary.getDefaultSFConnector());
    }

    @Override
    public HftsDataset calculate(HftsDataset dataset) {
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
                    diversityEntities += (double) knownEntities.get(s).size() / (double) connectorEntity.get(idx);
                }

                /* check if we have a known surface form in dict and dataset */
                if (knownSurfaceForms.containsKey(sf) && ((idx = connectorSf.contains(sf)) != -1)) {
                    diversitySurfaceForms += (double) knownSurfaceForms.get(sf).size() / (double) connectorSf.get(idx);
                }
            }
        }

        double resultDiversityEntities = 0.0;
        double resultDiversitySurfaceForms = 0.0;
        if (!dataset.getMarkings().isEmpty()) {
            resultDiversityEntities = diversityEntities / (double) dataset.getMarkings().size();
            resultDiversitySurfaceForms = diversitySurfaceForms / (double) dataset.getMarkings().size();
        }

        dataset.getMetaInformations().put(HftsOnt.diversityEntities, String.valueOf(resultDiversityEntities));
        dataset.getMetaInformations().put(HftsOnt.diversitySurfaceForms, String.valueOf(resultDiversitySurfaceForms));
        Logger.debug("Diversity of entities for {} is {}", dataset.getName(), dataset.getMetaInformations().get(HftsOnt.diversityEntities));
        Logger.debug("Diversity of surface forms for {} is {}", dataset.getName(), dataset.getMetaInformations().get(HftsOnt.diversitySurfaceForms));

        /* flush if requested */
        return dataset;
    }

    @Override
    public HftsDataset calculateMicro(HftsDataset dataset) {
        return calculate(dataset);
    }

    @Override
    public HftsDataset calculateMacro(HftsDataset dataset) {
        return calculate(dataset);
    }
}
