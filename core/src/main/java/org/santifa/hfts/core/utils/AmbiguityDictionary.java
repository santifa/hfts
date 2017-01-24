package org.santifa.hfts.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * One file represents a mapping between the number
 * of surface forms used for one entity (known as entity Mapping)
 * and the other a mapping between the amount of possible entities
 * denoted by an surface form (known as Surface Form Mapping).
 *
 * <p>
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class AmbiguityDictionary extends Dictionary<Integer> {

    /**
     * Instantiates a new dictionary connector.
     * Provide a files which first column is a number
     * and the second one containing a word surrounded by '<>'.
     *
     * @param file the dictionary path
     */
    public AmbiguityDictionary(Path file) {
        super(file);
    }

    public void readFile(Path file, Dictionary.Entry[] values) throws IOException {
        Logger.debug("Loading file {}", file);
        BufferedReader reader = Files.newBufferedReader(file);
        String line;
        int next = 0;

        while ((line = reader.readLine()) != null) {
            // a line has as first part the ambiguity counter and as second the entity name with < >
            String[] split = StringUtils.split(line, " ");
            String entity = StringUtils.substringBetween(split[1], "<", ">").toLowerCase();
            values[next] = new Entry<>(hash(entity), Integer.decode(split[0]));
            next++;
        }
    }

    /**
     * Gets the default dictionary for entity mention to surface forms mappings.
     *
     * @return the default entity mentions connector
     */
    public static Dictionary<Integer> getDefaultEntityConnector() {
        return new AmbiguityDictionary(Paths.get("..", "data", "ambiguity_e"));
    }

    /**
     * Gets default dictionary for surface form to entities mapping.
     *
     * @return the default surface form dictionary
     */
    public static Dictionary<Integer> getDefaultSFConnector() {
        return new AmbiguityDictionary(Paths.get("..", "data", "ambiguity_sf"));
    }
}