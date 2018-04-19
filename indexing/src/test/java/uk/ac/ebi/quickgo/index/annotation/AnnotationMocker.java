package uk.ac.ebi.quickgo.index.annotation;

/**
 * A class for creating stubbed annotations, representing rows of data read from
 * annotation source files.
 *
 * Created 22/04/16
 * @author Edd
 */
public class AnnotationMocker {
    public static Annotation createValidAnnotation() {
        Annotation annotation = new Annotation();

        annotation.db = "IntAct";
        annotation.dbObjectId = "EBI-10043081";
        annotation.dbReferences = "PMID:12871976";
        annotation.qualifier = "enables";
        annotation.goId = "GO:0000977";
        annotation.interactingTaxonId = "taxon:12345";
        annotation.evidenceCode = "ECO:0000353";
        annotation.with = "GO:0036376,GO:1990573";
        annotation.assignedBy = "IntAct";
        annotation.annotationExtension = "occurs_in(CL:1000428)";
        annotation.annotationProperties =
                "go_evidence=IEA|taxon_id=35758|db_subset=TrEMBL|db_object_symbol=moeA5|db_object_type=protein" +
                        "|target_set=BHF-UCL,Exosome,KRUK|taxon_lineage=1,2,3,4";
        return annotation;
    }
}
