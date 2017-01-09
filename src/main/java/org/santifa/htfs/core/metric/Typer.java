package org.santifa.htfs.core.metric;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;
import org.santifa.htfs.core.NifDataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by ratzeputz on 30.12.16.
 */
public class Typer extends Sparql implements Metric {

    public Typer(String endpoint, String graph, String query) {
        super(endpoint, graph, query);
    }


    public static Typer getDefaultTyper() {
        String query = "prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "select distinct ?e ?t where { values ?e {##} . ?e rdf:type ?t . }";
        return new Typer("http://dbpedia.org/sparql", "http://dbpedia.org", query);
    }

    @Override
    public NifDataset calculate(NifDataset dataset) {
        return type(dataset);
    }

    private NifDataset type(NifDataset dataset) {
        List<String> entities = new ArrayList<>();

        /* get all meanings for the chunk */
        for (Marking m : dataset.getMarkings()) {
            if (m instanceof MeaningSpan) {
                entities.addAll(((MeaningSpan) m).getUris());
            }
        }

        HashMap<String, List<String>> typedEntities = chunk(entities);

        for (Document d : dataset.changeDocuments()) {
            List<Marking> markings = new ArrayList<>();
            for (Marking m : d.getMarkings()) {
                if (m instanceof MeaningSpan) {
                    /* create a new typed entity */
                    TypedNamedEntity typedNamedEntity = new TypedNamedEntity(
                            ((MeaningSpan) m).getStartPosition(), ((MeaningSpan) m).getLength(),
                            ((MeaningSpan) m).getUris(), new HashSet<>());

                    /* if we have the types for some uri, add it */
                    for (String uri : ((MeaningSpan) m).getUris()) {
                        if (typedEntities.containsKey(uri)) {
                            typedNamedEntity.getTypes().addAll(typedEntities.get(uri));
                        }
                    }
                    markings.add(typedNamedEntity);
                } else {
                    /* preserve all other markings which are not named entities */
                    markings.add(m);
                }
            }
            d.setMarkings(markings);
        }

        return dataset;
    }

    @Override
    protected HashMap<String, List<String>> query(String query) {
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

}
