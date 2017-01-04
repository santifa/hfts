package org.santifa.htfs.core;

import org.santifa.htfs.core.metric.Metric;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The hfts api provides a fluent interface for on the fly
 * enhancement of nif data sets. <br/>
 * Nif data sets can be loaded from file or string and
 * several metrics can be applied on them.<br/>
 * Afterwards the resulting nif data sets are returned and
 * can be serialised into turtle.
 *
 * Since metrics are provided from outside make sure, that
 * the individual classes have a long life since some of them
 * have high memory consumptions.
 *
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class HftsApi {

    private List<NifDataset> datasets = new ArrayList<>();

    private List<Metric> metrics = new ArrayList<>();

    public HftsApi() { }

    List<NifDataset> getDatasets() {
        return datasets;
    }

    public HftsApi withDataset(String name, Path p) throws IOException {
        datasets.add(new NifDataset(name, p));
        return this;
    }

    public HftsApi withDataset(String name, String nifContent) {
        datasets.add(new NifDataset(name, nifContent));
        return this;
    }

    public HftsApi withDataset(NifDataset dataset) {
        datasets.add(dataset);
        return this;
    }

    public HftsApi withMetric(Metric... m) {
        Collections.addAll(metrics, m);
        return this;
    }

    public List<NifDataset> run() {
        List<NifDataset> result = new ArrayList<>(datasets.size());
        for (NifDataset d : datasets) {
            for (Metric m : metrics) {
                d = m.calculate(d);
            }
            result.add(d);
        }

        return result;
    }
}
