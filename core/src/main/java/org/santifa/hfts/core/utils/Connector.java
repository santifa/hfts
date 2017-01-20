package org.santifa.hfts.core.utils;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ratzeputz on 19.01.17.
 */
public interface Connector {

    void readFile();

    /**
     * Gets the {@link HashMap} containing the dictionary which
     * is mostly a relation between an entity and something other.
     *
     * @return the entity map
     */
    HashMap<String, String> getMapping() throws IOException;

    default void flush(HashMap<String, String> mapping) {
        mapping.clear();
    }
}
