package uk.ac.ebi.quickgo.common.search.results;

import java.util.List;

import static java.util.Objects.requireNonNull;

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
        this.id = requireNonNull(id);
        this.matches = requireNonNull(matches);
    }

    public List<FieldHighlight> getMatches() {
        return matches;
    }

    public String getId() {
        return id;
    }
}
