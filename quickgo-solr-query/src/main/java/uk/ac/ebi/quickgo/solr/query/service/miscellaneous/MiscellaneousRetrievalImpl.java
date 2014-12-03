package uk.ac.ebi.quickgo.solr.query.service.miscellaneous;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.TermsResponse.Term;
import org.apache.solr.client.solrj.util.ClientUtils;

import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.solr.mapper.EntityMapper;
import uk.ac.ebi.quickgo.solr.model.miscellaneous.SolrMiscellaneous;
import uk.ac.ebi.quickgo.solr.model.miscellaneous.SolrMiscellaneous.SolrMiscellaneousDocumentType;
import uk.ac.ebi.quickgo.solr.query.model.miscellaneous.enums.MiscellaneousField;
import uk.ac.ebi.quickgo.solr.server.SolrServerProcessor;

/**
 * Service for retrieving Miscellaneous information from Solr
 * @author cbonill
 */
public class MiscellaneousRetrievalImpl implements MiscellaneousRetrieval{
		
	SolrServerProcessor serverProcessor;
	
	EntityMapper<SolrMiscellaneous, Miscellaneous> miscellaneousEntityMapper;
	
	@Override
	public Miscellaneous findById(String id) throws SolrServerException {
		String query = MiscellaneousField.TAXONOMY_ID.getValue() + ":" + id;
		SolrQuery solrQuery = new SolrQuery().setQuery(query);
		List<SolrMiscellaneous> results = (List<SolrMiscellaneous>) serverProcessor.findByQuery(solrQuery,SolrMiscellaneous.class, -1);
		return miscellaneousEntityMapper.toEntityObject(results, SolrMiscellaneousDocumentType.getAsInterfaces());
	}
	
	@Override
	public Miscellaneous findByMiscellaneousId(String idValue, String idName) throws SolrServerException {
		String query = idName + ":" + ClientUtils.escapeQueryChars(idValue);
		SolrQuery solrQuery = new SolrQuery().setQuery(query);
		List<SolrMiscellaneous> results = (List<SolrMiscellaneous>) serverProcessor.findByQuery(solrQuery,SolrMiscellaneous.class, -1);
		return miscellaneousEntityMapper.toEntityObject(results, SolrMiscellaneousDocumentType.getAsInterfaces());
	}
	
	@Override
	public List<Miscellaneous> findByName(String name) throws SolrServerException {
		return null;
	}

	@Override
	public List<Miscellaneous> findAll() throws SolrServerException {
		return null;
	}

	@Override
	public List<Miscellaneous> findByQuery(String query, int numRows)
			throws SolrServerException {		
		List<SolrMiscellaneous> solrMiscellaneousList = serverProcessor.findByQuery(new SolrQuery(query), SolrMiscellaneous.class, numRows);		
		List<Miscellaneous> miscellaneousValues = new ArrayList<>();
		for(SolrMiscellaneous solrMiscellaneous : solrMiscellaneousList){
			miscellaneousValues.add(miscellaneousEntityMapper.toEntityObject(Arrays.asList(solrMiscellaneous)));
		}		
		return miscellaneousValues;
	}

	public void setServerProcessor(SolrServerProcessor serverProcessor) {
		this.serverProcessor = serverProcessor;
	}

	public void setMiscellaneousEntityMapper(
			EntityMapper<SolrMiscellaneous, Miscellaneous> miscellaneousEntityMapper) {
		this.miscellaneousEntityMapper = miscellaneousEntityMapper;
	}

	@Override
	public List<Term> getTopTerms(String termFields, int numRows)
			throws SolrServerException {
		return null;
	}

	@Override
	public Map<String, Integer> getFacetFieldsWithPivots(String query,
			String facetQuery, String facetFields, String pivotFields,
			int numTerms) throws SolrServerException {		
		return null;
	}

	@Override
	public List<Count> getFacetFields(String query, String facetQuery,
			String facetFields, int numTerms) throws SolrServerException {
		return null;
	}

	@Override
	public Map<String, Map<String, String>> getFieldValues(String query, String fieldID, String fields) throws SolrServerException {
		return serverProcessor.getFields(query, fieldID, fields);
	}	
}
