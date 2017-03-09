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
 * Created by ratzeputz on 19.01.17.
 */
@RunWith(Parameterized.class)
public class MaxRecallTest {

    private static MaxRecall recall = new MaxRecall(MetricTests.surfaceForms);

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
        testset3.reload();

        HftsDataset testset4 = new HftsDataset("test", file);
        testset4.getDocuments().get(0).setMarkings(new ArrayList<>());
        testset4.getDocuments().get(1).setMarkings(new ArrayList<>());
        testset4.reload();

        return Arrays.asList(new Object[][] {
                {HftsDatasetTest.getTestDataset(), "1.0", "1.0"},
                {testset1, "0.0", "0.0"},
                {testset2, "0.0", "0.0"},
                {testset3, "1.0", "1.0"},
                {testset4, "0.3333333333333333", "1.0"}
        });
    }

    private HftsDataset dataset;

    private String expectationMicro;

    private String expectationMacro;

    public MaxRecallTest(HftsDataset dataset, String expectationMacro, String expectationMicro) {
        this.dataset = dataset;
        this.expectationMacro = expectationMacro;
        this.expectationMicro = expectationMicro;
    }

    @Test
    public void testMaxRecall() {
        dataset = recall.calculate(dataset);
        dataset.write(System.out);
        Assert.assertThat(dataset.getMetaInformations().get(HftsOnt.macroMaxRecall), is(expectationMacro));
        Assert.assertThat(dataset.getMetaInformations().get(HftsOnt.microMaxRecall), is(expectationMicro));

    }
}