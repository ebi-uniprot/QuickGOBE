package uk.ac.ebi.quickgo.repo.solr.document.ontology;

import org.apache.solr.client.solrj.beans.Field;
import uk.ac.ebi.quickgo.repo.solr.document.QuickGODocument;

import java.util.List;

/**
 * Solr document class defining all fields within the ontology core.
 *
 * See {@link http://docs.spring.io/spring-data/solr/docs/current/reference/html/}.
 *
 * TODO: Discuss
 * I have tried modelling multiple docTypes in different XDocument classes, using
 * basic inheritance/interfaces -- however, this does not work cleanly, and this
 * is not really the place for distinguishing different document types even though
 * they all reside within the same core.
 *
 *      We could possibly have multiple Repositories accessing the same core, for adding/retrieving different
 *      docTypes,
 *      e.g.,
 *      SynonymDocument/TermDocument. However, the CrudRepositories won't then work for queries that fetch
 *      information across multiple Repos (e.g., fetch synonyms for a term).
 *
 *      I believe we should retain a single OntologyRepository, which can have complex queries
 *      defined, spanning whatever docTypes we want. Then, at the service layer, we can
 *      transform results into whatever we want, on demand. I'd suggest transforming into
 *      DTOs. E.g., given results:
 *      [SynDoc1, TermDoc1, SynDoc2, TermDoc2] -> [SynModel1, TermModel1, SynModel2, TermModel2]
 *      where *Model is an object that can be produced from a *Doc object, e.g., using Spring's
 *      converter libraries.
 *
 *      See http://docs.spring.io/spring/docs/current/spring-framework-reference/html/validation.html
 *
 *      Moreover, the DTOs can then be handled at the REST layer, and serialized to whatever format required,
 *      on demand, e.g., json, xml, etc.
 *
 *
 * Created 11/11/15
 * @author Edd
 */
public class OntologyDocument implements QuickGODocument {

    // schema 2.0 -- flattened and cleaned
    @Field
    public String id;           // e.g., 0000001 (for GO:0000001)
    @Field
    public String ontologyType;       // e.g., go      (for GO:0000001)
    @Field
    public String name;
    @Field
    public boolean isObsolete;
    @Field
    public String definition;
    @Field
    public String comment;
    @Field("secondaryId")
    public List<String> secondaryIds;
    @Field
    public String usage;
    // the stored synonym field, which can be reconstructed
    // e.g., [ "syn1|type1", "syn2|type2" ]
    @Field("synonym")
    public List<String> synonyms;
    // the indexed synonym names
    @Field("synonymName")
    public List<String> synonymNames;
    @Field("subset")
    public List<String> subsets;
    @Field
    public String replacedBy;
    @Field("consider")
    public List<String> considers;
    @Field
    public List<String> children;
    @Field("ancestor")
    public List<String> ancestors;
    @Field
    public String aspect;
    @Field
    public List<String> history;
    @Field("xref")
    public List<String> xrefs;
    @Field("taxonConstraint")
    public List<String> taxonConstraints;
    @Field("blacklist")
    public List<String> blacklist;
    @Field("annotationGuideline")
    public List<String> annotationGuidelines;
    @Field("xRelation")
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
