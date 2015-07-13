package uk.ac.ebi.quickgo.solr.query.service.geneproduct;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.TermsResponse.Term;
import org.apache.solr.client.solrj.util.ClientUtils;

import uk.ac.ebi.quickgo.geneproduct.GeneProduct;
import uk.ac.ebi.quickgo.solr.mapper.EntityMapper;
import uk.ac.ebi.quickgo.solr.model.geneproduct.SolrGeneProduct;
import uk.ac.ebi.quickgo.solr.model.geneproduct.SolrGeneProduct.SolrGeneProductDocumentType;
import uk.ac.ebi.quickgo.solr.query.model.geneproduct.enums.GeneProductField;
import uk.ac.ebi.quickgo.solr.server.SolrServerProcessor;

/**
 * Service for retrieving Gene Products from Solr
 * @author cbonill
 */
public class GeneProductRetrievalImpl implements GeneProductRetrieval{

	// Log
	private static final Logger logger = LoggerFactory.getLogger(GeneProductRetrievalImpl.class);
	
	SolrServerProcessor serverProcessor;
	
	EntityMapper<SolrGeneProduct, GeneProduct> geneProductEntityMapper;
	
	@Override
	public GeneProduct findById(String id) throws SolrServerException {		
		SolrQuery solrQuery = new SolrQuery().setQuery(GeneProductField.DBOBJECTID.getValue() + ":" + ClientUtils.escapeQueryChars(id)); // IDs can contain characters that must be escaped, e.g., "dbObjectId:O94813:PRO_0000007727"
		List<SolrGeneProduct> results = serverProcessor.findByQuery(solrQuery,SolrGeneProduct.class, -1);
		return geneProductEntityMapper.toEntityObject(results, SolrGeneProductDocumentType.getAsInterfaces());
	}

	@Override
	public List<GeneProduct> findByName(String name) throws SolrServerException {
		SolrQuery solrQuery = new SolrQuery().setQuery(GeneProductField.DBOBJECTNAME.getValue() + ":" + name);
		List<SolrGeneProduct> results = serverProcessor.findByQuery(solrQuery, SolrGeneProduct.class, -1);
		List<GeneProduct> geneProducts = new ArrayList<>();
		if (results != null) {
			for (SolrGeneProduct solrGeneProduct : results) {
				geneProducts.add(geneProductEntityMapper.toEntityObject(Collections.singletonList(solrGeneProduct)));
			}
		}
		return geneProducts;
	}

	@Override
	public List<GeneProduct> findAll() throws SolrServerException {
		List<GeneProduct> geneProducts = new ArrayList<>();
		List<SolrGeneProduct> results = null;
		SolrQuery solrQuery = new SolrQuery().setQuery(GeneProductField.DOCTYPE.getValue() + ":" + SolrGeneProduct.SolrGeneProductDocumentType.GENEPRODUCT.getValue());
		try {
			results = serverProcessor.findByQuery(solrQuery, SolrGeneProduct.class, -1);
		}
		catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		if (results != null) {
			for (SolrGeneProduct solrGeneProduct : results) {
				geneProducts.add(geneProductEntityMapper.toEntityObject(Collections.singletonList(solrGeneProduct)));
			}
		}
		return geneProducts;
	}

	@Override
	public List<GeneProduct> findByQuery(String query, int numRows) throws SolrServerException {
		List<GeneProduct> geneProducts = new ArrayList<>();		 
		SolrQuery solrQuery = new SolrQuery().setQuery(query);
		List<SolrGeneProduct> results = serverProcessor.findByQuery(solrQuery, SolrGeneProduct.class, numRows);
		if (results != null) {
			for (SolrGeneProduct solrTerm : results) {
				geneProducts.add(geneProductEntityMapper.toEntityObject(Collections.singletonList(solrTerm)));
			}
		}
		return geneProducts;
	}

	public void setServerProcessor(SolrServerProcessor serverProcessor) {
		this.serverProcessor = serverProcessor;
	}

	public void setGeneProductEntityMapper(
			EntityMapper<SolrGeneProduct, GeneProduct> geneProductEntityMapper) {
		this.geneProductEntityMapper = geneProductEntityMapper;
	}

	@Override
	public List<Term> getTopTerms(String termFields, int numRows) throws SolrServerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Integer> getFacetFieldsWithPivots(String query, String facetQuery, String facetFields, String pivotFields, int numTerms) throws SolrServerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Count> getFacetFields(String query, String facetQuery, String facetFields, int numTerms) throws SolrServerException {
		// TODO Auto-generated method stub
		return null;
	}	
	
	@Override
	public List<GeneProduct> autosuggest(String text, String filterQuery, int numResults) throws SolrServerException {
		SolrQuery solrQuery = new SolrQuery();		
		solrQuery.setRequestHandler("/spell");
		solrQuery.setQuery("*" + text + "*");				
		solrQuery.setFilterQueries(GeneProductField.DOCTYPE.getValue() + ":" + SolrGeneProductDocumentType.GENEPRODUCT.getValue());
		if(filterQuery != null && !filterQuery.trim().isEmpty()) {
			solrQuery.addFilterQuery(filterQuery);	
		}
		List<GeneProduct> geneproducts = new ArrayList<>();
		List<SolrGeneProduct> results = serverProcessor.findByQuery(solrQuery, SolrGeneProduct.class, numResults);
		if (results != null) {
			for (SolrGeneProduct solrGeneProduct : results) {
				geneproducts.add(geneProductEntityMapper.toEntityObject(Collections.singletonList(solrGeneProduct)));
			}
		}
		return geneproducts;
	}
	
	@Override
	public List<GeneProduct> highlight(String text, String fq, int start, int rows) throws SolrServerException {
		SolrQuery query = new SolrQuery();
		query.setQuery(text);
		query.setFilterQueries(fq);
		query.setHighlight(true);
		query.setParam("hl.fl", GeneProductField.DBOBJECTNAME.getValue());
		List<GeneProduct> geneproducts = new ArrayList<>();
				
		List<SolrGeneProduct> results = serverProcessor.findByQuery(query.setStart(start).setRows(rows), SolrGeneProduct.class, rows);
		if (results != null) {
			for (SolrGeneProduct solrGeneProduct : results) {
				geneproducts.add(geneProductEntityMapper.toEntityObject(Collections.singletonList(solrGeneProduct)));
			}
		}
		return geneproducts;
	}
	
	@Override
	public long getTotalNumberHighlightResults(String text, String fq) throws SolrServerException {
		SolrQuery query = new SolrQuery();		
		query.setQuery(text);
		query.setHighlight(true);
		query.setParam("hl.fl", GeneProductField.DBOBJECTNAME.getValue());
		query.setFilterQueries(fq);
		return serverProcessor.getTotalNumberDocuments(query);
	}	
}
