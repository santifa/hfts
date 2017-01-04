package org.santifa.htfs.core.metric;

import org.aksw.gerbil.transfer.nif.Document;
import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;
import org.santifa.htfs.core.NifDataset;

/**
 * Created by ratzeputz on 30.12.16.
 */
public class Density implements Metric {

    @Override
    public NifDataset calculate(NifDataset dataset) {
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

        dataset.setMacroDensity((double) dataset.getMarkings().size() / (double) words);
        Logger.debug("Macro Density for {} is {}", dataset.getName(), dataset.getMacroDensity());
        return dataset;
    }
}
