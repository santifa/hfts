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
public class NotAnnotatedTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException, IOException {
        NifDataset testset1 = NifDatasetTest.getTestDataset();
        testset1.getDocuments().get(0).setMarkings(new ArrayList<>());

        NifDataset testset2 = NifDatasetTest.getTestDataset();
        testset2.getDocuments().get(0).setMarkings(new ArrayList<>());
        testset2.getDocuments().add(new DocumentImpl("", "", new ArrayList<Marking>()));

        Path file = Paths.get(NotAnnotated.class.getResource("/kore50-nif-short.ttl").toURI());
        NifDataset testset3 = new NifDataset("test", file);

        NifDataset testset4 = new NifDataset("test", file);
        testset4.getDocuments().get(0).setMarkings(new ArrayList<>());
        testset4.getDocuments().get(1).setMarkings(new ArrayList<>());

        return Arrays.asList(new Object[][] {
                {NifDatasetTest.getTestDataset(), 0.0},
                {testset1, 1.0},
                {testset2, 1.0},
                {testset3, 0.0},
                {testset4, 0.6666666666666666}
        });
    }

    private NifDataset dataset;

    private double expectation;

    public NotAnnotatedTest(NifDataset dataset, double expectation) {
        this.dataset = dataset;
        this.expectation = expectation;
    }

    @Test
    public void testNotAnnotatedMetric() {
        Metric metric = new NotAnnotated();
        dataset = metric.calculate(dataset);
        Assert.assertThat(dataset.getNotAnnotatedDocs(), is(expectation));
    }
}