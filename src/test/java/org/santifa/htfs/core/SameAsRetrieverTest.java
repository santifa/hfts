package org.santifa.htfs.core;

import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Meaning;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;

/**
 * Created by ratzeputz on 28.12.16.
 */
public class SameAsRetrieverTest {

    @Test
    public void retrieve() throws Exception {
        NifDataset testDataset = NifDatasetTest.getTestDataset();
        SameAsRetriever retriever = new SameAsRetriever();
        retriever.retrieve(testDataset);

        List<Document> documents = testDataset.getDocuments();
        Assert.assertThat(documents.size(), is(1));
        Assert.assertThat(testDataset.getMarkings().size(), is(2));

        List<Marking> markings = testDataset.getMarkings();
        Assert.assertThat(((Meaning) markings.get(0)).getUris().size(), is(111));
        Assert.assertThat(((Meaning) markings.get(1)).getUris().size(), is(195));
    }

}