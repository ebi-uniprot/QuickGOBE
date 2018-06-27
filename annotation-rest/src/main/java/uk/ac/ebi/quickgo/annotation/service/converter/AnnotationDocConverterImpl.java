package uk.ac.ebi.quickgo.annotation.service.converter;

import uk.ac.ebi.quickgo.annotation.common.AnnotationDocument;
import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.model.Annotation.AbstractXref;
import uk.ac.ebi.quickgo.annotation.model.Annotation.ConnectedXRefs;
import uk.ac.ebi.quickgo.annotation.model.GeneProduct;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Convert the persisted version of the Annotation to our model of the Annotation
 *
 * @author Tony Wardell
 * Date: 26/04/2016
 * Time: 16:52
 */
public class AnnotationDocConverterImpl implements AnnotationDocConverter {

    private static final String COMMA = ",";
    private static final String COLON = ":";
    private static final String PIPE = "[|]";
    private static Predicate<String> empty = String::isEmpty;

    @Override
    public Annotation convert(AnnotationDocument annotationDocument) {
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

    private List<ConnectedXRefs<Annotation.SimpleXRef>> asWithFromXRefList(
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

    private List<ConnectedXRefs<Annotation.RelationXref>> asExtensionsXRefList(String extension,
                                                                               Function<String, Annotation.RelationXref> xrefCreator) {
        if (extension != null && !extension.isEmpty()) {
            return Stream.of(ordElements(extension))
                    .filter(Objects::nonNull)
                    .filter(empty.negate())
                    .map(xrefs -> createConnectedXRefs(xrefCreator, xrefs))
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    /**
     * Break up the annotation extension into its or'd components i.e. those separated by a pipe symbol
     *
     * r(d:i),r(d:i)|r(d:i) -> 1. r(d:i),r(d:i) 2. r(d:i)
     */

    private String[] ordElements(String extension) {
        return Objects.nonNull(extension) ? extension.split(PIPE) : new String[0];
    }

    private <T extends AbstractXref> ConnectedXRefs<T> createConnectedXRefs(Function<String, T> xrefCreator,
                                                                            String xrefs) {
        ConnectedXRefs<T> connectedXRefs = new ConnectedXRefs<>();

        Stream.of(xrefs.split(COMMA)).map(xrefCreator)
                .forEach(connectedXRefs::addXref);

        return connectedXRefs;
    }

    private Annotation.SimpleXRef createSimpleXRef(String xref) {
        String[] dbAndSig = extractDBAndSignature(xref);
        return new Annotation.SimpleXRef(dbAndSig[0], dbAndSig[1]);
    }

    private Annotation.RelationXref createQualifiedXRef(String xref) {
        String[] dbAndSig = extractDBAndSignature(extractContentsWithinParenthesis(xref));
        String qualifier = extractQualifier(xref);
        return new Annotation.RelationXref(dbAndSig[0], dbAndSig[1], qualifier);
    }

    private String extractQualifier(String unformattedXref) {
        return unformattedXref.substring(0, unformattedXref.indexOf('('));
    }

    private String extractContentsWithinParenthesis(String unformattedXref) {
        return unformattedXref.substring(unformattedXref.indexOf('(') + 1, unformattedXref.indexOf(')'));
    }

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
}
