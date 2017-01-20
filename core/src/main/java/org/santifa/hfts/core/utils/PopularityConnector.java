package org.santifa.hfts.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

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
     * Instantiates a new lazy popularity connector.
     * Provide two files which first column is a number
     * and the second one containing a word surrounded by '<>'.
     *
     */
    public PopularityConnector(Path mappingFile) {
        this.p = mappingFile;
    }

    public void flush() {
        Logger.debug("Flushing popularity dictionary...");
        mapping.clear();
    }

    public void readFile(Path file) throws IOException {
        Logger.debug("Loading file {}", file);
        BufferedReader reader = Files.newBufferedReader(file);
        String s;

        while ((s = reader.readLine()) != null) {
            String uri = StringUtils.substringBetween(s, "<", "> ");
            String popularity = StringUtils.substringBetween(s, "> \"", "\"^^");
            this.mapping.put(uri, popularity);
        }
    }

    public HashMap<String, String> getMapping() throws IOException {
        if (!loaded) {
            readFile(p);
            loaded = true;
        }
        return mapping;
    }
}
