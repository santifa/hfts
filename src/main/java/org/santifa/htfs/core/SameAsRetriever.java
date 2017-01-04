package org.santifa.htfs.core;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.nnsoft.sameas4j.*;
import org.pmw.tinylog.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ratzeputz on 28.12.16.
 */
class SameAsRetriever {

    SameAsService service = DefaultSameAsServiceFactory.createNew();

    NifDataset retrieve(NifDataset dataset) {
        for (Document d : dataset.getDocuments()) {

            /* we can only retrieve additional uris for markings
             * which contains already at least one uri or so called meaning */
            for (Meaning m : d.getMarkings(Meaning.class)) {
                try {
                    retrieveEquivalence(m);
                } catch (SameAsServiceException e) {
                    Logger.error("Failed to retrieve duplicate uris for meaning {} ", m);
                }
            }
        }

        return dataset;
    }

    private void retrieveEquivalence(Meaning annotation) throws SameAsServiceException {
        Set<String> sameAsUris = new HashSet<>();

        for (String uri : annotation.getUris()) {
            try {
                Equivalence equivalences = service.getDuplicates(new URI(uri));
                equivalences.iterator().forEachRemaining(e -> sameAsUris.add(e.toString()));

            } catch (URISyntaxException e) {
                Logger.error("Failed to convert string {} into a valid uri.", uri);
            }
        }
        annotation.setUris(sameAsUris);
    }
}
