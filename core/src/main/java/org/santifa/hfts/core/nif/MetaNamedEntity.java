package org.santifa.hfts.core.nif;

import com.hp.hpl.jena.rdf.model.Property;
import org.aksw.gerbil.transfer.nif.MeaningSpan;
import org.aksw.gerbil.transfer.nif.data.TypedNamedEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * An implementation for the {@link MetaSpan} interface which
 * provides a possibility to store additional meta information about a document.
 *
 *
 * Created by Henrik Jürges (juerges.henrik@gmail.com)
 */
public class MetaNamedEntity extends TypedNamedEntity implements MetaSpan {

    private HashMap<Property, String> metaInformations;

    public MetaNamedEntity(int startPosition, int length, String uri, Set<String> types) {
        super(startPosition, length, uri, types);
        this.metaInformations = new HashMap<>();
    }

    public MetaNamedEntity(int startPosition, int length, Set<String> uris, Set<String> types) {
        super(startPosition, length, uris, types);
        this.metaInformations = new HashMap<>();
    }

    public MetaNamedEntity(MeaningSpan m) {
        super(new TypedNamedEntity(m.getStartPosition(), m.getLength(), m.getUris(), new HashSet<>()));
        this.metaInformations = new HashMap<>();
    }

    public MetaNamedEntity(TypedNamedEntity typedNamedEntity) {
        super(typedNamedEntity);
        this.metaInformations = new HashMap<>();
    }

    @Override
    public HashMap<Property, String> getMetaInformations() {
        return this.metaInformations;
    }

    @Override
    public void setMetaInformations(HashMap<Property, String> metaInformations) {
        this.metaInformations = metaInformations;
    }

    @Override
    public String toString() {
        return "meta[" + super.toString() + "\n" +
                "metaInformations=" + metaInformations + ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MetaNamedEntity entity = (MetaNamedEntity) o;
        return metaInformations != null ? metaInformations.equals(entity.metaInformations) : entity.metaInformations == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (metaInformations != null ? metaInformations.hashCode() : 0);
        return result;
    }
}
