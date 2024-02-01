package uk.ac.ebi.quickgo.ontology.service.converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.quickgo.common.converter.FlatFieldBuilder;
import uk.ac.ebi.quickgo.common.converter.FlatFieldLeaf;
import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.model.OBOTerm;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static uk.ac.ebi.quickgo.ontology.common.document.OntologyDocMocker.createOBODoc;

/**
 * Tests the behaviour of the {@link DefinitionConverter} class.
 */
class DefinitionConverterTest {
    private DefinitionConverter converter;

    private OntologyDocument doc;

    @BeforeEach
    void setUp() {
        converter = new DefinitionConverter();
        doc = createOBODoc("id", "name");
    }

    @Test
    void convertsDocWithDefinitionTextAndNoTextXrefsIntoADefinitionObjectWithJustTheText() {
        String text = "This is the definition of the ontology term";

        doc.definition = text;
        doc.definitionXrefs = Collections.emptyList();

        OBOTerm.Definition def = converter.apply(doc);

        assertThat(def.text, is(text));
        assertThat(def.xrefs, hasSize(0));
    }

    @Test
    void convertsDocWithNullDefinitionXrefListIntoDefinitionObjectWithEmptyXrefs() {
        String text = "This is the definition of the ontology term";

        doc.definition = text;
        doc.definitionXrefs = null;

        OBOTerm.Definition def = converter.apply(doc);

        assertThat(def.text, is(text));
        assertThat(def.xrefs, hasSize(0));
    }

    @Test
    void convertsDocWithNoDefinitionTextAndWithASingleTextXrefIntoADefinitionObjectWithJustTheXref() {
        String text = null;

        String xrefId = "id";
        String xrefDb = "db";
        String xrefText = createXrefText(xrefDb, xrefId);

        doc.definition = text;
        doc.definitionXrefs = Collections.singletonList(xrefText);

        OBOTerm.Definition def = converter.apply(doc);

        assertThat(def.text, is(text));

        List<OBOTerm.XRef> retrievedXrefs = def.xrefs;
        assertThat(def.xrefs, hasSize(1));

        OBOTerm.XRef retrievedXref = retrievedXrefs.get(0);
        assertThat(retrievedXref.dbId, is(xrefId));
        assertThat(retrievedXref.dbCode, is(xrefDb));
    }

    @Test
    void convertsDocWithADefinitionTextAndWithMultipleTextXrefsIntoADefinitionObjectWithADefinitionAndXrefs() {
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

        List<OBOTerm.XRef> retrievedXrefs = def.xrefs;
        assertThat(retrievedXrefs, hasSize(2));
        assertThat(collectionHasObjectWithContents(retrievedXrefs, xrefId1, xrefDb1), is(true));
        assertThat(collectionHasObjectWithContents(retrievedXrefs, xrefId2, xrefDb2), is(true));
    }

    private String createXrefText(String db, String id) {
        return FlatFieldBuilder.newFlatField()
                .addField(FlatFieldLeaf.newFlatFieldLeaf(db))
                .addField(FlatFieldLeaf.newFlatFieldLeaf(id))
                .buildString();
    }

    private boolean collectionHasObjectWithContents(Collection<OBOTerm.XRef> xrefs, String id, String db) {
        return xrefs.stream()
                .filter(xref -> xref.dbId.equals(id) && xref.dbCode.equals(db))
                .findFirst()
                .isPresent();
    }
}
