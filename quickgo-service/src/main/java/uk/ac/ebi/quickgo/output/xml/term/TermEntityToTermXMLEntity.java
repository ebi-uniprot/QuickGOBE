package uk.ac.ebi.quickgo.output.xml.term;

import uk.ac.ebi.quickgo.ontology.eco.ECOTerm;
import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.output.xml.EntityToXMLEntity;
import uk.ac.ebi.quickgo.output.xml.model.XMLEntity;
import uk.ac.ebi.quickgo.output.xml.term.model.ECOTermXML;
import uk.ac.ebi.quickgo.output.xml.term.model.GOTermOBOXML;
import uk.ac.ebi.quickgo.output.xml.term.model.GOTermXML;
import uk.ac.ebi.quickgo.render.Format;

/**
 * To convert an Entity into a XML one
 * @author cbonill
 *
 */
public class TermEntityToTermXMLEntity implements EntityToXMLEntity<GenericTerm>{

	@Override
	public XMLEntity convert(GenericTerm genericTerm, Format format) {
		if(genericTerm.getId().startsWith(GOTerm.GO.toString())){
			switch (format){
			case XML:
				GOTermXML goTermXML = new GOTermXML((GOTerm)genericTerm);
				return goTermXML;
			case OBOXML:
				GOTermOBOXML goTermOboXML = new GOTermOBOXML((GOTerm)genericTerm);
				return goTermOboXML;
			}			
		}else if(genericTerm.getId().startsWith(ECOTerm.ECO.toString())){
			ECOTermXML ecoTermXML = new ECOTermXML((ECOTerm)genericTerm);
			return ecoTermXML;
		}
		return null;		
	}
}