package uk.ac.ebi.quickgo.common.search.results;

import com.google.common.base.Preconditions;
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
        return matches;
    }

    public String getId() {
        return id;
    }
}
