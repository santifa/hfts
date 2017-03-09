package org.santifa.hfts.core.metric;

import com.hp.hpl.jena.rdf.model.Property;
import org.pmw.tinylog.Logger;
import org.santifa.hfts.core.HftsDataset;
import org.santifa.hfts.core.nif.HftsOnt;
import org.santifa.hfts.core.nif.MetaNamedEntity;
import org.santifa.hfts.core.utils.Dictionary;
import org.santifa.hfts.core.utils.PopularityDictionary;

import java.util.List;

/**
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class PopularityAssignor implements Metric {

    private Property property;

    private Dictionary<Double> connector;

    public PopularityAssignor(Dictionary<Double> connector, Property property) {
        this.connector = connector;
        this.property = property;
    }

    @Override
    public HftsDataset calculate(HftsDataset dataset) {
        List<MetaNamedEntity> meanings = dataset.getMarkings();
        Logger.debug("Assign popularity {} to dataset {}", property, dataset.getName());

        for (MetaNamedEntity m : meanings) {
                int idx;
                if ((idx = connector.contains(m.getUri().toLowerCase())) != -1) {
                    m.getMetaInformations().put(property, String.valueOf(connector.get(idx)));
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


    public static PopularityAssignor getDefaultPageRank() {
        return new PopularityAssignor(PopularityDictionary.getPageRankConnector(), HftsOnt.pagerank);
    }

    public static PopularityAssignor getDefaultHits() {
        return new PopularityAssignor(PopularityDictionary.getHitsConnector(), HftsOnt.hits);
    }
}
