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

    public Definition definition;

    public String comment;

    // list of term IDs that are ancestors of this term
    public List<String> ancestors;

    // list of term IDs that are descendants of this term
    public List<String> descendants;

    public List<Synonym> synonyms;

    // indicates all ontology terms that are effectively or can be replaced by the this term
    public List<Relation> replaces;

    // Contains a list of ontology terms that either replace the current term, or that can be considered as a
    // replacement
    public List<Relation> replacements;

    // each term can be in one or more subsets; these are used for two purposes: slims and term usage constraints.
    // Slim subsets have names of the form "goslim_xxx", while usage constraint subsets have names like "gocheck_xxx".
    public List<String> subsets;

    // list of relations indicating the child ids as well as their relationship with the parent
    public List<Relation> children;

    public List<String> secondaryIds;

    public List<History> history;

    public List<XRef> xRefs;

    public List<XORelation> xRelations;

    public List<AnnotationGuideLine> annotationGuidelines;

    public List<TaxonConstraint> taxonConstraints;

    public List<Credit> credits;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Synonym implements FieldType {
        public String name;
        public String type;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class History implements FieldType {
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

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Definition implements FieldType {
        public String text;
        public List<XRef> xrefs;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Relation implements FieldType {
        public String id;
        public String type;
    }


    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Credit implements FieldType {
        public String code;
        public String url;
    }
}