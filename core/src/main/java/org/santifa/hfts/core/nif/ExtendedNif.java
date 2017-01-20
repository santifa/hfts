package org.santifa.hfts.core.nif;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import org.aksw.gerbil.transfer.nif.vocabulary.NIF;

/**
 * Created by ratzeputz on 16.01.17.
 */
public class ExtendedNif {

    public final static Property notAnnotatedProperty = ResourceFactory.createProperty(NIF.getURI(), "notAnnotatedProperty");

    /* density properties for document and dataset level */
    public static final Property density = ResourceFactory.createProperty(NIF.getURI(), "density");
    public static final Property microDensity = ResourceFactory.createProperty(NIF.getURI(), "microDensity");
    public static final Property macroDensity = ResourceFactory.createProperty(NIF.getURI(), "macroDensity");

    /* popularity properties */
    public static final Property pagerank = ResourceFactory.createProperty(NIF.getURI(), "pagerank");
    public static final Property hits = ResourceFactory.createProperty(NIF.getURI(), "hits");

    /* diversity properties for dataset level  */
    public static final Property diversityEntities = ResourceFactory.createProperty(NIF.getURI(), "diversityEntities");
    public static final Property diversitySurfaceForms = ResourceFactory.createProperty(NIF.getURI(), "diversitySurfaceForms");

    /* ambiguity properties document and annotation level */
    public static final Property ambiguityEntity = ResourceFactory.createProperty(NIF.getURI(), "ambiguityEntity");
    public static final Property ambiguitySurfaceForm = ResourceFactory.createProperty(NIF.getURI(), "ambiguitySurfaceForm");
    public static final Property ambiguityEntities = ResourceFactory.createProperty(NIF.getURI(), "ambiguityEntities");
    public static final Property ambiguitySurfaceForms = ResourceFactory.createProperty(NIF.getURI(), "ambiguitySurfaceForms");
    /* dataset level */
    public static final Property microAmbiguityEntities = ResourceFactory.createProperty(NIF.getURI(), "microAmbiguityEntities");
    public static final Property microAmbiguitySurfaceForms = ResourceFactory.createProperty(NIF.getURI(), "microAmbiguitySurfaceForms");
    public static final Property macroAmbiguityEntities = ResourceFactory.createProperty(NIF.getURI(), "macroAmbiguityEntities");
    public static final Property macroAmbiguitySurfaceForms = ResourceFactory.createProperty(NIF.getURI(), "macroAmbiguitySurfaceForms");

    /* document and dataset level */
    public static final Property maxRecall = ResourceFactory.createProperty(NIF.getURI(), "maxRecall");
    public static final Property macroMaxRecall = ResourceFactory.createProperty(NIF.getURI(), "macroMaxRecall");
    public static final Property microMaxRecall = ResourceFactory.createProperty(NIF.getURI(), "microMaxRecall");

    public static Literal getTypedLiteral(Property p, String value) {
        if (p.getURI().equals(ambiguityEntity.getURI()) || p.getURI().equals(ambiguitySurfaceForm.getURI())) {
            return ResourceFactory.createTypedLiteral(value, XSDDatatype.XSDinteger);
        }
        return ResourceFactory.createTypedLiteral(value, XSDDatatype.XSDdouble);
    }
}
