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


    private DictionaryConnector connector;

    private boolean flush = false;

    public Ambiguity(DictionaryConnector connector, boolean flush) {
        this.connector = connector;
        this.flush = flush;
    }

    public Ambiguity(Path entityFile, Path surfaceFormFile, boolean flush) throws IOException {
        this.connector = new DictionaryConnector(entityFile, surfaceFormFile);
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
            connector.flush();
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
                    if (connector.getEntityMappping().containsKey(e)) {
                        entity.getMetaInformations().put(ExtendedNif.ambiguityEntity, String.valueOf(connector.getEntityMappping().get(e)));
                        entities += connector.getEntityMappping().get(e);

                    }

                    if (connector.getSfMapping().containsKey(sf)) {
                        entity.getMetaInformations().put(ExtendedNif.ambiguitySurfaceForm, String.valueOf(connector.getSfMapping().get(sf)));
                        surfaceForms += connector.getSfMapping().get(sf);
                    }
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }

            double resultEntities = 0.0;
            double resultSurfaceForms = 0.0;
            if (dataset.getMarkings().size() != 0) {
                resultEntities = (double) entities / (double) dataset.getMarkings().size();
                resultSurfaceForms = (double) surfaceForms / (double) dataset.getMarkings().size();
            }

            d.getMetaInformations().put(ExtendedNif.ambiguityEntities, String.valueOf(resultEntities));
            d.getMetaInformations().put(ExtendedNif.ambiguitySurfaceForms, String.valueOf(resultSurfaceForms));
            Logger.debug("Macro ambiguity of entities for {} is {}", d.getDocumentURI(), resultEntities);
            Logger.debug("Macro ambiguity of surface forms for {} is {}", d.getDocumentURI(), resultSurfaceForms);
        }
        return dataset;
    }

    @Override
    public NifDataset calculateMicro(NifDataset dataset) {
        int entities = 0;
        int surfaceForms = 0;

            /* add every annotation ambiguity and increase number of stored ambiguities */
        System.out.println(dataset.getMarkings().size());
        for (MetaNamedEntity meaning : dataset.getMarkings()) {
            entities += Integer.valueOf(meaning.getMetaInformations().get(ExtendedNif.ambiguityEntity));
            surfaceForms += Integer.valueOf(meaning.getMetaInformations().get(ExtendedNif.ambiguitySurfaceForm));
        }


        double resultMicroEntities = 0.0;
        double resultMicroSurfaceForms = 0.0;
        if (!dataset.getMarkings().isEmpty()) {
            resultMicroEntities = (double) entities / (double) dataset.getMarkings().size();
            resultMicroSurfaceForms = (double) surfaceForms / (double) dataset.getMarkings().size();
        }

        dataset.getMetaInformations().put(ExtendedNif.microAmbiguityEntities, String.valueOf(resultMicroEntities));
        dataset.getMetaInformations().put(ExtendedNif.microAmbiguitySurfaceForms, String.valueOf(resultMicroSurfaceForms));

        Logger.debug("Macro ambiguity of entities for {} is {}", dataset.getName(), resultMicroEntities);
        Logger.debug("Macro ambiguity of surface forms for {} is {}", dataset.getName(), resultMicroSurfaceForms);
        return dataset;
    }

    @Override
    public NifDataset calculateMacro(NifDataset dataset) {
        double ambiguityEntities = 0;
        double ambiguitySurfaceForms = 0;

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
