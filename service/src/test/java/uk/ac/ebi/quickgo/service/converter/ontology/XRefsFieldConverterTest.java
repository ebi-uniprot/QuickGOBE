package uk.ac.ebi.quickgo.service.converter.ontology;

import uk.ac.ebi.quickgo.service.model.ontology.OBOTerm;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.ac.ebi.quickgo.ff.flatfield.FlatFieldBuilder.newFlatFieldFromDepth;
import static uk.ac.ebi.quickgo.ff.flatfield.FlatFieldLeaf.newFlatFieldLeaf;

/**
 * Created 01/12/15
 * @author Edd
 */
public class XRefsFieldConverterTest {
    private XRefsFieldConverter converter;

    @Before
    public void setup() {
        this.converter = new XRefsFieldConverter();
    }

    @Test
    public void convertsXrefs() {
        String dbCode0 = "db code 0";
        String dbId0 = "db id 0";
        String dbName0 = "db name 0";

        String dbCode1 = "db code 1";
        String dbId1 = "db id 1";
        String dbName1 = "db name 1";

        List<String> rawXrefs = new ArrayList<>();
        rawXrefs.add(
                newFlatFieldFromDepth(2)
                        .addField(newFlatFieldLeaf(dbCode0))
                        .addField(newFlatFieldLeaf(dbId0))
                        .addField(newFlatFieldLeaf(dbName0))
                        .buildString()
        );
        rawXrefs.add(
                newFlatFieldFromDepth(2)
                        .addField(newFlatFieldLeaf(dbCode1))
                        .addField(newFlatFieldLeaf(dbId1))
                        .addField(newFlatFieldLeaf(dbName1))
                        .buildString()
        );

        List<OBOTerm.XRef> xRefs = converter.convertField(rawXrefs);
        assertThat(xRefs.size(), is(2));
        assertThat(xRefs.get(0).dbCode, is(dbCode0));
        assertThat(xRefs.get(0).name, is(dbName0));
        assertThat(xRefs.get(1).dbCode, is(dbCode1));
        assertThat(xRefs.get(1).name, is(dbName1));
    }
}