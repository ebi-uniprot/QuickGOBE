package uk.ac.ebi.quickgo.solr.model.ontology;

import java.util.Comparator;

import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm.SolrTermDocumentType;

/**
 * Comparator to avoid duplicated relations
 * @author cbonill
 *
 */
public class SolrTermComparator implements Comparator<SolrTerm> {

	@Override
	public int compare(SolrTerm solrTerm1, SolrTerm solrTerm2) {
		// Two relations are equal if they have the same child, parent and relation type
		if (solrTerm1 != null && solrTerm2 != null && solrTerm1.getDocType() != null && solrTerm2.getDocType() != null) {			
			if (solrTerm1.getDocType().equals(SolrTermDocumentType.RELATION.getValue()) && solrTerm2.getDocType().equals(SolrTermDocumentType.RELATION.getValue())) {				
				return (solrTerm1.getChild().hashCode() - solrTerm2.getChild().hashCode()) + 
						(solrTerm1.getParent().hashCode() - solrTerm2.getParent().hashCode()) + 
						(solrTerm1.getRelationType().hashCode() - solrTerm2.getRelationType().hashCode());				
			}
		}
		return 1;
	}
}