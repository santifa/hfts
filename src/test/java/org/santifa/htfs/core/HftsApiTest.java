package org.santifa.htfs.core;

import org.junit.Assert;
import org.junit.Test;
import org.santifa.htfs.core.metric.Ambiguity;
import org.santifa.htfs.core.metric.Density;
import org.santifa.htfs.core.metric.Diversity;
import org.santifa.htfs.core.metric.NotAnnotated;
import org.santifa.htfs.core.utils.DictionaryConnector;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.core.Is.is;

/**
 * Created by ratzeputz on 30.12.16.
 */
public class HftsApiTest {

    @Test
    public void testDatasetLoading() throws URISyntaxException, IOException {
        Path file = Paths.get(getClass().getResource("/kore50-nif-short.ttl").toURI());
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

        HftsApi api = new HftsApi().withDataset("file", file).withDataset("string", data);
        Assert.assertThat(api.getDatasets().size(), is(2));
        Assert.assertThat(api.getDatasets().get(0).getName(), is("file"));
        Assert.assertThat(api.getDatasets().get(1).getName(), is("string"));
    }

    @Test
    public void testCalculation() {
        NifDataset dataset = NifDatasetTest.getTestDataset();
        DictionaryConnector connector = DictionaryConnector.getDefaultConnector();
        HftsApi api = new HftsApi().withDataset(dataset)
                .withMetric(new NotAnnotated(), new Density(),
                            new Ambiguity(connector), new Diversity(connector));
        List<NifDataset> results = api.run();

        Assert.assertThat(results.size(), is(1));
        dataset = results.get(0);
        Assert.assertThat(dataset.getName(), is("test"));
        Assert.assertThat(dataset.getMacroDensity(), is(0.16666666666666666));
        Assert.assertThat(dataset.getNotAnnotatedDocs(), is(0.0));
        Assert.assertThat(dataset.getAverageMacroAmbiguityOfEntities(), is(27.5));
        Assert.assertThat(dataset.getAverageMacroAmbiguityOfSurfaceforms(), is(1926.0));
        Assert.assertThat(dataset.getAverageMacroDiversityOfEntities(), is(0.03787878787878788));
        Assert.assertThat(dataset.getAverageMacroDiversityOfSurfaceforms(), is(9.738979176990524E-4));
    }

    @Test
    public void runHftsApi() {
        NifDataset dataset = NifDatasetTest.getTestDataset();
        DictionaryConnector connector = DictionaryConnector.getDefaultConnector();
        HftsApi api = new HftsApi().withDataset(dataset)
                .withMetric(new NotAnnotated(), new Density(),
                        new Ambiguity(connector), new Diversity(connector));
        List<NifDataset> results = api.run();

        for (NifDataset ds : results) {
            System.out.println("##### Printing dataset " + ds.getName());
            System.out.println(ds.write());
        }
    }
}