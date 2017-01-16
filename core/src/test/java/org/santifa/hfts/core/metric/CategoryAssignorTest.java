package org.santifa.hfts.core.metric;

import org.junit.Test;
import org.santifa.hfts.core.NifDataset;
import org.santifa.hfts.core.NifDatasetTest;

/**
 * Created by ratzeputz on 09.01.17.
 */
public class CategoryAssignorTest {

    @Test
    public void calculate() throws Exception {
        Metric typer = CategoryAssignor.getDefaultAssignor();
        NifDataset dataset = NifDatasetTest.getTestDataset();

        dataset = typer.calculate(dataset);
        System.out.println(dataset);
    }

}