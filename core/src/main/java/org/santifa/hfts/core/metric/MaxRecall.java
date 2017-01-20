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
 * Created by ratzeputz on 16.01.17.
 */
public class MaxRecall implements Metric {

    private DictionaryConnector connector;

    private boolean flush = false;

    public MaxRecall(DictionaryConnector connector, boolean flush) {
        this.connector = connector;
        this.flush = flush;
    }

    public MaxRecall(Path file, boolean flush) throws IOException {
        this.connector = new DictionaryConnector(file);
        this.flush = flush;
    }

    public static MaxRecall getDefaultMaxRecall() {
        try {
            return new MaxRecall(Paths.get("..", "data", "ambiguity_sf"), true);
        } catch (IOException e) {
            Logger.error("Failed to load internal surface form file {} with {}", "../data/ambiguity_sf", e);
        }
        return null;
    }

    @Override
    public NifDataset calculate(NifDataset dataset) {
        calculateDocumentLevel(dataset);
        calculateMacro(dataset);
        calculateMicro(dataset);

        if (flush) {
            connector.flush();
        }
        return dataset;
    }


    private NifDataset calculateDocumentLevel(NifDataset dataset) {
        for (MetaDocument d : dataset.getDocuments()) {
            int inDict = 0;

            for (MetaNamedEntity entity : d.getMarkings(MetaNamedEntity.class)) {
                String sf = StringUtils.substring(d.getText(), entity.getStartPosition(),
                        entity.getStartPosition() + entity.getLength()).toLowerCase();

                try {
                    /* only approximate and not check every sf to entity relation */
                    if (connector.getMapping().containsKey(sf)) {
                        inDict++;
                    }
                } catch (IOException e) {
                    Logger.error("Failed to load connector");
                }
            }

            double maxRecall = 0.0;
            if (!d.getMarkings().isEmpty()) {
                maxRecall = (double) inDict / (double) d.getMarkings().size();
            }
            d.getMetaInformations().put(ExtendedNif.maxRecall, String.valueOf(maxRecall));
            Logger.debug("Max recall for document {} is {}", d.getDocumentURI(), maxRecall);
        }
        return dataset;
    }

    @Override
    public NifDataset calculateMicro(NifDataset dataset) {
        int inDict = 0;

        for (MetaDocument d : dataset.getDocuments()) {
            for (MetaNamedEntity entity : d.getMarkings(MetaNamedEntity.class)) {
                String sf = StringUtils.substring(d.getText(), entity.getStartPosition(),
                        entity.getStartPosition() + entity.getLength()).toLowerCase();

                try {
                    /* only approximate and not check every sf to entity relation */
                    if (connector.getMapping().containsKey(sf)) {
                        inDict++;
                    }
                } catch (IOException e) {
                    Logger.error("Failed to load connector");
                }
            }
        }

        double maxRecall = 0.0;
        if (!dataset.getMarkings().isEmpty()) {
            maxRecall = (double) inDict / (double) dataset.getMarkings().size();
        }
        dataset.getMetaInformations().put(ExtendedNif.microMaxRecall, String.valueOf(maxRecall));
        Logger.debug("Max recall for document {} is {}", dataset.getName(), maxRecall);
        return dataset;
    }

    @Override
    public NifDataset calculateMacro(NifDataset dataset) {
        double maxRecall = 0.0;

        for (MetaDocument d : dataset.getDocuments()) {
            maxRecall += Double.valueOf(d.getMetaInformations().get(ExtendedNif.maxRecall));
        }

        if (!dataset.getDocuments().isEmpty()) {
            maxRecall = maxRecall / (double) dataset.getDocuments().size();
        }
        dataset.getMetaInformations().put(ExtendedNif.macroMaxRecall, String.valueOf(maxRecall));
        Logger.debug("Macro max recall for dataset {} is {}", dataset.getName(), maxRecall);
        return dataset;
    }
}
