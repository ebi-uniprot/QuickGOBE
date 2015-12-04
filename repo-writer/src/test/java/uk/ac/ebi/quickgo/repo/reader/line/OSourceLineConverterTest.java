package uk.ac.ebi.quickgo.repo.reader.line;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.document.ontology.OntologyDocMocker.createGODoc;
import static uk.ac.ebi.quickgo.document.ontology.OntologyDocMocker.createOBODelimitedStr;

/**
 * Created 04/12/15
 * @author Edd
 */
public class OSourceLineConverterTest {
    private static OSourceLineConverter converter;

    @BeforeClass
    public static void setupClass() {
        converter = new OSourceLineConverter();
    }
    @Test
    public void testStringToDocConversion() {
        OntologyDocument goDocOrig = createGODoc("GO:0000001", "name1");
        String flatDoc = createOBODelimitedStr(goDocOrig);
        System.out.println(flatDoc);
        OntologyDocument goDocReconstructed = converter.apply(flatDoc);
        System.out.println(goDocReconstructed);
        assertThat(goDocOrig, is(goDocReconstructed));
    }
}