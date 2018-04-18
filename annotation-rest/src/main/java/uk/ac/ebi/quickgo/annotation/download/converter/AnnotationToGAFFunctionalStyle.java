package uk.ac.ebi.quickgo.annotation.download.converter;

import uk.ac.ebi.quickgo.annotation.download.converter.helpers.*;
import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.common.model.Aspect;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static uk.ac.ebi.quickgo.common.model.Aspect.fromScientificName;

public class AnnotationToGAFFunctionalStyle implements BiFunction<Annotation, List<String>, List<String>> {

    static final String OUTPUT_DELIMITER = "\t";
    private static final String PIPE = "|";
    private static final String TAXON = "taxon:";
    private static List<Function<AnnotationToGAFFunctionalStyle.GafSource, String>> gafColumnFunctions =
            new ArrayList<>();

    static {
        Function<AnnotationToGAFFunctionalStyle.GafSource, GeneProduct> gpSource =
                AnnotationToGAFFunctionalStyle.GafSource::getGeneProduct;
        Function<AnnotationToGAFFunctionalStyle.GafSource, Annotation> annotationSource =
                AnnotationToGAFFunctionalStyle.GafSource::getAnnotation;

        //Functions to access or convert annotation or related data to GAF formatted data.
        gafColumnFunctions.add(gpSource.andThen(GeneProduct::db));
        gafColumnFunctions.add(gpSource.andThen(GeneProduct::id));
        gafColumnFunctions.add(annotationSource.andThen(a -> a.symbol));
        gafColumnFunctions.add(annotationSource.andThen(a -> a.qualifier));
        gafColumnFunctions.add(AnnotationToGAFFunctionalStyle.GafSource::getGoId);
        gafColumnFunctions.add(annotationSource.andThen(a -> a.reference));
        gafColumnFunctions.add(annotationSource.andThen(a -> a.goEvidence));
        gafColumnFunctions.add(annotationSource.andThen(a -> a.withFrom).andThen(WithFrom::nullOrEmptyListToString));
        gafColumnFunctions.add(annotationSource.andThen(AnnotationToGAFFunctionalStyle::aspectAsSingleCharacter));
        gafColumnFunctions.add(annotationSource.andThen(a -> a.name));
        gafColumnFunctions.add(annotationSource.andThen(a -> a.synonyms));
        gafColumnFunctions.add(gpSource.andThen(GeneProduct::type));
        gafColumnFunctions.add(annotationSource.andThen(AnnotationToGAFFunctionalStyle::gafTaxonAsString));
        gafColumnFunctions.add(annotationSource.andThen(a -> a.date).andThen(DateConverter::toYearMonthDay));
        gafColumnFunctions.add(annotationSource.andThen(a -> a.assignedBy));
        gafColumnFunctions.add((annotationSource.andThen(a -> a.extensions)
                                        .andThen(AnnotationExtensions::nullOrEmptyListToEmptyString)));
        gafColumnFunctions.add(gpSource.andThen(GeneProduct::withIsoformOrVariant));
    }

    private static String aspectAsSingleCharacter(Annotation a) {
        return fromScientificName(a.goAspect).map(Aspect::getCharacter).orElse(null);
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
            return Collections.singletonList(toOutputRecord(gafSource));
        } else {
            return annotation.slimmedIds.stream()
                    .map(goId -> createGAFSource(annotation, goId))
                    .map(this::toOutputRecord)
                    .collect(toList());
        }
    }

    private GafSource createGAFSource(Annotation annotation, String goId) {
        final GeneProduct geneProduct = GeneProduct.fromString(annotation.geneProductId);
        return new GafSource(geneProduct, annotation, goId);
    }

    private String toOutputRecord(AnnotationToGAFFunctionalStyle.GafSource gafSource) {
        return gafColumnFunctions.stream()
                .map(f -> f.apply(gafSource))
                .map(Helper::nullToEmptyString)
                .collect(joining(OUTPUT_DELIMITER));
    }

    private static String gafTaxonAsString(Annotation annotation) {
        StringBuilder taxonBuilder = new StringBuilder();
        taxonBuilder.append(TAXON)
                .append(annotation.taxonId)
                .append(annotation.interactingTaxonId > 0 ? PIPE + annotation.interactingTaxonId : "");
        return taxonBuilder.toString();
    }

    /**
     * A class to hold the date to be outputted
     */
    static class GafSource{
        final GeneProduct geneProduct;
        final Annotation annotation;
        final String goId;

        private GafSource(GeneProduct geneProduct, Annotation annotation, String goId) {
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
