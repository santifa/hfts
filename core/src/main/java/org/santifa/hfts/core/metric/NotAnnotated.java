package org.santifa.hfts.core.metric;

import org.aksw.gerbil.transfer.nif.Document;
import org.pmw.tinylog.Logger;
import org.santifa.hfts.core.HftsDataset;
import org.santifa.hfts.core.nif.HftsOnt;

/**
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class NotAnnotated implements Metric {

    @Override
    public HftsDataset calculate(HftsDataset dataset) {
        int emptydocs = 0;
        for (Document d : dataset.getDocuments()) {
            if (d.getMarkings().isEmpty()) {
                emptydocs++;
            }
        }

        /* determine the ratio between empty documents and all documents */
        double result = (double) emptydocs / (double) dataset.getDocuments().size();
        dataset.getMetaInformations().put(HftsOnt.notAnnotatedProperty, String.valueOf(result));
        Logger.debug("Not-annotated for {} is {}", dataset.getName(), result);
        return dataset;
    }

    @Override
    public HftsDataset calculateMicro(HftsDataset dataset) {
        return calculate(dataset);
    }

    @Override
    public HftsDataset calculateMacro(HftsDataset dataset) {
        return calculate(dataset);
    }
}
