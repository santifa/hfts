package org.santifa.htfs.core.utils;


import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Created by ratzeputz on 30.12.16.
 */
public class DictionaryConnector {

    private HashMap<String, Integer> entityMap = new HashMap<>();

    private HashMap<String, Integer> surfaceformMap = new HashMap<>();

    public DictionaryConnector(Path entityFile, Path surfaceFormFile) throws IOException {
        readAmbiguityFile(entityFile, entityMap);
        readAmbiguityFile(surfaceFormFile, surfaceformMap);
    }

    public static DictionaryConnector getDefaultConnector() {
        try {
            return new DictionaryConnector(Paths.get(DictionaryConnector.class.getClass().getResource("/filter/ambiguity_e").toURI()),
                    Paths.get(DictionaryConnector.class.getClass().getResource("/filter/ambiguity_sf").toURI()));
        } catch (IOException | URISyntaxException e) {
            Logger.error("Failed to load internal entity file {} and surface form file {} with {}",
                    "/filter/ambiguity_e", "/filter/ambiguity_sf", e);
        }
        return null;
    }

    private void readAmbiguityFile(Path file, HashMap<String, Integer> map) throws IOException {
        BufferedReader reader = Files.newBufferedReader(file);
        String line;

        while ((line = reader.readLine()) != null) {
            // a line has as first part the ambiguity counter and as second the entity name with < >
            String[] split = StringUtils.split(line, " ");
            Integer ambiguity = Integer.decode(split[0]);
            String entity = StringUtils.remove(split[1], "<");
            entity = StringUtils.remove(entity, ">");
            map.put(entity, ambiguity);
        }
    }

    public HashMap<String, Integer> getEntityMap() {
        return entityMap;
    }

    public HashMap<String, Integer> getSurfaceformMap() {
        return surfaceformMap;
    }
}
