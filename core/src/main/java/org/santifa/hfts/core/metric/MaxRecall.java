package org.santifa.hfts.core.metric;

import org.pmw.tinylog.Logger;
import org.santifa.hfts.core.NifDataset;
import org.santifa.hfts.core.nif.ExtendedNif;
import org.santifa.hfts.core.nif.MetaDocument;
import org.santifa.hfts.core.nif.MetaNamedEntity;
import org.santifa.hfts.core.utils.DictionaryConnector;
import org.santifa.hfts.core.utils.NifHelper;

/**
 * Created by ratzeputz on 16.01.17.
 */
public class MaxRecall implements Metric {

    private DictionaryConnector connector;

    public MaxRecall(DictionaryConnector connector) {
        this.connector = connector;
    }

    public static MaxRecall getDefaultMaxRecall(int timeToLive) {
            return new MaxRecall(DictionaryConnector.getDefaultSFConnector(timeToLive));
    }

    @Override
    public NifDataset calculate(NifDataset dataset) {
        calculateDocumentLevel(dataset);
        calculateMacro(dataset);
        calculateMicro(dataset);

        connector.flush();
        return dataset;
    }


    private NifDataset calculateDocumentLevel(NifDataset dataset) {
        for (MetaDocument d : dataset.getDocuments()) {
            int inDict = 0;

            for (MetaNamedEntity entity : d.getMarkings(MetaNamedEntity.class)) {
                String sf = NifHelper.getSurfaceForm(d.getText(), entity);

            //    try {
                    /* only approximate and not check every sf to entity relation */
                    if (connector.contains(sf)) {
                        inDict++;
                    }
           /*     } catch (IOException e) {
                    Logger.error("Failed to load connector");
                }*/
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
                String sf = NifHelper.getSurfaceForm(d.getText(), entity);

             //   try {
                    /* only approximate and not check every sf to entity relation */
                    if (connector.contains(sf)) {
                        inDict++;
                    }
           /*     } catch (IOException e) {
                    Logger.error("Failed to load connector");
                }*/
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
