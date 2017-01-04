package org.santifa.htfs.core.metric;

import org.aksw.gerbil.transfer.nif.Document;
import org.pmw.tinylog.Logger;
import org.santifa.htfs.core.NifDataset;

/**
 * Created by ratzeputz on 30.12.16.
 */
public class NotAnnotated implements Metric {

    @Override
    public NifDataset calculate(NifDataset dataset) {
        int emptydocs = 0;
        for (Document d : dataset.getDocuments()) {
            if (d.getMarkings().isEmpty()) {
                emptydocs++;
            }
        }

        /* determine the ratio between empty documents and all documents */
        dataset.setNotAnnotatedDocs((double) emptydocs / (double) dataset.getDocuments().size());
        Logger.debug("Macro not-annotated for {} is {}", dataset.getName(), dataset.getNotAnnotatedDocs());
        return dataset;
    }
}
