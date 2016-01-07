package uk.ac.ebi.quickgo.service.model;

import uk.ac.ebi.quickgo.service.converter.FieldConverter;

/**
 * Simple marker to restrict {@link FieldConverter} behaviour to only classes
 * that implement this interface. Alternative is to use annotations, but this
 * would require more costly reflection to check that an object has such
 * an annotation. Moreover, in this case, a marker interface is visually
 * clear at the level of the {@link FieldConverter}.
 *
 * Created 01/12/15
 * @author Edd
 */
public interface FieldType {
}
