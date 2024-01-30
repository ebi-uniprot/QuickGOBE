package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.TestHelper.connectedXrefs;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.WithFrom.*;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.WithFromTest.FakeWithFromItem.GO_1;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.WithFromTest.FakeWithFromItem.GO_2;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.WithFromTest.FakeWithFromItem.GO_3;

/**
 * Can the WithFrom helper class properly create a string representation of the references handed to it?
 * @author Tony Wardell
 * Date: 10/04/2018
 * Time: 08:26
 * Created with IntelliJ IDEA.
 */
class WithFromTest {

    private static final String EMPTY_STRING = "";
    private static final List<List<Supplier<Annotation.SimpleXRef>>> WITH_FROM = asList(
            singletonList(GO_1), asList(GO_2, GO_3));

    @Test
    void nullReferenceComesBackAsEmptyString() {
        assertThat(nullOrEmptyListToString(null), is(EMPTY_STRING));
    }

    @Test
    void emptyReferenceComesBackAsEmptyString() {
        assertThat(nullOrEmptyListToString(Collections.emptyList()), is(EMPTY_STRING));
    }

    @Test
    void successfulConversionToString() {
        List<Annotation.ConnectedXRefs<Annotation.SimpleXRef>> model = connectedXrefs(WITH_FROM);
        assertThat(nullOrEmptyListToString(model), is("GO:0000001|GO:0000002,GO:0000003"));
    }

    enum FakeWithFromItem implements Supplier<Annotation.SimpleXRef> {
        GO_1("GO", "0000001"),
        GO_2("GO", "0000002"),
        GO_3("GO", "0000003");

        private final String db;
        private final String id;

        FakeWithFromItem(String db, String id) {
            this.db = db;
            this.id = id;
        }

        @Override public Annotation.SimpleXRef get() {
            return new Annotation.SimpleXRef(db, id);
        }

        @Override public String toString() {
            return db + ":" + id;
        }
    }
}
