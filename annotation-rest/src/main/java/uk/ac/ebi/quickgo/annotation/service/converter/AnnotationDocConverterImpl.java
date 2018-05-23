package uk.ac.ebi.quickgo.annotation.service.converter;

import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.model.GeneProduct;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Converter the persisted version of the Annotation to our model of the Annotation
 *
 * @author Tony Wardell
 * Date: 26/04/2016
 * Time: 16:52
 */
public class AnnotationDocConverterImpl implements AnnotationDocConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationDocConverterImpl.class);

    private static final String COMMA = ",";
    private static final String COLON = ":";
    private static final String PIPE = "[|]";
    private static Predicate<String> notPopulated = String::isEmpty;

    @Override public Annotation convert(AnnotationDocument annotationDocument) {
        Annotation annotation = new Annotation();
        annotation.id = annotationDocument.id;
        annotation.geneProductId = annotationDocument.geneProductId;
        GeneProduct geneProduct = GeneProduct.fromCurieId(annotationDocument.geneProductId);
        annotation.setGeneProduct(geneProduct);
        annotation.canonicalId = geneProduct.canonicalId();
        annotation.qualifier = annotationDocument.qualifier;
        annotation.goId = annotationDocument.goId;
        annotation.goEvidence = annotationDocument.goEvidence;
        annotation.goAspect = annotationDocument.goAspect;
        annotation.evidenceCode = annotationDocument.evidenceCode;
        annotation.reference = annotationDocument.reference;
        annotation.taxonId = annotationDocument.taxonId;
        annotation.symbol = annotationDocument.symbol;
        annotation.assignedBy = annotationDocument.assignedBy;
        annotation.interactingTaxonId = annotationDocument.interactingTaxonId;
        annotation.targetSets = asUnmodifiableList(annotationDocument.targetSets);
        annotation.withFrom = asWithFromXRefList(annotationDocument.withFrom, this::createSimpleXRef);
        annotation.extensions = asExtensionsXRefList(annotationDocument.extensions, this::createQualifiedXRef);
        annotation.date = annotationDocument.date;

        return annotation;
    }

    private <T> List<T> asUnmodifiableList(List<T> list) {
        List<T> unmodifiableList;

        if (list != null) {
            unmodifiableList = Collections.unmodifiableList(list);
        } else {
            unmodifiableList = null;
        }

        return unmodifiableList;
    }

    private List<Annotation.ConnectedXRefs<Annotation.SimpleXRef>> asWithFromXRefList(
            List<String> csvs,
            Function<String, Annotation.SimpleXRef> xrefCreator) {
        if (csvs != null && !csvs.isEmpty()) {

            return csvs.stream()
                    .map(xrefs -> createConnectedXRefs(xrefCreator, xrefs))
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    private List<Annotation.ConnectedXRefs<Annotation.QualifiedXref>> asExtensionsXRefList(String extension,
                                                                                           Function<String,
                                                                                                   Annotation
                                                                                                           .QualifiedXref> xrefCreator) {
        if (extension != null && !extension.isEmpty()) {
            List<String> csvs = constructExtensions(extension);
            return csvs.stream().filter(Objects::nonNull).filter(notPopulated.negate())
                       .map(xrefs -> createConnectedXRefs(xrefCreator, xrefs))
                       .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    private <T extends Annotation.AbstractXref> Annotation.ConnectedXRefs<T> createConnectedXRefs(
            Function<String, T> xrefCreator,
            String xrefs) {
        Annotation.ConnectedXRefs<T> connectedXRefs = new Annotation.ConnectedXRefs<>();

        streamCSV(xrefs)
                .map(xrefCreator).peek(System.out::println)
                .forEach(connectedXRefs::addXref);

        return connectedXRefs;
    }

    private Annotation.SimpleXRef createSimpleXRef(String xref) {
        String[] dbAndSig = extractDBAndSignature(xref);
        return new Annotation.SimpleXRef(dbAndSig[0], dbAndSig[1]);
    }

    private Annotation.QualifiedXref createQualifiedXRef(String xref) {
        String[] dbAndSig = extractDBAndSignature(extractContentsWithinParenthesis(xref));
        String qualifier = extractQualifier(xref);
        return new Annotation.QualifiedXref(dbAndSig[0], dbAndSig[1], qualifier);
    }

    private String extractQualifier(String unformattedXref) {
        return unformattedXref.substring(0, unformattedXref.indexOf("("));
    }

    private String extractContentsWithinParenthesis(String unformattedXref) {
        LOGGER.info("extractContentsWithinParenthesis" + unformattedXref);
        return unformattedXref.substring(unformattedXref.indexOf("(") + 1, unformattedXref.indexOf(")"));
    }

    private Stream<String> streamCSV(String xrefs) {return Stream.of(xrefs.split(COMMA));}

    private String[] extractDBAndSignature(String xref) {
        int colonPos = xref.indexOf(COLON);

        String database;
        String signature;

        if (colonPos == -1) {
            database = xref;
            signature = null;
        } else {
            database = xref.substring(0, colonPos);
            signature = xref.substring(colonPos + 1, xref.length());
        }

        return new String[]{database, signature};
    }

    private List<String> constructExtensions(String extension) {
        return createNullableStringListFromDelimitedValues(extension, PIPE);
    }

    private List<String> createNullableStringListFromDelimitedValues(String value, String delimiter) {
        return value == null ? null : Arrays.asList(splitValue(value, delimiter));
    }

    /**
     * Splits a {@link String} value on occurrences of a {@link String} delimiter.
     * @param value the value to split
     * @param delimiter the delimiter on which splitting takes place
     * @return an array of {@link String} values
     */
    private static String[] splitValue(String value, String delimiter) {
        checkArgument(delimiter != null, "Delimiter cannot be null");

        return Optional.ofNullable(value).filter(v -> !v.isEmpty()).map(v -> v.split(delimiter)).orElse(new String[0]);
    }
}
