package org.santifa.hfts.core.metric;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.santifa.hfts.core.NifDataset;
import org.santifa.hfts.core.NifDatasetTest;
import org.santifa.hfts.core.nif.ExtendedNif;
import org.santifa.hfts.core.nif.MetaDocument;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.core.Is.is;

/**
 * Created by ratzeputz on 30.12.16.
 */
@RunWith(Parameterized.class)
public class AmbiguityTest {

    private static Ambiguity ambiguity = Ambiguity.getDefaultAmbiguity(5);

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException, IOException {
        NifDataset testset1 = NifDatasetTest.getTestDataset();
        testset1.getDocuments().get(0).setMarkings(new ArrayList<>());

        NifDataset testset2 = NifDatasetTest.getTestDataset();
        testset2.getDocuments().get(0).setMarkings(new ArrayList<>());
        testset2.getDocuments().add(new MetaDocument("", "", new ArrayList<>()));

        Path file = Paths.get(NotAnnotated.class.getResource("/kore50-nif-short.ttl").toURI());
        NifDataset testset3 = new NifDataset("test", file);

        NifDataset testset4 = new NifDataset("test", file);
        testset4.getDocuments().get(0).setMarkings(new ArrayList<>());
        testset4.getDocuments().get(1).setMarkings(new ArrayList<>());

        return Arrays.asList(new Object[][] {
                {NifDatasetTest.getTestDataset(), "27.5", "1926.0", "27.5", "1926.0"},
                {testset1, "0.0", "0.0", "0.0", "0.0"},
                {testset2, "0.0", "0.0", "0.0", "0.0"},
                {testset3, "28.166666666666668", "440.8333333333333", "28.166666666666668", "1322.5"},
                {testset4, "9.166666666666666", "642.0", "27.5", "1926.0"}
        });
    }

    private NifDataset dataset;

    private String expectedMacroEntityAmb;

    private String expectedMacroSfAmb;

    private String expectedMicroEntityAmb;

    private String expectedMicroSfAmb;

    public AmbiguityTest(NifDataset dataset, String expectedMacroEntityAmb, String expectedMacroSfAmb,
                         String expectedMicroEntityAmb, String expectedMicroSfAmb) {
        this.dataset = dataset;
        this.expectedMacroEntityAmb = expectedMacroEntityAmb;
        this.expectedMacroSfAmb = expectedMacroSfAmb;
        this.expectedMicroEntityAmb = expectedMicroEntityAmb;
        this.expectedMicroSfAmb = expectedMicroSfAmb;
    }

    @Test
    public void calculate() throws Exception {
        dataset = ambiguity.calculate(dataset);
        dataset.write(System.out);
        Assert.assertThat(dataset.getMetaInformations().get(ExtendedNif.macroAmbiguityEntities), is(expectedMacroEntityAmb));
        Assert.assertThat(dataset.getMetaInformations().get(ExtendedNif.macroAmbiguitySurfaceForms), is(expectedMacroSfAmb));
        Assert.assertThat(dataset.getMetaInformations().get(ExtendedNif.microAmbiguityEntities), is(expectedMicroEntityAmb));
        Assert.assertThat(dataset.getMetaInformations().get(ExtendedNif.microAmbiguitySurfaceForms), is(expectedMicroSfAmb));
    }
}