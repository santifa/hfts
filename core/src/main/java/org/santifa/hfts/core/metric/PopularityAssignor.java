package org.santifa.hfts.core.metric;

import com.hp.hpl.jena.rdf.model.Property;
import org.pmw.tinylog.Logger;
import org.santifa.hfts.core.NifDataset;
import org.santifa.hfts.core.nif.ExtendedNif;
import org.santifa.hfts.core.nif.MetaNamedEntity;
import org.santifa.hfts.core.utils.PopularityConnector;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by ratzeputz on 09.01.17.
 */
public class PopularityAssignor implements Metric {

    private Path file;

    private Property property;

    private PopularityConnector connector;

    public PopularityAssignor(Path file, Property property) {
        this.file = file;
        this.connector = new PopularityConnector(file);
        this.property = property;
    }

    public static PopularityAssignor getPageRankAssignor() {
        return new PopularityAssignor(Paths.get("../data/pagerank_scores_en_2015.ttl"), ExtendedNif.pagerank);
    }

    public static PopularityAssignor getHitsAssignor() {
        return new PopularityAssignor(Paths.get("../data/hits_scores_en_2015.ttl"), ExtendedNif.hits);
    }

    @Override
    public NifDataset calculate(NifDataset dataset) {
        List<MetaNamedEntity> meanings = dataset.getMarkings();
        Logger.debug("Assign popularity {} to dataset {}", property, dataset.getName());

        try {
            for (MetaNamedEntity m : meanings) {
                if (connector.getMappping().containsKey(m.getUri())) {
                    m.getMetaInformations().put(property, connector.getMappping().get((m.getUri())));
                }
            }
        } catch (IOException e) {
            Logger.error("Popularity file {} not loaded.", file);
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
}
