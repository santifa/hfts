package org.santifa.hfts.core.utils;


import org.pmw.tinylog.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * This class loads a simple text file containing a simple relation.
 * <br/>
 * The relations are simply stored in an array.
 * The keys are hashed and sorted for faster access with binary search.
 * The dictionary values shall not be mutated.
 * <p>
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public abstract class Dictionary<T> {

    protected Entry<T>[] values;

    /**
     * Instantiates a new lazy dictionary connector.
     * Provide two files which first column is a number
     * and the second one containing a word surrounded by '<>'.
     *
     * @param file the file
     */
    public Dictionary(Path file) {
        try {
            initArrays(file);
            readFile(file, values);
            Arrays.parallelSort(values, (o1, o2) -> {
                if (o1.hash == o2.hash) {
                    return 0;
                } else if (o1.hash < o2.hash) {
                    return -1;
                } else {
                    return 1;
                }
            });
        } catch (IOException e) {
            Logger.error("Failed to load the dictionary from {}.\n Reason {}", file, e);
        }
    }

    @SuppressWarnings(value = "unchecked")
    private void initArrays(Path file) throws IOException {
        BufferedReader reader = Files.newBufferedReader(file);

        int counter = 0;
        while (reader.readLine() != null) {
            counter++;
        }

        this.values = (Entry<T>[]) Array.newInstance(Entry.class, counter);
       // this.values = new Entry<T>[counter];
    }

    /**
     * Implement the real line reading process.
     *
     * @param file   the file
     * @param values the array of {@link Entry}s
     * @throws IOException the io exception
     */
    protected abstract void readFile(Path file, Entry[] values) throws IOException;

    /**
     * Binary search for a specific element.
     *
     * @param e the search key
     * @return the position if found or -1 otherwise
     */
    public int contains(String e) {
        int key = hash(e);

        /* adjusted binary search from Arrays class */
        int low = 0;
        int high = values.length - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = values[mid].hash;

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else

                return mid; // key found
        }
        return -1;  // key not found.
    }

    /**
     * Get the value to the given key.
     * Get key position with {@link Dictionary#contains(String)}
     *
     * @param idx the index position
     * @return the assigned value
     */
    public T get(int idx) {
        return values[idx].value;
    }

    /**
     * Hash a key object and store it as
     * an int.
     *
     * @param key the key
     * @return the hash
     */
    protected int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 10);
    }

    protected class Entry<T> {

        private int hash;

        private T value;

        /**
         * Instantiates a new dictionary entry.
         *
         * @param hash  the hash key
         * @param value the related value
         */
        Entry(int hash, T value) {
            this.hash = hash;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Entry<T> entry = (Entry<T>) o;
            return hash == entry.hash;
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
