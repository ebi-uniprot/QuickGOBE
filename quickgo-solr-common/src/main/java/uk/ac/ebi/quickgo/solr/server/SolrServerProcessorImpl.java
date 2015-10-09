package uk.ac.ebi.quickgo.solr.server;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.TermsResponse;
import org.apache.solr.client.solrj.response.TermsResponse.Term;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Solr server processor implementation
 *
 * @author cbonill
 *
 */
public class SolrServerProcessorImpl implements SolrServerProcessor,Serializable {

	private static final Logger logger = LoggerFactory.getLogger(SolrServerProcessorImpl.class);
	private static final long serialVersionUID = -7846227382523035421L;

	// Solr server
	private HttpSolrServer solrServer;

	// Set via Spring
	private String solrURL;

	// Max number of rows to be returned by Solr queries
	private final int NUM_ROWS = 100000;
	private Properties properties;

	/**
	 * See {@link SolrServerProcessor#findByQuery(SolrQuery, Class, int)}
	 */
	public <T> List<T> findByQuery(SolrQuery solRQuery, Class<T> type, int numRows) throws SolrServerException {
		solRQuery.setRows(numRows <= -1 ? NUM_ROWS : numRows);
		return getSolrServer().query(solRQuery).getBeans(type);
	}

	/**
	 * See {@link SolrServerProcessor#indexBeans(Collection)}
	 */
	public <T> void indexBeans (Collection<T> beans) throws SolrServerException, IOException {
		SolrServer server = getSolrServer();
		server.addBeans(beans);
		server.commit();
	}

	/**
	 * See {@link SolrServerProcessor#indexBeansAutoCommit(Collection)}
	 */
	public <T> void indexBeansAutoCommit(Collection<T> beans) throws SolrServerException, IOException {

		//If the beans length / size of entries is too large is creates a
		// <code>java.lang.OutOfMemoryError: Requested array size exceeds VM limit</code>
		// eer
		getSolrServer().addBeans(beans);
	}

	/**
	 * See {@link SolrServerProcessor#deleteAll()}
	 */
	public void deleteAll() throws SolrServerException, IOException {
		SolrServer server = getSolrServer();
		server.deleteByQuery("*:*");// Deletes everything
		server.commit();
	}

	/**
	 * See {@link SolrServerProcessor#deleteByQuery(String)}
	 */
	public void deleteByQuery(String query) throws SolrServerException, IOException {
		SolrServer server = getSolrServer();
		server.deleteByQuery(query);// Deletes by query
		server.commit();
	}

	/**
	 * See {@link SolrServerProcessor#getTotalNumberDocuments(SolrQuery)}
	 */
	public long getTotalNumberDocuments(SolrQuery query) throws SolrServerException {
		//SolrQuery q = new SolrQuery(query);
	    query.setRows(0);  // Don't actually request any data
	    return getSolrServer().query(query).getResults().getNumFound();
	}

	/**
	 * See {@link SolrServerProcessor#getTopTerms(SolrQuery)}
	 */
	public List<Term> getTopTerms(SolrQuery solrQuery)	throws SolrServerException {
		List<Term> terms = new ArrayList<>();
		QueryResponse queryResponse = getSolrServer().query(solrQuery);
		TermsResponse termsResponse = queryResponse.getTermsResponse();
		Map<String, List<Term>> map = termsResponse.getTermMap();
		for (String id : map.keySet()) {
			terms.addAll(map.get(id));
		}
		return terms;
	}

	/**
	 * See {@link SolrServerProcessor#getFacetTerms(SolrQuery)}
	 */
	@Override
	public List<Count> getFacetTerms(SolrQuery solrQuery) throws SolrServerException {
		List<Count> counts = new ArrayList<>();
		solrQuery.setRows(0);//Don't actually request any data
		QueryResponse queryResponse = getSolrServer().query(solrQuery);
		for (FacetField facetField : queryResponse.getFacetFields()) {
			String facetFieldName = facetField.getName();
			counts = queryResponse.getFacetField(facetFieldName).getValues();
		}
		return counts;
	}

	/**
	 * See {@link SolrServerProcessor#getFields(String, String, String)}
	 */
	public Map<String, Map<String, String>> getFields(String query, String fieldID, String fields) throws SolrServerException{
		Map<String, Map<String, String>> values = new HashMap<>();

		SolrQuery solrQuery = new SolrQuery(query);
		solrQuery.setRows(Integer.MAX_VALUE);
		solrQuery.setFields(fieldID + "," + fields);// Fields to return
		QueryResponse queryResponse = getSolrServer().query(solrQuery);

		for (SolrDocument solrDocument : queryResponse.getResults()) {
			String id = String.valueOf(solrDocument.getFieldValue(fieldID));
			Map<String, String> fieldValues = new HashMap<>();
			for (String field : fields.split(",")) {
				fieldValues.put(field, (String) solrDocument.getFieldValue(field));
			}
			values.put(id, fieldValues);
		}
		return values;
	}

	@Override
	public Map<String, Integer> getFacetTermsWithPivot(SolrQuery solrQuery) throws SolrServerException {
		Map<String, Integer> countsMap = new HashMap<>();
		solrQuery.setRows(0);//Don't actually request any data
		QueryResponse queryResponse = getSolrServer().query(solrQuery);
		NamedList<List<PivotField>> pivots = queryResponse.getFacetPivot();
		List<PivotField> pivotFields = pivots.get(solrQuery.get("facet.pivot"));

		for (PivotField pivotField : pivotFields) {
			List<PivotField> lastPivots = pivotField.getPivot();
			int total = 0;
			for (PivotField lastPivot : lastPivots) {
				total = total + lastPivot.getCount();
			}
			countsMap.put((String)pivotField.getValue(), total);
		}

		return countsMap;
	}

	private SolrServer getSolrServer() {
		if (solrServer == null) {
			solrServer = new HttpSolrServer(solrURL);
			logger.info("Solr url is " + solrURL);
		}
		return solrServer;
	}

	public String getSolrURL() {
		return solrURL;
	}

	public void setSolrURL(String solrURL) {
		this.solrURL = solrURL;
	}

	public void setSolrServer(HttpSolrServer solrServer) {
		this.solrServer = solrServer;
	}

	@Override
	public QueryResponse query(SolrQuery query) throws SolrServerException {
		return getSolrServer().query(query);
	}

	@Override
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	@Override
	public long getTotalNumberDistinctValues(String query, String field) throws SolrServerException {
		SolrQuery solrQuery = new SolrQuery(query);
		solrQuery.add("group", "true");
		solrQuery.add("group.field", field);// Group by the field
		solrQuery.add("group.ngroups", "true");
		solrQuery.setRows(0);
		return getSolrServer().query(solrQuery).getGroupResponse().getValues().get(0).getNGroups();
	}

}
