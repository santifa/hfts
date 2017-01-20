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

/**
 * Created by ratzeputz on 30.12.16.
 */
public class Ambiguity implements Metric {


    private DictionaryConnector connectorEntity;

    private DictionaryConnector connectorSf;

    private boolean flush = false;

    public Ambiguity(DictionaryConnector connectorEntity, DictionaryConnector connectorSf, boolean flush) {
        this.connectorSf = connectorSf;
        this.connectorEntity = connectorEntity;
        this.flush = flush;
    }

    public Ambiguity(Path entityFile, Path surfaceFormFile, boolean flush) throws IOException {
        this.connectorEntity = new DictionaryConnector(entityFile);
        this.connectorSf = new DictionaryConnector(surfaceFormFile);
        this.flush = flush;
    }

    public static Ambiguity getDefaultAmbiguity() {
        try {
            return new Ambiguity(Paths.get("..", "data", "ambiguity_e"), Paths.get("..", "data", "ambiguity_sf"), true);
        } catch (IOException e) {
            Logger.error("Failed to load internal entity file {} and surface form file {} with {}",
                    "../data/ambiguity_e", "../data/ambiguity_sf", e);
        }
        return null;
    }

    @Override
    public NifDataset calculate(NifDataset dataset) {
        dataset = calculateDocumentLevel(dataset);
        dataset = calculateMicro(dataset);
        dataset = calculateMacro(dataset);
        if (flush) {
            connectorEntity.flush();
            connectorSf.flush();
        }
       return dataset;
    }

    private NifDataset calculateDocumentLevel(NifDataset dataset) {
        for (MetaDocument d : dataset.getDocuments()) {
            int entities = 0;
            int surfaceForms = 0;

            for (MetaNamedEntity entity : d.getMarkings(MetaNamedEntity.class)) {
                String e = getEntityName(entity.getUri());
                String sf = StringUtils.substring(d.getText(), entity.getStartPosition(),
                        entity.getStartPosition() + entity.getLength()).toLowerCase();
                sf = StringUtils.replace(sf, "_", " ").toLowerCase();

                try {
                    if (connectorEntity.getMapping().containsKey(e)) {
                        entity.getMetaInformations().put(ExtendedNif.ambiguityEntity, String.valueOf(connectorEntity.getMapping().get(e)));
                        entities += connectorEntity.getMapping().get(e);
                    } else {
                        /* set to at least one if we have no information */
                        entity.getMetaInformations().put(ExtendedNif.ambiguityEntity, "1");
                        entities++;
                    }

                    if (connectorSf.getMapping().containsKey(sf)) {
                        entity.getMetaInformations().put(ExtendedNif.ambiguitySurfaceForm, String.valueOf(connectorSf.getMapping().get(sf)));
                        surfaceForms += connectorSf.getMapping().get(sf);
                    } else {
                        entity.getMetaInformations().put(ExtendedNif.ambiguitySurfaceForm, "1");
                        surfaceForms++;
                    }
                } catch (IOException io) {
                    Logger.error("Failed to load connector. ", e);
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


        double resultMicroEntities = 0.0;
        double resultMicroSurfaceForms = 0.0;
        if (!dataset.getMarkings().isEmpty()) {
            resultMicroEntities = entities / (double) dataset.getMarkings().size();
            resultMicroSurfaceForms = surfaceForms / (double) dataset.getMarkings().size();
        }

        dataset.getMetaInformations().put(ExtendedNif.microAmbiguityEntities, String.valueOf(resultMicroEntities));
        dataset.getMetaInformations().put(ExtendedNif.microAmbiguitySurfaceForms, String.valueOf(resultMicroSurfaceForms));

        Logger.debug("Micro ambiguity of entities for {} is {}", dataset.getName(), resultMicroEntities);
        Logger.debug("Micro ambiguity of surface forms for {} is {}", dataset.getName(), resultMicroSurfaceForms);
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

    private String getEntityName(String s) {
        if (StringUtils.contains(s, "sentence-")) {
            return StringUtils.substringAfterLast(s, "sentence-").toLowerCase();
        } else {
            return StringUtils.substringAfterLast(s, "/").toLowerCase();
        }
    }
}
