package org.santifa.htfs.core;

import org.aksw.gerbil.transfer.nif.Document;
import org.junit.Assert;
import org.junit.Test;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;

/**
 * Test the creation of nif datasets.
 *
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class NifDatasetTest {

    static {
        Logger.getConfiguration().level(Level.DEBUG).activate();
    }

    @Test
    public void testDatasetCreation() throws URISyntaxException, IOException {
        Path file = Paths.get(getClass().getResource("/kore50-nif-short.ttl").toURI());
        NifDataset dataset = new NifDataset("test", file);
        Assert.assertNotNull(dataset.getDocuments());
    }

    @Test(expected = IOException.class)
    public void testDatasetCreationException() throws URISyntaxException, IOException {
        NifDataset dataset = new NifDataset("test", Paths.get(".sdaf"));
    }

    @Test
    public void testDatasetCreationFromString() {
        String data = "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
                "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n" +
                "@prefix owl: <http://www.w3.org/2002/07/owl#> .\n" +
                "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n" +
                "@prefix nif: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
                "@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> ." +
                "" +
                "<http://www.mpi-inf.mpg.de/yago-naga/aida/download/KORE50.tar.gz/AIDA.tsv/CEL01#char=0,>\n" +
                "a nif:String , nif:Context , nif:RFC5147String ;\n" +
                "nif:broaderContext <http://www.mpi-inf.mpg.de/yago-naga/aida/download/KORE50.tar.gz/AIDA.tsv#char=0,> ;\n" +
                "nif:isString \"David and Victoria named their children Brooklyn, Romeo, Cruz, and Harper Seven.\"^^xsd:string .\n" +
                "\n" +
                "<http://www.mpi-inf.mpg.de/yago-naga/aida/download/KORE50.tar.gz/AIDA.tsv/CEL01#char=0,5>\n" +
                "a nif:String , nif:RFC5147String ;\n" +
                "nif:referenceContext <http://www.mpi-inf.mpg.de/yago-naga/aida/download/KORE50.tar.gz/AIDA.tsv/CEL01#char=0,> ;\n" +
                "nif:anchorOf \"David\"^^xsd:string ;\n" +
                "nif:beginIndex \"0\"^^xsd:long ;\n" +
                "nif:endIndex \"5\"^^xsd:long ;\n" +
                "a nif:Phrase ;\n" +
                "itsrdf:taIdentRef  <http://dbpedia.org/resource/David_Beckham> .\n" +
                "\n" +
                "<http://www.mpi-inf.mpg.de/yago-naga/aida/download/KORE50.tar.gz/AIDA.tsv/CEL01#char=10,18>\n" +
                "a nif:String , nif:RFC5147String ;\n" +
                "nif:referenceContext <http://www.mpi-inf.mpg.de/yago-naga/aida/download/KORE50.tar.gz/AIDA.tsv/CEL01#char=0,> ;\n" +
                "nif:anchorOf \"Victoria\"^^xsd:string ;\n" +
                "nif:beginIndex \"10\"^^xsd:long ;\n" +
                "nif:endIndex \"18\"^^xsd:long ;\n" +
                "a nif:Phrase ;\n" +
                "itsrdf:taIdentRef  <http://dbpedia.org/resource/Victoria_Beckham> .\n";

        NifDataset dataset = new NifDataset("test", data);
        Assert.assertThat(dataset.getDocuments().size(), is(1));

        Document d = dataset.getDocuments().get(0);
        Assert.assertThat(d.getDocumentURI(), is("http://www.mpi-inf.mpg.de/yago-naga/aida/download/KORE50.tar.gz/AIDA.tsv/CEL01"));
        Assert.assertThat(dataset.getMarkings().size(), is(2));
        Assert.assertThat(d.getText(), is("David and Victoria named their children Brooklyn, Romeo, Cruz, and Harper Seven."));
    }

    @Test
    public void testWriting() {
        NifDataset dataset = getTestDataset();
        dataset.setNotAnnotatedDocs(0.0);
        dataset.setMacroDensity(0.1);
        dataset.setAverageMacroAmbiguityOfEntities(0.2);
        dataset.setAverageMacroAmbiguityOfSurfaceforms(0.3);
        dataset.setAverageMacroDiversityOfEntities(0.4);
        dataset.setAverageMacroDiversityOfSurfaceforms(0.5);
        String result = dataset.write();
        System.out.println(result);
    }

    public static NifDataset getTestDataset() {
        String data = "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
                "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n" +
                "@prefix owl: <http://www.w3.org/2002/07/owl#> .\n" +
                "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n" +
                "@prefix nif: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
                "@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> ." +
                "" +
                "<http://www.mpi-inf.mpg.de/yago-naga/aida/download/KORE50.tar.gz/AIDA.tsv/CEL01#char=0,>\n" +
                "a nif:String , nif:Context , nif:RFC5147String ;\n" +
                "nif:broaderContext <http://www.mpi-inf.mpg.de/yago-naga/aida/download/KORE50.tar.gz/AIDA.tsv#char=0,> ;\n" +
                "nif:isString \"David and Victoria named their children Brooklyn, Romeo, Cruz, and Harper Seven.\"^^xsd:string .\n" +
                "\n" +
                "<http://www.mpi-inf.mpg.de/yago-naga/aida/download/KORE50.tar.gz/AIDA.tsv/CEL01#char=0,5>\n" +
                "a nif:String , nif:RFC5147String ;\n" +
                "nif:referenceContext <http://www.mpi-inf.mpg.de/yago-naga/aida/download/KORE50.tar.gz/AIDA.tsv/CEL01#char=0,> ;\n" +
                "nif:anchorOf \"David\"^^xsd:string ;\n" +
                "nif:beginIndex \"0\"^^xsd:long ;\n" +
                "nif:endIndex \"5\"^^xsd:long ;\n" +
                "a nif:Phrase ;\n" +
                "itsrdf:taIdentRef  <http://dbpedia.org/resource/David_Beckham> .\n" +
                "\n" +
                "<http://www.mpi-inf.mpg.de/yago-naga/aida/download/KORE50.tar.gz/AIDA.tsv/CEL01#char=10,18>\n" +
                "a nif:String , nif:RFC5147String ;\n" +
                "nif:referenceContext <http://www.mpi-inf.mpg.de/yago-naga/aida/download/KORE50.tar.gz/AIDA.tsv/CEL01#char=0,> ;\n" +
                "nif:anchorOf \"Victoria\"^^xsd:string ;\n" +
                "nif:beginIndex \"10\"^^xsd:long ;\n" +
                "nif:endIndex \"18\"^^xsd:long ;\n" +
                "a nif:Phrase ;\n" +
                "itsrdf:taIdentRef  <http://dbpedia.org/resource/Victoria_Beckham> .\n";

        return new NifDataset("test", data);
    }

}