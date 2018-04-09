package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A home for the logic to format AnnotationExtensions into Strings.
 *
 * @author Tony Wardell
 * Date: 09/04/2018
 * Time: 13:06
 * Created with IntelliJ IDEA.
 */
public class AnnotationExtensions {
    private static final String PIPE = "|";
    private static final String COMMA = ",";

    private AnnotationExtensions() {}


    private static String asString(List<Annotation.ConnectedXRefs<Annotation.QualifiedXref>> connectedXRefs) {
        Objects.requireNonNull(connectedXRefs);
        return connectedXRefs.stream()
                .map(AnnotationExtensions::simpleRefAndToString)
                .collect(Collectors.joining(PIPE));
    }

    private static String simpleRefAndToString(Annotation.ConnectedXRefs<Annotation.QualifiedXref> itemList) {
        return itemList.getConnectedXrefs()
                .stream()
                .map(Annotation.QualifiedXref::asXref)
                .collect(Collectors.joining(COMMA));
    }

    public static String nullOrEmptyListToEmptyString(List<Annotation.ConnectedXRefs<Annotation.QualifiedXref>> connectedXRefs){
        return Objects.nonNull(connectedXRefs) && !connectedXRefs.isEmpty() ? asString(connectedXRefs) : "";
    }

}
