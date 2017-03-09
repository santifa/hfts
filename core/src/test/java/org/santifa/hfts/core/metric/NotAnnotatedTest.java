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
public class NotAnnotatedTest {

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
                {HftsDatasetTest.getTestDataset(), "0.0"},
                {testset1, "1.0"},
                {testset2, "1.0"},
                {testset3, "0.0"},
                {testset4, "0.6666666666666666"}
        });
    }

    private HftsDataset dataset;

    private String expectation;

    public NotAnnotatedTest(HftsDataset dataset, String expectation) {
        this.dataset = dataset;
        this.expectation = expectation;
    }

    @Test
    public void testNotAnnotatedMetric() {
        Metric metric = new NotAnnotated();
        dataset = metric.calculate(dataset);
        Assert.assertThat(dataset.getMetaInformations().get(HftsOnt.notAnnotatedProperty), is(expectation));
    }
}