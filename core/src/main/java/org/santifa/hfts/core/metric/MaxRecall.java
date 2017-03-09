package org.santifa.hfts.core.metric;

import org.pmw.tinylog.Logger;
import org.santifa.hfts.core.HftsDataset;
import org.santifa.hfts.core.nif.HftsOnt;
import org.santifa.hfts.core.nif.MetaDocument;
import org.santifa.hfts.core.nif.MetaNamedEntity;
import org.santifa.hfts.core.utils.Dictionary;
import org.santifa.hfts.core.utils.HftsHelper;

/**
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class MaxRecall implements Metric {

    private Dictionary<Integer> connector;

    public MaxRecall(Dictionary<Integer> connector) {
        this.connector = connector;
    }

    @Override
    public HftsDataset calculate(HftsDataset dataset) {
        calculateDocumentLevel(dataset);
        calculateMacro(dataset);
        calculateMicro(dataset);
        return dataset;
    }


    private HftsDataset calculateDocumentLevel(HftsDataset dataset) {
        for (MetaDocument d : dataset.getDocuments()) {
            int inDict = 0;

            for (MetaNamedEntity entity : d.getMarkings(MetaNamedEntity.class)) {
                String sf = HftsHelper.getSurfaceForm(d.getText(), entity);

                /* only approximate and not check every sf to entity relation */
                if (connector.contains(sf) != -1) {
                    inDict++;
                }
            }

            double maxRecall = 0.0;
            if (!d.getMarkings().isEmpty()) {
                maxRecall = (double) inDict / (double) d.getMarkings().size();
            }
            d.getMetaInformations().put(HftsOnt.maxRecall, String.valueOf(maxRecall));
            Logger.debug("Max recall for document {} is {}", d.getDocumentURI(), maxRecall);
        }
        return dataset;
    }

    @Override
    public HftsDataset calculateMicro(HftsDataset dataset) {
        int inDict = 0;

        for (MetaDocument d : dataset.getDocuments()) {
            for (MetaNamedEntity entity : d.getMarkings(MetaNamedEntity.class)) {
                String sf = HftsHelper.getSurfaceForm(d.getText(), entity);

                /* only approximate and not check every sf to entity relation */
                if (connector.contains(sf) != -1) {
                    inDict++;
                }
            }
        }

        double maxRecall = 0.0;
        if (!dataset.getMarkings().isEmpty()) {
            maxRecall = (double) inDict / (double) dataset.getMarkings().size();
        }
        dataset.getMetaInformations().put(HftsOnt.microMaxRecall, String.valueOf(maxRecall));
        Logger.debug("Micro max recall for dataset {} is {}", dataset.getName(), maxRecall);
        return dataset;
    }

    @Override
    public HftsDataset calculateMacro(HftsDataset dataset) {
        double maxRecall = 0.0;

        for (MetaDocument d : dataset.getDocuments()) {
            maxRecall += Double.valueOf(d.getMetaInformations().get(HftsOnt.maxRecall));
        }

        if (!dataset.getDocuments().isEmpty()) {
            maxRecall = maxRecall / (double) dataset.getDocuments().size();
        }
        dataset.getMetaInformations().put(HftsOnt.macroMaxRecall, String.valueOf(maxRecall));
        Logger.debug("Macro max recall for dataset {} is {}", dataset.getName(), maxRecall);
        return dataset;
    }
}
