package uk.ac.ebi.quickgo.repo.solr.document.ontology;

import uk.ac.ebi.quickgo.repo.solr.document.QuickGODocument;

import java.util.List;
import org.apache.solr.client.solrj.beans.Field;

/**
 * Solr document class defining all fields within the ontology core.
 *
 * Created 11/11/15
 * @author Edd
 */
public class OntologyDocument implements QuickGODocument {

    @Field(OntologyFields.ID)
    public String id;

    @Field(OntologyFields.ONTOLOGY_TYPE)
    public String ontologyType;

    @Field(OntologyFields.NAME)
    public String name;

    @Field(OntologyFields.IS_OBSOLETE)
    public boolean isObsolete;

    @Field(OntologyFields.DEFINITION)
    public String definition;

    @Field(OntologyFields.COMMENT)
    public String comment;

    @Field(OntologyFields.SECONDARY_ID)
    public List<String> secondaryIds;

    @Field(OntologyFields.USAGE)
    public String usage;

    @Field(OntologyFields.SYNONYM)
    public List<String> synonyms;

    @Field(OntologyFields.SYNONYM_NAME)
    public List<String> synonymNames;

    @Field(OntologyFields.SUBSET)
    public List<String> subsets;

    @Field(OntologyFields.REPLACED_BY)
    public String replacedBy;

    @Field(OntologyFields.CONSIDER)
    public List<String> considers;

    @Field(OntologyFields.CHILDREN)
    public List<String> children;

    @Field(OntologyFields.ANCESTOR)
    public List<String> ancestors;

    @Field(OntologyFields.ASPECT)
    public String aspect;

    @Field(OntologyFields.HISTORY)
    public List<String> history;

    @Field(OntologyFields.XREF)
    public List<String> xrefs;

    @Field(OntologyFields.TAXON_CONSTRAINT)
    public List<String> taxonConstraints;

    @Field(OntologyFields.BLACKLIST)
    public List<String> blacklist;

    @Field(OntologyFields.ANNOTATION_GUIDELINE)
    public List<String> annotationGuidelines;

    @Field(OntologyFields.XRELATION)
    public List<String> xRelations;

    @Override
    public String getUniqueName() {
        return this.id;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OntologyDocument that = (OntologyDocument) o;

        if (isObsolete != that.isObsolete) {
            return false;
        }
        if (!id.equals(that.id)) {
            return false;
        }
        if (ontologyType != null ? !ontologyType.equals(that.ontologyType) : that.ontologyType != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (definition != null ? !definition.equals(that.definition) : that.definition != null) {
            return false;
        }
        if (comment != null ? !comment.equals(that.comment) : that.comment != null) {
            return false;
        }
        if (secondaryIds != null ? !secondaryIds.equals(that.secondaryIds) : that.secondaryIds != null) {
            return false;
        }
        if (usage != null ? !usage.equals(that.usage) : that.usage != null) {
            return false;
        }
        if (synonyms != null ? !synonyms.equals(that.synonyms) : that.synonyms != null) {
            return false;
        }
        if (synonymNames != null ? !synonymNames.equals(that.synonymNames) : that.synonymNames != null) {
            return false;
        }
        if (subsets != null ? !subsets.equals(that.subsets) : that.subsets != null) {
            return false;
        }
        if (replacedBy != null ? !replacedBy.equals(that.replacedBy) : that.replacedBy != null) {
            return false;
        }
        if (considers != null ? !considers.equals(that.considers) : that.considers != null) {
            return false;
        }
        if (children != null ? !children.equals(that.children) : that.children != null) {
            return false;
        }
        if (ancestors != null ? !ancestors.equals(that.ancestors) : that.ancestors != null) {
            return false;
        }
        if (aspect != null ? !aspect.equals(that.aspect) : that.aspect != null) {
            return false;
        }
        if (history != null ? !history.equals(that.history) : that.history != null) {
            return false;
        }
        if (xrefs != null ? !xrefs.equals(that.xrefs) : that.xrefs != null) {
            return false;
        }
        if (taxonConstraints != null ? !taxonConstraints.equals(that.taxonConstraints) :
                that.taxonConstraints != null) {
            return false;
        }
        if (blacklist != null ? !blacklist.equals(that.blacklist) : that.blacklist != null) {
            return false;
        }
        if (annotationGuidelines != null ? !annotationGuidelines.equals(that.annotationGuidelines) :
                that.annotationGuidelines != null) {
            return false;
        }
        return !(xRelations != null ? !xRelations.equals(that.xRelations) : that.xRelations != null);

    }

    @Override public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (ontologyType != null ? ontologyType.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (isObsolete ? 1 : 0);
        result = 31 * result + (definition != null ? definition.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (secondaryIds != null ? secondaryIds.hashCode() : 0);
        result = 31 * result + (usage != null ? usage.hashCode() : 0);
        result = 31 * result + (synonyms != null ? synonyms.hashCode() : 0);
        result = 31 * result + (synonymNames != null ? synonymNames.hashCode() : 0);
        result = 31 * result + (subsets != null ? subsets.hashCode() : 0);
        result = 31 * result + (replacedBy != null ? replacedBy.hashCode() : 0);
        result = 31 * result + (considers != null ? considers.hashCode() : 0);
        result = 31 * result + (children != null ? children.hashCode() : 0);
        result = 31 * result + (ancestors != null ? ancestors.hashCode() : 0);
        result = 31 * result + (aspect != null ? aspect.hashCode() : 0);
        result = 31 * result + (history != null ? history.hashCode() : 0);
        result = 31 * result + (xrefs != null ? xrefs.hashCode() : 0);
        result = 31 * result + (taxonConstraints != null ? taxonConstraints.hashCode() : 0);
        result = 31 * result + (blacklist != null ? blacklist.hashCode() : 0);
        result = 31 * result + (annotationGuidelines != null ? annotationGuidelines.hashCode() : 0);
        result = 31 * result + (xRelations != null ? xRelations.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "OntologyDocument{" +
                "id='" + id + '\'' +
                ", ontologyType='" + ontologyType + '\'' +
                ", name='" + name + '\'' +
                ", isObsolete=" + isObsolete +
                ", definition='" + definition + '\'' +
                ", comment='" + comment + '\'' +
                ", secondaryIds=" + secondaryIds +
                ", usage='" + usage + '\'' +
                ", synonyms=" + synonyms +
                ", synonymNames=" + synonymNames +
                ", subsets=" + subsets +
                ", replacedBy='" + replacedBy + '\'' +
                ", considers=" + considers +
                ", children=" + children +
                ", ancestors=" + ancestors +
                ", aspect=" + aspect +
                ", history=" + history +
                ", xrefs=" + xrefs +
                ", taxonConstraints=" + taxonConstraints +
                ", blacklist=" + blacklist +
                ", annotationGuidelines=" + annotationGuidelines +
                ", xRelations=" + xRelations +
                '}';
    }
}
