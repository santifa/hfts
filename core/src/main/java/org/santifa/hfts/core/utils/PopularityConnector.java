package org.santifa.hfts.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by ratzeputz on 17.01.17.
 */
public class PopularityConnector extends DictionaryConnector {


    /**
     * Instantiates a new lazy dictionary connector.
     * Provide two files which first column is a number
     * and the second one containing a word surrounded by '<>'.
     *
     * @param file
     * @param timeToLive
     * @throws IOException the io exception
     */
    public PopularityConnector(Path file, int timeToLive) {
        super(file, timeToLive);
    }

    @Override
    protected void readFile(Path file, Entry[] values) throws IOException {
        //protected void readFile(Path file, List<Entry> key) throws IOException {
        Logger.debug("Loading file {}", file);
        BufferedReader reader = Files.newBufferedReader(file);
        String s;
        int next = 0;

        while ((s = reader.readLine()) != null) {
            String uri = StringUtils.substringBetween(s, "<", "> ");
            String popularity = StringUtils.substringBetween(s, "> \"", "\"^^");
            //map.put(uri, popularity);
            //key.add(new Entry(hash(uri), popularity));
            //value.add(popularity);
            //keys[next] = hash(uri.toLowerCase());
            values[next] = new Entry(hash(uri.toLowerCase()), popularity);
            next++;
        }
    }

    public static PopularityConnector getPageRankConnector(int timeToLive) {
        return new PopularityConnector(Paths.get("../data/pagerank_scores_en_2015.ttl"), timeToLive);
     }

    public static PopularityConnector getHitsConnector(int timeToLive) {
        return new PopularityConnector(Paths.get("../data/hits_scores_en_2015.ttl"), timeToLive);
    }

}
