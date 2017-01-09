package org.santifa.htfs.core.metric;

import com.hp.hpl.jena.rdf.model.ResourceFactory;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.vocabulary.NIF;
import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;
import org.santifa.htfs.core.NifDataset;
import org.santifa.htfs.core.utils.DictionaryConnector;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by ratzeputz on 30.12.16.
 */
public class Ambiguity extends AbstractMetric {

    DictionaryConnector connector;

    public Ambiguity(DictionaryConnector connector) {
        super(ResourceFactory.createProperty(NIF.getURI(), "microAmbiguity"),
            ResourceFactory.createProperty(NIF.getURI(), "macroAmbiguity"));
        this.connector = connector;
    }

    public Ambiguity(Path entityFile, Path surfaceFormFile) throws IOException {
        super(ResourceFactory.createProperty(NIF.getURI(), "microAmbiguity"),
                ResourceFactory.createProperty(NIF.getURI(), "macroAmbiguity"));
        this.connector = new DictionaryConnector(entityFile, surfaceFormFile);
    }

    public static Ambiguity getDefaultAmbiguity() {
        try {
            return new Ambiguity(Paths.get(Diversity.class.getClass().getResource("/filter/ambiguity_e").toURI()),
                    Paths.get(Diversity.class.getClass().getResource("/filter/ambiguity_sf").toURI()));
        } catch (IOException | URISyntaxException e) {
            Logger.error("Failed to load internal entity file {} and surface form file {} with {}",
                    "/filter/ambiguity_e", "/filter/ambiguity_sf", e);
        }
        return null;
    }

    @Override
    public NifDataset calculate(NifDataset dataset) {
        int ambiguityEntities = 0;
        int ambiguitySf = 0;
        int counter = 0;

        for (Document d : dataset.getDocuments()) {
            List<MeaningSpan> meanings = d.getMarkings(MeaningSpan.class);

            /* add every annotation ambiguity and increase number of stored ambiguities */
            for (MeaningSpan meaning : meanings) {
                String s = getEntityName(meaning.getUri());
                String sf = StringUtils.substring(d.getText(), meaning.getStartPosition(),
                        meaning.getStartPosition() + meaning.getLength()).toLowerCase();
                sf = StringUtils.replace(sf, "_", " ");

                if (connector.getEntityMappping().containsKey(s)) {
                    ambiguityEntities += connector.getEntityMappping().get(s);
                }


                if (connector.getSfMapping().containsKey(sf)) {
                    ambiguitySf += connector.getSfMapping().get(sf);
                }
            }
            counter += meanings.size();
        }

        if (ambiguityEntities != 0.0) {
            dataset.setAverageMacroAmbiguityOfEntities((double) ambiguityEntities / (double) counter);
        }

        if (ambiguitySf != 0.0) {
            dataset.setAverageMacroAmbiguityOfSurfaceForms((double) ambiguitySf / (double) counter);
        }
        Logger.debug("Macro ambiguity of entities for {} is {}", dataset.getName(), dataset.getAverageMacroAmbiguityOfEntities());
        Logger.debug("Macro ambiguity of surface forms for {} is {}", dataset.getName(), dataset.getAverageMacroAmbiguityOfSurfaceForms());
        return dataset;
    }

    @Override
    public NifDataset calculateMicro(NifDataset dataset) {
        return null;
    }

    @Override
    public NifDataset calculateMacro(NifDataset dataset) {
        return null;
    }

    private String getEntityName(String s) {
        if (StringUtils.contains(s, "sentence-")) {
            return StringUtils.substringAfterLast(s, "sentence-");
        } else {
            return StringUtils.substringAfterLast(s, "/");
        }
    }
}
