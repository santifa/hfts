package org.santifa.hfts.cli;

import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.MutuallyExclusiveWith;
import com.github.rvesse.airline.annotations.restrictions.Required;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.santifa.hfts.core.HftsApi;
import org.santifa.hfts.core.HftsDataset;
import org.santifa.hfts.core.metric.*;
import org.santifa.hfts.core.utils.AmbiguityDictionary;
import org.santifa.hfts.core.utils.Dictionary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Provide a simple interface to the hfts library.
 *
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
@Command(name = "run", description = "Run the hfts api on some datasets.")
public class Cli implements Runnable {

    @Option(name = {"-v", "--setVerbose"}, description = "Produce setVerbose output.")
    private boolean verbose = false;

    @Option(name = "--macro", description = "Run macro calculation only.")
    @MutuallyExclusiveWith(tag = "--micro")
    private boolean macro = false;

    @Option(name = "--micro", description = "Run micro calculation only.")
    @MutuallyExclusiveWith(tag = "--macro")
    private boolean micro = false;

    @Option(name = "--sameAs", description = "Run with sameAs retrieval.")
    private boolean sameAs = false;

    @Option(name = {"-m", "--metrics"}, description = "Select only provided metrics. Provide as a comma seperated list.\n" +
            "Available: notannotated, density, hits, pagerank, type, diversity, ambiguity.")
    private String metrics = "";

    @Arguments(description = "Datasets for processing.")
    @Required
    @com.github.rvesse.airline.annotations.restrictions.Path(mustExist = true)
    private List<File> datasets = new ArrayList<>();

    private static void setVerbose(boolean verbose) {
        if (verbose) {
            Logger.getConfiguration().writer(new ConsoleWriter()).level(Level.DEBUG).activate();
        }
    }

    @Override
    public void run() {
        Logger.info("Started hfts api...");
        setVerbose(verbose);
        Logger.debug("Running in setVerbose mode...");
        Logger.debug("Processing {} datasets...", datasets);

        HftsApi api = new HftsApi();

        if (sameAs) {
            Logger.debug("Enable sameAs retrieval...");
            api.withSameAsRetrival();
        }

        if (micro) {
            Logger.debug("Calculate only micro metrics...");
            api.microOnly();
        } else if (macro) {
            Logger.debug("Calculate only macro metrics...");
            api.microOnly();
        }

        for (File ds : datasets) {
            try {
                api.withDataset(ds.getName(), Paths.get(ds.toURI()));
            } catch (IOException e) {
                Logger.error("Failed to load dataset from path {}...\n{}", ds, e);
            }
        }

        /* handle metrics selection */
        if (metrics.isEmpty()) {
            Logger.debug("Running all metrics.");
            Dictionary<Integer> connectorEntity = AmbiguityDictionary.getDefaultEntityConnector();
            Dictionary<Integer> connectorSf = AmbiguityDictionary.getDefaultSFConnector();
            api.withMetric(new NotAnnotated(), new Density(), new Ambiguity(connectorEntity, connectorSf), new Diversity(connectorEntity, connectorSf),
                    CategoryAssignor.getDefaultAssignor(), PopularityAssignor.getDefaultHits(), PopularityAssignor.getDefaultPageRank());
        } else {
            Dictionary<Integer> connectorEntity = null;
            Dictionary<Integer> connectorSf = null;

            for (String m : metrics.split(",")) {
                switch (m.toLowerCase()) {
                    case "density": api.withMetric(new Density());
                        break;
                    case "notannotated": api.withMetric(new NotAnnotated());
                        break;
                    case "hits": api.withMetric(PopularityAssignor.getDefaultHits());
                        break;
                    case "pagerank": api.withMetric(PopularityAssignor.getDefaultPageRank());
                        break;
                    case "type": api.withMetric(CategoryAssignor.getDefaultAssignor());
                        break;
                    case "diversity":
                        if (connectorEntity == null) {
                            connectorEntity = AmbiguityDictionary.getDefaultEntityConnector();
                            connectorSf= AmbiguityDictionary.getDefaultSFConnector();

                        }
                        api.withMetric(new Diversity(connectorEntity, connectorSf));
                        break;
                    case "ambiguity":
                        if (connectorEntity == null) {
                            connectorEntity = AmbiguityDictionary.getDefaultEntityConnector();
                            connectorSf = AmbiguityDictionary.getDefaultSFConnector();

                        }
                        api.withMetric(new Ambiguity(connectorEntity, connectorSf));
                        break;
                    default: Logger.debug("Not recognized metric {}...", m);
                        break;
                }
            }
        }

        /* run everything */
        List<HftsDataset> result = api.run();

        /* write back to disk */
        for (HftsDataset d : result) {
            try {
                FileOutputStream fos = new FileOutputStream(d.getName());
                d.write(fos);
            } catch (IOException e) {
                Logger.error("Failed to write result for {}...\n{}", d, e);
            }
        }

    }

    public static void main(String[] args) {
        SingleCommand<Cli> parser = SingleCommand.singleCommand(Cli.class);
        Cli cli = parser.parse(args);
        cli.run();
    }

}
