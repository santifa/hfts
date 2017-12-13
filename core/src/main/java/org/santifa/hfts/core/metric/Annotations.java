package org.santifa.hfts.core.metric;

import org.santifa.hfts.core.HftsDataset;
import org.santifa.hfts.core.nif.MetaDocument;
import org.santifa.hfts.core.nif.MetaNamedEntity;

public class Annotations implements Metric {

    @Override
    public HftsDataset calculate(HftsDataset dataset) {
        for (MetaDocument d : dataset.getDocuments()) {
            //System.out.println(d.getDocumentURI() + "\t" + d.getMarkings().size());
            for (MetaNamedEntity m : d.getMarkings(MetaNamedEntity.class)) {
                if (m.getTypes().isEmpty()) {
                    System.out.println(m.getUri());
                }
            }
        }
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
