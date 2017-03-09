package org.santifa.hfts.core.metric;

import org.junit.Test;
import org.santifa.hfts.core.HftsDataset;
import org.santifa.hfts.core.HftsDatasetTest;
import org.santifa.hfts.core.nif.HftsOnt;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * Created by ratzeputz on 12.01.17.
 */
public class PopularityAssignorTest {

    private static PopularityAssignor hits = new PopularityAssignor(MetricTests.hits, HftsOnt.hits);

    private static PopularityAssignor pagerank = new PopularityAssignor(MetricTests.pagerank, HftsOnt.pagerank);

    @Test
    public void testPageRank() throws URISyntaxException {
        HftsDataset testdata = HftsDatasetTest.getTestDataset();
        testdata = pagerank.calculate(testdata);
        testdata.write(System.out);
    }


    @Test
    public void testPageRankBig() throws URISyntaxException, IOException {
        HftsDataset testdata = new HftsDataset("test", Paths.get(this.getClass().getResource("/kore50-nif-short.ttl").toURI()));
        testdata = pagerank.calculate(testdata);
        testdata.write(System.out);
    }

    @Test
    public void testHits() throws URISyntaxException {
        HftsDataset testdata = HftsDatasetTest.getTestDataset();
        testdata = hits.calculate(testdata);
        testdata.write(System.out);
    }

    @Test
    public void testHitsBig() throws URISyntaxException, IOException {
        HftsDataset testdata = new HftsDataset("test", Paths.get(this.getClass().getResource("/kore50-nif-short.ttl").toURI()));
        testdata = hits.calculate(testdata);
        testdata.write(System.out);
    }
}