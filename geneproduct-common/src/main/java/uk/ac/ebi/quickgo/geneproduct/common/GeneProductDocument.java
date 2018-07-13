package uk.ac.ebi.quickgo.geneproduct.common;

import uk.ac.ebi.quickgo.common.QuickGODocument;

import java.util.List;
import java.util.Objects;
import org.apache.solr.client.solrj.beans.Field;

import static uk.ac.ebi.quickgo.geneproduct.common.GeneProductFields.*;

/**
 * Solr document class defining all necessary fields within the gene product core.
 *
 * @author Ricardo antunes
 */
public class GeneProductDocument implements QuickGODocument {
    @Field(ID)
    public String id;

    @Field(DATABASE)
    public String database;

    @Field(SYMBOL)
    public String symbol;

    @Field(NAME)
    public String name;

    @Field(SYNONYM)
    public List<String> synonyms;

    @Field(TYPE)
    public String type;

    @Field(TAXON_ID)
    public int taxonId;

    @Field(TAXON_NAME)
    public String taxonName;

    @Field(DATABASE_SUBSET)
    public String databaseSubset;

    @Field(IS_ANNOTATED)
    public boolean isAnnotated;

    @Field(PARENT_ID)
    public String parentId;

    @Field(PROTEOME)
    public String proteome;

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
        GeneProductDocument that = (GeneProductDocument) o;
        return taxonId == that.taxonId &&
                isAnnotated == that.isAnnotated &&
                Objects.equals(id, that.id) &&
                Objects.equals(database, that.database) &&
                Objects.equals(symbol, that.symbol) &&
                Objects.equals(name, that.name) &&
                Objects.equals(synonyms, that.synonyms) &&
                Objects.equals(type, that.type) &&
                Objects.equals(taxonName, that.taxonName) &&
                Objects.equals(databaseSubset, that.databaseSubset) &&
                Objects.equals(proteome, that.proteome) &&
                Objects.equals(parentId, that.parentId) &&
                Objects.equals(targetSet, that.targetSet);
    }

    @Override public int hashCode() {

        return Objects
                .hash(id, database, symbol, name, synonyms, type, taxonId, taxonName, databaseSubset,
                        proteome, isAnnotated, parentId, targetSet);
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
                ", taxonName='" + taxonName + '\'' +
                ", databaseSubset='" + databaseSubset + '\'' +
                ", proteome='" + proteome + '\'' +
                ", isAnnotated=" + isAnnotated +
                ", parentId='" + parentId + '\'' +
                ", targetSet=" + targetSet +
                '}';
    }
}
