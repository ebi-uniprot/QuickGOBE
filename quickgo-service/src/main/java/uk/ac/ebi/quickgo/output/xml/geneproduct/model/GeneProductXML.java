package uk.ac.ebi.quickgo.output.xml.geneproduct.model;

import uk.ac.ebi.quickgo.geneproduct.GeneProduct;
import uk.ac.ebi.quickgo.output.xml.model.XMLEntity;
import ebi.ac.uk.ws.quickgo.Lookupdeftype;
import ebi.ac.uk.ws.quickgo.ObjectFactory;

/**
 * Gene Product XML representation
 * @author cbonill
 *
 */
public class GeneProductXML implements XMLEntity{

	Object xmlRepresentation;

	public GeneProductXML(GeneProduct geneProduct){
		
		ObjectFactory objectFactory = new ObjectFactory();
		
		// Term
		Lookupdeftype lookupdeftype = objectFactory.createLookupdeftype();
		lookupdeftype.setAccession(geneProduct.getDbObjectId());
		lookupdeftype.setGeneName(geneProduct.getDbObjectSymbol());
		lookupdeftype.setDescription(geneProduct.getDbObjectName());
		lookupdeftype.setTaxonId(String.valueOf(geneProduct.getTaxonId()));
		lookupdeftype.setTaxonName(geneProduct.getTaxonName());
		
		this.xmlRepresentation = lookupdeftype;
	}

	public Object getXmlRepresentation() {
		return xmlRepresentation;
	}

	public void setXmlRepresentation(Object xmlRepresentation) {
		this.xmlRepresentation = xmlRepresentation;
	}	
}