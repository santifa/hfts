package org.santifa.hfts.core;

import org.pmw.tinylog.Logger;
import org.santifa.hfts.core.metric.Metric;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The hfts api provides a fluent interface for on the fly
 * enhancement of nif data sets. <br/>
 * Nif data sets can be loaded from file or string and
 * several {@link Metric}s can be applied on them.<br/>
 * Afterwards the resulting nif data sets are returned and
 * can be serialised into turtle.
 * <p>
 * Since metrics are provided from outside make sure, that
 * the individual classes have a long life since some of them
 * have high memory consumptions.
 * <p>
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class HftsApi {

    private List<NifDataset> datasets = new ArrayList<>();

    private List<Metric> metrics = new ArrayList<>();

    private SameAsRetriever retriever = new SameAsRetriever();

    private boolean sameAs = false;

    private boolean microOnly = false;

    private boolean macroOnly = false;

    private boolean preserve = false;

    /**
     * Instantiates a new Hfts api
     * which can be used multiple times.
     */
    public HftsApi() { }

    /**
     * Gets data sets which are already stored for processing.
     *
     * @return the datasets
     */
    List<NifDataset> getDatasets() {
        return datasets;
    }

    /**
     * Adds a new data set for processing from a file.
     *
     * @param name the name of the data set
     * @param p    the path to the data file
     * @return the hfts api
     * @throws IOException the io exception if file not found
     */
    public HftsApi withDataset(String name, Path p) throws IOException {
        datasets.add(new NifDataset(name, p));
        return this;
    }

    /**
     * Adds a new data set for processing from a string.
     *
     * @param name       the name
     * @param nifContent the nif content
     * @return the hfts api
     */
    public HftsApi withDataset(String name, String nifContent) {
        datasets.add(new NifDataset(name, nifContent));
        return this;
    }

    /**
     * Adds a new data set for processing.
     *
     * @param dataset the dataset
     * @return the hfts api
     */
    public HftsApi withDataset(NifDataset dataset) {
        datasets.add(dataset);
        return this;
    }

    /**
     * Add a bunch of {@link Metric} which are applied
     * on the data sets.
     *
     * @param m the metrics - see org.santifa.hfts.core.metric
     * @return the hfts api
     */
    public HftsApi withMetric(Metric... m) {
        Collections.addAll(metrics, m);
        return this;
    }

    public HftsApi withSameAsRetrival() {
        this.sameAs = true;
        return this;
    }

    public HftsApi microOnly() {
        this.microOnly = true;
        this.macroOnly = false;
        return this;
    }

    public HftsApi macroOnly() {
        this.macroOnly = true;
        this.microOnly = false;
        return this;
    }

    public HftsApi preserveMetrics() {
        this.preserve = true;
        return this;
    }

    /**
     * Process the data sets.
     * <br/>
     * Afterwards the processed data sets and used metrics are
     * cleared.
     *
     * @return the list
     */
    public List<NifDataset> run() {
        List<NifDataset> result = new ArrayList<>(datasets.size());

        for (NifDataset d : datasets) {
            for (Metric m : metrics) {
                Logger.debug("Procssesing dataset {}...", d.getName());

                if (macroOnly) {
                    d = m.calculateMacro(d);
                } else if (microOnly) {
                    d = m.calculateMicro(d);
                } else {
                    d = m.calculate(d);
                }

                /* do as last, can confuse some metrics and lowers the performance */
                if (sameAs) {
                    retriever.retrieve(d);
                }
                result.add(d);
            }
        }

        /* clear the metrics and data sets for the next run */
        datasets.clear();

        if (!preserve) {
            metrics.clear();
        }

        return result;
    }
}
