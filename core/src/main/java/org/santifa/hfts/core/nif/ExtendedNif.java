package org.santifa.hfts.core.nif;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import org.aksw.gerbil.transfer.nif.vocabulary.NIF;

/**
 * Created by ratzeputz on 16.01.17.
 */
public class ExtendedNif {

    public final static Property notAnnotatedProperty = ResourceFactory.createProperty(NIF.getURI(), "notAnnotatedProperty");

    public static final Property density = ResourceFactory.createProperty(NIF.getURI(), "density");
    public static final Property microDensity = ResourceFactory.createProperty(NIF.getURI(), "microDensity");
    public static final Property macroDensity = ResourceFactory.createProperty(NIF.getURI(), "macroDensity");

    public static final Property pagerank = ResourceFactory.createProperty(NIF.getURI(), "pagerank");
    public static final Property hits = ResourceFactory.createProperty(NIF.getURI(), "hits");

    public static final Property diversityE = ResourceFactory.createProperty(NIF.getURI(), "diversityE");
    public static final Property diversitySF = ResourceFactory.createProperty(NIF.getURI(), "diversitySF");
    public static final Property microDiversityE = ResourceFactory.createProperty(NIF.getURI(), "microDiversityE");
    public static final Property microDiversitySF = ResourceFactory.createProperty(NIF.getURI(), "microDiversitySF");
    public static final Property macroDiversityE = ResourceFactory.createProperty(NIF.getURI(), "macroDiversityE");
    public static final Property macroDiversitySF = ResourceFactory.createProperty(NIF.getURI(), "macroDiversitySF");

    public static final Property ambiguityE = ResourceFactory.createProperty(NIF.getURI(), "ambiguityEntities");
    public static final Property ambiguitySF = ResourceFactory.createProperty(NIF.getURI(), "ambiguitySurfaceForms");
    public static final Property microAmbiguityE = ResourceFactory.createProperty(NIF.getURI(), "microAmbiguityEntities");
    public static final Property microAmbiguitySF = ResourceFactory.createProperty(NIF.getURI(), "microAmbiguitySurfaceForms");
    public static final Property macroAmbiguityE = ResourceFactory.createProperty(NIF.getURI(), "macroAmbiguityEntities");
    public static final Property macroAmbiguitySF = ResourceFactory.createProperty(NIF.getURI(), "macroAmbiguitySurfaceForms");

}
