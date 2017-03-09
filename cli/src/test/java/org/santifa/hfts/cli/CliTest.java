package org.santifa.hfts.cli;

import org.junit.Test;

/**
 * Basic test.
 *
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class CliTest {

    @Test
    public void testVerbose() throws Exception {
        String[] args = {"-v", "../core/src/test/resources/kore50-nif-short.ttl"};
        Cli.main(args);
    }

}