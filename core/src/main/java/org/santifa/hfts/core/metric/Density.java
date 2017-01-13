package org.santifa.hfts.core.metric;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.vocabulary.NIF;
import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;
import org.santifa.hfts.core.NifDataset;

/**
 * Created by ratzeputz on 30.12.16.
 */
public class Density implements Metric {

    public static final Property microDensity = ResourceFactory.createProperty(NIF.getURI(), "microDensity");

    public static final Property macroDensity = ResourceFactory.createProperty(NIF.getURI(), "macroDensity");

    @Override
    public NifDataset calculate(NifDataset dataset) {
        dataset = calculateMacro(dataset);
        dataset = calculateMicro(dataset);
        return dataset;
    }

    @Override
    public NifDataset calculateMicro(NifDataset dataset) {
        double density = 0;

        for (Document d : dataset.getDocuments()) {
            int words = 0;
            String[] split = StringUtils.split(d.getText(), " ");

            for (String s : split) {
                s = StringUtils.trim(s);
                if (!s.isEmpty()) {
                    words++;
                }
            }
            if (words != 0) {
                density += (double) d.getMarkings().size() / (double) words;
            }
        }

        double result = density / dataset.getDocuments().size();
        dataset.getMetaInformations().put(microDensity, String.valueOf(result));
        Logger.debug("Micro Density for {} is {}", dataset.getName(), result);
        return dataset;
    }

    @Override
    public NifDataset calculateMacro(NifDataset dataset) {
        int words = 0;

        for (Document d : dataset.getDocuments()) {
            String[] split = StringUtils.split(d.getText(), " ");

            for (String s : split) {
                s = StringUtils.trim(s);
                if (!s.isEmpty()) {
                    words++;
                }
            }
        }

        double result = 0.0;
        if (words != 0) {
            result = (double) dataset.getMarkings().size() / (double) words;
        }

        dataset.getMetaInformations().put(macroDensity, String.valueOf(result));
        Logger.debug("Macro Density for {} is {}", dataset.getName(), result);
        return dataset;
    }
}
