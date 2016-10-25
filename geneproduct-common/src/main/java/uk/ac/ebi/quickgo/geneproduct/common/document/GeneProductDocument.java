package uk.ac.ebi.quickgo.geneproduct.common.document;

import uk.ac.ebi.quickgo.common.QuickGODocument;

import java.util.List;
import org.apache.solr.client.solrj.beans.Field;

/**
 * Solr document class defining all necessary fields within the gene product core.
 *
 * @author Ricardo antunes
 */
public class GeneProductDocument implements QuickGODocument {

    @Field(GeneProductFields.ID)
    public String id;

    @Field(GeneProductFields.DATABASE)
    public String database;

    @Field(GeneProductFields.SYMBOL)
    public String symbol;

    @Field(GeneProductFields.NAME)
    public String name;

    @Field(GeneProductFields.SYNONYM)
    public List<String> synonyms;

    @Field(GeneProductFields.TYPE)
    public String type;

    @Field(GeneProductFields.TAXON_ID)
    public int taxonId;

    @Field(GeneProductFields.TAXON_NAME)
    public String taxonName;

    @Field(GeneProductFields.DATABASE_SUBSET)
    public String databaseSubset;

    @Field(GeneProductFields.COMPLETE_PROTEOME)
    public boolean isCompleteProteome;

    @Field(GeneProductFields.REFERENCE_PROTEOME)
    public String referenceProteome;

    @Field(GeneProductFields.IS_ISOFORM)
    public boolean isIsoform;

    @Field(GeneProductFields.IS_ANNOTATED)
    public boolean isAnnotated;

    @Field(GeneProductFields.PARENT_ID)
    public String parentId;

    @Field(GeneProductFields.TARGET_SET)
    public List<String> targetSet;

    @Override public String getUniqueName() {
        return id;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GeneProductDocument document = (GeneProductDocument) o;

        if (taxonId != document.taxonId) {
            return false;
        }
        if (isCompleteProteome != document.isCompleteProteome) {
            return false;
        }
        if (isIsoform != document.isIsoform) {
            return false;
        }
        if (isAnnotated != document.isAnnotated) {
            return false;
        }
        if (id != null ? !id.equals(document.id) : document.id != null) {
            return false;
        }
        if (taxonName != null ? !taxonName.equals(document.taxonName) : document.taxonName != null) {
            return false;
        }
        if (database != null ? !database.equals(document.database) : document.database != null) {
            return false;
        }
        if (symbol != null ? !symbol.equals(document.symbol) : document.symbol != null) {
            return false;
        }
        if (name != null ? !name.equals(document.name) : document.name != null) {
            return false;
        }
        if (synonyms != null ? !synonyms.equals(document.synonyms) : document.synonyms != null) {
            return false;
        }
        if (type != null ? !type.equals(document.type) : document.type != null) {
            return false;
        }
        if (databaseSubset != null ? !databaseSubset.equals(document.databaseSubset) :
                document.databaseSubset != null) {
            return false;
        }
        if (referenceProteome != null ? !referenceProteome.equals(document.referenceProteome) :
                document.referenceProteome != null) {
            return false;
        }
        return parentId != null ? parentId.equals(document.parentId) : document.parentId == null;

    }

    @Override public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (database != null ? database.hashCode() : 0);
        result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (synonyms != null ? synonyms.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + taxonId;
        result = 31 * result + (databaseSubset != null ? databaseSubset.hashCode() : 0);
        result = 31 * result + (taxonName != null ? taxonName.hashCode() : 0);
        result = 31 * result + (isCompleteProteome ? 1 : 0);
        result = 31 * result + (referenceProteome != null ? referenceProteome.hashCode() : 0);
        result = 31 * result + (isIsoform ? 1 : 0);
        result = 31 * result + (isAnnotated ? 1 : 0);
        result = 31 * result + (parentId != null ? parentId.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "GeneProductDocument{" +
                "id='" + id + '\'' +
                ", database='" + database + '\'' +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", synonyms=" + synonyms +
                ", type='" + type + '\'' +
                ", taxonId=" + taxonId +
                ", databaseSubset=" + databaseSubset +
                ", taxonName=" + taxonName +
                ", isCompleteProteome=" + isCompleteProteome +
                ", referenceProteome='" + referenceProteome + '\'' +
                ", isIsoform=" + isIsoform +
                ", isAnnotated=" + isAnnotated +
                ", parentId='" + parentId + '\'' +
                '}';
    }
}
