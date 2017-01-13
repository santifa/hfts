package org.santifa.htfs.core.metric;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.aksw.gerbil.transfer.nif.vocabulary.NIF;
import org.pmw.tinylog.Logger;
import org.santifa.htfs.core.NifDataset;
import org.santifa.htfs.core.utils.Grep;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by ratzeputz on 09.01.17.
 */
public class PopularityAssignor implements Metric {

    public static final Property pagerank = ResourceFactory.createProperty(NIF.getURI(), "pagerank");

    public static final Property hits = ResourceFactory.createProperty(NIF.getURI(), "hits");

    private Path file;

    private Property property;

    public PopularityAssignor(Path file, Property property) {
        this.file = file;
        this.property = property;
    }

    @Override
    public NifDataset calculate(NifDataset dataset) {
        List<Marking> meanings = dataset.getMarkings();
        StringBuilder regex = new StringBuilder();

        /* collect all entities */
        regex.append('(');
        for (Marking m : meanings) {
            if (m instanceof Meaning) {
                for (String uri : ((Meaning) m).getUris()) {
                    regex.append(uri).append("|");
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
                for (Meaning m : d.getMarkings(Meaning.class)) {

                    /* check which uris where found */
                    for (String s : matches) {
                        /* get the uri and check the meaning set */
                        String uri = s.substring(s.indexOf('<') + 1, s.indexOf('>', s.indexOf('<')));

                        if (m.getUris().contains(uri)) {
                            String popularity = s.substring(s.indexOf('>', s.indexOf('>') + 1) + 3, s.indexOf('^') - 1);
                            System.out.println(uri + ":" + Double.valueOf(popularity));
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
