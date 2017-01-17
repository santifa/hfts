package org.santifa.hfts.core.nif;

import com.hp.hpl.jena.rdf.model.Property;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ratzeputz on 17.01.17.
 */
public class MetaDocument extends DocumentImpl implements Document {

    private HashMap<Property, String> metaInformations;

    public MetaDocument(String text, String uri, List<Marking> markings) {
        super(text, uri, markings);
        this.metaInformations = new HashMap<>();
    }

    public HashMap<Property, String> getMetaInformations() {
        return metaInformations;
    }

    public void setMetaInformations(HashMap<Property, String> metaInformations) {
        this.metaInformations = metaInformations;
    }
}
