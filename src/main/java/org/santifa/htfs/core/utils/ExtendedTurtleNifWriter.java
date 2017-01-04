package org.santifa.htfs.core.utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.gerbil.io.nif.impl.TurtleNIFWriter;
import org.aksw.gerbil.transfer.nif.vocabulary.NIF;
import org.apache.jena.riot.Lang;
import org.santifa.htfs.core.NifDataset;

import java.io.OutputStream;
import java.io.StringWriter;

/**
 * Created by ratzeputz on 04.01.17.
 */
public class ExtendedTurtleNifWriter extends TurtleNIFWriter {

    private String lang = Lang.TURTLE.getName();

    public Model extendModel(NifDataset dataset) {
        Model nifModel = createNIFModel(dataset.getDocuments());

        /* create a dataset property */
        Resource ds = ResourceFactory.createResource(NIF.getURI() + "dataset/" + dataset.getName());
        Property dsProp = ResourceFactory.createProperty(NIF.getURI(), "dataset");

        /* metrics */
        Property notAnnotated = ResourceFactory.createProperty(NIF.getURI(), "notAnnotated");
        Property density = ResourceFactory.createProperty(NIF.getURI(), "macroDensity");
        Property ambOfSf = ResourceFactory.createProperty(NIF.getURI(), "macroAmbiguityOfSf");
        Property ambOfE = ResourceFactory.createProperty(NIF.getURI(), "macroAmbiguityOfE");
        Property divOfE = ResourceFactory.createProperty(NIF.getURI(), "macroDiversityOfE");
        Property divOfSf = ResourceFactory.createProperty(NIF.getURI(), "macroDiversityOfSf");

        nifModel.add(ds, RDF.type, dsProp);
        nifModel.add(ds, notAnnotated, String.valueOf(dataset.getNotAnnotatedDocs()));
        nifModel.add(ds, density, String.valueOf(dataset.getMacroDensity()));
        nifModel.add(ds, ambOfE, String.valueOf(dataset.getAverageMacroAmbiguityOfEntities()));
        nifModel.add(ds, ambOfSf, String.valueOf(dataset.getAverageMacroAmbiguityOfSurfaceforms()));
        nifModel.add(ds, divOfE, String.valueOf(dataset.getAverageMacroDiversityOfEntities()));
        nifModel.add(ds, divOfSf, String.valueOf(dataset.getAverageMacroDiversityOfSurfaceforms()));
        return nifModel;
    }

    public String writeNIF(NifDataset dataset) {
        Model nifModel = extendModel(dataset);
        StringWriter writer = new StringWriter();
        nifModel.write(writer, lang);
        return writer.toString();
    }

    public void writeNIF(NifDataset dataset, OutputStream os) {
        Model nifModel = extendModel(dataset);
        nifModel.write(os, lang);
    }
}
