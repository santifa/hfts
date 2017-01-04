package org.santifa.htfs.core.metric;

import com.hp.hpl.jena.query.QueryExecutionFactory;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.santifa.htfs.core.NifDataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by ratzeputz on 30.12.16.
 */
public class Typer implements Metric {

    private QueryExecutionFactory factory;

    public Typer(String endpoint, String graph) {
       /* this.factory = FluentQueryExecutionFactory.http(endpoint, graph)
                .config()
                    .withPagination(800)
                    .withDelay(500, TimeUnit.MILLISECONDS)
                    .withRetry(5, 5000, TimeUnit.MILLISECONDS)
                .end().create();*/
    }

    public static Typer getDefaultTyper() {
        return new Typer("http://dbpedia.org/sparql", "http://dbpedia.org");
    }

    @Override
    public NifDataset calculate(NifDataset dataset) {
        return null;
    }

    public NifDataset type(NifDataset dataset, String property, String query) {

        for (Document doc : dataset.getDocuments()) {
            /* type all named entities */
            List<Marking> typedNamedEntities = query(doc.getMarkings(MeaningSpan.class), query);

            doc.setMarkings(typedNamedEntities);
        }


        return dataset;
    }

    private List<Marking> query(List<MeaningSpan> entities, String query) {
        List<Marking> typedEntities = new ArrayList<>();


        return typedEntities;
    }


    public interface TypedMeaning extends MeaningSpan {

        public void addType(String uri);

        public String getType();
    }

    public class TypedAnnotation extends NamedEntity implements TypedMeaning {

        private String typeUri;

        public TypedAnnotation(int startPosition, int length, Set<String> uris) {
            super(startPosition, length, uris);
        }

        @Override
        public void addType(String uri) {
            this.typeUri = uri;
        }

        @Override
        public String getType() {
            return typeUri;
        }
    }
}
