package org.santifa.hfts.core.utils;


import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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

    //protected List<Entry> key = new ArrayList<>(500000);

    //protected int[] keys;

    protected Entry[] values;

    private Path file;

    private int timeToLive;

    private int pointer = 0;

    /**
     * Instantiates a new lazy dictionary connector.
     * Provide two files which first column is a number
     * and the second one containing a word surrounded by '<>'.
     *
     * @throws IOException the io exception
     */
    public DictionaryConnector(Path file, int timeToLive) {
        this.file = file;
        this.timeToLive = timeToLive;
        try {
            initArrays(file);
            readFile(file, values);
            Arrays.sort(values, (o1, o2) -> {
                if (o1.hash == o2.hash) {
                    return 0;
                } else if (o1.hash < o2.hash) {
                    return -1;
                } else {
                    return 1;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initArrays(Path file) throws IOException {
        //Logger.debug("Getting line count {}", file);
        BufferedReader reader = Files.newBufferedReader(file);

        int counter = 0;
        while (reader.readLine() != null) {
            counter++;
        }

        //Logger.debug("line count {}", counter);
        //this.keys = new int[counter];
        this.values = new Entry[counter];
    }

 //   protected void readFile(Path file, List<Entry> key) throws IOException {
    protected void readFile(Path file, Entry[] values) throws IOException {
        Logger.debug("Loading file {}", file);
        BufferedReader reader = Files.newBufferedReader(file);
        String line;
        int next = 0;

        while ((line = reader.readLine()) != null) {
            // a line has as first part the ambiguity counter and as second the entity name with < >
            String[] split = StringUtils.split(line, " ");
            //value.add(split[0]);
            String entity = StringUtils.remove(split[1], "<");
            entity = StringUtils.remove(entity, ">");
            //key.add(new Entry(hash(entity.toLowerCase()), split[0]));
            //keys[next] = hash(entity.toLowerCase());
            values[next] = new Entry(hash(entity.toLowerCase()), split[0]);
            next++;
        }
    }

  /*  public void present() {
        if (key.isEmpty()) {
            try {
                readFile(file, keys, values);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

    public int contains(String e) {
        int searchHash = hash(e);
        int ret = -1;
        //Logger.debug("Start pointer {}", pointer);
        /* prevent running out of bounds since array size 0..n-1 */
        if (pointer == values.length) {
            pointer--;
        }

        if (searchHash < values[pointer].hash) {
           // Logger.debug("downwards");
            /* search backwards from point */
            for (; pointer > 0; --pointer) {
                if (searchHash == values[pointer].hash) {
                    ret = pointer;
                    break;
                }
            }

        } else if (searchHash > values[pointer].hash) {
            /* search forwards */
            //Logger.debug("forwards");
            for (; pointer < values.length; ++pointer) {
                if (searchHash == values[pointer].hash) {
                    ret = pointer;
                    break;
                }
            }

        } else if (searchHash == values[pointer].hash) {
            /* we have the element directly */
            //Logger.debug("equals");
            ret = pointer;
        }

        //Logger.debug("End pointer {}", pointer);
        //present();
        return ret;//key.indexOf(new Entry(hash(e), null));
    }

    public String get(int idx) {
        //present();
        //int idx = key.indexOf(new Entry(hash(e), null));
        return values[idx].value;//key.get(idx).value;
    }

    public void flush() {
        //this.timeToLive--;
        if (timeToLive <= 0) {
            Logger.debug("Flushing Dictionary...");
            //key.clear();
            //value.clear();
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

    protected int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    protected class Entry {

        private int hash;

        private String value;

        public Entry(int hash, String value) {
            this.hash = hash;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Entry entry = (Entry) o;
            return hash == entry.hash;
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
