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
public class AmbiguityTest {

    static Ambiguity ambiguity = Ambiguity.getDefaultAmbiguity();

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
                {NifDatasetTest.getTestDataset(), 27.5, 1926.0},
                {testset1, 0.0, 0.0},
                {testset2, 0.0, 0.0},
                {testset3, 28.166666666666668, 1322.5},
                {testset4, 27.5, 1926.0}
        });
    }

    private NifDataset dataset;

    private double expectedEntityAmb;

    private double expectedSfAmb;

    public AmbiguityTest(NifDataset dataset, double expectedEntityAmb, double expectedSfAmb) {
        this.dataset = dataset;
        this.expectedEntityAmb = expectedEntityAmb;
        this.expectedSfAmb = expectedSfAmb;
    }

    @Test
    public void calculate() throws Exception {
        dataset = ambiguity.calculate(dataset);
        Assert.assertThat(dataset.getAverageMacroAmbiguityOfEntities(), is(expectedEntityAmb));
        Assert.assertThat(dataset.getAverageMacroAmbiguityOfSurfaceForms(), is(expectedSfAmb));
    }
}