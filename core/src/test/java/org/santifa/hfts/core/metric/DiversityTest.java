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
public class DiversityTest {

    private static Diversity diversity = new Diversity(MetricTests.entities, MetricTests.surfaceForms);

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException, IOException {
        HftsDataset testset1 = HftsDatasetTest.getTestDataset();
        testset1.getDocuments().get(0).setMarkings(new ArrayList<>());

        HftsDataset testset2 = HftsDatasetTest.getTestDataset();
        testset2.getDocuments().get(0).setMarkings(new ArrayList<>());
        testset2.getDocuments().add(new MetaDocument("", "", new ArrayList<>()));

        Path file = Paths.get(NotAnnotated.class.getResource("/kore50-nif-short.ttl").toURI());
        HftsDataset testset3 = new HftsDataset("test", file);

        HftsDataset testset4 = new HftsDataset("test", file);
        testset4.getDocuments().get(0).setMarkings(new ArrayList<>());
        testset4.getDocuments().get(1).setMarkings(new ArrayList<>());

        return Arrays.asList(new Object[][] {
                {HftsDatasetTest.getTestDataset(), "0.03787878787878788", "9.738979176990524E-4"},
                {testset1, "0.0", "0.0"},
                {testset2, "0.0", "0.0"},
                {testset3, "0.04387626262626263", "0.17158838562939582"},
                {testset4, "0.012626262626262626", "3.246326392330175E-4"}
        });
    }

    private HftsDataset dataset;

    private String expectedEntitiesDiversity;

    private String expectedSfDiversity;

    public DiversityTest(HftsDataset dataset, String expectedDiversityEntities, String expectedDiversitySurfaceForms) {
        this.dataset = dataset;
        this.expectedEntitiesDiversity = expectedDiversityEntities;
        this.expectedSfDiversity = expectedDiversitySurfaceForms;
    }

    @Test
    public void calculate() throws Exception {
        dataset = diversity.calculate(dataset);
        dataset.write(System.out);
        Assert.assertThat(dataset.getMetaInformations().get(HftsOnt.diversityEntities), is(expectedEntitiesDiversity));
        Assert.assertThat(dataset.getMetaInformations().get(HftsOnt.diversitySurfaceForms), is(expectedSfDiversity));
    }

}