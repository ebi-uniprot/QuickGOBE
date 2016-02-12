package uk.ac.ebi.quickgo.rest.search.results;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.List;

/**
 * Represents the highlights related to a document. It comprises a document identifier,
 * together with a {@link List} of {@link FieldHighlight} instances.
 *
 * Created 01/02/16
 * @author Edd
 */
public class DocHighlight {
    private final String id;
    private final List<FieldHighlight> matches;

    public DocHighlight(String id, List<FieldHighlight> matches) {
        Preconditions.checkArgument(id != null, "Document identifier can not be null");
        Preconditions.checkArgument(matches != null, "Highlighted matches can not be null");

        this.id = id;
        this.matches = matches;
    }

    public List<FieldHighlight> getMatches() {
        return Collections.unmodifiableList(matches);
    }

    public String getId() {
        return id;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DocHighlight that = (DocHighlight) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        return matches != null ? matches.equals(that.matches) : that.matches == null;

    }

    @Override public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (matches != null ? matches.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "DocHighlight{" +
                "id='" + id + '\'' +
                ", matches=" + matches +
                '}';
    }
}
