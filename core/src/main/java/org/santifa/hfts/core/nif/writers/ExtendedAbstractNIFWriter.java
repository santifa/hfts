package org.santifa.hfts.core.nif.writers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.shared.PrefixMapping;
import org.aksw.gerbil.io.nif.DocumentWriter;
import org.aksw.gerbil.io.nif.utils.NIFTransferPrefixMapping;
import org.aksw.gerbil.transfer.nif.Document;
import org.santifa.hfts.core.nif.HftsOnt;

import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * A simple replacement of the
 *
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public abstract class ExtendedAbstractNIFWriter {

    private String httpContentType;
    private String language;
    protected DocumentWriter writer;

    public ExtendedAbstractNIFWriter(String httpContentType, String language, DocumentWriter writer) {
        this.httpContentType = httpContentType;
        this.language = language;
        this.writer = writer;
    }

    protected Model createNIFModel(List<Document> document) {
        Model nifModel = ModelFactory.createDefaultModel();
        PrefixMapping mapping = NIFTransferPrefixMapping.getInstance();
        mapping = mapping.setNsPrefix("hfts", HftsOnt.getUri() + "#");
        nifModel.setNsPrefixes(mapping);

        for (Document d : document) {
            writer.writeDocumentToModel(nifModel, d);
        }
        return nifModel;
    }

    public String getHttpContentType() {
        return httpContentType;
    }

    public String writeNIF(List<Document> document) {
        StringWriter writer = new StringWriter();
        writeNIF(document, writer);
        return writer.toString();
    }

    public void writeNIF(List<Document> document, OutputStream os) {
        Model nifModel = createNIFModel(document);
        nifModel.write(os, language);
    }

    public void writeNIF(List<Document> document, Writer writer) {
        Model nifModel = createNIFModel(document);
        nifModel.write(writer, language);
    }
}
