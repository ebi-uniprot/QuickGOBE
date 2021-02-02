package uk.ac.ebi.quickgo.annotation.download.converter;

import uk.ac.ebi.quickgo.annotation.download.converter.helpers.*;
import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.model.GeneProduct;
import uk.ac.ebi.quickgo.common.model.Aspect;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.DateConverter.ISO_8601_FORMATTER;
import static uk.ac.ebi.quickgo.common.model.Aspect.fromScientificName;

/**
 * Convert an {@link Annotation}  to a String representation.
 * See http://geneontology.org/page/go-annotation-file-gaf-format-22}
 *
 * The columns that constitute a GAF file are:
 <pre>
 DB
 ID
 SYMBOL
 QUALIFIER
 GO TERM
 REFERENCE
 EVIDENCE
 WITH FROM
 ASPECT
 GENE PRODUCT NAME
 GENE PRODUCT SYNONYMS
 GENE PRODUCT TYPE
 TAXON (& interacting taxon)
 DATE
 ASSIGNED BY
 ANNOTATION EXTENSIONS
 GENE PRODUCT FORM ID (non canonical format i.e. with isoform or variant information)
 </pre>
 *
 * @author Tony Wardell
 * Date: 17/01/2017
 * Time: 11:54
 * Created with IntelliJ IDEA.
 */
public class AnnotationToGAF implements BiFunction<Annotation, List<String>, List<String>> {

    static final String OUTPUT_DELIMITER = "\t";
    private static final List<Function<GafSource, String>> gafColumnFunctions = new ArrayList<>();

    static {
        Function<GafSource, GeneProduct> gpSource = GafSource::getGeneProduct;
        Function<GafSource, Annotation> annotationSource = GafSource::getAnnotation;

        //Functions to access or convert annotation or related data to GAF formatted data.
        gafColumnFunctions.add(gpSource.andThen(GeneProduct::db));
        gafColumnFunctions.add(gpSource.andThen(GeneProduct::canonicalId));
        gafColumnFunctions.add(annotationSource.andThen(a -> a.symbol));
        gafColumnFunctions.add(annotationSource.andThen(a -> a.qualifier).andThen(Qualifier::gafQualifierAsString));
        gafColumnFunctions.add(GafSource::getGoId);
        gafColumnFunctions.add(annotationSource.andThen(a -> a.reference));
        gafColumnFunctions.add(annotationSource.andThen(a -> a.goEvidence));
        gafColumnFunctions.add(annotationSource.andThen(a -> a.withFrom).andThen(WithFrom::nullOrEmptyListToString));
        gafColumnFunctions.add(annotationSource.andThen(AnnotationToGAF::aspectAsSingleCharacter));
        gafColumnFunctions.add(annotationSource.andThen(a -> a.name));
        gafColumnFunctions.add(annotationSource.andThen(a -> a.synonyms));
        gafColumnFunctions.add(gpSource.andThen(GeneProduct::type));
        gafColumnFunctions.add(annotationSource.andThen(a -> Taxon.taxonIdToCurie(a.taxonId, a.interactingTaxonId)));
        gafColumnFunctions.add(annotationSource.andThen(a -> a.date).andThen(ISO_8601_FORMATTER));
        gafColumnFunctions.add(annotationSource.andThen(a -> a.assignedBy));
        gafColumnFunctions.add((annotationSource.andThen(a -> a.extensions)
                                        .andThen(AnnotationExtensions::nullOrEmptyListToEmptyString)));
        gafColumnFunctions.add(gpSource.andThen(AnnotationToGAF::fullIdIfCanonicalNotEqualToCanonicalId));
    }

    /**
     * Convert an {@link Annotation} to a String representation.
     *
     * @param annotation     instance
     * @param selectedFields ignore for GAF
     * @return String TSV delimited representation of an annotation in GAF format.
     */
    @Override
    public List<String> apply(Annotation annotation, List<String> selectedFields) {
        if (isNull(annotation.slimmedIds) || annotation.slimmedIds.isEmpty()) {
            GafSource gafSource = createGAFSource(annotation, annotation.goId);
            return isValidGafRecord(gafSource) ? Collections.singletonList(toOutputRecord(gafSource)) : Collections.emptyList();
        } else {
            return annotation.slimmedIds.stream()
                    .map(goId -> createGAFSource(annotation, goId))
                    .filter(this::isValidGafRecord)
                    .map(this::toOutputRecord)
                    .collect(toList());
        }
    }

    private static String aspectAsSingleCharacter(Annotation a) {
        return fromScientificName(a.goAspect).map(Aspect::getCharacter).orElse(null);
    }

    private static String fullIdIfCanonicalNotEqualToCanonicalId(GeneProduct g) {
        return Objects.isNull(g.nonCanonicalId()) ? "" : g.fullId();
    }

    private GafSource createGAFSource(Annotation annotation, String goId) {
        final GeneProduct geneProduct = annotation.getGeneProduct();
        return new GafSource(geneProduct, annotation, goId);
    }

    private String toOutputRecord(GafSource gafSource) {
        return gafColumnFunctions.stream()
                .map(f -> f.apply(gafSource))
                .map(Helper::nullToEmptyString)
                .collect(joining(OUTPUT_DELIMITER));
    }

    private boolean isValidGafRecord(GafSource gafSource){
        if(gafSource == null)
            return false;
        // GOA-3253: annotations with an ECO code that has no mapping to a GO evidence code should not be output in GAF files
        // See details in jira comments
        if (gafSource.annotation !=null)
            return gafSource.annotation.goEvidence != null && !gafSource.annotation.goEvidence.trim().isEmpty();
        //Everything else is good to go
        return true;
    }

    /**
     * A class to hold the date to be outputted
     */
    static class GafSource {
        final GeneProduct geneProduct;
        final Annotation annotation;
        final String goId;

        private GafSource(GeneProduct geneProduct, Annotation annotation, String goId) {
            Objects.requireNonNull(geneProduct);
            Objects.requireNonNull(annotation);
            this.geneProduct = geneProduct;
            this.annotation = annotation;
            this.goId = goId;
        }

        public GeneProduct getGeneProduct() {
            return geneProduct;
        }

        public Annotation getAnnotation() {
            return annotation;
        }

        public String getGoId() {
            return goId;
        }
    }
}
