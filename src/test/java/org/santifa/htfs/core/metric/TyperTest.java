package org.santifa.htfs.core.metric;

import org.junit.Test;
import org.santifa.htfs.core.NifDataset;
import org.santifa.htfs.core.NifDatasetTest;

/**
 * Created by ratzeputz on 09.01.17.
 */
public class TyperTest {

    @Test
    public void calculate() throws Exception {
        Metric typer = Typer.getDefaultTyper();
        NifDataset dataset = NifDatasetTest.getTestDataset();

        dataset = typer.calculate(dataset);
        System.out.println(dataset);
    }

}