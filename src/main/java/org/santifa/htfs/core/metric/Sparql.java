package org.santifa.htfs.core.metric;

import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.pmw.tinylog.Logger;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ratzeputz on 09.01.17.
 */
public abstract class Sparql {
    private static IRIFactory iriFactory = IRIFactory.semanticWebImplementation();

    protected String endpoint;

    protected String graph;

    protected String[] queryParts;

    protected Sparql(String endpoint, String graph, String query) {
        this.queryParts = query.split("##");
        this.endpoint = endpoint;
        this.graph = graph;
    }

    protected HashMap<String, List<String>> chunk(List<String> entities) {
        HashMap<String, List<String>> typedEntities = new HashMap<>();
        int chunk = 0;
        String query;

        /* process 50 entities at once or the remaining if the chunk is bigger */
        while (chunk < entities.size()) {
            if (entities.size() < chunk + 50) {
                query = buildQuery(entities.subList(chunk, entities.size()));
                /* possible overwrite of keys stored before, no merge */
                typedEntities.putAll(query(query));
            } else {
                query = buildQuery(entities.subList(chunk, chunk + 50));
                typedEntities.putAll(query(query));
            }

            chunk = chunk + 50;
        }
        return typedEntities;
    }

    protected abstract HashMap<String, List<String>> query(String queryParts);

    /**
     * Build the query string for every chunk and check if
     * the IRIs are valid.
     *
     * @param entities the entities
     * @return the chunk
     */
    private String buildQuery(List<String> entities) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < queryParts.length && i % 2 == 0; i =+ 2) {
            builder.append(queryParts[i]).append(' ');

            for (int j = 0; j < entities.size(); j++) {
                String entity = entities.get(j);
                // only valid iris pass this test;
                // this is for keeping the mess of non valid iris away from the filters and backends
                //entity = StringUtils.replace(entity, " ", "");
                IRI iri = iriFactory.create(entity);
                if (iri.hasViolation(false)) {
                    Logger.error("IRI violates the w3c standard; ignoring " + iri.toDisplayString());
                } else if (iri.hasViolation(true)) {
                    Logger.warn("IRI is not valid; using despite of " + iri.toDisplayString());
                    builder.append("<").append(entity).append("> ");
                } else {
                    builder.append("<").append(entity).append("> ");
                }
            }
            builder.append(' ').append(queryParts[i+1]);
        }
        return builder.toString();
    }
}
