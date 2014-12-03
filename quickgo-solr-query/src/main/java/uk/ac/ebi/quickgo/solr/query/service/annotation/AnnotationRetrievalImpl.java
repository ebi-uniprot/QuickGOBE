package uk.ac.ebi.quickgo.solr.query.service.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.TermsResponse.Term;

import uk.ac.ebi.quickgo.annotation.Annotation;
import uk.ac.ebi.quickgo.solr.mapper.EntityMapper;
import uk.ac.ebi.quickgo.solr.model.annotation.SolrAnnotation;
import uk.ac.ebi.quickgo.solr.model.annotation.SolrAnnotation.SolrAnnotationDocumentType;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.solr.server.SolrServerProcessor;

/**
 * Service for retrieving annotations from Solr
 * 
 * @author cbonill
 * 
 */
public class AnnotationRetrievalImpl implements AnnotationRetrieval {
	
	SolrServerProcessor annotationServerProcessor;

	EntityMapper<SolrAnnotation, Annotation> annotationEntityMapper;

	@Override
	public Annotation findById(String id) throws SolrServerException {
		String query = AnnotationField.ID.getValue() + ":" + id;
		SolrQuery solrQuery = new SolrQuery().setQuery(query);
		List<SolrAnnotation> results = (List<SolrAnnotation>) annotationServerProcessor.findByQuery(solrQuery,SolrAnnotation.class, -1);
		return annotationEntityMapper.toEntityObject(results, SolrAnnotationDocumentType.getAsInterfaces());
	}

	@Override
	public List<Annotation> findByName(String name) throws SolrServerException {
		return null;
	}

	@Override
	public List<Annotation> findAll() throws SolrServerException {
		List<Annotation> annotations = new ArrayList<>();
		List<SolrAnnotation> results = (List<SolrAnnotation>) annotationServerProcessor.findByQuery(new SolrQuery("*:*"), SolrAnnotation.class, -1);
		for(SolrAnnotation solrAnnotation : results){
			annotations.add(annotationEntityMapper.toEntityObject(Arrays.asList(solrAnnotation), SolrAnnotationDocumentType.getAsInterfaces()));
		}		
		return annotations;
	}

	@Override
	public List<Annotation> findByQuery(String query, int numRows)
			throws SolrServerException {		
		return annotationServerProcessor.findByQuery(new SolrQuery(query), Annotation.class, -1);
	}

	public void setAnnotationEntityMapper(
			EntityMapper<SolrAnnotation, Annotation> annotationEntityMapper) {
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
		return annotationServerProcessor.getTotalNumberDocuments(new SolrQuery(query));
	}

	@Override
	public long getTotalNumberProteins(String query) throws SolrServerException {		
		return annotationServerProcessor.getTotalNumberDistinctValues(query, AnnotationField.DBOBJECTID.getValue());
	}

	@Override
	public QueryResponse query(String query, String fields, int numRows)
			throws SolrServerException {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(query);
		if (fields != null) {
			solrQuery.setFields(fields);
		}
		solrQuery.setRows(numRows);
		QueryResponse queryResponse = annotationServerProcessor.query(solrQuery);
		return queryResponse;
	}

	@Override
	public List<Annotation> findByQuery(String query, int start, int rows) throws SolrServerException {
		List<Annotation> annotations = new ArrayList<>();
		List<SolrAnnotation> results = (List<SolrAnnotation>) annotationServerProcessor.findByQuery(new SolrQuery(query).setStart(start).setRows(rows),	SolrAnnotation.class, rows);
		for (SolrAnnotation solrAnnotation : results) {
			annotations.add(annotationEntityMapper.toEntityObject(Arrays.asList(solrAnnotation), SolrAnnotationDocumentType.getAsInterfaces()));
		}
		return annotations;
	}

	public void setAnnotationServerProcessor(SolrServerProcessor annotationServerProcessor) {
		this.annotationServerProcessor = annotationServerProcessor;
	}	
}