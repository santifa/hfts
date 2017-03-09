package org.santifa.hfts.core.nif.writers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.gerbil.io.nif.DocumentWriter;
import org.aksw.gerbil.io.nif.impl.TurtleNIFWriter;
import org.aksw.gerbil.io.nif.utils.NIFUriHelper;
import org.aksw.gerbil.transfer.nif.Document;
import org.santifa.hfts.core.HftsDataset;
import org.santifa.hfts.core.nif.HftsOnt;

import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * A very quick re-implementation to the {@link TurtleNIFWriter}.
 * <br/>
 * Since the {@link org.aksw.gerbil.io.nif.NIFWriter} interface
 * is very restrictive the shortest solution was to extend and didn't
 * use the interface at all. Maybe changed later.
 * <p>
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class ExtendedTurtleNifWriter extends ExtendedAbstractNIFWriter {

    private static final String HTTP_CONTENT_TYPE = "application/x-turtle";
    private static final String LANGUAGE = "TTL";
    private static final DocumentWriter writer = new ExtendedDocumentWriter(new ExtendedAnnotationWriterImpl());

    public ExtendedTurtleNifWriter() {
        super(HTTP_CONTENT_TYPE, LANGUAGE, writer);
    }

    /**
     * Create a {@link Model} from the {@link org.aksw.gerbil.transfer.nif.Document}s
     * and insert the extension from the {@link HftsDataset}.
     *
     * @param dataset the dataset
     * @return the model
     */
    public Model extendModel(HftsDataset dataset) {
        List<Document> docs = new ArrayList<>();
        for (Document d : dataset.getDocuments()) {
            docs.add(d);
        }

        Model nifModel = createNIFModel(docs);

        /* create a dataset property */
        Resource ds = ResourceFactory.createResource(HftsOnt.getUri() + "/" + dataset.getName());

        if (!dataset.getMetaInformations().isEmpty()) {
            nifModel.add(ds, RDF.type, HftsOnt.Dataset);

            /* store reference docs */
            for (Document d : docs) {
                String text = d.getText();
                int end = text.codePointCount(0, text.length());
                String documentUri = NIFUriHelper.getNifUri(d, end);
                Resource documentResource = nifModel.createResource(documentUri);
                nifModel.add(ds, HftsOnt.refDocs, documentResource);
            }
        }

        /* metrics */
        for (Property metaInformation : dataset.getMetaInformations().keySet()) {
            nifModel.add(ds, metaInformation, HftsOnt.getTypedLiteral(metaInformation, dataset.getMetaInformations().get(metaInformation)));
        }
        return nifModel;
    }

    /**
     * Write a {@link HftsDataset} into a huge {@link String}
     * in turtle.
     *
     * @param dataset the dataset
     * @return the string
     */
    public String writeNIF(HftsDataset dataset) {
        Model nifModel = extendModel(dataset);
        StringWriter writer = new StringWriter();
        nifModel.write(writer, LANGUAGE);
        return writer.toString();
    }

    /**
     * Write a {@link HftsDataset} into a file or some
     * other {@link OutputStream}.
     *
     * @param dataset the dataset
     * @param os      the os
     */
    public void writeNIF(HftsDataset dataset, OutputStream os) {
        Model nifModel = extendModel(dataset);
        nifModel.write(os, LANGUAGE);
    }
}
