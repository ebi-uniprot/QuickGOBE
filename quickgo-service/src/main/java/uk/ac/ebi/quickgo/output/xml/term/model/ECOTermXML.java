package uk.ac.ebi.quickgo.output.xml.term.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import uk.ac.ebi.quickgo.ontology.eco.ECOTerm;
import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.output.xml.model.XMLEntity;
import ebi.ac.uk.ws.quickgo.Lookupdeftype;
import ebi.ac.uk.ws.quickgo.ObjectFactory;

/**
 * ECO XML representation
 * @author cbonill
 */
public class ECOTermXML implements XMLEntity{

	Object xmlRepresentation;
	
	public ECOTermXML(ECOTerm ecoTerm){
		ObjectFactory objectFactory = new ObjectFactory();
		
		// Term
		Lookupdeftype lookupdeftype = objectFactory.createLookupdeftype();
		lookupdeftype.setId(ecoTerm.getId());
		lookupdeftype.setName(ecoTerm.getName());
		lookupdeftype.setIsObsolete(ecoTerm.isObsolete());
		
		//Ancestors
	    List<String> lineage = new ArrayList<String>();
		for(GenericTerm ancestor : ecoTerm.getAllAncestors()){
		    lineage.add(ancestor.getId());
		}
		
		lookupdeftype.setAncestors(StringUtils.arrayToDelimitedString(lineage.toArray(), ","));
		
		this.xmlRepresentation = lookupdeftype;
	}
	
	public Object getXmlRepresentation() {
		return xmlRepresentation;
	}

	public void setXmlRepresentation(Object xmlRepresentation) {
		this.xmlRepresentation = xmlRepresentation;
	}
}