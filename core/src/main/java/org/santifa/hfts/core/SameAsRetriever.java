package org.santifa.hfts.core;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.nnsoft.sameas4j.*;
import org.pmw.tinylog.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

/**
 * The class takes a {@link HftsDataset} and resolves
 * all 'owl:sameAs' relations for every URI present in the data set.
 * <br/>
 * This can be used with some metrics which are utilizing URIs
 * or the typing of entities.
 *
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
class SameAsRetriever {

    private SameAsService service = DefaultSameAsServiceFactory.createNew();

    /**
     * Retrieve all sameAs relations for URIs present in a {@link HftsDataset}
     *
     * @param dataset the data set
     * @return the extended data set
     */
    HftsDataset retrieve(HftsDataset dataset) {
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
