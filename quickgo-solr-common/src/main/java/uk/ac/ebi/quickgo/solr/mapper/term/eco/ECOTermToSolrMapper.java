package uk.ac.ebi.quickgo.solr.mapper.term.eco;

import java.util.List;

import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.solr.mapper.term.TermToSolrMapper;
import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm;

/**
 * Mapper for ECO terms
 */
@Service("solrECOTermMapper")
public class ECOTermToSolrMapper extends TermToSolrMapper {

	@Override
	public void mapSpecificFields(GenericTerm term, List<SolrTerm> solrTerms,
			List<SolrDocumentType> solrDocumentTypes) {
	}
}
