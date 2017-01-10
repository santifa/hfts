package org.santifa.htfs.core.metric;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.vocabulary.NIF;
import org.pmw.tinylog.Logger;
import org.santifa.htfs.core.NifDataset;

/**
 * Created by ratzeputz on 30.12.16.
 */
public class NotAnnotated implements Metric {

    public final static Property notAnnotatedProperty = ResourceFactory.createProperty(NIF.getURI(), "notAnnotatedProperty");

    @Override
    public NifDataset calculate(NifDataset dataset) {
        int emptydocs = 0;
        for (Document d : dataset.getDocuments()) {
            if (d.getMarkings().isEmpty()) {
                emptydocs++;
            }
        }

        /* determine the ratio between empty documents and all documents */
        double result = (double) emptydocs / (double) dataset.getDocuments().size();
        dataset.getMetaInformations().put(notAnnotatedProperty, String.valueOf(result));
        Logger.debug("Macro not-annotated for {} is {}", dataset.getName(), result);
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
