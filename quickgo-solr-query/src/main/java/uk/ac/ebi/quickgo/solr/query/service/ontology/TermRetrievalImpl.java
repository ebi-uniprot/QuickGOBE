package uk.ac.ebi.quickgo.solr.query.service.ontology;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.TermsResponse.Term;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("termRetrieval")
public class TermRetrievalImpl implements TermRetrieval, Serializable {
    private static final long serialVersionUID = 2405824287000526742L;

    // Log
    private static final Logger logger = LoggerFactory.getLogger(TermRetrievalImpl.class);

    SolrServerProcessor serverProcessor;

    //TODO: this all needs to be cleaned up - there appears to be some confusion about whether we're dealing with
    // GenericTerm, GOTerm, or ECOTerm objects
    EntityMapper<SolrTerm, GOTerm> termEntityMapper;
    private static final EntityGOTermMapper goTermMapper = new EntityGOTermMapper();
    private static final EntityECOTermMapper ecoTermMapper = new EntityECOTermMapper();

    /**
     * See {@link Retrieval#findById(String)}
     */
    public GOTerm findById(String id) throws SolrServerException {
        String idFormatted = convertToSolrCompatibleText(id);
        String query = TermField.ID.getValue() + ":" + idFormatted +
                " OR (" + TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.RELATION.getValue() +
                " AND (" + TermField.CHILD.getValue() + ":" + idFormatted + " OR " + TermField.PARENT.getValue() + ":" +
                idFormatted + "))" +
                " OR (" + TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.REPLACE.getValue() + " AND " +
                TermField.OBSOLETE_ID.getValue() + ":" + idFormatted + ")";
        SolrQuery solrQuery = new SolrQuery().setQuery(query);
        List<SolrTerm> results = serverProcessor.findByQuery(solrQuery, SolrTerm.class, -1);
        return termEntityMapper.toEntityObject(results, SolrTermDocumentType.getAsInterfaces());
    }

    /**
     * See
     * {@link TermRetrieval#findByType(uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm.SolrTermDocumentType)}
     */
    public List<GOTerm> findByType(SolrTerm.SolrTermDocumentType type) throws SolrServerException {
        List<GOTerm> terms = new ArrayList<>();
        SolrQuery solrQuery = new SolrQuery().setQuery(TermField.TYPE.getValue() + ":" + type.getValue());
        List<SolrTerm> results = serverProcessor.findByQuery(solrQuery, SolrTerm.class, -1);
        if (results != null) {
            for (SolrTerm solrTerm : results) {
                terms.add(termEntityMapper.toEntityObject(Collections.singletonList(solrTerm)));
            }
        }
        return terms;
    }

    /**
     * See {@link Retrieval#findByName(String)}
     */
    public List<GOTerm> findByName(String name) throws SolrServerException {
        List<GOTerm> terms = new ArrayList<>();
        SolrQuery solrQuery = new SolrQuery().setQuery(TermField.NAME.getValue() + ":" + name);
        List<SolrTerm> results = serverProcessor.findByQuery(solrQuery, SolrTerm.class, -1);
        if (results != null) {
            for (SolrTerm solrTerm : results) {
                terms.add(termEntityMapper.toEntityObject(Collections.singletonList(solrTerm)));
            }
        }
        return terms;
    }

    /**
     * See {@link Retrieval#findAll()}
     */
    public List<GOTerm> findAll() {
        List<GOTerm> terms = new ArrayList<>();
        List<SolrTerm> results = null;
        SolrQuery solrQuery = new SolrQuery()
                .setQuery(TermField.TYPE.getValue() + ":" + SolrTerm.SolrTermDocumentType.TERM.getValue());
        try {
            results = serverProcessor.findByQuery(solrQuery, SolrTerm.class, -1);
        } catch (SolrServerException e) {
            logger.error(e.getMessage());
        }
        if (results != null) {
            for (SolrTerm solrTerm : results) {
                terms.add(termEntityMapper.toEntityObject(Collections.singletonList(solrTerm)));
            }
        }
        return terms;
    }

    /**
     * See {@link Retrieval#findByQuery(String, int)}
     */
    public List<GOTerm> findByQuery(String query, int numRows) throws SolrServerException {
        List<GOTerm> terms = new ArrayList<>();
        SolrQuery solrQuery = new SolrQuery().setQuery(query);
        List<SolrTerm> results = serverProcessor.findByQuery(solrQuery, SolrTerm.class, numRows);
        if (results != null) {
            for (SolrTerm solrTerm : results) {
                terms.add(termEntityMapper.toEntityObject(Collections.singletonList(solrTerm)));
            }
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
    public List<Term> getTopTerms(String termFields, int numRows) throws SolrServerException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Integer> getFacetFieldsWithPivots(String query, String facetQuery, String facetFields,
                                                         String pivotFields, int numTerms) throws SolrServerException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Count> getFacetFields(String query, String facetQuery, String facetFields, int numTerms)
            throws SolrServerException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Map<String, String>> getFieldValues(String query, String fieldID, String fields)
            throws SolrServerException {
        return serverProcessor.getFields(query, fieldID, fields);
    }

    @Override
    public List<GenericTerm> autosuggest(String text, String filterQuery, int numResults) throws SolrServerException {
        List<GenericTerm> terms = new ArrayList<>();
        SolrQuery solrQuery = new SolrQuery(convertToSolrCompatibleText(text));
        solrQuery.setParam("group", true);
        solrQuery.add("group.field", "id");

        List<SolrTerm> results = serverProcessor.groupByQuery(solrQuery, SolrTerm.class, numResults);
        if (results != null) {
            for (SolrTerm solrTerm : results) {
                if (solrTerm.getId().startsWith(ECOTerm.ECO)) {
                    terms.add(ecoTermMapper.toEntityObject(Collections.singletonList(solrTerm)));
                } else {
                    terms.add(goTermMapper.toEntityObject(Collections.singletonList(solrTerm)));
                }
            }
        }

        return terms;
    }

    public List<GenericTerm> autosuggestOnlyGoTerms(String text, String fq, int numResults) throws SolrServerException {
        String queryText = convertToSolrCompatibleText(text) + " AND " + TermField.DOCTYPE.getValue() + ":" + "term";

        SolrQuery solrQuery = new SolrQuery(queryText);
        solrQuery.addSort(TermField.ID.getValue(), SolrQuery.ORDER.asc);

        List<SolrTerm> results = serverProcessor.findByQuery(solrQuery, SolrTerm.class, numResults);

        List<GenericTerm> terms = new ArrayList<>();
        if (results != null) {
            for (SolrTerm solrTerm : results) {
                if (solrTerm.getId().startsWith(ECOTerm.ECO)) {
                    terms.add(ecoTermMapper.toEntityObject(Collections.singletonList(solrTerm)));
                } else {
                    terms.add(goTermMapper.toEntityObject(Collections.singletonList(solrTerm)));
                }
            }
        }

        return terms;
    }

    @Override
    public List<GenericTerm> highlight(String text, String fq, int start, int rows) throws SolrServerException {
        SolrQuery query = new SolrQuery(convertToSolrCompatibleText(text));
        query.setFilterQueries(fq);
        query.setHighlight(true);
        query.setParam("hl.fl", TermField.NAME.getValue());
        List<GenericTerm> terms = new ArrayList<>();

        List<SolrTerm> results = serverProcessor.findByQuery(query.setStart(start).setRows(rows), SolrTerm.class, rows);
        if (results != null) {
            for (SolrTerm solrTerm : results) {
                if (solrTerm.getId().startsWith(ECOTerm.ECO)) {
                    terms.add(ecoTermMapper.toEntityObject(Collections.singletonList(solrTerm)));
                } else {
                    terms.add(goTermMapper.toEntityObject(Collections.singletonList(solrTerm)));
                }
            }
        }
        return terms;
    }

    @Override
    public long getTotalNumberHighlightResults(String text, String fq) throws SolrServerException {
        SolrQuery query = new SolrQuery(convertToSolrCompatibleText(text));
        query.setHighlight(true);
        query.setParam("hl.fl", TermField.NAME.getValue());
        query.setFilterQueries(fq);
        return serverProcessor.getTotalNumberDocuments(query);
    }

    private String convertToSolrCompatibleText(String search) {
        return ClientUtils.escapeQueryChars(search);
    }
}
