package org.santifa.hfts.core.nif;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * Created by ratzeputz on 16.01.17.
 */
public class HftsOnt {

    private static final String uri = "https://raw.githubusercontent.com/santifa/hfts/master/ont/hfts.ttl";

    public static final Property refDocs = property("referenceDocuments");
    public static final Resource Dataset = resource("Dataset");

    public static final Property notAnnotatedProperty = property("notAnnotatedProperty");

    /* density properties for document and dataset level */
    public static final Property density = property("density");
    public static final Property microDensity = property("microDensity");
    public static final Property macroDensity = property("macroDensity");

    /* popularity properties */
    public static final Property pagerank = property("pagerank");
    public static final Property hits = property("hits");

    /* diversity properties for dataset level  */
    public static final Property diversityEntities = property("diversityEntities");
    public static final Property diversitySurfaceForms = property("diversitySurfaceForms");

    /* ambiguity properties document and annotation level */
    public static final Property ambiguityEntity = property("ambiguityEntity");
    public static final Property ambiguitySurfaceForm = property("ambiguitySurfaceForm");
    public static final Property ambiguityEntities = property("ambiguityEntities");
    public static final Property ambiguitySurfaceForms = property("ambiguitySurfaceForms");
    /* dataset level */
    public static final Property microAmbiguityEntities = property("microAmbiguityEntities");
    public static final Property microAmbiguitySurfaceForms = property("microAmbiguitySurfaceForms");
    public static final Property macroAmbiguityEntities = property("macroAmbiguityEntities");
    public static final Property macroAmbiguitySurfaceForms = property("macroAmbiguitySurfaceForms");

    /* document and dataset level */
    public static final Property maxRecall = property("maxRecall");
    public static final Property macroMaxRecall = property("macroMaxRecall");
    public static final Property microMaxRecall = property("microMaxRecall");

    protected static Resource resource(String local) {
        return ResourceFactory.createResource(uri + "#" + local);
    }

    protected static Property property(String local) {
        return ResourceFactory.createProperty(uri, "#" + local);
    }

    public static Literal getTypedLiteral(Property p, String value) {
        if (p.getURI().equals(ambiguityEntity.getURI()) || p.getURI().equals(ambiguitySurfaceForm.getURI())) {
            return ResourceFactory.createTypedLiteral(value, XSDDatatype.XSDinteger);
        }
        return ResourceFactory.createTypedLiteral(value, XSDDatatype.XSDdouble);
    }

    public static String getUri() {
        return uri;
    }
}
