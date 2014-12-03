package uk.ac.ebi.quickgo.output.xml.geneproduct;

import uk.ac.ebi.quickgo.geneproduct.GeneProduct;
import uk.ac.ebi.quickgo.output.xml.EntityToXMLEntity;
import uk.ac.ebi.quickgo.output.xml.geneproduct.model.GeneProductXML;
import uk.ac.ebi.quickgo.output.xml.model.XMLEntity;
import uk.ac.ebi.quickgo.render.Format;

/**
 * Transform GP entities into XML ones
 * @author cbonill
 *
 */
public class GPEntityToGPXMLEntity implements EntityToXMLEntity<GeneProduct> {

	@Override
	public XMLEntity convert(GeneProduct entity, Format format) {
		GeneProductXML geneProductXML = new GeneProductXML(entity);
		return geneProductXML;
	}	
}
