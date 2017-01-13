package org.santifa.htfs.core.metric;

import org.junit.Test;
import org.santifa.htfs.core.NifDataset;
import org.santifa.htfs.core.NifDatasetTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * Created by ratzeputz on 12.01.17.
 */
public class PopularityAssignorTest {

    @Test
    public void testPageRank() throws URISyntaxException {
        NifDataset testdata = NifDatasetTest.getTestDataset();
        PopularityAssignor pagerank = new PopularityAssignor(Paths.get("../data/pagerank_scores_en_2015.ttl"), PopularityAssignor.pagerank);
        testdata = pagerank.calculate(testdata);
    }


    @Test
    public void testPageRankBig() throws URISyntaxException, IOException {
        NifDataset testdata = new NifDataset("test", Paths.get(this.getClass().getResource("/kore50-nif-short.ttl").toURI()));
        PopularityAssignor pagerank = new PopularityAssignor(Paths.get("../data/pagerank_scores_en_2015.ttl"), PopularityAssignor.pagerank);
        testdata = pagerank.calculate(testdata);
    }

    @Test
    public void testHits() throws URISyntaxException {
        NifDataset testdata = NifDatasetTest.getTestDataset();
        PopularityAssignor hits = new PopularityAssignor(Paths.get("../data/hits_scores_en_2015.ttl"), PopularityAssignor.hits);
        testdata = hits.calculate(testdata);
    }

    @Test
    public void testHitsBig() throws URISyntaxException, IOException {
        NifDataset testdata = new NifDataset("test", Paths.get(this.getClass().getResource("/kore50-nif-short.ttl").toURI()));
        PopularityAssignor hits = new PopularityAssignor(Paths.get("../data/hits_scores_en_2015.ttl"), PopularityAssignor.hits);
        testdata = hits.calculate(testdata);
    }
}