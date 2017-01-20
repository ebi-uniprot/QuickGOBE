package uk.ac.ebi.quickgo.annotation.model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tony Wardell
 * Date: 20/01/2017
 * Time: 17:01
 * Created with IntelliJ IDEA.
 */
public class ConversionUtil {

    private static final String COMMA = ",";
    private static final String PIPE = "|";

    public String withFromAsString(List<Annotation.ConnectedXRefs> connectedXRefs) {
        return connectedXRefs.stream()
                             .map(itemList -> simpleRefAndToString(itemList))
                             .collect(Collectors.joining(PIPE));
    }

    private String simpleRefAndToString(Annotation.ConnectedXRefs itemList) {
        return itemList.getConnectedXrefs()
                       .stream()
                       .map(cr -> {
                           Annotation.SimpleXRef sr = ((Annotation.SimpleXRef) cr);
                           return sr.asXref();
                       })
                       .collect(Collectors.joining(COMMA)).toString();

    }

    public String extensionsAsString(List<Annotation.ConnectedXRefs> connectedXRefs) {
        return connectedXRefs.stream()
                             .map(itemList -> qualifiedRefAndToString(itemList))
                             .collect(Collectors.joining(PIPE));
    }

    private String qualifiedRefAndToString(Annotation.ConnectedXRefs itemList) {
        return itemList.getConnectedXrefs()
                       .stream()
                       .map(cr -> {
                           Annotation.QualifiedXref sr = ((Annotation.QualifiedXref) cr);
                           return sr.asXref();
                       })
                       .collect(Collectors.joining(COMMA)).toString();
    }

    private static final String ID_DELIMITER = ":";
    public String[] idToComponents(Annotation annotation) {

        return annotation.id.split(ID_DELIMITER);
    }
}
