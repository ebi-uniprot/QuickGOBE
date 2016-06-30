package uk.ac.ebi.quickgo.annotation.model;

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

    public String qualifier;

    public String goId;

    public String goEvidence;

    public String ecoId;

    public String reference;

    public List<String> withFrom;

    public int taxonId;

    public String assignedBy;

    public List<String> extensions;

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
        if (!id.equals(that.id)) {
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
        if (goEvidence != null ? !goEvidence.equals(that.goEvidence) : that.goEvidence != null) {
            return false;
        }
        if (ecoId != null ? !ecoId.equals(that.ecoId) : that.ecoId != null) {
            return false;
        }
        if (reference != null ? !reference.equals(that.reference) : that.reference != null) {
            return false;
        }
        if (withFrom != null ? !withFrom.equals(that.withFrom) : that.withFrom != null) {
            return false;
        }
        if (assignedBy != null ? !assignedBy.equals(that.assignedBy) : that.assignedBy != null) {
            return false;
        }
        return extensions != null ? extensions.equals(that.extensions) : that.extensions == null;

    }

    @Override public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (geneProductId != null ? geneProductId.hashCode() : 0);
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0);
        result = 31 * result + (goId != null ? goId.hashCode() : 0);
        result = 31 * result + (goEvidence != null ? goEvidence.hashCode() : 0);
        result = 31 * result + (ecoId != null ? ecoId.hashCode() : 0);
        result = 31 * result + (reference != null ? reference.hashCode() : 0);
        result = 31 * result + (withFrom != null ? withFrom.hashCode() : 0);
        result = 31 * result + taxonId;
        result = 31 * result + (assignedBy != null ? assignedBy.hashCode() : 0);
        result = 31 * result + (extensions != null ? extensions.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "Annotation{" +
                "id='" + id + '\'' +
                ", geneProductId='" + geneProductId + '\'' +
                ", qualifier='" + qualifier + '\'' +
                ", goId='" + goId + '\'' +
                ", goEvidence='" + goEvidence + '\'' +
                ", ecoId='" + ecoId + '\'' +
                ", reference='" + reference + '\'' +
                ", withFrom=" + withFrom +
                ", taxonId=" + taxonId +
                ", assignedBy='" + assignedBy + '\'' +
                ", extensions=" + extensions +
                '}';
    }
}
