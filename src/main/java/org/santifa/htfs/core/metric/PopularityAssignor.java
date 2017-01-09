package org.santifa.htfs.core.metric;

import org.santifa.htfs.core.NifDataset;

import java.nio.file.Path;

/**
 * Created by ratzeputz on 09.01.17.
 */
public class PopularityAssignor implements Metric {

    private Path file;

    private String property;

    protected PopularityAssignor(Path file, String property) {
        this.file = file;
        this.property = property;
    }

    @Override
    public NifDataset calculate(NifDataset dataset) {
        return null;
    }

    @Override
    public NifDataset calculateMicro(NifDataset dataset) {
        return null;
    }

    @Override
    public NifDataset calculateMacro(NifDataset dataset) {
        return null;
    }
}
