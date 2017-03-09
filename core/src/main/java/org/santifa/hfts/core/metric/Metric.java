package org.santifa.hfts.core.metric;

import org.santifa.hfts.core.HftsDataset;

/**
 * Implement this interface to add new measures
 * to the meta information generation process.
 *
 * If no micro or macro measure is possible call
 * the normal {@link Metric#calculate(HftsDataset)}.
 *
 * Created by Henrik Jürges (jürges.henrik@gmail.com)
 */
public interface Metric {

    /**
     * Calculate the general measure for a hfts dataset.
     *
     * @param dataset the dataset
     * @return the hfts dataset
     */
    HftsDataset calculate(HftsDataset dataset);

    /**
     * Calculate micro measure for a hfts dataset.
     *
     * @param dataset the dataset
     * @return the hfts dataset
     */
    HftsDataset calculateMicro(HftsDataset dataset);

    /**
     * Calculate macro measure for a hfts dataset.
     *
     * @param dataset the dataset
     * @return the hfts dataset
     */
    HftsDataset calculateMacro(HftsDataset dataset);

}
