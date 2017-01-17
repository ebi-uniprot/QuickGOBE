package uk.ac.ebi.quickgo.common.converter;

import java.util.List;

/**
 * Models a field (possibly containing nested fields) which can be
 * flattened to a {@link String}.
 *
 * Created 26/11/15
 * @author Edd
 */
public interface FlatField {
    List<FlatField> getFields();
    String buildString();
}
