package uk.ac.ebi.quickgo.output.xml;

import uk.ac.ebi.quickgo.output.xml.model.XMLEntity;
import uk.ac.ebi.quickgo.render.Format;

/**
 * Interface for converting Entity objects into XML ones
 * @author cbonill
 *
 * @param <T>
 */
public interface EntityToXMLEntity<T> {

	public XMLEntity convert(T entity, Format format);
}
