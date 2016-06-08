package uk.ac.ebi.quickgo.ontology.model;

import uk.ac.ebi.quickgo.common.FieldType;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * This is a general term DTO, which should typically contain
 * the general information specified by the Open Biomedical Ontology (OBO) foundry:
 *  http://www.obofoundry.org/
 * <p>
 * All instance variables are not initialised, including lists, and are therefore null
 * by default.
 *
 * Created 19/11/15
 * @author Edd
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OBOTerm {
    public String id;

    public boolean isObsolete;

    public String name;

    public String definition;

    public String comment;

    // list of term IDs to consider when replacing
    // TODO confirm with Tony S
    public List<String> consider;

    // list of term IDs that are ancestors of this term
    public List<String> ancestors;

    // list of term IDs that are descendants of this term
    public List<String> descendants;

    public List<Synonym> synonyms;

    // a term ID that replaces this one
    public String replacedBy;

    // each term can be in one or more subsets; these are used for two purposes: slims and term usage constraints.
    // Slim subsets have names of the form "goslim_xxx", while usage constraint subsets have names like "gocheck_xxx".
    public List<String> subsets;

    // list of term IDs that are children of this term
    public List<String> children;

    public List<String> secondaryIds;

    public List<History> history;

    public List<XRef> xRefs;

    public List<XORelation> xRelations;

    public List<AnnotationGuideLine> annotationGuidelines;
    public List<TaxonConstraint> taxonConstraints;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Synonym implements FieldType {
        public String synonymName;
        public String synonymType;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class History implements FieldType {
        public String name;
        public String timestamp;
        public String action;
        public String category;
        public String text;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class XRef implements FieldType {
        public String dbCode;
        public String dbId;
        public String name;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class AnnotationGuideLine implements FieldType {
        public String description;
        public String url;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class TaxonConstraint implements FieldType {
        public String ancestorId;
        public String ancestorName;
        public String relationship;
        public String taxId;
        public String taxIdType;
        public String taxName;
        public List<Literature> citations;
    }

    public static class Literature implements FieldType {
        public String id;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class XORelation implements FieldType {
        public String id;
        public String term;
        public String namespace;
        public String url;
        public String relation;
    }
}
