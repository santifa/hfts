package org.santifa.hfts.core.metric;

import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;
import org.santifa.hfts.core.HftsDataset;
import org.santifa.hfts.core.nif.HftsOnt;
import org.santifa.hfts.core.nif.MetaDocument;
import org.santifa.hfts.core.nif.MetaNamedEntity;
import org.santifa.hfts.core.utils.AmbiguityDictionary;
import org.santifa.hfts.core.utils.Dictionary;
import org.santifa.hfts.core.utils.HftsHelper;

/**
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class Ambiguity implements Metric {


    private Dictionary<Integer> connectorEntity;

    private Dictionary<Integer> connectorSf;

    public Ambiguity(Dictionary<Integer> connectorEntity, Dictionary<Integer> connectorSf) {
        this.connectorSf = connectorSf;
        this.connectorEntity = connectorEntity;
    }

    public static Ambiguity getDefaultAmbiguity() {
            return new Ambiguity(AmbiguityDictionary.getDefaultEntityConnector(),
                    AmbiguityDictionary.getDefaultSFConnector());
    }

    @Override
    public HftsDataset calculate(HftsDataset dataset) {
        dataset = calculateDocumentLevel(dataset);
        dataset = calculateMicro(dataset);
        dataset = calculateMacro(dataset);
        return dataset;
    }

    private HftsDataset calculateDocumentLevel(HftsDataset dataset) {

        for (MetaDocument d : dataset.getDocuments()) {
            int entities = 0;
            int surfaceForms = 0;

            for (MetaNamedEntity entity : d.getMarkings(MetaNamedEntity.class)) {
                int idx;
                String e = HftsHelper.getEntityName(entity.getUri());
                String sf = HftsHelper.getSurfaceForm(d.getText(), entity);
                sf = StringUtils.replace(sf, "_", " ").toLowerCase();

                if ((idx = connectorEntity.contains(e)) != -1) {
                    entity.getMetaInformations().put(HftsOnt.ambiguityEntity, String.valueOf(connectorEntity.get(idx)));
                    entities += connectorEntity.get(idx);
                } else {
                    /* set to at least one if we have no information */
                    entity.getMetaInformations().put(HftsOnt.ambiguityEntity, "1");
                    entities++;
                }

                if ((idx = connectorSf.contains(sf)) != -1) {
                    entity.getMetaInformations().put(HftsOnt.ambiguitySurfaceForm, String.valueOf(connectorSf.get(idx)));
                    surfaceForms += connectorSf.get(idx);
                } else {
                    entity.getMetaInformations().put(HftsOnt.ambiguitySurfaceForm, "1");
                    surfaceForms++;
                }
          }

            double resultEntities = 0.0;
            double resultSurfaceForms = 0.0;
            if (!d.getMarkings().isEmpty()) {
                resultEntities = (double) entities / (double) d.getMarkings().size();
                resultSurfaceForms = (double) surfaceForms / (double) d.getMarkings().size();
            }

            d.getMetaInformations().put(HftsOnt.ambiguityEntities, String.valueOf(resultEntities));
            d.getMetaInformations().put(HftsOnt.ambiguitySurfaceForms, String.valueOf(resultSurfaceForms));
            Logger.debug("Document ambiguity of entities for {} is {}", d.getDocumentURI(), resultEntities);
            Logger.debug("Document ambiguity of surface forms for {} is {}", d.getDocumentURI(), resultSurfaceForms);
        }

        return dataset;
    }

    @Override
    public HftsDataset calculateMicro(HftsDataset dataset) {
        double entities = 0;
        double surfaceForms = 0;

        /* add every annotation ambiguity and increase number of stored ambiguities */
        for (MetaNamedEntity meaning : dataset.getMarkings()) {
            entities += Double.parseDouble(meaning.getMetaInformations().get(HftsOnt.ambiguityEntity));
            surfaceForms += Double.parseDouble(meaning.getMetaInformations().get(HftsOnt.ambiguitySurfaceForm));
        }

        if (!dataset.getMarkings().isEmpty()) {
            entities = entities / (double) dataset.getMarkings().size();
            surfaceForms = surfaceForms / (double) dataset.getMarkings().size();
        } else {
            entities = 0.0;
            surfaceForms = 0.0;
        }

        dataset.getMetaInformations().put(HftsOnt.microAmbiguityEntities, String.valueOf(entities));
        dataset.getMetaInformations().put(HftsOnt.microAmbiguitySurfaceForms, String.valueOf(surfaceForms));

        Logger.debug("Micro ambiguity of entities for {} is {}", dataset.getName(), entities);
        Logger.debug("Micro ambiguity of surface forms for {} is {}", dataset.getName(), surfaceForms);
        return dataset;
    }

    @Override
    public HftsDataset calculateMacro(HftsDataset dataset) {
        double ambiguityEntities = 0.0;
        double ambiguitySurfaceForms = 0.0;

        for (MetaDocument d : dataset.getDocuments()) {
            ambiguityEntities += Double.valueOf(d.getMetaInformations().get(HftsOnt.ambiguityEntities));
            ambiguitySurfaceForms += Double.valueOf(d.getMetaInformations().get(HftsOnt.ambiguitySurfaceForms));
        }

        double resultEntities = 0.0;
        double resultSurfaceForms = 0.0;
        if (!dataset.getMarkings().isEmpty()) {
            resultEntities = ambiguityEntities / (double) dataset.getDocuments().size();
            resultSurfaceForms = ambiguitySurfaceForms / (double) dataset.getDocuments().size();
        }

        dataset.getMetaInformations().put(HftsOnt.macroAmbiguityEntities, String.valueOf(resultEntities));
        dataset.getMetaInformations().put(HftsOnt.macroAmbiguitySurfaceForms, String.valueOf(resultSurfaceForms));
        Logger.debug("Macro ambiguity of entities for {} is {}", dataset.getName(), resultEntities);
        Logger.debug("Macro ambiguity of surface forms for {} is {}", dataset.getName(), resultSurfaceForms);
        return dataset;
    }
}
