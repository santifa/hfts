package org.santifa.hfts.core;

import org.junit.Assert;
import org.junit.Test;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;
import org.santifa.hfts.core.metric.*;
import org.santifa.hfts.core.nif.ExtendedNif;
import org.santifa.hfts.core.utils.DictionaryConnector;
import org.apache.commons.lang3.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.core.Is.is;

/**
 * Some basic tests for the api.
 *
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class HftsApiTest {

    static {
        Logger.getConfiguration().level(Level.DEBUG).activate();
    }

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
        DictionaryConnector connectorEntity = DictionaryConnector.getDefaultEntityConnector(2);
        DictionaryConnector connectorSf = DictionaryConnector.getDefaultSFConnector(2);
        HftsApi api = new HftsApi().withDataset(dataset)
                .withMetric(new NotAnnotated(), new Density(),
                            new Ambiguity(connectorEntity, connectorSf),
                            new Diversity(connectorEntity, connectorSf));
        List<NifDataset> results = api.run();

        Assert.assertThat(results.size(), is(1));
        dataset = results.get(0);
        Assert.assertThat(dataset.getName(), is("test"));
        Assert.assertThat(dataset.getMetaInformations().get(ExtendedNif.macroDensity), is("0.16666666666666666"));
        Assert.assertThat(dataset.getMetaInformations().get(ExtendedNif.notAnnotatedProperty), is("0.0"));
        Assert.assertThat(dataset.getMetaInformations().get(ExtendedNif.macroAmbiguityEntities), is("27.5"));
        Assert.assertThat(dataset.getMetaInformations().get(ExtendedNif.macroAmbiguitySurfaceForms), is("1926.0"));
        Assert.assertThat(dataset.getMetaInformations().get(ExtendedNif.diversityEntities), is("0.03787878787878788"));
        Assert.assertThat(dataset.getMetaInformations().get(ExtendedNif.diversitySurfaceForms), is("9.738979176990524E-4"));
    }

    @Test
    public void runHftsApiTyper() {
        NifDataset dataset = NifDatasetTest.getTestDataset();
        HftsApi api = new HftsApi().withDataset(dataset)
                .withMetric(CategoryAssignor.getDefaultAssignor(), PopularityAssignor.getDefaultPageRank(1));
        List<NifDataset> results = api.run();

        for (NifDataset ds : results) {
            System.out.println("##### Printing dataset " + ds.getName());
            System.out.println(ds.write());
        }
    }

    @Test
    public void testHftsApi() throws IOException, URISyntaxException {
        Path file = Paths.get(getClass().getResource("/kore50-nif-short.ttl").toURI());
        //NifDataset dataset = NifDatasetTest.getTestDataset();
        DictionaryConnector connectorEntity = DictionaryConnector.getDefaultEntityConnector(2);
        DictionaryConnector connectorSf = DictionaryConnector.getDefaultSFConnector(2);
        HftsApi api = new HftsApi().withDataset(file.toFile().getName(), file)
                .withMetric(new NotAnnotated(), new Density(),
                        new Ambiguity(connectorEntity, connectorSf),
                        new Diversity(connectorEntity, connectorSf),
                        CategoryAssignor.getDefaultAssignor(),
                        PopularityAssignor.getDefaultPageRank(1),
                        PopularityAssignor.getDefaultHits(1)
                );
        List<NifDataset> results = api.run();

        for (NifDataset ds : results) {
            System.out.println("##### Printing dataset " + ds.getName());
            ds.write(new FileOutputStream(ds.getName() + "test.ttl"));
        }
    }

    @Test
    public void runHftsApi() throws IOException, URISyntaxException {
        Path file2 = Paths.get("../data/wes2015-dataset-nif.ttl");
        Path file = Paths.get("../data/kore50-nif.ttl");

        DictionaryConnector connectorEntity = DictionaryConnector.getDefaultEntityConnector(2);
        DictionaryConnector connectorSf = DictionaryConnector.getDefaultSFConnector(3);
        String filename = StringUtils.substringBeforeLast(file.toFile().getName(), ".");
        String filename2 = StringUtils.substringBeforeLast(file2.toFile().getName(), ".");

        HftsApi api = new HftsApi().withDataset(filename, file)//.withDataset(filename2, file2)
                .withMetric(new NotAnnotated(), new Density(),
                        CategoryAssignor.getDefaultAssignor(),
                        new MaxRecall(connectorSf),
                        new Ambiguity(connectorEntity, connectorSf),
                        new Diversity(connectorEntity, connectorSf),
                        PopularityAssignor.getDefaultPageRank(1),
                        PopularityAssignor.getDefaultHits(1)
                );
        List<NifDataset> results = api.run();

        for (NifDataset ds : results) {
            System.out.println("##### Printing dataset " + ds.getName());
            ds.write(new FileOutputStream(ds.getName() + "-ext.ttl"));
        }
    }

}