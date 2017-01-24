package org.santifa.hfts.core.metric;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.santifa.hfts.core.utils.AmbiguityDictionary;
import org.santifa.hfts.core.utils.Dictionary;
import org.santifa.hfts.core.utils.PopularityDictionary;

/**
 * Run the tests for all provided metrics.
 *
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({NotAnnotatedTest.class, DensityTest.class, CategoryAssignorTest.class,
    PopularityAssignorTest.class, MaxRecallTest.class, AmbiguityTest.class,
    DiversityTest.class})
public class MetricTests {

    static Dictionary<Double> hits = PopularityDictionary.getHitsConnector();
    static Dictionary<Double> pagerank = PopularityDictionary.getPageRankConnector();

    static Dictionary<Integer> entities = AmbiguityDictionary.getDefaultEntityConnector();
    static Dictionary<Integer> surfaceForms = AmbiguityDictionary.getDefaultSFConnector();

}
