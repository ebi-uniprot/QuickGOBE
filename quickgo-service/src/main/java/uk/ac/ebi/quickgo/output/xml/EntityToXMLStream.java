package uk.ac.ebi.quickgo.output.xml;

import java.io.OutputStream;

import javax.xml.bind.JAXBException;

import uk.ac.ebi.quickgo.output.xml.model.XMLEntity;
import uk.ac.ebi.quickgo.render.Format;

/**
 * Class for converting Java objects into XML streams
 * 
 * @author cbonill
 * 
 * @param <T>
 *            Object type
 */

public class EntityToXMLStream<T> {

	/**
	 * For converting an Entity into a XML Entity
	 */
	EntityToXMLEntity<T> entityToXMLEntity;

	/**
	 * For converting a XML Entity into a strem
	 */
	XMLEntityToStream<XMLEntity> xmlEntityToStream;

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
		XMLEntity xmlEntity = entityToXMLEntity.convert(entity, format);
		xmlEntityToStream.convert(xmlEntity, format, outputStream);
	}

	public EntityToXMLEntity<T> getEntityToXMLEntity() {
		return entityToXMLEntity;
	}

	public void setEntityToXMLEntity(EntityToXMLEntity<T> entityToXMLEntity) {
		this.entityToXMLEntity = entityToXMLEntity;
	}

	public XMLEntityToStream<XMLEntity> getXmlEntityToStream() {
		return xmlEntityToStream;
	}

	public void setXmlEntityToStream(XMLEntityToStream<XMLEntity> xmlEntityToStream) {
		this.xmlEntityToStream = xmlEntityToStream;
	}
}
