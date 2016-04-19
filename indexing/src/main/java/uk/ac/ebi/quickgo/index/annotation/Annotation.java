package uk.ac.ebi.quickgo.index.annotation;

/**
 * An intermediate object used to store the data retrieved from a row in an annotation file.
 *
 * This object can be later transformed into a more fine grained domain object.
 * 
 * Created 19/04/16
 * @author Edd
 */
public class Annotation {
    String name;
    String db;
    String dbObjectId;
    String qualifier;
    String goId;
    String dbReferences;
    String eco;
    String with;
    String interactingTaxonId;
    String date;
    String assignedBy;
    String annotationExtension;
    String annotationProperties;

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Annotation that = (Annotation) o;

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (db != null ? !db.equals(that.db) : that.db != null) {
            return false;
        }
        if (dbObjectId != null ? !dbObjectId.equals(that.dbObjectId) : that.dbObjectId != null) {
            return false;
        }
        if (qualifier != null ? !qualifier.equals(that.qualifier) : that.qualifier != null) {
            return false;
        }
        if (goId != null ? !goId.equals(that.goId) : that.goId != null) {
            return false;
        }
        if (dbReferences != null ? !dbReferences.equals(that.dbReferences) : that.dbReferences != null) {
            return false;
        }
        if (eco != null ? !eco.equals(that.eco) : that.eco != null) {
            return false;
        }
        if (with != null ? !with.equals(that.with) : that.with != null) {
            return false;
        }
        if (interactingTaxonId != null ? !interactingTaxonId.equals(that.interactingTaxonId) :
                that.interactingTaxonId != null) {
            return false;
        }
        if (date != null ? !date.equals(that.date) : that.date != null) {
            return false;
        }
        if (assignedBy != null ? !assignedBy.equals(that.assignedBy) : that.assignedBy != null) {
            return false;
        }
        if (annotationExtension != null ? !annotationExtension.equals(that.annotationExtension) :
                that.annotationExtension != null) {
            return false;
        }
        return annotationProperties != null ? annotationProperties.equals(that.annotationProperties) :
                that.annotationProperties == null;

    }

    @Override public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (db != null ? db.hashCode() : 0);
        result = 31 * result + (dbObjectId != null ? dbObjectId.hashCode() : 0);
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0);
        result = 31 * result + (goId != null ? goId.hashCode() : 0);
        result = 31 * result + (dbReferences != null ? dbReferences.hashCode() : 0);
        result = 31 * result + (eco != null ? eco.hashCode() : 0);
        result = 31 * result + (with != null ? with.hashCode() : 0);
        result = 31 * result + (interactingTaxonId != null ? interactingTaxonId.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (assignedBy != null ? assignedBy.hashCode() : 0);
        result = 31 * result + (annotationExtension != null ? annotationExtension.hashCode() : 0);
        result = 31 * result + (annotationProperties != null ? annotationProperties.hashCode() : 0);
        return result;
    }
}
