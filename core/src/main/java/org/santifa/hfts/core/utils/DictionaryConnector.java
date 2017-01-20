package org.santifa.hfts.core.utils;


import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    protected List<String> key = new ArrayList<>();

    protected List<String> value = new ArrayList<>();

    //protected HashMap<String, String> mapping = new HashMap<>();

    private Path file;

    private int timeToLive;

    /**
     * Instantiates a new lazy dictionary connector.
     * Provide two files which first column is a number
     * and the second one containing a word surrounded by '<>'.
     *
     * @throws IOException the io exception
     */
    public DictionaryConnector(Path file, int timeToLive) {
        try {
            readFile(file, key, value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.timeToLive = timeToLive;
    }

    protected void readFile(Path file, List<String> key, List<String> value) throws IOException {
        Logger.debug("Loading file {}", file);
        BufferedReader reader = Files.newBufferedReader(file);
        String line;

        while ((line = reader.readLine()) != null) {
            // a line has as first part the ambiguity counter and as second the entity name with < >
            String[] split = StringUtils.split(line, " ");
            value.add(split[0]);
            String entity = StringUtils.remove(split[1], "<");
            entity = StringUtils.remove(entity, ">");
            key.add(entity.toLowerCase());
        }
    }



    /**
     * Gets the {@link HashMap} containing the Relation between
     * one entity and multiple surface forms.
     *
     * @return the entity map
     */
 /*   public HashMap<String, String> getMapping() throws IOException {
        if (key.isEmpty()) {
            readFile(file, key, value);
        }

        return mapping;
    }
*/

    public boolean contains(String e) {
        return key.contains(e);
    }

    public String get(String e) {
        int idx = key.indexOf(e);
        return value.get(idx);
    }

    public void flush() {
        this.timeToLive--;
        if (timeToLive <= 0) {
            Logger.debug("Flushing Dictionary...");
            key.clear();
            value.clear();
        }
    }

    /**
     * Gets a new {@link DictionaryConnector} initialized
     * with the internal files.<br/>
     * Keep in mind that this loads a huge amount of data into the
     * memory. Use this object wisely.
     *
     * @return the default connector
     */
    public static DictionaryConnector getDefaultEntityConnector(int timeToLive) {
        return new DictionaryConnector(Paths.get("..","data", "ambiguity_e"), timeToLive);
    }

    public static DictionaryConnector getDefaultSFConnector(int timeToLive) {
        return new DictionaryConnector(Paths.get("..", "data", "ambiguity_sf"), timeToLive);
    }
}
