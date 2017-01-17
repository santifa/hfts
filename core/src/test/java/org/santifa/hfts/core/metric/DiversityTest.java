package org.santifa.hfts.core.metric;

import org.aksw.gerbil.transfer.nif.Marking;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.santifa.hfts.core.NifDataset;
import org.santifa.hfts.core.NifDatasetTest;
import org.santifa.hfts.core.nif.ExtendedNif;
import org.santifa.hfts.core.nif.MetaDocument;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.core.Is.is;

/**
 * Created by ratzeputz on 30.12.16.
 */
@RunWith(Parameterized.class)
public class DiversityTest {

    static Diversity diversity = Diversity.getDefaultDiversity();

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws URISyntaxException, IOException {
        NifDataset testset1 = NifDatasetTest.getTestDataset();
        testset1.getDocuments().get(0).setMarkings(new ArrayList<>());

        NifDataset testset2 = NifDatasetTest.getTestDataset();
        testset2.getDocuments().get(0).setMarkings(new ArrayList<>());
        testset2.getDocuments().add(new MetaDocument("", "", new ArrayList<Marking>()));

        Path file = Paths.get(NotAnnotated.class.getResource("/kore50-nif-short.ttl").toURI());
        NifDataset testset3 = new NifDataset("test", file);

        NifDataset testset4 = new NifDataset("test", file);
        testset4.getDocuments().get(0).setMarkings(new ArrayList<>());
        testset4.getDocuments().get(1).setMarkings(new ArrayList<>());

        return Arrays.asList(new Object[][] {
                {NifDatasetTest.getTestDataset(), "0.03787878787878788", "0.26666666666666666"},
                {testset1, "0.0", "0.0"},
                {testset2, "0.0", "0.0"},
                {testset3, "0.04387626262626263", "0.005822619863630071"},
                {testset4, "0.03787878787878788", "0.26666666666666666"}
        });
    }

    private NifDataset dataset;

    private String expectedMacroEntitiesDiversity;

    private String expectedMacroSfDiversity;

    public DiversityTest(NifDataset dataset, String expectedMacroEntitiesDiversity, String expectedMacroSfDiversity) {
        this.dataset = dataset;
        this.expectedMacroEntitiesDiversity = expectedMacroEntitiesDiversity;
        this.expectedMacroSfDiversity = expectedMacroSfDiversity;
    }

    @Test
    public void calculate() throws Exception {
        dataset = diversity.calculate(dataset);
        dataset.write(System.out);
        Assert.assertThat(dataset.getMetaInformations().get(ExtendedNif.diversityEntities), is(expectedMacroEntitiesDiversity));
        Assert.assertThat(dataset.getMetaInformations().get(ExtendedNif.diversitySurfaceForms), is(expectedMacroSfDiversity));
    }

}