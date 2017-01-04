package org.santifa.htfs.core.metric;

import org.santifa.htfs.core.NifDataset;

/**
 * Created by ratzeputz on 30.12.16.
 */
public interface Metric {

    public NifDataset calculate(NifDataset dataset);
}
