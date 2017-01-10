package org.santifa.htfs.core.metric;

import com.hp.hpl.jena.rdf.model.Property;

/**
 * Every metric should provide the properties which are used
 * to describe them in NIF. So classes which are implementing a
 * micro and a macro version shall extend this class.
 *
 * Otherwise implement the {@link Metric} interface directly and
 * link the micro and macro version to the same calculation.
 *
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public abstract class AbstractMetric implements Metric {

    protected final Property microMetric;

    protected final Property macroMetric;

    public AbstractMetric(Property microMetric, Property macroMetric) {
        this.microMetric = microMetric;
        this.macroMetric = macroMetric;
    }

    public Property getMicroProperty() {
        return microMetric;
    }

    public Property getMacroProperty() {
        return macroMetric;
    }
}
