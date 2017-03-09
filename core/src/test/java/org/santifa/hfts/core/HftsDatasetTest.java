package org.santifa.hfts.core;

import org.aksw.gerbil.transfer.nif.Document;
import org.junit.Assert;
import org.junit.Test;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;
import org.santifa.hfts.core.nif.HftsOnt;
import org.santifa.hfts.core.nif.MetaNamedEntity;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;

/**
 * Test the creation and writing of nif data sets.
 * <br/>
 * Also provide a static test data set.
 *
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class HftsDatasetTest {

    static {
        Logger.getConfiguration().level(Level.DEBUG).activate();
    }

    @Test
    public void testDatasetCreation() throws URISyntaxException, IOException {
        Path file = Paths.get(getClass().getResource("/kore50-nif-short.ttl").toURI());
        HftsDataset dataset = new HftsDataset("test", file);
        Assert.assertNotNull(dataset.getDocuments());
    }

    @Test(expected = IOException.class)
    public void testDatasetCreationException() throws URISyntaxException, IOException {
        HftsDataset dataset = new HftsDataset("test", Paths.get(".sdaf"));
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

        HftsDataset dataset = new HftsDataset("test", data);
        Assert.assertThat(dataset.getDocuments().size(), is(1));

        Document d = dataset.getDocuments().get(0);
        Assert.assertThat(d.getDocumentURI(), is("http://www.mpi-inf.mpg.de/yago-naga/aida/download/KORE50.tar.gz/AIDA.tsv/CEL01"));
        Assert.assertThat(dataset.getMarkings().size(), is(2));
        Assert.assertThat(d.getText(), is("David and Victoria named their children Brooklyn, Romeo, Cruz, and Harper Seven."));
        Assert.assertThat(d.getMarkings(MetaNamedEntity.class).size(), is(2));
    }

    @Test
    public void testWriting() {
        HftsDataset dataset = getTestDataset();
        dataset.getMetaInformations().put(HftsOnt.notAnnotatedProperty, "0.0");
        dataset.getMetaInformations().put(HftsOnt.macroDensity, "0.1");
        dataset.getMetaInformations().put(HftsOnt.macroAmbiguityEntities, "0.2");
        dataset.getMetaInformations().put(HftsOnt.macroAmbiguitySurfaceForms, "0.3");
        dataset.getMetaInformations().put(HftsOnt.diversityEntities, "0.4");
        dataset.getMetaInformations().put(HftsOnt.diversitySurfaceForms, "0.5");
        String result = dataset.write();
        System.out.println(result);
    }

    /**
     * Gets a {@link HftsDataset} which contains one sentence with two entities
     * 'Victoria Beckham' and 'David Beckham'
     *
     * @return the test data set
     */
    public static HftsDataset getTestDataset() {
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

        return new HftsDataset("test", data);
    }
}