package org.santifa.hfts.core;

import org.junit.Assert;
import org.junit.Test;
import org.santifa.hfts.core.nif.MetaDocument;
import org.santifa.hfts.core.nif.MetaNamedEntity;

import java.util.List;

import static org.hamcrest.core.Is.is;

/**
 * Created by Henrik Jürges (jürges.henrik@gmail.com)
 */
public class SameAsRetrieverTest {

    @Test
    public void retrieve() throws Exception {
        HftsDataset testDataset = HftsDatasetTest.getTestDataset();
        SameAsRetriever retriever = new SameAsRetriever();
        retriever.retrieve(testDataset);

        List<MetaDocument> documents = testDataset.getDocuments();
        Assert.assertThat(documents.size(), is(1));
        Assert.assertThat(testDataset.getMarkings().size(), is(2));

        List<MetaNamedEntity> markings = testDataset.getMarkings();
        Assert.assertThat(markings.get(0).getUris().size(), is(111));
        Assert.assertThat(markings.get(1).getUris().size(), is(195));
    }

}