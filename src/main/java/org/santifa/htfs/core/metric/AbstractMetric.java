package org.santifa.htfs.core.metric;

import com.hp.hpl.jena.rdf.model.Property;

/**
 * Every metric should provide the properties which are used
 * to describe them in NIF.
 *
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public abstract class AbstractMetric implements Metric {

    protected Property microMetric;

    protected Property macroMetric;

    public AbstractMetric(Property microMetric, Property macroMetric) {
        this.microMetric = microMetric;
        this.macroMetric = macroMetric;
    }
}
