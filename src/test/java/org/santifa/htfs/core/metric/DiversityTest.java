package org.santifa.htfs.core.metric;

import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.santifa.htfs.core.NifDataset;
import org.santifa.htfs.core.NifDatasetTest;

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

    static Diversity diversity = Diversity.getDefaultDiversity();

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException, IOException {
        NifDataset testset1 = NifDatasetTest.getTestDataset();
        testset1.changeDocuments().get(0).setMarkings(new ArrayList<>());

        NifDataset testset2 = NifDatasetTest.getTestDataset();
        testset2.changeDocuments().get(0).setMarkings(new ArrayList<>());
        testset2.changeDocuments().add(new DocumentImpl("", "", new ArrayList<Marking>()));

        Path file = Paths.get(NotAnnotated.class.getResource("/kore50-nif-short.ttl").toURI());
        NifDataset testset3 = new NifDataset("test", file);

        NifDataset testset4 = new NifDataset("test", file);
        testset4.changeDocuments().get(0).setMarkings(new ArrayList<>());
        testset4.changeDocuments().get(1).setMarkings(new ArrayList<>());

        return Arrays.asList(new Object[][] {
                {NifDatasetTest.getTestDataset(), "0.03787878787878788", "9.738979176990524E-4"},
                {testset1, "0.0", "0.0"},
                {testset2, "0.0", "0.0"},
                {testset3, "0.06581439393939394", "0.007760031877746056"},
                {testset4, "0.03787878787878788", "9.738979176990524E-4"}
        });
    }

    private NifDataset dataset;

    private String expectedEntitiesDiversity;

    private String expectedSfDiversity;

    public DiversityTest(NifDataset dataset, String expectedEntitiesDiversity, String expectedSfDiversity) {
        this.dataset = dataset;
        this.expectedEntitiesDiversity = expectedEntitiesDiversity;
        this.expectedSfDiversity = expectedSfDiversity;
    }

    @Test
    public void calculate() throws Exception {
        dataset = diversity.calculate(dataset);
        Assert.assertThat(dataset.getMetaInformations().get(Diversity.macroDiversityE), is(expectedEntitiesDiversity));
        Assert.assertThat(dataset.getMetaInformations().get(Diversity.macroDiversitySF), is(expectedSfDiversity));
    }

}