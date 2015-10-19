package uk.ac.ebi.quickgo.solr.query.service.annotation;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.TermsResponse.Term;

import uk.ac.ebi.quickgo.solr.mapper.EntityMapper;
import uk.ac.ebi.quickgo.solr.model.annotation.GOAnnotation;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.solr.query.service.statistics.StatsCache;
import uk.ac.ebi.quickgo.solr.server.SolrServerProcessor;

/**
 * Service for retrieving annotations from Solr
 *
 * @author cbonill
 *
 */
public class AnnotationRetrievalImpl implements AnnotationRetrieval {

	SolrServerProcessor annotationServerProcessor;

	EntityMapper<GOAnnotation, GOAnnotation> annotationEntityMapper;



	@Override
	public GOAnnotation findById(String id) throws SolrServerException {
/*
		String query = AnnotationField.ID.getValue() + ":" + id;
		SolrQuery solrQuery = new SolrQuery().setQuery(query);
		List<GOAnnotation> results = (List<GOAnnotation>) annotationServerProcessor.findByQuery(solrQuery,GOAnnotation.class, -1);
		return annotationEntityMapper.toEntityObject(results, GOAnnotation.SolrAnnotationDocumentType.getAsInterfaces());
*/
		List l = annotationServerProcessor.findByQuery(new SolrQuery().setQuery(AnnotationField.ID.getValue() + ":" + id), GOAnnotation.class, -1);
		return (l.size() == 1) ? (GOAnnotation)l.get(0) : null;
	}

	@Override
	public List<GOAnnotation> findByName(String name) throws SolrServerException {
		return null;
	}

	@Override
	public List<GOAnnotation> findAll() throws SolrServerException {
/*
		List<GOAnnotation> annotations = new ArrayList<>();
		List<GOAnnotation> results = (List<GOAnnotation>) annotationServerProcessor.findByQuery(new SolrQuery("*:*"), GOAnnotation.class, -1);
		for(GOAnnotation annotation : results){
			annotations.add(annotationEntityMapper.toEntityObject(Arrays.asList(annotation), GOAnnotation.SolrAnnotationDocumentType.getAsInterfaces()));
		}
		return annotations;
*/
		// this can now be replaced, I think, by...
		return annotationServerProcessor.findByQuery(new SolrQuery("*:*"), GOAnnotation.class, -1);
	}

	@Override
	public List<GOAnnotation> findByQuery(String query, int numRows) throws SolrServerException {
		return annotationServerProcessor.findByQuery(new SolrQuery(query), GOAnnotation.class, -1);
	}

	public void setAnnotationEntityMapper(EntityMapper<GOAnnotation, GOAnnotation> annotationEntityMapper) {
		this.annotationEntityMapper = annotationEntityMapper;
	}

	@Override
	public List<Term> getTopTerms(String termFields, int numRows) throws SolrServerException {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setRequestHandler("/terms");
		solrQuery.setTerms(true);
		for(String term : termFields.split(",")){
			solrQuery.addTermsField(term);
		}
		solrQuery.setTermsLimit(numRows);

		return annotationServerProcessor.getTopTerms(solrQuery);
	}

	@Override
	public List<Count> getFacetFields(String query, String facetQuery, String facetFields, int numTerms) throws SolrServerException {
		SolrQuery solrQuery = new SolrQuery(query);
		solrQuery.setFacetMinCount(1);
		if (facetQuery != null) {
			solrQuery.addFacetQuery(facetQuery);
		}
		solrQuery.addFacetField(facetFields);
		solrQuery.setFacetLimit(numTerms);
		solrQuery.setFacetSort("count");// Highest first
		return annotationServerProcessor.getFacetTerms(solrQuery);
	}

	@Override
	public Map<String, Integer> getFacetFieldsWithPivots(String query, String facetQuery, String facetFields, String pivotFields, int numTerms) throws SolrServerException {
		SolrQuery solrQuery = new SolrQuery(query);
		if (facetQuery != null) {
			solrQuery.addFacetQuery(facetQuery);
		}
		solrQuery.addFacetField(facetFields);
		solrQuery.setFacetLimit(numTerms);
		solrQuery.setFacetMinCount(1);
		solrQuery.addFacetPivotField(pivotFields);
		solrQuery.setFacetSort("count");// Highest first
		return annotationServerProcessor.getFacetTermsWithPivot(solrQuery);
	}

	@Override
	public long getTotalNumberAnnotations(String query) throws SolrServerException {
		if(StatsCache.INSTANCE.getTotalAnnotations(query)==0){
			StatsCache.INSTANCE.setTotalAnnotations(query,annotationServerProcessor.getTotalNumberDocuments(new SolrQuery(query)));
		}
		return StatsCache.INSTANCE.getTotalAnnotations(query);
	}

	@Override
	public long getTotalNumberProteins(String query) throws SolrServerException {
		if(StatsCache.INSTANCE.getTotalGeneProducts(query)==0){
			StatsCache.INSTANCE.setTotalGeneProducts(query, annotationServerProcessor.getTotalNumberDistinctValues(query, AnnotationField.DBOBJECTID.getValue()));
		}
		return StatsCache.INSTANCE.getTotalGeneProducts(query);
	}

	@Override
	public QueryResponse query(String query, String fields, int numRows) throws SolrServerException {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(query);
		if (fields != null) {
			solrQuery.setFields(fields);
		}
		solrQuery.setRows(numRows);
		return annotationServerProcessor.query(solrQuery);
	}

	@Override
	public List<GOAnnotation> findByQuery(String query, int start, int rows) throws SolrServerException {
/*
		List<GOAnnotation> annotations = new ArrayList<>();
		List<GOAnnotation> results = (List<GOAnnotation>) annotationServerProcessor.findByQuery(new SolrQuery(query).setStart(start).setRows(rows), GOAnnotation.class, rows);
		for (GOAnnotation annotation : results) {
			annotations.add(annotationEntityMapper.toEntityObject(Arrays.asList(annotation), GOAnnotation.SolrAnnotationDocumentType.getAsInterfaces()));
		}
		return annotations;
*/
		// I'm pretty sure this can now all be replaced by...
		return annotationServerProcessor.findByQuery(new SolrQuery(query).setStart(start).setRows(rows), GOAnnotation.class, rows);

	}

	public void setAnnotationServerProcessor(SolrServerProcessor annotationServerProcessor) {
		this.annotationServerProcessor = annotationServerProcessor;
	}
}
