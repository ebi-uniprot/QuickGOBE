package uk.ac.ebi.quickgo.output.xml.term;

import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import uk.ac.ebi.quickgo.output.xml.XMLEntityToStream;
import uk.ac.ebi.quickgo.output.xml.term.model.ECOTermXML;
import uk.ac.ebi.quickgo.render.Format;
import ebi.ac.uk.ws.quickgo.Lookupdeftype;
import ebi.ac.uk.ws.quickgo.ObjectFactory;

/**
 * To convert a XML entity into a XML stream
 * @author cbonill
 *
 */
public class ECOTermXMLEntityToXMLStream implements XMLEntityToStream<ECOTermXML> {

	@Override
	public void convert(ECOTermXML xmlEntity, Format format, OutputStream outputStream) throws JAXBException {
		
		ObjectFactory objectFactory = new ObjectFactory();
		
		JAXBContext context = JAXBContext.newInstance("ebi.ac.uk.ws.quickgo");
		JAXBElement<Lookupdeftype> element = objectFactory.createLookup((Lookupdeftype)xmlEntity.getXmlRepresentation());;
		Marshaller marshaller = context.createMarshaller();		
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		marshaller.marshal( element, outputStream);		
	}
}