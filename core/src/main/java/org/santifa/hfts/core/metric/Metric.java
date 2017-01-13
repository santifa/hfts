package org.santifa.hfts.core.metric;

import org.santifa.hfts.core.NifDataset;

/**
 * Created by ratzeputz on 30.12.16.
 */
public interface Metric {

    NifDataset calculate(NifDataset dataset);

    NifDataset calculateMicro(NifDataset dataset);

    NifDataset calculateMacro(NifDataset dataset);

}
