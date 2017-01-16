package org.santifa.hfts.core.nif;

import com.hp.hpl.jena.rdf.model.Property;
import org.aksw.gerbil.transfer.nif.Marking;

import java.util.HashMap;

/**
 * A basic interface which integrates meta information into
 * NIF documents.
 *
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public interface MetaSpan extends Marking {

    HashMap<Property, String> getMetaInformations();

    void setMetaInformations(HashMap<Property, String> metaInformations);
}
