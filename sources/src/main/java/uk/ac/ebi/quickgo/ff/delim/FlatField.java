package uk.ac.ebi.quickgo.ff.delim;

import java.util.List;

/**
 * Models a field (possibly containing nested fields) which can be
 * flattened to a {@link String}.
 *
 * Created 26/11/15
 * @author Edd
 */
public abstract class FlatField {
    public abstract List<FlatField> getFields();
    public abstract String buildString();
    protected abstract String buildStringFromLevel(int level);
}
