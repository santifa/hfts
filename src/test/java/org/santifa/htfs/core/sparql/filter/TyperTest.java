package org.santifa.htfs.core.sparql.filter;

import org.junit.Test;
import org.santifa.htfs.core.NifDataset;
import org.santifa.htfs.core.NifDatasetTest;
import org.santifa.htfs.core.metric.Typer;

/**
 * Created by ratzeputz on 30.12.16.
 */
public class TyperTest {

    @Test
    public void testAnnotateTypes() {
        NifDataset dataset = NifDatasetTest.getTestDataset();
        Typer typer = Typer.getDefaultTyper();
        typer.type(dataset, "type", "select distinct ?e ?t where { values ?e {##} . ?v rdf:type ?t . }");


    }

}