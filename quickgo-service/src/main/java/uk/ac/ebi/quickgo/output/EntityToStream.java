package uk.ac.ebi.quickgo.output;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.ebi.quickgo.output.json.EntityToJSONStream;
import uk.ac.ebi.quickgo.output.xml.EntityToXMLStream;
import uk.ac.ebi.quickgo.render.Format;
import uk.ac.ebi.quickgo.render.JSONSerialise;

/**
 * Abstract class for converting Java objects into streams
 * 
 * @author cbonill
 * 
 * @param <T>
 *            Object type
 */

public class EntityToStream<T extends JSONSerialise> {

	/**
	 * For converting Java objects into XML streams
	 */
	EntityToXMLStream<T> entityToXMLStream;

	/**
	 * For converting Java objects into JSON streams
	 */
	@Autowired
	EntityToJSONStream<T> entityToJSONStream;
	
	/**
	 * Converts Java object into XML stream
	 * 
	 * @param entity
	 *            Java object
	 * @param outputStream
	 *            Output stream representation
	 */
	public void convertToXMLStream(T entity, Format format, OutputStream outputStream)
			throws JAXBException {
		entityToXMLStream.convertToXMLStream(entity, format, outputStream);
	}

	/**
	 * Converts Java object into JSON stream
	 * 
	 * @param entity
	 *            Java object 
	 * @param outputStream
	 *            Output stream representation
	 * @throws IOException 
	 */
	public void convertToJSONStream(T entity, OutputStream outputStream) throws IOException {
		entityToJSONStream.convertToJSONStream(entity, outputStream);
	}

	public EntityToXMLStream<T> getEntityToXMLStream() {
		return entityToXMLStream;
	}

	public void setEntityToXMLStream(EntityToXMLStream<T> entityToXMLStream) {
		this.entityToXMLStream = entityToXMLStream;
	}	
}