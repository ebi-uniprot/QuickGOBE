package uk.ac.ebi.quickgo.index.annotation;

/**
 * A class for creating stubbed annotations, representing rows of data read from
 * annotation source files.
 *
 * Created 22/04/16
 * @author Edd
 */
class AnnotationMocker {
    static Annotation createValidAnnotation() {
        Annotation annotation = new Annotation();
        annotation.db = "IntAct";
        annotation.dbObjectId = "EBI-10043081";
        annotation.dbReferences = "PMID:12871976";
        annotation.qualifier = "enables";
        annotation.goId = "GO:0000977";
        annotation.eco = "ECO:0000353";
        annotation.with = "GO:0036376,GO:1990573";
        annotation.interactingTaxonId = null;
        annotation.date = "20150122";
        annotation.assignedBy = "IntAct";
        annotation.annotationExtension = "occurs_in(CL:1000428)";
        annotation.annotationProperties = "go_evidence=IPI";

        return annotation;
    }
}
