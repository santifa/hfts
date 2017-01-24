package org.santifa.hfts.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Loads a n-triple file where the
 * subject is a resource and the literal a popularity scoring.
 *
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class PopularityDictionary extends Dictionary<Double> {


    /**
     * Instantiates a new dictionary.
     * Provide a file where the first entry is a URI surrounded by
     * '<' and '>'  and the third is a score surrounded by '"' and '"^^'
     *
     * @param file - dictionary path
     */
    public PopularityDictionary(Path file) {
        super(file);
    }

    @Override
    protected void readFile(Path file, Entry[] values) throws IOException {
        Logger.debug("Loading file {}", file);
        BufferedReader reader = Files.newBufferedReader(file);
        String s;
        int next = 0;

        while ((s = reader.readLine()) != null) {
            String uri = StringUtils.substringBetween(s, "<", "> ").toLowerCase();
            String popularity = StringUtils.substringBetween(s, "> \"", "\"^^");
            values[next] = new Entry<>(hash(uri), Double.parseDouble(popularity));
            next++;
        }
    }

    /**
     * @return the default dictionary for the PageRank
     */
    public static Dictionary<Double> getPageRankConnector() {
        return new PopularityDictionary(Paths.get("../data/pagerank_scores_en_2015.ttl"));
     }

    /**
     * @return the default dictionary for the HIT score
     */
    public static Dictionary<Double> getHitsConnector() {
        return new PopularityDictionary(Paths.get("../data/hits_scores_en_2015.ttl"));
    }

}
