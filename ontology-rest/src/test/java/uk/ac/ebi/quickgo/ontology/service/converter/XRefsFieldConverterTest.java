package uk.ac.ebi.quickgo.ontology.service.converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder;
import uk.ac.ebi.quickgo.common.converter.FlatFieldLeaf;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created 01/12/15
 * @author Edd
 */
class XRefsFieldConverterTest {
    private XRefsFieldConverter converter;

    @BeforeEach
    void setup() {
        this.converter = new XRefsFieldConverter();
    }

    @Test
    void convertsXrefs() {
        String dbCode0 = "db code 0";
        String dbId0 = "db id 0";
        String dbName0 = "db name 0";

        String dbCode1 = "db code 1";
        String dbId1 = "db id 1";
        String dbName1 = "db name 1";

        List<String> rawXrefs = new ArrayList<>();
        rawXrefs.add(
                FlatFieldBuilder.newFlatField()
                        .addField(FlatFieldLeaf.newFlatFieldLeaf(dbCode0))
                        .addField(FlatFieldLeaf.newFlatFieldLeaf(dbId0))
                        .addField(FlatFieldLeaf.newFlatFieldLeaf(dbName0))
                        .buildString()
        );
        rawXrefs.add(
                FlatFieldBuilder.newFlatField()
                        .addField(FlatFieldLeaf.newFlatFieldLeaf(dbCode1))
                        .addField(FlatFieldLeaf.newFlatFieldLeaf(dbId1))
                        .addField(FlatFieldLeaf.newFlatFieldLeaf(dbName1))
                        .buildString()
        );

        List<OBOTerm.XRef> xRefs = converter.convertFieldList(rawXrefs);
        assertThat(xRefs.size(), is(2));
        assertThat(xRefs.get(0).dbCode, is(dbCode0));
        assertThat(xRefs.get(0).name, is(dbName0));
        assertThat(xRefs.get(1).dbCode, is(dbCode1));
        assertThat(xRefs.get(1).name, is(dbName1));
    }

    @Test
    void gracefullyHandleWrongFieldCount() {
        Optional<OBOTerm.XRef> result = converter.apply(
                FlatFieldBuilder.newFlatField().addField(FlatFieldLeaf.newFlatFieldLeaf("wrong format"))
                .buildString());
        assertThat(result.isPresent(), is(false));
    }
}