package uk.ac.ebi.quickgo.repo.solr.document.geneproduct;

import org.apache.solr.client.solrj.beans.Field;
import uk.ac.ebi.quickgo.repo.solr.document.QuickGODocument;

/**
 * Created by edd on 16/01/2016.
 */
public class GeneProductDocument implements QuickGODocument {
    @Field
    public String dbObjectId;

    @Field
    public String id;

    @Override
    public String getUniqueName() {
        return dbObjectId;
    }
}
