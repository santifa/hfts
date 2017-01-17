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
public class DensityTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException, IOException {
        NifDataset testset1 = NifDatasetTest.getTestDataset();
        testset1.getDocuments().get(0).setMarkings(new ArrayList<>());
        testset1.reload();

        NifDataset testset2 = NifDatasetTest.getTestDataset();
        testset2.getDocuments().get(0).setMarkings(new ArrayList<>());
        testset2.getDocuments().add(new MetaDocument("", "", new ArrayList<>()));
        testset2.reload();

        Path file = Paths.get(NotAnnotated.class.getResource("/kore50-nif-short.ttl").toURI());
        NifDataset testset3 = new NifDataset("test", file);
        testset3.reload();

        NifDataset testset4 = new NifDataset("test", file);
        testset4.getDocuments().get(0).setMarkings(new ArrayList<>());
        testset4.getDocuments().get(1).setMarkings(new ArrayList<>());
        testset4.reload();

        return Arrays.asList(new Object[][] {
                {NifDatasetTest.getTestDataset(), "0.16666666666666666", "0.16666666666666666"},
                {testset1, "0.0", "0.0"},
                {testset2, "0.0", "0.0"},
                {testset3, "0.19444444444444442", "0.1875"},
                {testset4, "0.05555555555555555", "0.0625"}
        });
    }

    private NifDataset dataset;

    private String expectationMicro;

    private String expectationMacro;

    public DensityTest(NifDataset dataset, String expectationMacro, String expectationMicro) {
        this.dataset = dataset;
        this.expectationMacro = expectationMacro;
        this.expectationMicro = expectationMicro;
    }

    @Test
    public void testDensityCalculation() {
        Metric density = new Density();
        dataset = density.calculate(dataset);
        dataset.write(System.out);
        Assert.assertThat(dataset.getMetaInformations().get(ExtendedNif.macroDensity), is(expectationMacro));
        Assert.assertThat(dataset.getMetaInformations().get(ExtendedNif.microDensity), is(expectationMicro));
    }
}