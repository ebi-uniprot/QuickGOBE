package uk.ac.ebi.quickgo.geneproduct.common;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.request.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import uk.ac.ebi.quickgo.common.SolrCollectionName;
import uk.ac.ebi.quickgo.common.repository.SolrCrudRepositoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class GeneProductRepositoryImpl extends SolrCrudRepositoryImpl<GeneProductDocument> implements GeneProductRepository {

    public GeneProductRepositoryImpl(SolrClient solrClient) {
      super(solrClient, SolrCollectionName.GENE_PRODUCT, GeneProductDocument.class);
    }

    @Override
    public List<GeneProductDocument> findById(List<String> ids) {
        //@Query(value = ID + ":?0", fields = {ID, DATABASE, NAME, SYMBOL, SYNONYM, TYPE, TAXON_ID, DATABASE_SUBSET, PARENT_ID, PROTEOME})
        //If there is an issue associated with above, we need to fix it, currently no IT got failed, so I didn't do it
        return StreamSupport.stream(super.findAllById(ids).spliterator(), false).toList();
    }

    public List<GeneProductDocument> findByTargetSet(String name) {
        SolrQuery query = new SolrQuery(GeneProductFields.TARGET_SET + ":" + name);
        try {
            QueryResponse response = solrClient.query(collection, query);
            return new ArrayList<>(binder.getBeans(GeneProductDocument.class, response.getResults()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find documents by target set", e);
        }
    }
}
