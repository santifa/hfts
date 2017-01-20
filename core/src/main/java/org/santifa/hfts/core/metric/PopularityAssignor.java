package org.santifa.hfts.core.metric;

import com.hp.hpl.jena.rdf.model.Property;
import org.pmw.tinylog.Logger;
import org.santifa.hfts.core.NifDataset;
import org.santifa.hfts.core.nif.ExtendedNif;
import org.santifa.hfts.core.nif.MetaNamedEntity;
import org.santifa.hfts.core.utils.DictionaryConnector;
import org.santifa.hfts.core.utils.PopularityConnector;

import java.util.List;

/**
 * Created by ratzeputz on 09.01.17.
 */
public class PopularityAssignor implements Metric {

    private Property property;

    private DictionaryConnector connector;

    public PopularityAssignor(DictionaryConnector connector, Property property) {
        this.connector = connector;
        this.property = property;
    }

    @Override
    public NifDataset calculate(NifDataset dataset) {
        List<MetaNamedEntity> meanings = dataset.getMarkings();
        Logger.debug("Assign popularity {} to dataset {}", property, dataset.getName());

        for (MetaNamedEntity m : meanings) {
                int idx;
                if ((idx = connector.contains(m.getUri())) != -1) {
                    m.getMetaInformations().put(property, connector.get(idx));
                }
            }

        connector.flush();
        return dataset;
    }

    @Override
    public NifDataset calculateMicro(NifDataset dataset) {
        return calculate(dataset);
    }

    @Override
    public NifDataset calculateMacro(NifDataset dataset) {
        return calculate(dataset);
    }


    public static PopularityAssignor getDefaultPageRank(int timeToLive) {
        return new PopularityAssignor(PopularityConnector.getPageRankConnector(timeToLive), ExtendedNif.pagerank);
    }

    public static PopularityAssignor getDefaultHits(int timeToLive) {
        return new PopularityAssignor(PopularityConnector.getHitsConnector(timeToLive), ExtendedNif.hits);
    }
}
