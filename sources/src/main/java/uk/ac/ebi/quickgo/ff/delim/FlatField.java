package uk.ac.ebi.quickgo.ff.delim;

import java.util.List;

/**
 * Created 26/11/15
 * @author Edd
 */
public abstract class FlatField {
    public abstract List<FlatField> getFields();
    public abstract String buildString();
    protected abstract String buildString(int level);
}
