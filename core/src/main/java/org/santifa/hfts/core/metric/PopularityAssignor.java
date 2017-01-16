package org.santifa.hfts.core.metric;

import com.hp.hpl.jena.rdf.model.Property;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.pmw.tinylog.Logger;
import org.santifa.hfts.core.NifDataset;
import org.santifa.hfts.core.nif.ExtendedNif;
import org.santifa.hfts.core.nif.MetaNamedEntity;
import org.santifa.hfts.core.utils.Grep;

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

    public PopularityAssignor(Path file, Property property) {
        this.file = file;
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
        List<Marking> meanings = dataset.getMarkings();
        StringBuilder regex = new StringBuilder();

        /* collect all entity uris from dbpedia */
        regex.append('(');
        for (Marking m : meanings) {
            if (m instanceof Meaning) {

                for (String uri : ((Meaning) m).getUris()) {
                    if (uri.startsWith("http://dbpedia.org")) {
                        regex.append(uri).append("|");
                    }
                }
            }
        }
        regex.deleteCharAt(regex.length() - 1).append(')');

        Grep g = new Grep();
        try {
            /* grep for the entity uris */
            List<String> matches = g.grep(file, regex.toString());

            /* go through dataset and found matched uris */
            for (Document d : dataset.changeDocuments()) {
                for (MetaNamedEntity m : d.getMarkings(MetaNamedEntity.class)) {
                    /* check which uris where found */
                    for (String s : matches) {
                        /* get the uri and check the meaning set */
                        String uri = s.substring(s.indexOf('<') + 1, s.indexOf('>', s.indexOf('<')));

                        if (m.getUris().contains(uri)) {
                            String popularity = s.substring(s.indexOf('>', s.indexOf('>') + 1) + 3, s.indexOf('^') - 1);
                            m.getMetaInformations().put(property, popularity);
                        }
                    }
                }
            }

        } catch (IOException e) {
            Logger.error("Failed to grep through the popularity file {} with regex {}", file, regex.toString());
        }

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
