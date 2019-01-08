package uk.ac.ebi.quickgo.annotation.common;

import uk.ac.ebi.quickgo.common.QuickGODocument;

import java.util.Date;
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

    @Field(AnnotationFields.SYMBOL)
    public String symbol;

    @Field(AnnotationFields.GENE_PRODUCT_TYPE)
    public String geneProductType;

    @Field(AnnotationFields.GENE_PRODUCT_SUBSET)
    public String geneProductSubset;

    @Field(AnnotationFields.TAXON_ID)
    public int taxonId;

    @Field(AnnotationFields.TAXON_ANCESTORS)
    public List<Integer> taxonAncestors;

    @Field(AnnotationFields.GO_ID)
    public String goId;

    @Field(AnnotationFields.GO_EVIDENCE)
    public String goEvidence;

    @Field(AnnotationFields.EVIDENCE_CODE)
    public String evidenceCode;

    @Field(AnnotationFields.REFERENCE)
    public String reference;

    @Field(AnnotationFields.WITH_FROM)
    public List<String> withFrom;

    @Field(AnnotationFields.INTERACTING_TAXON_ID)
    public int interactingTaxonId;

    @Field(AnnotationFields.ASSIGNED_BY)
    public String assignedBy;

    @Field(AnnotationFields.EXTENSION) public String extensions;

    @Field(AnnotationFields.TARGET_SET)
    public List<String> targetSets;

    @Field(AnnotationFields.GO_ASPECT)
    public String goAspect;

    @Field(AnnotationFields.DATE)
    public Date date;

    @Field(AnnotationFields.PROTEOME)
    public String proteome;

    @Field(AnnotationFields.DEFAULt_SORT)
    public String defaultSort;

    @Override public String getUniqueName() {
        return id;
    }
}
