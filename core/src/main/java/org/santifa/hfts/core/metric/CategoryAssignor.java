package org.santifa.hfts.core.metric;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.pmw.tinylog.Logger;
import org.santifa.hfts.core.HftsDataset;
import org.santifa.hfts.core.nif.MetaNamedEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class CategoryAssignor implements Metric {

    private static IRIFactory iriFactory = IRIFactory.semanticWebImplementation();

    private String endpoint;

    private String graph;

    private String[] queryParts;

    public CategoryAssignor(String endpoint, String graph, String query) {
        this.queryParts = query.split("##");
        this.endpoint = endpoint;
        this.graph = graph;
    }


    public static CategoryAssignor getDefaultAssignor() {
        String query = "prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "select distinct ?e ?t where { values ?e {##} . ?e rdf:type ?t . }";
        return new CategoryAssignor("http://dbpedia.org/sparql", "http://dbpedia.org", query);
    }

    @Override
    public HftsDataset calculate(HftsDataset dataset) {
        return type(dataset);
    }

    @Override
    public HftsDataset calculateMicro(HftsDataset dataset) {
        return type(dataset);
    }

    @Override
    public HftsDataset calculateMacro(HftsDataset dataset) {
        return type(dataset);
    }

    private HftsDataset type(HftsDataset dataset) {
        List<String> entities = new ArrayList<>();
        Logger.debug("Assign categories to dataset {}", dataset.getName());

        /* get all meanings for the chunk */
        for (MetaNamedEntity m : dataset.getMarkings()) {
            entities.addAll(m.getUris());
        }

        HashMap<String, List<String>> typedEntities = chunk(entities);

        for (MetaNamedEntity m : dataset.getMarkings()) {
            for (String uri : m.getUris()) {
                if (typedEntities.containsKey(uri)) {
                    m.getTypes().addAll(typedEntities.get(uri));
                }
            }
        }

        return dataset;
    }

    private HashMap<String, List<String>> query(String query) {
        HashMap<String, List<String>> typedEntities = new HashMap<>();

        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query, graph)) {
            ResultSet rs = qexec.execSelect();

            while (rs.hasNext()) {
                QuerySolution qs = rs.next();

                if (typedEntities.containsKey(qs.getResource("e").getURI())) {
                    List<String> types = typedEntities.get(qs.getResource("e").getURI());
                    types.add(qs.getResource("t").getURI());
                } else {
                    List<String> types = new ArrayList<>();
                    types.add(qs.getResource("t").toString());
                    typedEntities.put(qs.getResource("e").getURI(), types);
                }
            }
        }
        return typedEntities;
    }

    private HashMap<String, List<String>> chunk(List<String> entities) {
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
