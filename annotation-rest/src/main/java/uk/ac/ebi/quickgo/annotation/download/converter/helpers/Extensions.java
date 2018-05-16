package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A home for the logic to format extensions into Strings.
 *
 * @author Tony Wardell
 * Date: 09/04/2018
 * Time: 14:33
 * Created with IntelliJ IDEA.
 */
public class Extensions {

    private static final String COMMA = ",";
    private static final String PIPE = "|";

    private Extensions() {}

    public static String asString(List<Annotation.ConnectedXRefs<Annotation.QualifiedXref>> connectedXRefs) {
        if (connectedXRefs == null || connectedXRefs.isEmpty()) {
            return "";
        }
        return connectedXRefs.stream()
                .map(Extensions::qualifiedRefAndToString)
                .collect(Collectors.joining(PIPE));
    }

    private static String qualifiedRefAndToString(Annotation.ConnectedXRefs<Annotation.QualifiedXref> itemList) {
        return itemList.getConnectedXrefs()
                .stream()
                .map(Annotation.QualifiedXref::asXref)
                .collect(Collectors.joining(COMMA));
    }
}
