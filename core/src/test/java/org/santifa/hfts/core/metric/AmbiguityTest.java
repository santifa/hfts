package org.santifa.hfts.core.metric;

import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.santifa.hfts.core.NifDataset;
import org.santifa.hfts.core.NifDatasetTest;

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

    private static Ambiguity ambiguity = Ambiguity.getDefaultAmbiguity();

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException, IOException {
        NifDataset testset1 = NifDatasetTest.getTestDataset();
        testset1.changeDocuments().get(0).setMarkings(new ArrayList<>());

        NifDataset testset2 = NifDatasetTest.getTestDataset();
        testset2.changeDocuments().get(0).setMarkings(new ArrayList<>());
        testset2.changeDocuments().add(new DocumentImpl("", "", new ArrayList<>()));

        Path file = Paths.get(NotAnnotated.class.getResource("/kore50-nif-short.ttl").toURI());
        NifDataset testset3 = new NifDataset("test", file);

        NifDataset testset4 = new NifDataset("test", file);
        testset4.changeDocuments().get(0).setMarkings(new ArrayList<>());
        testset4.changeDocuments().get(1).setMarkings(new ArrayList<>());

        return Arrays.asList(new Object[][] {
                {NifDatasetTest.getTestDataset(), "27.5", "1926.0", "27.5", "1926.0"},
                {testset1, "0.0", "0.0", "0.0", "0.0"},
                {testset2, "0.0", "0.0", "0.0", "0.0"},
                {testset3, "28.166666666666668", "1322.5", "28.166666666666668", "1322.5"},
                {testset4, "27.5", "1926.0", "9.166666666666666", "642.0"}
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
        Assert.assertThat(dataset.getMetaInformations().get(Ambiguity.macroAmbiguityE), is(expectedMacroEntityAmb));
        Assert.assertThat(dataset.getMetaInformations().get(Ambiguity.macroAmbiguitySF), is(expectedMacroSfAmb));
        Assert.assertThat(dataset.getMetaInformations().get(Ambiguity.microAmbiguityE), is(expectedMicroEntityAmb));
        Assert.assertThat(dataset.getMetaInformations().get(Ambiguity.microAmbiguitySF), is(expectedMicroSfAmb));
    }
}