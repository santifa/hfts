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
import java.util.List;

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
        dataset = calculateMicro(dataset);
        dataset = calculateMacro(dataset);
        if (flush) {
            connector.flush();
        }
       return dataset;
    }

    @Override
    public NifDataset calculateMicro(NifDataset dataset) {
        double entities = 0.0;
        double surfaceForms = 0.0;

        for (Document d : dataset.getDocuments()) {
            int ambiguityEntities = 0;
            int ambiguitySf = 0;

            /* add every annotation ambiguity and increase number of stored ambiguities */
            for (MetaNamedEntity meaning : dataset.getMarkings()) {
                String s = getEntityName(meaning.getUri());
                String sf = StringUtils.substring(d.getText(), meaning.getStartPosition(),
                        meaning.getStartPosition() + meaning.getLength()).toLowerCase();
                sf = StringUtils.replace(sf, "_", " ");
                try {

                    if (connector.getEntityMappping().containsKey(s)) {
                        ambiguityEntities += connector.getEntityMappping().get(s);

                    }


                    if (connector.getSfMapping().containsKey(sf)) {
                        ambiguitySf += connector.getSfMapping().get(sf);
                    }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
            if (dataset.getMarkings().size() != 0) {
                entities += (double) ambiguityEntities / (double) dataset.getMarkings().size();
                surfaceForms += (double) ambiguitySf / (double) dataset.getMarkings().size();
            }
        }

        double resultMicroEntities = 0.0;
        if (!dataset.getDocuments().isEmpty()) {
            resultMicroEntities = entities / (double) dataset.getDocuments().size();
        }
        dataset.getMetaInformations().put(ExtendedNif.microAmbiguityE, String.valueOf(resultMicroEntities));

        double resultMicroSurfaceForms = 0.0;
        if (!dataset.getDocuments().isEmpty()) {
            resultMicroSurfaceForms = surfaceForms / (double) dataset.getDocuments().size();
        }
        dataset.getMetaInformations().put(ExtendedNif.microAmbiguitySF, String.valueOf(resultMicroSurfaceForms));

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
            List<MetaNamedEntity> meanings = d.getMarkings(MetaNamedEntity.class);

            /* add every annotation ambiguity and increase number of stored ambiguities */
            for (MetaNamedEntity meaning : meanings) {
                String s = getEntityName(meaning.getUri());
                String sf = StringUtils.substring(d.getText(), meaning.getStartPosition(),
                        meaning.getStartPosition() + meaning.getLength()).toLowerCase();
                sf = StringUtils.replace(sf, "_", " ");

                try {
                    if (connector.getEntityMappping().containsKey(s)) {
                        ambiguityEntities += connector.getEntityMappping().get(s);
                    }


                    if (connector.getSfMapping().containsKey(sf)) {
                        ambiguitySf += connector.getSfMapping().get(sf);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            counter += meanings.size();
        }

        double resultMacroEntities = 0.0;
        if (counter != 0.0) {
            resultMacroEntities = (double) ambiguityEntities / (double) counter;
        }
        dataset.getMetaInformations().put(ExtendedNif.macroAmbiguityE, String.valueOf(resultMacroEntities));

        double resultMacroSurfaceForms = 0.0;
        if (counter != 0.0) {
            resultMacroSurfaceForms = (double) ambiguitySf / (double) counter;
        }
        dataset.getMetaInformations().put(ExtendedNif.macroAmbiguitySF, String.valueOf(resultMacroSurfaceForms));

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
