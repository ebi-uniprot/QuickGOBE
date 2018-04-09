package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A home for the logic to format ConnectedXRefs into Strings.
 *
 * @author Tony Wardell
 * Date: 09/04/2018
 * Time: 13:06
 * Created with IntelliJ IDEA.
 */
public class ConnectedXRefs {
    private static final String PIPE = "|";
    private static final String COMMA = ",";

    public ConnectedXRefs() {}

    public static String asString(List<Annotation.ConnectedXRefs<Annotation.SimpleXRef>> connectedXRefs) {
        if (connectedXRefs == null || connectedXRefs.isEmpty()) {
            return "";
        }
        return connectedXRefs.stream()
                .map(ConnectedXRefs::simpleRefAndToString)
                .collect(Collectors.joining(PIPE));
    }

    private static String simpleRefAndToString(Annotation.ConnectedXRefs<Annotation.SimpleXRef> itemList) {
        return itemList.getConnectedXrefs()
                .stream()
                .map(Annotation.SimpleXRef::asXref)
                .collect(Collectors.joining(COMMA));
    }

}
