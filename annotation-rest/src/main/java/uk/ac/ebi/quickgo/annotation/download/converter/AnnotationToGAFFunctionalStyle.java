package uk.ac.ebi.quickgo.annotation.download.converter;

import uk.ac.ebi.quickgo.annotation.download.converter.helpers.*;
import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.common.model.Aspect;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.DateConverter.toYYYYMMDD;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.Helper.nullToEmptyString;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.Qualifier.gafQualifierAsString;
import static uk.ac.ebi.quickgo.common.model.Aspect.fromScientificName;

public class AnnotationToGAFFunctionalStyle implements BiFunction<Annotation, List<String>, List<String>> {

    static final String OUTPUT_DELIMITER = "\t";
    private static final String PIPE = "|";
    private static final String TAXON = "taxon:";

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
            return Collections.singletonList(toOutputRecord(annotation, annotation.goId));
        } else {
            return annotation.slimmedIds.stream()
                    .map(goId -> this.toOutputRecord(annotation, goId))
                    .collect(toList());
        }
    }

    private String toOutputRecord(Annotation annotation, String goId) {
        StringJoiner tsvJoiner = new StringJoiner(OUTPUT_DELIMITER);
        final Optional<GeneProduct> geneProduct = GeneProduct.fromString(annotation.geneProductId);
        return tsvJoiner
                .add(geneProduct.map(GeneProduct::db).orElse(""))
                .add(geneProduct.map(GeneProduct::id).orElse(""))
                .add(nullToEmptyString(annotation.symbol))
                .add(gafQualifierAsString(annotation.qualifier))
                .add(nullToEmptyString(goId))
                .add(nullToEmptyString(annotation.reference))
                .add(nullToEmptyString(annotation.goEvidence))
                .add(WithFrom.nullOrEmptyListToString(annotation.withFrom))
                .add(fromScientificName(annotation.goAspect).map(Aspect::getCharacter).orElse(""))
                .add(nullToEmptyString(annotation.name))
                .add(nullToEmptyString(annotation.synonyms))
                .add(geneProduct.map(GeneProduct::type).orElse(""))
                .add(gafTaxonAsString(annotation))
                .add(nonNull(annotation.date) ? toYYYYMMDD.apply(annotation.date) : "")
                .add(nullToEmptyString(annotation.assignedBy))
                .add(AnnotationExtensions.nullOrEmptyListToEmptyString(annotation.extensions))
                .add(geneProduct.map(GeneProduct::withIsoformOrVariant).orElse(""))
                .toString();
    }

    private String processOutputRecord() {
        List<Function<GeneProduct,String>> tsvJoiner = new ArrayList<>();

        Function<GeneProduct,String> x = GeneProduct::db;
        x.compose()

        return tsvJoiner.add(GeneProduct.db())
                .add(geneProduct.map(GeneProduct::id).orElse(""))
                .add(nullToEmptyString(annotation.symbol))
                .add(gafQualifierAsString(annotation.qualifier))
                .add(nullToEmptyString(goId))
                .add(nullToEmptyString(annotation.reference))
                .add(nullToEmptyString(annotation.goEvidence))
                .add(WithFrom.nullOrEmptyListToString(annotation.withFrom))
                .add(fromScientificName(annotation.goAspect).map(Aspect::getCharacter).orElse(""))
                .add(nullToEmptyString(annotation.name))
                .add(nullToEmptyString(annotation.synonyms))
                .add(geneProduct.map(GeneProduct::type).orElse(""))
                .add(gafTaxonAsString(annotation))
                .add(nonNull(annotation.date) ? toYYYYMMDD.apply(annotation.date) : "")
                .add(nullToEmptyString(annotation.assignedBy))
                .add(AnnotationExtensions.nullOrEmptyListToEmptyString(annotation.extensions))
                .add(geneProduct.map(GeneProduct::withIsoformOrVariant).orElse(""))
                .toString();
    }

    private String toOutputRecord(GeneProduct geneProduct, List<Function<GeneProduct,String>> ops) {
        return ops.stream()
                .map(f -> f.apply(geneProduct))
                .map(Helper::nullToEmptyString)
                .collect(joining(OUTPUT_DELIMITER));

    }

    static List<Function<AnnotationToGAFFunctionalStyle.GafSource, String>> opsO;

    static {
        Function<AnnotationToGAFFunctionalStyle.GafSource, GeneProduct> gpSource =
                AnnotationToGAFFunctionalStyle.GafSource::getGeneProduct;
        Function<AnnotationToGAFFunctionalStyle.GafSource, Annotation> annotationSource =
                AnnotationToGAFFunctionalStyle.GafSource::getAnnotation;

        //Functions to access or convert annotation or related data to GAF formatted data.
        opsO.add(gpSource.andThen(GeneProduct::db));
        opsO.add(gpSource.andThen(GeneProduct::id));
        opsO.add(annotationSource.andThen(a -> a.symbol));
        opsO.add(annotationSource.andThen(a -> a.qualifier));
        opsO.add(AnnotationToGAFFunctionalStyle.GafSource::getGoId);
        opsO.add(annotationSource.andThen(a -> a.reference));
        opsO.add(annotationSource.andThen(a -> a.goEvidence));
        opsO.add(annotationSource.andThen(a -> a.withFrom).andThen(WithFrom::nullOrEmptyListToString));
        opsO.add(annotationSource.andThen(AnnotationToGAFFunctionalStyle::aspectAsSingleCharacter));
        opsO.add(annotationSource.andThen(a -> a.name));
        opsO.add(annotationSource.andThen(a -> a.synonyms));
        opsO.add(gpSource.andThen(GeneProduct::type));
        opsO.add(annotationSource.andThen(AnnotationToGAFFunctionalStyle::gafTaxonAsString));
        opsO.add(annotationSource.andThen(a -> a.date).andThen(DateConverter::toYearMonthDay));
        opsO.add(annotationSource.andThen(a -> a.assignedBy));
        opsO.add((annotationSource.andThen(a -> a.extensions)
                          .andThen(AnnotationExtensions::nullOrEmptyListToEmptyString)));
        opsO.add(gpSource.andThen(GeneProduct::withIsoformOrVariant));

    }

    private String toOutputRecordUsingSupplier(AnnotationToGAFFunctionalStyle.GafSource gafSource) {

//        List<Function<GeneProduct,String>> ops = new ArrayList<>();

//        Function<GeneProduct, String>  dbSource = GeneProduct::db;
//        Function<GeneProduct, String> dbFull = dbSource.andThen( v -> Objects.nonNull(v) ? v : "");
//        Function<GeneProduct, String> dbFull = dbSource.andThen( v -> Objects.nonNull(v) ? v : "");
//        ops.add( (dbFull);
//        List<Function<GeneProduct,String>> opsO = new ArrayList<>();
//        opsO.add( (dbSource);
//        opsO.add( (dbFull);

//        Function<GafSource, GeneProduct>  gpSource = GafSource::getGeneProduct;
//        Function<GafSource, Annotation>  annotationSource = GafSource::getAnnotation;

//        Function<GeneProduct, String>  dbSource = GeneProduct::db;
//        Function<GafSource, String>  dbFinal = gpSource.andThen(dbSource);
//        Function<GafSource, String>  dbFinal = gpSource.andThen(GeneProduct::db);
//
//        Function<GafSource, String> dbComplete = dbFinal.andThen( v -> Objects.nonNull(v) ? v : "");

//        List<Function<GafSource,String>> opsO = new ArrayList<>();
////        opsO.add((dbFinal);
////        opsO.add(dbComplete);
//        opsO.add(gpSource.andThen(GeneProduct::db));
//        opsO.add(gpSource.andThen(geneProduct -> geneProduct.id()));
//        opsO.add(annotationSource.andThen((annotation) -> annotation.symbol) );
//        opsO.add(annotationSource.andThen((annotation) -> annotation.qualifier) );


        return opsO.stream()
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

    static class GafSource{
        final GeneProduct geneProduct;
        final Annotation annotation;
        final String goId;

        public GafSource(GeneProduct geneProduct, Annotation annotation, String goId) {
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
