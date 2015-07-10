package uk.ac.ebi.quickgo.solr.model.annotation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.solr.client.solrj.beans.Field;
import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;

/**
 * Class that represents a GO annotation
 *
 * $Date$
 * $Revision$
 * <p/>
 * $Log$
 */
public class GOAnnotation implements Serializable {
    private static final long serialVersionUID = 2592634776573970012L;

    @Field
   	private String id;
    @Field
   	private String docType;

    @Field
    private String db;
    @Field
    private String dbObjectID;
    @Field
    private String dbObjectSymbol;
    @Field
    private String dbObjectName;
    @Field
    private String dbObjectType;
    @Field
    private List<String> dbObjectSynonyms = new ArrayList<>();
    @Field
    private String qualifier;
    @Field
    private String goID;
    @Field
    private String termName;
    @Field
    private String goAspect;
    @Field
    private String ecoID;
    @Field
    private String goEvidence;
    @Field
    private String reference;
    @Field
    private List<String> with;
    @Field
    private String fullWith;
    @Field
    private String interactingTaxID;
    @Field
    private String date;
    @Field
    private String assignedBy;
    @Field
    private List<String> extension;
    @Field
    private String fullExtension;
    @Field
    private String properties;

    // GO Ancestors, calculated over various sets of relations
    @Field
    private List<String> ancestorsI;
    @Field
    private List<String> ancestorsIPO;
    @Field
    private List<String> ancestorsIPOR;

    // ECO Ancestors
    @Field
    private List<String> ecoAncestorsI;

    // Taxonomy info
    @Field
    private int taxonomyId;
    @Field
    private String taxonomyName;
    @Field
    private List<Integer> taxonomyClosure;

    // Extra fields
    @Field
    private List<String> targetSets;
    @Field
    private int sequenceLength;
    @Field
    private List<String> xrefs;

    public GOAnnotation() {
    }

    public GOAnnotation(String db, String dbObjectID, String qualifier, String goID, String termName, String goAspect, String ecoID, String goEvidence,
                      String reference, List<String> with, String fullWith, String interactingTaxID, String date, String assignedBy,
                      List<String> extension, String fullExtension, String properties) {
        this.db = db;
        this.dbObjectID = dbObjectID;
        this.qualifier = qualifier;
        this.goID = goID;
        this.termName = termName;
        this.goAspect = goAspect;
        this.ecoID = ecoID;
        this.goEvidence = goEvidence;
        this.reference = reference;
        this.with = with;
        this.fullWith = fullWith;
        this.interactingTaxID = interactingTaxID;
        this.date = date;
        this.assignedBy = assignedBy;
        this.extension = extension;
        this.fullExtension = fullExtension;
        this.properties = properties;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getDbObjectID() {
        return dbObjectID;
    }

    public void setDbObjectID(String dbObjectID) {
        this.dbObjectID = dbObjectID;
    }

    public String getDbObjectSymbol() {
        return dbObjectSymbol;
    }

    public void setDbObjectSymbol(String dbObjectSymbol) {
        this.dbObjectSymbol = dbObjectSymbol;
    }

    public String getDbObjectName() {
        return dbObjectName;
    }

    public void setDbObjectName(String dbObjectName) {
        this.dbObjectName = dbObjectName;
    }

    public String getDbObjectType() {
        return dbObjectType;
    }

    public void setDbObjectType(String dbObjectType) {
        this.dbObjectType = dbObjectType;
    }

    public List<String> getDbObjectSynonyms() {
        return dbObjectSynonyms;
    }

    public void setDbObjectSynonyms(List<String> dbObjectSynonyms) {
        this.dbObjectSynonyms = dbObjectSynonyms;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public String getGoID() {
        return goID;
    }

    public void setGoID(String goID) {
        this.goID = goID;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getGoAspect() {
        return goAspect;
    }

    public void setGoAspect(String goAspect) {
        this.goAspect = goAspect;
    }

    public String getEcoID() {
        return ecoID;
    }

    public void setEcoID(String ecoID) {
        this.ecoID = ecoID;
    }

    public String getGoEvidence() {
        return goEvidence;
    }

    public void setGoEvidence(String goEvidence) {
        this.goEvidence = goEvidence;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public List<String> getWith() {
        return with;
    }

    public void setWith(List<String> with) {
        this.with = with;
    }

    public String getFullWith() {
        return fullWith;
    }

    public void setFullWith(String fullWith) {
        this.fullWith = fullWith;
    }

    public String getInteractingTaxID() {
        return interactingTaxID;
    }

    public void setInteractingTaxID(String interactingTaxID) {
        this.interactingTaxID = interactingTaxID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public List<String> getExtension() {
        return extension;
    }

    public void setExtension(List<String> extension) {
        this.extension = extension;
    }

    public String getFullExtension() {
        return fullExtension;
    }

    public void setFullExtension(String fullExtension) {
        this.fullExtension = fullExtension;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public List<String> getAncestorsI() {
        return ancestorsI;
    }

    public void setAncestorsI(List<String> ancestorsI) {
        this.ancestorsI = ancestorsI;
    }

    public List<String> getAncestorsIPO() {
        return ancestorsIPO;
    }

    public void setAncestorsIPO(List<String> ancestorsIPO) {
        this.ancestorsIPO = ancestorsIPO;
    }

    public List<String> getAncestorsIPOR() {
        return ancestorsIPOR;
    }

    public void setAncestorsIPOR(List<String> ancestorsIPOR) {
        this.ancestorsIPOR = ancestorsIPOR;
    }

    public List<String> getEcoAncestorsI() {
        return ecoAncestorsI;
    }

    public void setEcoAncestorsI(List<String> ecoAncestorsI) {
        this.ecoAncestorsI = ecoAncestorsI;
    }

    public int getTaxonomyId() {
        return taxonomyId;
    }

    public void setTaxonomyId(int taxonomyId) {
        this.taxonomyId = taxonomyId;
    }

    public String getTaxonomyName() {
        return taxonomyName;
    }

    public void setTaxonomyName(String taxonomyName) {
        this.taxonomyName = taxonomyName;
    }

    public List<Integer> getTaxonomyClosure() {
        return taxonomyClosure;
    }

    public void setTaxonomyClosure(List<Integer> taxonomyClosure) {
        this.taxonomyClosure = taxonomyClosure;
    }

    public List<String> getTargetSets() {
        return targetSets;
    }

    public void setTargetSets(List<String> targetSets) {
        this.targetSets = targetSets;
    }

    public int getSequenceLength() {
        return sequenceLength;
    }

    public void setSequenceLength(int sequenceLength) {
        this.sequenceLength = sequenceLength;
    }

    public List<String> getXrefs() {
        return xrefs;
    }

    public void setXrefs(List<String> xrefs) {
        this.xrefs = xrefs;
    }


    public enum SolrAnnotationDocumentType implements SolrDocumentType {
   		ANNOTATION("annotation");

   		String value;

   		SolrAnnotationDocumentType(String value) {
   			this.value = value;
   		}

   		public String getValue() {
   			return value;
   		}

   		public void setValue(String value) {
   			this.value = value;
   		}

   		/**
   		 * Get values as SolrDocumentType objects
   		 *
   		 * @return Enum values as SolrDocumentType objects
   		 */
   		public static List<SolrDocumentType> getAsInterfaces() {
            List<SolrDocumentType> documentTypes = new ArrayList<>();
            documentTypes.addAll(Arrays.asList(values()));
            return documentTypes;
   		}

   		/**
   		 * Get value as SolrDocumentType object
   		 *
   		 * @param solrAnnotationDocumentType
   		 *            Value to convert
   		 * @return Value as SolrDocumentType object
   		 */
   		public static SolrDocumentType getAsInterface(SolrAnnotationDocumentType solrAnnotationDocumentType) {
   			return solrAnnotationDocumentType;
   		}
   	}
}
