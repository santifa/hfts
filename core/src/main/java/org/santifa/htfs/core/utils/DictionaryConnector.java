package org.santifa.htfs.core.utils;


import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * This class loads a simple text file containing occurrences
 * and a word. One file represents a mapping between the number
 * of surface forms used for one entity (known as entity Mapping)
 * and the other a mapping between the amount of possible entities
 * denoted by an surface form (known as Surface Form Mapping).
 * <br/>
 * This object can use a huge amount of memory, since its stupidly loading the file
 * into a huge {@link HashMap}.
 * <p>
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class DictionaryConnector {

    private HashMap<String, Integer> entityMappping = new HashMap<>();

    private HashMap<String, Integer> sfMapping = new HashMap<>();

    /**
     * Instantiates a new dictionary connector.
     * Provide two files which first column is a number
     * and the second one containing a word surrounded by '<>'.
     *
     * @param entityFile      the entity mapping file
     * @param surfaceFormFile the surface form mapping file
     * @throws IOException the io exception
     */
    public DictionaryConnector(Path entityFile, Path surfaceFormFile) throws IOException {
        readAmbiguityFile(entityFile, entityMappping);
        readAmbiguityFile(surfaceFormFile, sfMapping);
    }

    /**
     * Gets a new {@link DictionaryConnector} initialized
     * with the internal files.<br/>
     * Keep in mind that this loads a huge amount of data into the
     * memory. Use this object wisely.
     *
     * @return the default connector
     */
    public static DictionaryConnector getDefaultConnector() {
        try {
            return new DictionaryConnector(Paths.get("..","data", "ambiguity_e"), Paths.get("..", "data", "ambiguity_sf"));
        } catch (IOException e) {
            Logger.error("Failed to load internal entity file {} and surface form file {} with {}",
                    "../data/ambiguity_e", "../data/ambiguity_sf", e);
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

    /**
     * Gets the {@link HashMap} containing the Relation between
     * one entity and multiple surface forms.
     *
     * @return the entity map
     */
    public HashMap<String, Integer> getEntityMappping() {
        return entityMappping;
    }

    /**
     * Gets {@link HashMap} containing the relation between one
     * surface form and multiple entities.
     *
     * @return the surface form map
     */
    public HashMap<String, Integer> getSfMapping() {
        return sfMapping;
    }
}
