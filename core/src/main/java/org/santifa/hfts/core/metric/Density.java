package org.santifa.hfts.core.metric;

import org.aksw.gerbil.transfer.nif.Document;
import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;
import org.santifa.hfts.core.NifDataset;
import org.santifa.hfts.core.nif.ExtendedNif;
import org.santifa.hfts.core.nif.MetaDocument;

/**
 * Created by ratzeputz on 30.12.16.
 */
public class Density implements Metric {


    @Override
    public NifDataset calculate(NifDataset dataset) {
        dataset = calculateDocumentLevel(dataset);
        dataset = calculateMacro(dataset);
        dataset = calculateMicro(dataset);
        return dataset;
    }

    private NifDataset calculateDocumentLevel(NifDataset dataset) {
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
            d.getMetaInformations().put(ExtendedNif.density, String.valueOf(result));
            Logger.debug("Density for document {} is {}", d.getDocumentURI(), result);
        }
        return dataset;
    }

    @Override
    public NifDataset calculateMicro(NifDataset dataset) {
        int words = 0;

        for (Document d : dataset.getDocuments()) {
            for (String s : StringUtils.split(d.getText(), " ")) {
                if (!s.trim().isEmpty()) {
                    words++;
                }
            }
        }

        double result = (double) dataset.getMarkings().size() / (double) words;
        dataset.getMetaInformations().put(ExtendedNif.microDensity, String.valueOf(result));
        Logger.debug("Micro Density for {} is {}", dataset.getName(), result);
        return dataset;
    }

    @Override
    public NifDataset calculateMacro(NifDataset dataset) {
        double density = 0;

        /* sum up the individual densities */
        for (MetaDocument d : dataset.getDocuments()) {
            density += Double.valueOf(d.getMetaInformations().get(ExtendedNif.density));
        }

        /* calculate the average of all individual densities */
        double result = 0.0;
        if (density != 0) {
            result = density / (double) dataset.getDocuments().size();
        }

        dataset.getMetaInformations().put(ExtendedNif.macroDensity, String.valueOf(result));
        Logger.debug("Macro Density for {} is {}", dataset.getName(), result);
        return dataset;
    }
}
