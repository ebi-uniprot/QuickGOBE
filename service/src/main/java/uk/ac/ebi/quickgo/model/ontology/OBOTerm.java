package uk.ac.ebi.quickgo.model.ontology;

import uk.ac.ebi.quickgo.model.FieldType;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * This is a general term DTO, which should typically contain
 * the general information specified by the Open Biomedical Ontology (OBO) foundry:
 *  http://www.obofoundry.org/
 *
 * Created 19/11/15
 * @author Edd
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OBOTerm {
    // term ID
    public String id;

    // true => obsolete; false otherwise
    public boolean isObsolete;

    // term name
    public String name;

    // term official definition
    public String definition;

    // additional term comment
    public String comment;

    // list of term IDs that are ancestors of this term
    // i.e., IS-A + PART-OF closure of this term
    public List<String> ancestors;

    // synonyms
    public List<Synonym> synonyms;

    // what this term replacedBy
    public String replacedBy;

    // each term can be in one or more subsets; these are used for two purposes: slims and term usage constraints.
    // Slim subsets have names of the form "goslim_xxx", while usage constraint subsets have names like "gocheck_xxx".
    public List<String> subsets;

    public List<String> children;

    public List<String> secondaryIds;

    public List<History> history;

    public List<XRef> xrefs;

    public List<XORelation> xRelations;

    public List<AnnotationGuideLine> annotationGuidelines;
    public List<TaxonConstraint> taxonConstraints;
    public List<BlacklistItem> blacklist;

    public static class Synonym implements FieldType {
        public String synonymName;
        public String synonymType;
    }

    public static class History implements FieldType {
        public String name;
        public String timestamp;
        public String action;
        public String category;
        public String text;
    }

    public static class XRef implements FieldType {
        public String dbCode;
        public String dbId;
        public String name;
    }

    public static class AnnotationGuideLine implements FieldType {
        public String description;
        public String url;
    }

    public static class TaxonConstraint implements FieldType {
        public String ancestorId;
        public String ancestorName;
        public String relationship;
        public String taxId;
        public String taxIdType;
        public String taxName;
        public List<Lit> citations;
    }

    public static class Lit implements FieldType {
        public String id;
    }

    public static class BlacklistItem implements FieldType {
        public String geneProductId;
        public String geneProductDb;
        public String reason;
        public String category;
        public String method;
    }

    public static class XORelation implements FieldType {
        public String id;
        public String term;
        public String namespace;
        public String url;
        public String relation;
    }
}
