package org.santifa.hfts.core.metric;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.santifa.hfts.core.HftsDataset;
import org.santifa.hfts.core.HftsDatasetTest;
import org.santifa.hfts.core.nif.HftsOnt;
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

    private static Ambiguity ambiguity = new Ambiguity(MetricTests.entities, MetricTests.surfaceForms);

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException, IOException {
        HftsDataset testset1 = HftsDatasetTest.getTestDataset();
        testset1.getDocuments().get(0).setMarkings(new ArrayList<>());
        testset1.reload();

        HftsDataset testset2 = HftsDatasetTest.getTestDataset();
        testset2.getDocuments().get(0).setMarkings(new ArrayList<>());
        testset2.getDocuments().add(new MetaDocument("", "", new ArrayList<>()));
        testset2.reload();

        Path file = Paths.get(NotAnnotated.class.getResource("/kore50-nif-short.ttl").toURI());
        HftsDataset testset3 = new HftsDataset("test", file);

        HftsDataset testset4 = new HftsDataset("test", file);
        testset4.getDocuments().get(0).setMarkings(new ArrayList<>());
        testset4.getDocuments().get(1).setMarkings(new ArrayList<>());
        testset4.reload();

        return Arrays.asList(new Object[][] {
                {HftsDatasetTest.getTestDataset(), "27.5", "1926.0", "27.5", "1926.0"},
                {testset1, "0.0", "0.0", "0.0", "0.0"},
                {testset2, "0.0", "0.0", "0.0", "0.0"},
                {testset3, "28.166666666666668", "1291.8333333333333", "28.166666666666668", "1291.8333333333333"},
                {testset4, "9.166666666666666", "642.0", "27.5", "1926.0"}
        });
    }

    private HftsDataset dataset;

    private String expectedMacroEntityAmb;

    private String expectedMacroSfAmb;

    private String expectedMicroEntityAmb;

    private String expectedMicroSfAmb;

    public AmbiguityTest(HftsDataset dataset, String expectedMacroEntityAmb, String expectedMacroSfAmb,
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
        Assert.assertThat(dataset.getMetaInformations().get(HftsOnt.macroAmbiguityEntities), is(expectedMacroEntityAmb));
        Assert.assertThat(dataset.getMetaInformations().get(HftsOnt.macroAmbiguitySurfaceForms), is(expectedMacroSfAmb));
        Assert.assertThat(dataset.getMetaInformations().get(HftsOnt.microAmbiguityEntities), is(expectedMicroEntityAmb));
        Assert.assertThat(dataset.getMetaInformations().get(HftsOnt.microAmbiguitySurfaceForms), is(expectedMicroSfAmb));
    }
}