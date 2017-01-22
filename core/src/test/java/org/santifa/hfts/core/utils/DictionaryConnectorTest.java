package org.santifa.hfts.core.utils;

import org.junit.Test;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by ratzeputz on 21.01.17.
 */
public class DictionaryConnectorTest {

    static {
        Logger.getConfiguration().level(Level.DEBUG).activate();
    }

    @Test
    public void testDictionaryLoading() {
        Path f = Paths.get("../data/ambiguity_sf");
        DictionaryConnector conn = new DictionaryConnector(f, 1);
    }

}