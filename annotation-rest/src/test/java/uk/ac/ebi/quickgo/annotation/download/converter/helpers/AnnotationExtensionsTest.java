package uk.ac.ebi.quickgo.annotation.download.converter.helpers;

import uk.ac.ebi.quickgo.annotation.model.Annotation;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.AnnotationExtensionsTest.FakeExtensionItem
        .OCCURS_IN_CL_1;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.AnnotationExtensionsTest.FakeExtensionItem
        .OCCURS_IN_CL_2;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.AnnotationExtensionsTest.FakeExtensionItem
        .OCCURS_IN_CL_3;
import static uk.ac.ebi.quickgo.annotation.download.converter.helpers.TestHelper.connectedXrefs;

/**
 * Can the AnnotationExtensions helper class properly create a string representation of the references handed to it?
 *
 * @author Tony Wardell
 * Date: 10/04/2018
 * Time: 08:05
 * Created with IntelliJ IDEA.
 */
public class AnnotationExtensionsTest {

    private static final String EMPTY_STRING = "";
    private static final List<List<Supplier<Annotation.QualifiedXref>>> EXTENSIONS = asList(
            singletonList(OCCURS_IN_CL_1),
            asList(OCCURS_IN_CL_2, OCCURS_IN_CL_3));
    private static final String EXPECTED_EXTENSION = "occurs_in(CL:0000001)|occurs_in(CL:0000002),occurs_in" +
            "(CL:0000003)";

    @Test
    public void nullReferenceComesBackAsEmptyString() {
        assertThat(AnnotationExtensions.nullOrEmptyListToEmptyString(null), is(EMPTY_STRING));
    }

    @Test
    public void emptyReferenceComesBackAsEmptyString() {
        assertThat(AnnotationExtensions.nullOrEmptyListToEmptyString(Collections.emptyList()), is(EMPTY_STRING));
    }

    @Test
    public void annotationExtensionsWithoutOrs() {
        List<Annotation.ConnectedXRefs<Annotation.QualifiedXref>> extensionsModel = connectedXrefs(EXTENSIONS);
        assertThat(AnnotationExtensions.nullOrEmptyListToEmptyString(extensionsModel), is(EXPECTED_EXTENSION));
    }

    enum FakeExtensionItem implements Supplier<Annotation.QualifiedXref> {
        OCCURS_IN_CL_1("occurs_in", "CL", "0000001"),
        OCCURS_IN_CL_2("occurs_in", "CL", "0000002"),
        OCCURS_IN_CL_3("occurs_in", "CL", "0000003");

        private final String qualifier;
        private final String db;
        private final String id;

        FakeExtensionItem(String qualifier, String db, String id) {
            this.qualifier = qualifier;
            this.db = db;
            this.id = id;
        }

        @Override public Annotation.QualifiedXref get() {
            return new Annotation.QualifiedXref(db, id, qualifier);
        }

        @Override public String toString() {
            return qualifier + "(" + db + ":" + id + ")";
        }
    }
}
