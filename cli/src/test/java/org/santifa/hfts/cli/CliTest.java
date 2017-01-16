package org.santifa.hfts.cli;

import org.junit.Test;

/**
 * Created by ratzeputz on 16.01.17.
 */
public class CliTest {

    @Test
    public void testVerbose() throws Exception {
        String[] args = {"-v", "../core/src/test/resources/kore50-nif-short.ttl"};
        Cli.main(args);
    }

}