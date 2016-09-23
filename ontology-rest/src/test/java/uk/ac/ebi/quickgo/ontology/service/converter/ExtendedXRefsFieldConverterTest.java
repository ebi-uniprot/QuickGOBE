package uk.ac.ebi.quickgo.ontology.service.converter;

import uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder;
import uk.ac.ebi.quickgo.common.converter.FlatFieldLeaf;
import uk.ac.ebi.quickgo.ontology.model.GOTerm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the behaviour of the {@link ExtendedXRefsFieldConverter} class.
 */
public class ExtendedXRefsFieldConverterTest {
    private ExtendedXRefsFieldConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new ExtendedXRefsFieldConverter();
    }

    @Test
    public void convertsExtendedXrefs() {
        String dbCode0 = "db code 0";
        String dbId0 = "db id 0";
        String dbSymbol0 = "db symbol 0";
        String dbName0 = "db name 0";

        String dbCode1 = "db code 1";
        String dbId1 = "db id 1";
        String dbSymbol1 = "db symbol 1";
        String dbName1 = "db name 1";

        List<String> rawXrefs = new ArrayList<>();
        rawXrefs.add(
                FlatFieldBuilder.newFlatField()
                        .addField(FlatFieldLeaf.newFlatFieldLeaf(dbCode0))
                        .addField(FlatFieldLeaf.newFlatFieldLeaf(dbId0))
                        .addField(FlatFieldLeaf.newFlatFieldLeaf(dbSymbol0))
                        .addField(FlatFieldLeaf.newFlatFieldLeaf(dbName0))
                        .buildString()
        );
        rawXrefs.add(
                FlatFieldBuilder.newFlatField()
                        .addField(FlatFieldLeaf.newFlatFieldLeaf(dbCode1))
                        .addField(FlatFieldLeaf.newFlatFieldLeaf(dbId1))
                        .addField(FlatFieldLeaf.newFlatFieldLeaf(dbSymbol1))
                        .addField(FlatFieldLeaf.newFlatFieldLeaf(dbName1))
                        .buildString()
        );

        List<GOTerm.ExtendedXRef> extractedXRefs = converter.convertFieldList(rawXrefs);
        assertThat(extractedXRefs.size(), is(2));

        assertThat(extendedXrefExists(dbCode0, dbId0, dbSymbol0, dbName0, extractedXRefs), is(true));
        assertThat(extendedXrefExists(dbCode1, dbId1, dbSymbol1, dbName1, extractedXRefs), is(true));
    }

    @Test
    public void gracefullyHandleWrongFieldCount() {
        Optional<GOTerm.ExtendedXRef> result = converter.apply(
                FlatFieldBuilder.newFlatField().addField(FlatFieldLeaf.newFlatFieldLeaf("wrong format"))
                        .buildString());
        assertThat(result.isPresent(), is(false));
    }

    private boolean extendedXrefExists(String dbCode, String id, String symbol, String name,
            List<GOTerm.ExtendedXRef> xrefs) {

        return xrefs.stream().filter(xref ->
                xref.dbCode.equals(dbCode)
                        && xref.dbId.equals(id)
                        && xref.symbol.equals(symbol)
                        && xref.name.equals(name)
        ).findFirst().isPresent();
    }
}