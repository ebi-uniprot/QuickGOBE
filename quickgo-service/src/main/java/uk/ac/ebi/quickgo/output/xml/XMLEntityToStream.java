package uk.ac.ebi.quickgo.output.xml;

import java.io.OutputStream;

import javax.xml.bind.JAXBException;

import uk.ac.ebi.quickgo.output.xml.model.XMLEntity;
import uk.ac.ebi.quickgo.render.Format;

/**
 * Interface for converting XML entities into XML streams
 * @author cbonill
 *
 * @param <T>
 */
public interface XMLEntityToStream<T extends XMLEntity> {

	public void convert(T xmlEntity, Format format, OutputStream outputStream) throws JAXBException;
}
