package uk.ac.ebi.quickgo.annotation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Annotation DTO used by the service layer.
 *
 * @author Tony Wardell
 *         Date: 21/04/2016
 *         Time: 11:28
 *         Created with IntelliJ IDEA.
 */
public class Annotation {

    public String id;

    public String geneProductId;

    @JsonIgnore public String canonicalId;

    @JsonIgnore private GeneProduct geneProduct;

    public String qualifier;

    public String goId;

    /**
     * An optional field whose value originates from an external service.
     */
    public String goName;

    public String goEvidence;

    public String goAspect;

    public String evidenceCode;

    public String reference;

    public List<ConnectedXRefs<Annotation.SimpleXRef>> withFrom;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public int taxonId;

    /**
     * An optional field whose value originates from an external service.
     */
    public String taxonName;

    public String assignedBy;

    public List<ConnectedXRefs<RelationXref>> extensions;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<String> slimmedIds;

    public List<String> targetSets;

    public String symbol;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
    public Date date;

    public int interactingTaxonId;

    /**
     * Optional fields whose value originates from the gene product service.
     */
    public String synonyms;
    public String name;

    public void setGeneProduct(GeneProduct geneProduct) {
        this.geneProduct = geneProduct;
    }

    @JsonIgnore
    public GeneProduct getGeneProduct() {
        return geneProduct;
    }

    @Override public String toString() {
        return "Annotation{" +
                "id='" + id + '\'' +
                ", geneProductId='" + geneProductId + '\'' +
                ", qualifier='" + qualifier + '\'' +
                ", goId='" + goId + '\'' +
                ", goName='" + goName + '\'' +
                ", goEvidence='" + goEvidence + '\'' +
                ", goAspect='" + goAspect + '\'' +
                ", evidenceCode='" + evidenceCode + '\'' +
                ", reference='" + reference + '\'' +
                ", withFrom=" + withFrom +
                ", taxonId=" + taxonId +
                ", taxonName='" + taxonName + '\'' +
                ", assignedBy='" + assignedBy + '\'' +
                ", extensions=" + extensions +
                ", slimmedIds=" + slimmedIds +
                ", targetSets=" + targetSets +
                ", symbol='" + symbol + '\'' +
                ", date=" + date +
                ", interactingTaxonId=" + interactingTaxonId +
                ", synonyms='" + synonyms + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Annotation that = (Annotation) o;

        if (taxonId != that.taxonId) {
            return false;
        }
        if (interactingTaxonId != that.interactingTaxonId) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (geneProductId != null ? !geneProductId.equals(that.geneProductId) : that.geneProductId != null) {
            return false;
        }
        if (qualifier != null ? !qualifier.equals(that.qualifier) : that.qualifier != null) {
            return false;
        }
        if (goId != null ? !goId.equals(that.goId) : that.goId != null) {
            return false;
        }
        if (goName != null ? !goName.equals(that.goName) : that.goName != null) {
            return false;
        }
        if (goEvidence != null ? !goEvidence.equals(that.goEvidence) : that.goEvidence != null) {
            return false;
        }
        if (goAspect != null ? !goAspect.equals(that.goAspect) : that.goAspect != null) {
            return false;
        }
        if (evidenceCode != null ? !evidenceCode.equals(that.evidenceCode) : that.evidenceCode != null) {
            return false;
        }
        if (reference != null ? !reference.equals(that.reference) : that.reference != null) {
            return false;
        }
        if (withFrom != null ? !withFrom.equals(that.withFrom) : that.withFrom != null) {
            return false;
        }
        if (taxonName != null ? !taxonName.equals(that.taxonName) : that.taxonName != null) {
            return false;
        }
        if (assignedBy != null ? !assignedBy.equals(that.assignedBy) : that.assignedBy != null) {
            return false;
        }
        if (extensions != null ? !extensions.equals(that.extensions) : that.extensions != null) {
            return false;
        }
        if (slimmedIds != null ? !slimmedIds.equals(that.slimmedIds) : that.slimmedIds != null) {
            return false;
        }
        if (targetSets != null ? !targetSets.equals(that.targetSets) : that.targetSets != null) {
            return false;
        }
        if (symbol != null ? !symbol.equals(that.symbol) : that.symbol != null) {
            return false;
        }
        if (date != null ? !date.equals(that.date) : that.date != null) {
            return false;
        }
        if (synonyms != null ? !synonyms.equals(that.synonyms) : that.synonyms != null) {
            return false;
        }
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (geneProductId != null ? geneProductId.hashCode() : 0);
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0);
        result = 31 * result + (goId != null ? goId.hashCode() : 0);
        result = 31 * result + (goName != null ? goName.hashCode() : 0);
        result = 31 * result + (goEvidence != null ? goEvidence.hashCode() : 0);
        result = 31 * result + (goAspect != null ? goAspect.hashCode() : 0);
        result = 31 * result + (evidenceCode != null ? evidenceCode.hashCode() : 0);
        result = 31 * result + (reference != null ? reference.hashCode() : 0);
        result = 31 * result + (withFrom != null ? withFrom.hashCode() : 0);
        result = 31 * result + taxonId;
        result = 31 * result + (taxonName != null ? taxonName.hashCode() : 0);
        result = 31 * result + (assignedBy != null ? assignedBy.hashCode() : 0);
        result = 31 * result + (extensions != null ? extensions.hashCode() : 0);
        result = 31 * result + (slimmedIds != null ? slimmedIds.hashCode() : 0);
        result = 31 * result + (targetSets != null ? targetSets.hashCode() : 0);
        result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + interactingTaxonId;
        result = 31 * result + (synonyms != null ? synonyms.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    /**
     * Represents a connected list of {@link AbstractXref} instances in the with/from or extensions column.
     *
     * See <a href="http://geneontology.org/page/go-annotation-file-gaf-format-21">GAF format</a>
     */
    public static class ConnectedXRefs<T extends AbstractXref> {
        private List<T> connectedXrefs;

        public ConnectedXRefs() {
            this.connectedXrefs = new ArrayList<>();
        }

        public void addXref(T xref) {
            connectedXrefs.add(xref);
        }

        public List<T> getConnectedXrefs() {
            return Collections.unmodifiableList(connectedXrefs);
        }

        @Override public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            ConnectedXRefs xrefs = (ConnectedXRefs) o;

            return connectedXrefs != null ? connectedXrefs.equals(xrefs.connectedXrefs) : xrefs.connectedXrefs == null;

        }

        @Override public int hashCode() {
            return connectedXrefs != null ? connectedXrefs.hashCode() : 0;
        }

        @Override public String toString() {
            return "Xrefs{" +
                    "connectedXrefs=" + connectedXrefs +
                    '}';
        }
    }

    public static abstract class AbstractXref {
        String db;
        protected String id;

        AbstractXref(String db, String id) {
            this.db = db;
            this.id = id;
        }

        public String getDb() {
            return db;
        }

        public String getId() {
            return id;
        }

        public String asXref() {
            return String.format("%s:%s", db, id);
        }

        @Override public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            AbstractXref that = (AbstractXref) o;

            if (db != null ? !db.equals(that.db) : that.db != null) {
                return false;
            }
            return id != null ? id.equals(that.id) : that.id == null;

        }

        @Override public int hashCode() {
            int result = db != null ? db.hashCode() : 0;
            result = 31 * result + (id != null ? id.hashCode() : 0);
            return result;
        }
    }

    /**
     * Class that represents a simple cross-reference containing just the database name and the entry id of
     * the Xref. Simple Xrefs can be found in the with state attribute.
     */
    public static class SimpleXRef extends AbstractXref {
        public SimpleXRef(String database, String signature) {
            super(database, signature);
        }

        @Override public String toString() {
            return "SimpleXref{" +
                    "database='" + db + '\'' +
                    ", id='" + id + '\'' +
                    '}';
        }
    }

    /**
     * Class that represents a cross-reference (database name and entry id) with an associated relation
     * containing just the database name. Relation Xrefs can be found in the extension state attribute.
     */
    public static class RelationXref extends AbstractXref {
        private String relation;

        public RelationXref(String database, String signature, String relation) {
            super(database, signature);
            this.relation = relation;
        }

        public String getRelation() {
            return relation;
        }

        @Override public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof RelationXref)) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }

            RelationXref that = (RelationXref) o;

            return relation != null ? relation.equals(that.relation) : that.relation == null;
        }

        @Override
        public String asXref() {
            return String.format("%s(%s:%s)", relation, db, id);
        }

        @Override public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (relation != null ? relation.hashCode() : 0);
            return result;
        }

        @Override public String toString() {
            return "RelationXref{" +
                    "database='" + db + '\'' +
                    ", id='" + id + '\'' +
                    ", relation='" + relation + '\'' +
                    '}';
        }
    }
}
