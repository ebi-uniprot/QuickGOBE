package uk.ac.ebi.quickgo.common;

import org.apache.commons.collections4.IterableUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import uk.ac.ebi.quickgo.common.repository.SolrCrudRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class SolrCrudRepositoryImpl<T> implements SolrCrudRepository<T, String> {

    protected final SolrClient solrClient;
    protected final DocumentObjectBinder binder;
    protected final String collection;
    protected final Class<T> documentClass;

    public SolrCrudRepositoryImpl(SolrClient solrClient, String collection, Class<T> documentClass) {
        this.solrClient = solrClient;
        this.binder = new DocumentObjectBinder();
        this.collection = collection;
        this.documentClass = documentClass;
    }

    public @NonNull <S extends T> S save(@NonNull S entity) {
        try {
            SolrInputDocument doc = binder.toSolrInputDocument(entity);
            solrClient.add(collection, doc);
            solrClient.commit(collection);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save entity for collection " + collection, e);
        }
    }

    public @NonNull <S extends T> Iterable<S> saveAll(@NonNull Iterable<S> entities) {
        if (IterableUtils.isEmpty(entities)) {
            return entities;
        }
        try {
            Collection<SolrInputDocument> docs = new ArrayList<>();
            for (T entity : entities) {
                docs.add(binder.toSolrInputDocument(entity));
            }
            solrClient.add(collection, docs);
            solrClient.commit(collection);
            return entities;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save all entities for collection " + collection, e);
        }
    }

    public @NonNull Page<T> findAll(@NonNull Pageable pageable) {
        SolrQuery query = new SolrQuery("*:*");
        return queryForPage(query, pageable);
    }

    public @NonNull List<T> findAll() {
        SolrQuery query = new SolrQuery("*:*");
        query.setRows(Integer.MAX_VALUE);
        return query(query);
    }

    public @NonNull Optional<T> findById(@NonNull String id) {
        throw new UnsupportedOperationException("Method not implemented");
//    SolrQuery query = new SolrQuery("id:" + id);
//
//    try {
//      QueryResponse response = solrClient.query(collection, query);
//      List<T> results = binder.getBeans(documentClass, response.getResults());
//      return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
//    } catch (Exception e) {
//      throw new RuntimeException("Failed to find document by id", e);
//    }
    }

    public @NonNull Iterable<T> findAllById(@NonNull Iterable<String> ids) {
        if (IterableUtils.isEmpty(ids)) {
            return List.of();
        }

        var solrQuery = new SolrQuery("id:" + String.join(" OR id:", ids));
        solrQuery.setRows(IterableUtils.size(ids));
        return query(solrQuery);
    }

    public void deleteById(@NonNull String id) {
        try {
            solrClient.deleteByQuery(collection, "id:" + id);
            solrClient.commit(collection);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete document by id for collection " + collection, e);
        }
    }

    public void deleteAll() {
        try {
            solrClient.deleteByQuery(collection, "*:*");
            solrClient.commit(collection);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete all documents for collection " + collection, e);
        }
    }

    public void deleteAllById(Iterable<? extends String> ids) {
        throw new UnsupportedOperationException("Method not implemented");
//    StringBuilder query = new StringBuilder("id:");
//    query.append(String.join(" OR id:", ids));
//
//    try {
//      solrClient.deleteByQuery(collection, query.toString());
//      solrClient.commit(collection);
//    } catch (Exception e) {
//      throw new RuntimeException("Failed to delete documents by ids", e);
//    }
    }

    public long count() {
        return count(new SolrQuery("*:*"));
    }

    public long count(@NonNull SolrQuery query) {
        try {
            QueryResponse response = solrClient.query(collection, query);
            return response.getResults().getNumFound();
        } catch (Exception e) {
            throw new RuntimeException("Failed to count documents for collection " + collection, e);
        }
    }

    public @NonNull List<T> query(@NonNull SolrQuery query) {
        try {
            QueryResponse response = solrClient.query(collection, query);
            return binder.getBeans(documentClass, response.getResults());
        } catch (Exception e) {
            throw new RuntimeException("Failed to query documents for collection " + collection, e);
        }
    }

//  public List<T> query(SolrQuery query, Pageable pageable) {
//    query.setRows(pageable.getPageSize());
//    query.setStart((int) pageable.getOffset());
//
//    try {
//      QueryResponse response = solrClient.query(collection, query);
//      return binder.getBeans(documentClass, response.getResults());
//    } catch (Exception e) {
//      throw new RuntimeException("Failed to query documents with pagination", e);
//    }
//  }

    public Page<T> queryForPage(SolrQuery query, Pageable pageable) {
        query.setRows(pageable.getPageSize());
        query.setStart((int) pageable.getOffset());

        try {
            QueryResponse response = solrClient.query(collection, query);
            List<T> results = binder.getBeans(documentClass, response.getResults());
            return new PageImpl<>(results, pageable, (int) response.getResults().getNumFound());
        } catch (Exception e) {
            throw new RuntimeException("Failed to perform pageable query documents for collection " + collection, e);
        }
    }


    public void delete(@NonNull T entity) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void deleteAll(@NonNull Iterable<? extends T> entities) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public Iterable<T> findAll(@NonNull Sort sort) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public boolean existsById(@NonNull String s) {
        throw new UnsupportedOperationException("Method not implemented");
    }
}

