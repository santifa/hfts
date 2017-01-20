package org.santifa.hfts.core.metric;

import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;
import org.santifa.hfts.core.NifDataset;
import org.santifa.hfts.core.nif.ExtendedNif;
import org.santifa.hfts.core.nif.MetaDocument;
import org.santifa.hfts.core.nif.MetaNamedEntity;
import org.santifa.hfts.core.utils.DictionaryConnector;
import org.santifa.hfts.core.utils.NifHelper;

/**
 * Created by ratzeputz on 30.12.16.
 */
public class Ambiguity implements Metric {


    private DictionaryConnector connectorEntity;

    private DictionaryConnector connectorSf;

    public Ambiguity(DictionaryConnector connectorEntity, DictionaryConnector connectorSf) {
        this.connectorSf = connectorSf;
        this.connectorEntity = connectorEntity;
    }

    public static Ambiguity getDefaultAmbiguity(int timeToLive) {
            return new Ambiguity(DictionaryConnector.getDefaultEntityConnector(timeToLive),
                    DictionaryConnector.getDefaultSFConnector(timeToLive));
    }

    @Override
    public NifDataset calculate(NifDataset dataset) {
        dataset = calculateDocumentLevel(dataset);
        dataset = calculateMicro(dataset);
        dataset = calculateMacro(dataset);
        connectorEntity.flush();
        connectorSf.flush();

       return dataset;
    }

    private NifDataset calculateDocumentLevel(NifDataset dataset) {

        for (MetaDocument d : dataset.getDocuments()) {
            int entities = 0;
            int surfaceForms = 0;

            for (MetaNamedEntity entity : d.getMarkings(MetaNamedEntity.class)) {
                int idx;
                String e = NifHelper.getEntityName(entity.getUri());
                String sf = NifHelper.getSurfaceForm(d.getText(), entity);
                sf = StringUtils.replace(sf, "_", " ").toLowerCase();

                if ((idx = connectorEntity.contains(e)) != -1) {
                    entity.getMetaInformations().put(ExtendedNif.ambiguityEntity, String.valueOf(connectorEntity.get(idx)));
                    entities += Integer.decode(connectorEntity.get(idx));
                } else {
                    /* set to at least one if we have no information */
                    entity.getMetaInformations().put(ExtendedNif.ambiguityEntity, "1");
                    entities++;
                }

                if ((idx = connectorSf.contains(sf)) != -1) {
                    entity.getMetaInformations().put(ExtendedNif.ambiguitySurfaceForm, String.valueOf(connectorSf.get(idx)));
                    surfaceForms += Integer.decode(connectorSf.get(idx));
                } else {
                    entity.getMetaInformations().put(ExtendedNif.ambiguitySurfaceForm, "1");
                    surfaceForms++;
                }
          }

            double resultEntities = 0.0;
            double resultSurfaceForms = 0.0;
            if (!d.getMarkings().isEmpty()) {
                resultEntities = (double) entities / (double) d.getMarkings().size();
                resultSurfaceForms = (double) surfaceForms / (double) d.getMarkings().size();
            }

            d.getMetaInformations().put(ExtendedNif.ambiguityEntities, String.valueOf(resultEntities));
            d.getMetaInformations().put(ExtendedNif.ambiguitySurfaceForms, String.valueOf(resultSurfaceForms));
            Logger.debug("Document ambiguity of entities for {} is {}", d.getDocumentURI(), resultEntities);
            Logger.debug("Document ambiguity of surface forms for {} is {}", d.getDocumentURI(), resultSurfaceForms);
        }

        return dataset;
    }

    @Override
    public NifDataset calculateMicro(NifDataset dataset) {
        double entities = 0.0;
        double surfaceForms = 0.0;

        /* add every annotation ambiguity and increase number of stored ambiguities */
        for (MetaNamedEntity meaning : dataset.getMarkings()) {
            entities += Double.valueOf(meaning.getMetaInformations().get(ExtendedNif.ambiguityEntity));
            surfaceForms += Double.valueOf(meaning.getMetaInformations().get(ExtendedNif.ambiguitySurfaceForm));
        }


        if (!dataset.getMarkings().isEmpty()) {
            entities = entities / (double) dataset.getMarkings().size();
            surfaceForms = surfaceForms / (double) dataset.getMarkings().size();
        } else {
            entities = 0.0;
            surfaceForms = 0.0;
        }

        dataset.getMetaInformations().put(ExtendedNif.microAmbiguityEntities, String.valueOf(entities));
        dataset.getMetaInformations().put(ExtendedNif.microAmbiguitySurfaceForms, String.valueOf(surfaceForms));

        Logger.debug("Micro ambiguity of entities for {} is {}", dataset.getName(), entities);
        Logger.debug("Micro ambiguity of surface forms for {} is {}", dataset.getName(), surfaceForms);
        return dataset;
    }

    @Override
    public NifDataset calculateMacro(NifDataset dataset) {
        double ambiguityEntities = 0.0;
        double ambiguitySurfaceForms = 0.0;

        for (MetaDocument d : dataset.getDocuments()) {
            ambiguityEntities += Double.valueOf(d.getMetaInformations().get(ExtendedNif.ambiguityEntities));
            ambiguitySurfaceForms += Double.valueOf(d.getMetaInformations().get(ExtendedNif.ambiguitySurfaceForms));
        }

        double resultEntities = 0.0;
        double resultSurfaceForms = 0.0;
        if (!dataset.getMarkings().isEmpty()) {
            resultEntities = ambiguityEntities / (double) dataset.getDocuments().size();
            resultSurfaceForms = ambiguitySurfaceForms / (double) dataset.getDocuments().size();
        }

        dataset.getMetaInformations().put(ExtendedNif.macroAmbiguityEntities, String.valueOf(resultEntities));
        dataset.getMetaInformations().put(ExtendedNif.macroAmbiguitySurfaceForms, String.valueOf(resultSurfaceForms));
        Logger.debug("Macro ambiguity of entities for {} is {}", dataset.getName(), resultEntities);
        Logger.debug("Macro ambiguity of surface forms for {} is {}", dataset.getName(), resultSurfaceForms);
        return dataset;
    }
}
