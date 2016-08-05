package uk.ac.ebi.quickgo.annotation.common.document;

import uk.ac.ebi.quickgo.common.QuickGODocument;

import java.util.List;
import org.apache.solr.client.solrj.beans.Field;

/**
 * Solr document class defining all fields within the annotation core.
 *
 * Created 14/04/16
 * @author Edd
 */
public class AnnotationDocument implements QuickGODocument {

    @Field(AnnotationFields.ID)
    public String id;

    @Field(AnnotationFields.GENE_PRODUCT_ID)
    public String geneProductId;

    @Field(AnnotationFields.QUALIFIER)
    public String qualifier;

    @Field(AnnotationFields.DB_OBJECT_SYMBOL)
    public String dbObjectSymbol;

    @Field(AnnotationFields.GENE_PRODUCT_TYPE)
    public String geneProductType;

    @Field(AnnotationFields.DB_SUBSET)
    public String dbSubset;

    @Field(AnnotationFields.TAXON_ID)
    public int taxonId;

    @Field(AnnotationFields.GO_ID)
    public String goId;

    @Field(AnnotationFields.GO_EVIDENCE)
    public String goEvidence;

    @Field(AnnotationFields.ECO_ID)
    public String ecoId;

    @Field(AnnotationFields.REFERENCE)
    public String reference;

    @Field(AnnotationFields.WITH_FROM)
    public List<String> withFrom;

    @Field(AnnotationFields.INTERACTING_TAXON_ID)
    public int interactingTaxonId;

    @Field(AnnotationFields.ASSIGNED_BY)
    public String assignedBy;

    @Field(AnnotationFields.EXTENSION)
    public List<String> extensions;

    @Field(AnnotationFields.TARGET_SET)
    public List<String> targetSets;

    @Override public String getUniqueName() {
        return id;
    }
}
