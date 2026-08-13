package uk.ac.ebi.quickgo.common.repository;

import org.apache.solr.client.solrj.SolrQuery;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SolrCrudRepository<T, ID> extends PagingAndSortingRepository<T, ID> {
  long count(@NonNull SolrQuery query);

  @NonNull List<T> query(@NonNull SolrQuery query);
}
