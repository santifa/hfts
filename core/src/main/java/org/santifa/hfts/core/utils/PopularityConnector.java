package org.santifa.hfts.core.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * Created by ratzeputz on 17.01.17.
 */
public class PopularityConnector {

    private HashMap<String, String> mapping = new HashMap<>();

    private boolean loaded = false;

    private Path p;
    /**
     * Instantiates a new dictionary connector.
     * Provide two files which first column is a number
     * and the second one containing a word surrounded by '<>'.
     *
     */
    public PopularityConnector(Path mappingFile) {
        this.p = mappingFile;
        //readAmbiguityFile(mappingFile);
    }

    private void readAmbiguityFile(Path file) throws IOException {
        BufferedReader reader = Files.newBufferedReader(file);
        String s;

        while ((s = reader.readLine()) != null) {
            String uri = StringUtils.substringBetween(s, "<", "> ");
            String popularity = StringUtils.substringBetween(s, "> \"", "\"^^");
            this.mapping.put(uri, popularity);
        }
    }

    /**
     * Gets the {@link HashMap} containing the Relation between
     * one entity and multiple surface forms.
     *
     * @return the entity map
     */
    public HashMap<String, String> getMappping() throws IOException {
        if (!loaded) {
            readAmbiguityFile(p);
            loaded = true;
        }
        return mapping;
    }

    public void flush() {
        mapping.clear();
    }

}
