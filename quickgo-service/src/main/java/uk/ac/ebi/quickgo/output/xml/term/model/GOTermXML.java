package uk.ac.ebi.quickgo.output.xml.term.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.output.xml.model.XMLEntity;
import uk.ac.ebi.quickgo.xml.term.Lookupdeftype;
import uk.ac.ebi.quickgo.xml.term.ObjectFactory;

/**
 * GO Term XML representation
 * @author cbonill
 *
 */
public class GOTermXML implements XMLEntity {

	Object xmlRepresentation;
	
	public GOTermXML(GOTerm goTerm) {

		ObjectFactory objectFactory = new ObjectFactory();
		
		// Term
		Lookupdeftype lookupdeftype = objectFactory.createLookupdeftype();
		lookupdeftype.setId(goTerm.getId());
		lookupdeftype.setName(goTerm.getName());
		lookupdeftype.setIsObsolete(goTerm.isObsolete());
		lookupdeftype.setComment(goTerm.getComment());
		lookupdeftype.setDefinition(goTerm.getDefinition());
		lookupdeftype.setUsage(goTerm.getUsageText());
		
		//Ancestors
	    List<String> lineage = new ArrayList<String>();
		for(GenericTerm ancestor : goTerm.getAllAncestors()){
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