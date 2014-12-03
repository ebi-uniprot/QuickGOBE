package uk.ac.ebi.quickgo.solr.mapper.term.eco;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.ontology.eco.ECOTerm;
import uk.ac.ebi.quickgo.solr.mapper.EntityMapper;
import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm.SolrTermDocumentType;

/**
 * For creating ECO Terms from Solr ones
 */
@Service("entityECOTermMapper")
public class EntityECOTermMapper implements EntityMapper<SolrTerm, ECOTerm>{

	// Log
	private static final Logger logger = Logger.getLogger(EntityECOTermMapper.class);
	
	@Override
	public ECOTerm toEntityObject(Collection<SolrTerm> solrObjects) {
		return toEntityObject(solrObjects,
				SolrTermDocumentType.getAsInterfaces());
	}

	@Override
	public ECOTerm toEntityObject(Collection<SolrTerm> solrObjects, List<SolrDocumentType> solrDocumentTypes) {

		ECOTerm term = new ECOTerm();

		for (SolrDocumentType termDocumentType : solrDocumentTypes) {
			SolrTermDocumentType solrTermDocumentType = ((SolrTermDocumentType) termDocumentType);

			switch (solrTermDocumentType) {

			case TERM:
				if(getAssociatedSolrTerms(solrObjects, SolrTermDocumentType.TERM).size() > 0){
					mapBasicInformation(getAssociatedSolrTerms(solrObjects, SolrTermDocumentType.TERM).get(0), term);
				}
				break;
			}
		}
		
		return term;
	}
	
	private void mapBasicInformation(SolrTerm solrTerm, ECOTerm term) {
		
		term.setId(solrTerm.getId());
		term.setName(solrTerm.getName());
		term.setObsolete(solrTerm.isObsolete());
		try {					
			String comments = "";
			if (solrTerm.getComments() != null) {
				for (String comment : solrTerm.getComments()) {
					comments = comments + comment + "\n";
				}
				term.setComment(comments);
			}
			String definitions = "";
			if (solrTerm.getDefinitions() != null) {
				for (String definition : solrTerm.getDefinitions()) {
					definitions = definitions + definition + "\n";
				}
				term.setDefinition(definitions);
			}			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}	
	
	/**
	 * Given a list of Solr terms, returns the ones that match with the
	 * specified document type
	 * 
	 * @param solrObjects
	 *            Solr term objects
	 * @param solrTermDocumentType
	 *            Type to check
	 * @return Solr terms that match with the specified document type
	 */
	protected List<SolrTerm> getAssociatedSolrTerms(
			Collection<SolrTerm> solrObjects,
			SolrTermDocumentType solrTermDocumentType) {
		List<SolrTerm> solrTerms = new ArrayList<>();
		for (SolrTerm solrTerm : solrObjects) {
			if (SolrTermDocumentType.valueOf(solrTerm.getDocType().toUpperCase()) == solrTermDocumentType) {
				solrTerms.add(solrTerm);
			}
		}
		return solrTerms;
	}
}