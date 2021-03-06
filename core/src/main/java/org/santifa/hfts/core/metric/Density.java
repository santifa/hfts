package org.santifa.hfts.core.metric;

import org.aksw.gerbil.transfer.nif.Document;
import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;
import org.santifa.hfts.core.HftsDataset;
import org.santifa.hfts.core.nif.HftsOnt;
import org.santifa.hfts.core.nif.MetaDocument;

/**
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class Density implements Metric {


    @Override
    public HftsDataset calculate(HftsDataset dataset) {
        dataset = calculateDocumentLevel(dataset);
        dataset = calculateMacro(dataset);
        dataset = calculateMicro(dataset);
        return dataset;
    }

    private HftsDataset calculateDocumentLevel(HftsDataset dataset) {
        for (MetaDocument d : dataset.getDocuments()) {
            String[] split = StringUtils.split(d.getText(), " ");
            int words = 0;

            for (String s : split) {
                if (!s.trim().isEmpty()) {
                    words++;
                }
            }

            double result = 0;
            if (words != 0) {
                result = (double) d.getMarkings().size() / (double) words;
            }
            d.getMetaInformations().put(HftsOnt.density, String.valueOf(result));
            Logger.debug("Density for document {} is {}", d.getDocumentURI(), result);
        }
        return dataset;
    }

    @Override
    public HftsDataset calculateMicro(HftsDataset dataset) {
        int words = 0;

        for (Document d : dataset.getDocuments()) {
            for (String s : StringUtils.split(d.getText(), " ")) {
                if (!s.trim().isEmpty()) {
                    words++;
                }
            }
        }

        double result = (double) dataset.getMarkings().size() / (double) words;
        dataset.getMetaInformations().put(HftsOnt.microDensity, String.valueOf(result));
        Logger.debug("Micro Density for {} is {}", dataset.getName(), result);
        return dataset;
    }

    @Override
    public HftsDataset calculateMacro(HftsDataset dataset) {
        double density = 0;

        /* sum up the individual densities */
        for (MetaDocument d : dataset.getDocuments()) {
            density += Double.valueOf(d.getMetaInformations().get(HftsOnt.density));
        }

        /* calculate the average of all individual densities */
        double result = 0.0;
        if (density != 0) {
            result = density / (double) dataset.getDocuments().size();
        }

        dataset.getMetaInformations().put(HftsOnt.macroDensity, String.valueOf(result));
        Logger.debug("Macro Density for {} is {}", dataset.getName(), result);
        return dataset;
    }
}
