package uk.ac.ebi.quickgo.solr.query.service.ontology;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.TermsResponse.Term;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.ontology.eco.ECOTerm;
import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.solr.mapper.EntityMapper;
import uk.ac.ebi.quickgo.solr.mapper.term.eco.EntityECOTermMapper;
import uk.ac.ebi.quickgo.solr.mapper.term.go.EntityGOTermMapper;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm.SolrTermDocumentType;
import uk.ac.ebi.quickgo.solr.query.model.ontology.enums.TermField;
import uk.ac.ebi.quickgo.solr.query.service.Retrieval;
import uk.ac.ebi.quickgo.solr.server.SolrServerProcessor;

@Service("termRetrieval")
public class TermRetrievalImpl implements TermRetrieval,Serializable{

	private static final long serialVersionUID = 2405824287000526742L;

	// Log
	private static final Logger logger = Logger.getLogger(TermRetrievalImpl.class);
		
	
	SolrServerProcessor serverProcessor;
		
	EntityMapper<SolrTerm, GOTerm> termEntityMapper;
	
	/**
	 * See {@link Retrieval#findById(String)}
	 */
	public GOTerm findById(String id) throws SolrServerException {
		String idFormatted = ClientUtils.escapeQueryChars(id);
		String query = TermField.ID.getValue() + ":" + idFormatted + 
				" OR (" + TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.RELATION.getValue() + " AND (" + TermField.CHILD.getValue() + ":" + idFormatted + " OR " + TermField.PARENT.getValue() + ":" + idFormatted + "))" +
						" OR (" + TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.REPLACE.getValue() +" AND " + TermField.OBSOLETE_ID.getValue() + ":" + idFormatted + ")";
		SolrQuery solrQuery = new SolrQuery().setQuery(query);
		List<SolrTerm> results = (List<SolrTerm>) serverProcessor.findByQuery(solrQuery, SolrTerm.class, -1);
		return termEntityMapper.toEntityObject(results, SolrTermDocumentType.getAsInterfaces());
	}

	/**
	 * See
	 * {@link TermRetrieval#findByType(uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm.SolrTermDocumentType)}
	 */
	public List<GOTerm> findByType(SolrTerm.SolrTermDocumentType type) throws SolrServerException {
		List<GOTerm> terms = new ArrayList<>();
		SolrQuery solrQuery = new SolrQuery().setQuery(TermField.TYPE.getValue() + ":" + type.getValue());
		List<SolrTerm> results = (List<SolrTerm>) serverProcessor.findByQuery(solrQuery, SolrTerm.class, -1);
		for (SolrTerm solrTerm : results) {
			terms.add(termEntityMapper.toEntityObject(Arrays.asList(solrTerm)));
		}
		return terms;
	}

	/**
	 * See {@link Retrieval#findByName(String)}
	 */
	public List<GOTerm> findByName(String name) throws SolrServerException {
		List<GOTerm> terms = new ArrayList<>();
		SolrQuery solrQuery = new SolrQuery().setQuery(TermField.NAME.getValue() + ":" + name);
		List<SolrTerm> results = (List<SolrTerm>) serverProcessor.findByQuery(solrQuery, SolrTerm.class, -1);
		for (SolrTerm solrTerm : results) {
			terms.add(termEntityMapper.toEntityObject(Arrays.asList(solrTerm)));
		}
		return terms;
	}

	/**
	 * See {@link Retrieval#findAll()}
	 */
	public List<GOTerm> findAll() {
		List<GOTerm> terms = new ArrayList<>();
		List<SolrTerm> results = null;
		SolrQuery solrQuery = new SolrQuery().setQuery(TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue());
		try {
			results = (List<SolrTerm>) serverProcessor.findByQuery(solrQuery, SolrTerm.class, -1);
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		for (SolrTerm solrTerm : results) {
			terms.add(termEntityMapper.toEntityObject(Arrays.asList(solrTerm)));
		}
		return terms;
	}

	/**
	 * See {@link Retrieval#findByQuery(String)}
	 */	
	public List<GOTerm> findByQuery(String query, int numRows) throws SolrServerException {
		List<GOTerm> terms = new ArrayList<>();		 
		SolrQuery solrQuery = new SolrQuery().setQuery(query);
		List<SolrTerm> results = (List<SolrTerm>) serverProcessor.findByQuery(solrQuery, SolrTerm.class, numRows);
		for (SolrTerm solrTerm : results) {
			terms.add(termEntityMapper.toEntityObject(Arrays.asList(solrTerm)));
		}
		return terms;
	}

	public void setServerProcessor(SolrServerProcessor serverProcessor) {
		this.serverProcessor = serverProcessor;
	}

	public void setTermEntityMapper(EntityMapper<SolrTerm, GOTerm> termEntityMapper) {
		this.termEntityMapper = termEntityMapper;
	}

	@Override
	public List<Term> getTopTerms(String termFields, int numRows)
			throws SolrServerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Integer> getFacetFieldsWithPivots(String query,
			String facetQuery, String facetFields, String pivotFields,
			int numTerms) throws SolrServerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Count> getFacetFields(String query, String facetQuery,
			String facetFields, int numTerms) throws SolrServerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public  Map<String, Map<String, String>> getFieldValues(String query, String fieldID, String fields) throws SolrServerException {
		return serverProcessor.getFields(query, fieldID, fields);		
	}

	@Override
	public List<GenericTerm> autosuggest(String text, String filterQuery, int numResults) throws SolrServerException {
		SolrQuery solrQuery = new SolrQuery();		
		solrQuery.setRequestHandler("/spell");
		String fQuery = TermField.TYPE.getValue() + ":" //Search for terms and synonyms
				+ SolrTermDocumentType.TERM.getValue() + " OR "
				+ TermField.TYPE.getValue() + ":"
				+ SolrTermDocumentType.SYNONYM.getValue();
		if (text.contains(":")) {//Replace ':' character
			text = text.replaceAll(":", "\":\"");
			fQuery = TermField.TYPE.getValue() + ":" + SolrTermDocumentType.TERM.getValue();// Just search for terms results
			text = "*" + text + "*";
		}
		//solrQuery.setQuery("*" + text + "*");
		solrQuery.setQuery(text);
		
		solrQuery.setFilterQueries(fQuery, filterQuery);
		List<GenericTerm> terms = new ArrayList<GenericTerm>();
		List<SolrTerm> results = (List<SolrTerm>)serverProcessor.findByQuery(solrQuery, SolrTerm.class, numResults);
		for (SolrTerm solrTerm : results) {
			if (solrTerm.getId().contains(ECOTerm.ECO.toString())) {
				terms.add(new EntityECOTermMapper().toEntityObject(Arrays.asList(solrTerm)));
			} else {
				terms.add(new EntityGOTermMapper().toEntityObject(Arrays.asList(solrTerm)));
			}
		}
		return terms;
	}

	@Override
	public List<GenericTerm> highlight(String text, String fq, int start, int rows) throws SolrServerException {
		SolrQuery query = new SolrQuery();
		query.setQuery(text);
		query.setFilterQueries(fq);
		query.setHighlight(true);
		query.setParam("hl.fl", TermField.NAME.getValue());
		List<GenericTerm> terms = new ArrayList<GenericTerm>();
				
		List<SolrTerm> results = (List<SolrTerm>)serverProcessor.findByQuery(query.setStart(start).setRows(rows), SolrTerm.class, rows);
		for (SolrTerm solrTerm : results) {
			if (solrTerm.getId().contains(ECOTerm.ECO.toString())) {
				terms.add(new EntityECOTermMapper().toEntityObject(Arrays.asList(solrTerm)));
			} else {
				terms.add(new EntityGOTermMapper().toEntityObject(Arrays.asList(solrTerm)));
			}
		}
		return terms;
	}
	

	@Override
	public long getTotalNumberHighlightResults(String text, String fq) throws SolrServerException {
		SolrQuery query = new SolrQuery();		
		query.setQuery(text);
		query.setHighlight(true);
		query.setParam("hl.fl", TermField.NAME.getValue());
		query.setFilterQueries(fq);
		return serverProcessor.getTotalNumberDocuments(query);
	}	
}