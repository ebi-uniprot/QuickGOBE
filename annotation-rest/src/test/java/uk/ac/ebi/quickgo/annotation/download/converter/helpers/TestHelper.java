package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Hold useful test code to share amongst the tests for the helpers package.
 *
 * @author Tony Wardell
 * Date: 10/04/2018
 * Time: 08:32
 * Created with IntelliJ IDEA.
 */
class TestHelper {

    static <T extends Annotation.AbstractXref> List<Annotation.ConnectedXRefs<T>> connectedXrefs(
            List<List<Supplier<T>>> items) {
        return items.stream().map(itemList -> {
                    Annotation.ConnectedXRefs<T> xrefs = new Annotation.ConnectedXRefs<>();
                    itemList.stream().map(Supplier::get).forEach(xrefs::addXref);
                    return xrefs;
                }
        ).collect(Collectors.toList());
    }

}
