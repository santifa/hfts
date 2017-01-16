package org.santifa.hfts.core.nif.writers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import org.aksw.gerbil.io.nif.AnnotationWriter;
import org.aksw.gerbil.io.nif.utils.NIFUriHelper;
import org.aksw.gerbil.transfer.nif.Span;
import org.santifa.hfts.core.nif.MetaSpan;

import java.util.HashMap;

/**
 * Write extended annotations to NIF models.
 *
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class ExtendedAnnotationWriterImpl extends AnnotationWriter {

    @Override
    public void addSpan(Model nifModel, Resource documentAsResource, String text, String documentURI, Span span) {
        super.addSpan(nifModel, documentAsResource, text, documentURI, span);

        /* create basic span uri like in super() */
        int startInJavaText = span.getStartPosition();
        int endInJavaText = startInJavaText + span.getLength();
        int start = text.codePointCount(0, startInJavaText);
        int end = start + text.codePointCount(startInJavaText, endInJavaText);
        String spanUri = NIFUriHelper.getNifUri(documentURI, start, end);
        Resource spanAsResource = nifModel.createResource(spanUri);

        /* store every meta property */
        if (span instanceof MetaSpan) {
            HashMap<Property, String> metaInformations = ((MetaSpan) span).getMetaInformations();
            for (Property property : metaInformations.keySet()) {
                nifModel.add(spanAsResource, property, ResourceFactory.createPlainLiteral(metaInformations.get(property)));
            }
        }
    }
}
