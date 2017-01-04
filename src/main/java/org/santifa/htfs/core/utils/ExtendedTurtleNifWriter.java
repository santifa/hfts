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
 * A very quick extension to the {@link TurtleNIFWriter}.
 * <br/>
 * Since the {@link org.aksw.gerbil.io.nif.NIFWriter} interface
 * is very restrictive the shortest solution was to extend and didn't
 * use the interface at all. Maybe changed later.
 * <p>
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class ExtendedTurtleNifWriter extends TurtleNIFWriter {

    private String lang = Lang.TURTLE.getName();

    /**
     * Create a {@link Model} from the {@link org.aksw.gerbil.transfer.nif.Document}s
     * and insert the extension from the {@link NifDataset}.
     *
     * @param dataset the dataset
     * @return the model
     */
    public Model extendModel(NifDataset dataset) {
        Model nifModel = createNIFModel(dataset.getDocuments());

        /* create a dataset property */
        Resource ds = ResourceFactory.createResource(NIF.getURI() + "dataset/" + dataset.getName());
        Property dsProp = ResourceFactory.createProperty(NIF.getURI(), "dataset");

        /* metrics */
        Property notAnnotated = ResourceFactory.createProperty(NIF.getURI(), "notAnnotated");
        Property density = ResourceFactory.createProperty(NIF.getURI(), "macroDensity");
        Property ambOfSf = ResourceFactory.createProperty(NIF.getURI(), "macroAmbiguitySurfaceForms");
        Property ambOfE = ResourceFactory.createProperty(NIF.getURI(), "macroAmbiguityEntities");
        Property divOfE = ResourceFactory.createProperty(NIF.getURI(), "macroDiversityOfEntities");
        Property divOfSf = ResourceFactory.createProperty(NIF.getURI(), "macroDiversitySurfaceForms");

        nifModel.add(ds, RDF.type, dsProp);
        nifModel.add(ds, notAnnotated, String.valueOf(dataset.getNotAnnotatedDocs()));
        nifModel.add(ds, density, String.valueOf(dataset.getMacroDensity()));
        nifModel.add(ds, ambOfE, String.valueOf(dataset.getAverageMacroAmbiguityOfEntities()));
        nifModel.add(ds, ambOfSf, String.valueOf(dataset.getAverageMacroAmbiguityOfSurfaceForms()));
        nifModel.add(ds, divOfE, String.valueOf(dataset.getAverageMacroDiversityOfEntities()));
        nifModel.add(ds, divOfSf, String.valueOf(dataset.getAverageMacroDiversityOfSurfaceForms()));
        return nifModel;
    }

    /**
     * Write a {@link NifDataset} into a huge {@link String}
     * in turtle.
     *
     * @param dataset the dataset
     * @return the string
     */
    public String writeNIF(NifDataset dataset) {
        Model nifModel = extendModel(dataset);
        StringWriter writer = new StringWriter();
        nifModel.write(writer, lang);
        return writer.toString();
    }

    /**
     * Write a {@link NifDataset} into a file or some
     * other {@link OutputStream}.
     *
     * @param dataset the dataset
     * @param os      the os
     */
    public void writeNIF(NifDataset dataset, OutputStream os) {
        Model nifModel = extendModel(dataset);
        nifModel.write(os, lang);
    }
}
