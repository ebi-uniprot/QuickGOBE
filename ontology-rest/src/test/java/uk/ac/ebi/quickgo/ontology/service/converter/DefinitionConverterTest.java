package uk.ac.ebi.quickgo.ontology.service.converter;

import uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder;
import uk.ac.ebi.quickgo.common.converter.FlatFieldLeaf;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static uk.ac.ebi.quickgo.ontology.common.document.OntologyDocMocker.createOBODoc;

/**
 * Tests the behaviour of the {@link DefinitionConverter} class.
 */
public class DefinitionConverterTest {
    private DefinitionConverter converter;

    private OntologyDocument doc;

    @Before
    public void setUp() throws Exception {
        converter = new DefinitionConverter();
        doc = createOBODoc("id", "name");
    }

    @Test
    public void convertsDocWithDefinitionTextAndNoTextXrefsIntoADefinitionObjectWithJustTheText() throws Exception {
        String text = "This is the definition of the ontology term";

        doc.definition = text;
        doc.definitionXrefs = Collections.emptyList();

        OBOTerm.Definition def = converter.apply(doc);

        assertThat(def.text, is(text));
        assertThat(def.definitionXrefs, hasSize(0));
    }

    @Test
    public void convertsDocWithNullDefinitionXrefListIntoDefinitionObjectWithEmptyXrefs() throws Exception {
        String text = "This is the definition of the ontology term";

        doc.definition = text;
        doc.definitionXrefs = null;

        OBOTerm.Definition def = converter.apply(doc);

        assertThat(def.text, is(text));
        assertThat(def.definitionXrefs, hasSize(0));
    }

    @Test
    public void convertsDocWithNoDefinitionTextAndWithASingleTextXrefIntoADefinitionObjectWithJustTheXref()
            throws Exception {
        String text = null;

        String xrefId = "id";
        String xrefDb = "db";
        String xrefText = createXrefText(xrefDb, xrefId);

        doc.definition = text;
        doc.definitionXrefs = Collections.singletonList(xrefText);

        OBOTerm.Definition def = converter.apply(doc);

        assertThat(def.text, is(text));

        List<OBOTerm.XRef> retrievedXrefs = def.definitionXrefs;
        assertThat(def.definitionXrefs, hasSize(1));

        OBOTerm.XRef retrievedXref = retrievedXrefs.get(0);
        assertThat(retrievedXref.dbId, is(xrefId));
        assertThat(retrievedXref.dbCode, is(xrefDb));
    }

    @Test
    public void convertsDocWithADefinitionTextAndWithMultipleTextXrefsIntoADefinitionObjectWithADefinitionAndXrefs()
            throws Exception {
        String text = "This is the definition of the ontology term";

        String xrefId1 = "id1";
        String xrefDb1 = "db1";
        String xrefText1 = createXrefText(xrefDb1, xrefId1);

        String xrefId2 = "id2";
        String xrefDb2 = "db2";
        String xrefText2 = createXrefText(xrefDb2, xrefId2);

        doc.definition = text;
        doc.definitionXrefs = Arrays.asList(xrefText1, xrefText2);

        OBOTerm.Definition def = converter.apply(doc);

        assertThat(def.text, is(text));

        List<OBOTerm.XRef> retrievedXrefs = def.definitionXrefs;
        assertThat(retrievedXrefs, hasSize(2));
    }

    private String createXrefText(String db, String id) {
        return FlatFieldBuilder.newFlatField()
                .addField(FlatFieldLeaf.newFlatFieldLeaf(db))
                .addField(FlatFieldLeaf.newFlatFieldLeaf(id))
                .buildString();
    }

    private OBOTerm.XRef createXref(String db, String id) {
        OBOTerm.XRef xref = new OBOTerm.XRef();
        xref.dbCode = db;
        xref.dbId = id;

        return xref;
    }
}
