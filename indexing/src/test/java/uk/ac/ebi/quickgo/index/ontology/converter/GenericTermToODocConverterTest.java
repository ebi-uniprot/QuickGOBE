package uk.ac.ebi.quickgo.index.ontology.converter;

import uk.ac.ebi.quickgo.model.ontology.generic.*;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;

import java.util.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the behaviour of the {@link GenericTermToODocConverter} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class GenericTermToODocConverterTest {
    private static final String TERM_ID = "id1";

    @Mock public GenericTerm term;

    private GenericTermToODocConverter converter = new GenericTermToODocConverter();

    @Before
    public void setup() {
        when(term.getId()).thenReturn(TERM_ID);
    }

    // considers
    @Test
    public void extractsNoReplaceElementsWhenRelationsIsNull() {
        List<TermRelation> relations = null;

        assertThat(converter.extractReplaceElementsFromRelations(relations), is(nullValue()));
    }

    @Test
    public void extractsAConsiderReplaceElementFromRelationsCollection() {
        RelationType relation = RelationType.CONSIDER;
        String replaceId = "id2";

        TermRelation mockReplace = mockReplaceRelation(replaceId, relation);

        Collection<TermRelation> relations = Collections.singletonList(mockReplace);

        List<String> replacesStrList = converter.extractReplaceElementsFromRelations(relations);
        assertThat(replacesStrList.size(), is(1));

        String replaceStr = replacesStrList.get(0);
        assertThat(replaceStr, containsString(replaceId));
        assertThat(replaceStr, containsString(relation.getFormalCode()));
    }

    @Test
    public void extractsAReplacedByReplaceElementWithinRelationsCollection() {
        RelationType relation = RelationType.REPLACEDBY;
        String replaceId = "id2";

        TermRelation mockReplace = mockReplaceRelation(replaceId, relation);

        Collection<TermRelation> relations = Collections.singletonList(mockReplace);

        List<String> replacesStrList = converter.extractReplaceElementsFromRelations(relations);
        assertThat(replacesStrList.size(), is(1));

        String replaceStr = replacesStrList.get(0);
        assertThat(replaceStr, containsString(replaceId));
        assertThat(replaceStr, containsString(relation.getFormalCode()));
    }

    @Test
    public void convertsATermWith2RelationsInReplacesSectionIntoDocWith2ReplacementElementsInReplacesSection() {
        TermRelation replacedByMock = mockReplaceRelation("id2", RelationType.REPLACEDBY);
        TermRelation considerModk = mockReplaceRelation("id3", RelationType.CONSIDER);

        List<TermRelation> relations = Arrays.asList(replacedByMock, considerModk);

        GenericTerm toConvert = mock(GenericTerm.class);
        when(toConvert.getReplaces()).thenReturn(relations);

        Optional<OntologyDocument> expectedDocOptional = converter.apply(Optional.of(toConvert));
        assertThat(expectedDocOptional.isPresent(), is(true));

        OntologyDocument expectedDoc = expectedDocOptional.get();

        assertThat(expectedDoc.replaces, hasSize(relations.size()));
    }

    @Test
    public void convertsATermWith2RelationsInReplacementsSectionIntoDocWith2ReplacementElementsInReplacementsSection() {
        TermRelation replacedByMock = mockReplaceRelation("id2", RelationType.REPLACEDBY);
        TermRelation considerModk = mockReplaceRelation("id3", RelationType.CONSIDER);

        List<TermRelation> relations = Arrays.asList(replacedByMock, considerModk);

        GenericTerm toConvert = mock(GenericTerm.class);
        when(toConvert.getReplacements()).thenReturn(relations);

        Optional<OntologyDocument> expectedDocOptional = converter.apply(Optional.of(toConvert));
        assertThat(expectedDocOptional.isPresent(), is(true));

        OntologyDocument expectedDoc = expectedDocOptional.get();

        assertThat(expectedDoc.replacements, hasSize(relations.size()));
    }

    private TermRelation mockReplaceRelation(String replacedTermId, RelationType relation) {
        GenericTerm replacedTerm = mock(GenericTerm.class);
        when(replacedTerm.getId()).thenReturn(replacedTermId);

        TermRelation mockReplace = mock(TermRelation.class);
        when(mockReplace.getChild()).thenReturn(replacedTerm);
        when(mockReplace.getTypeof()).thenReturn(relation);

        return mockReplace;
    }

    // history
    @Test
    public void extractsHistoryWhenExists() {
        AuditRecord auditRecord = mock(AuditRecord.class);

        when(auditRecord.getTermName()).thenReturn("term name");
        when(auditRecord.getTimestamp()).thenReturn("time 1");
        when(auditRecord.getAction()).thenReturn(AuditRecord.AuditAction.A);
        when(auditRecord.getCategory()).thenReturn(AuditRecord.AuditCategory.DEFINITION);
        when(auditRecord.getText()).thenReturn("text");

        TermOntologyHistory termOntologyHistory = mock(TermOntologyHistory.class);
        when(term.getHistory()).thenReturn(termOntologyHistory);

        when(termOntologyHistory.getHistoryAll()).thenReturn(Collections.singletonList(auditRecord));

        List<String> historyStrList = converter.extractHistory(term);
        assertThat(historyStrList, is(not(nullValue())));
        assertThat(historyStrList.size(), is(1));
        assertThat(historyStrList.get(0).contains("text"), is(true));
    }

    @Test
    public void extractHistoryWhenNotExists() {
        when(term.getHistory()).thenReturn(null);
        assertThat(converter.extractHistory(term), is(nullValue()));
    }

    // xrelations
    @Test
    public void extractXRelationsWhenExist() {
        CrossOntologyRelation xrelationMock = mock(CrossOntologyRelation.class);
        when(xrelationMock.getForeignID()).thenReturn("foreignId");
        when(xrelationMock.getForeignTerm()).thenReturn("foreignTerm");
        when(xrelationMock.getOtherNamespace()).thenReturn("otherNamespace");
        when(xrelationMock.getUrl()).thenReturn("url");
        when(xrelationMock.getRelation()).thenReturn("relation");

        when(term.getCrossOntologyRelations()).thenReturn(Collections.singletonList(xrelationMock));
        List<String> xrelationStrList = converter.extractXRelationsAsList(term);
        assertThat(xrelationStrList, is(not(nullValue())));
        assertThat(xrelationStrList.size(), is(1));
        assertThat(xrelationStrList.get(0).contains("otherNamespace"), is(true));
    }

    @Test
    public void extractXRelationsWhenNotExist() {
        when(term.getCrossOntologyRelations()).thenReturn(null);
        assertThat(converter.extractXRelationsAsList(term), is(nullValue()));
    }

    // xrefs
    @Test
    public void extractXRefsWhenExist() {
        NamedXRef namedXRef = mock(NamedXRef.class);
        when(namedXRef.getName()).thenReturn("name");
        when(namedXRef.getDb()).thenReturn("db");
        when(namedXRef.getId()).thenReturn("id");
        when(term.getXrefs()).thenReturn(Collections.singletonList(namedXRef));

        List<String> xrefStrList = converter.extractXRefs(term);
        assertThat(xrefStrList, is(not(nullValue())));
        assertThat(xrefStrList.size(), is(1));
        assertThat(xrefStrList.get(0).contains("db"), is(true));
    }

    @Test
    public void extractXrefsWhenNotExist() {
        when(term.getXrefs()).thenReturn(null);
        assertThat(converter.extractXRefs(term), is(nullValue()));
    }

    // synonyms
    @Test
    public void extractSynonymsWhenExist() {
        Synonym synonym = mock(Synonym.class);
        when(synonym.getName()).thenReturn("name1");
        when(synonym.getType()).thenReturn("type1");

        when(term.getSynonyms()).thenReturn(Collections.singletonList(synonym));

        // check doc.synonyms
        List<String> synonyms = converter.extractSynonyms(term);
        assertThat(synonyms, is(not(nullValue())));
        assertThat(synonyms.size(), is(1));
        assertThat(synonyms.get(0).contains("name1"), is(true));
        assertThat(synonyms.get(0).contains("type1"), is(true));

        // check doc.synonymNames
        List<String> synonymNames = converter.extractSynonymNames(term);
        assertThat(synonymNames, is(not(nullValue())));
        assertThat(synonymNames.size(), is(1));
        assertThat(synonymNames.get(0).contains("name1"), is(true));
        assertThat(synonymNames.get(0).contains("type1"), is(false));
    }

    @Test
    public void extractSynonymsWhenNotExist() {
        when(term.getSynonyms()).thenReturn(null);
        assertThat(converter.extractSynonyms(term), is(nullValue()));
        assertThat(converter.extractSynonymNames(term), is(nullValue()));
    }

    // simple fields
    @Test
    public void convertSimpleFields() {
        boolean isObsolete = false;
        when(term.isObsolete()).thenReturn(isObsolete);
        when(term.getComment()).thenReturn("comment1");
        when(term.getDefinition()).thenReturn("def1");
        when(term.getName()).thenReturn("name1");
        when(term.getOntologyType()).thenReturn("GO");
        when(term.secondaries()).thenReturn("sec1,sec2");
        when(term.getSubsetsNames()).thenReturn(Arrays.asList("goslim_generic", "goslim_yeast"));

        ArrayList<GenericTerm> replacedBy = new ArrayList<>();
        GenericTerm replacementTerm = mock(GenericTerm.class);
        when(replacementTerm.getId()).thenReturn("replacement1");
        replacedBy.add(replacementTerm);
        when(term.replacedBy()).thenReturn(replacedBy);

        Optional<OntologyDocument> result = converter.apply(Optional.of(term));
        assertThat(result.isPresent(), is(true));
        OntologyDocument document = result.get();
        assertThat(document.isObsolete, is(isObsolete));
        assertThat(document.comment, is("comment1"));
        assertThat(document.definition, is("def1"));
        assertThat(document.name, is("name1"));
        assertThat(document.ontologyType, is("GO"));
        assertThat(document.secondaryIds, contains("sec1", "sec2"));
        assertThat(document.subsets, contains("goslim_generic", "goslim_yeast"));
        assertThat(document.replacedBy, is("replacement1"));
    }

    @Test
    public void convertsSimpleNullFields() {
        Optional<OntologyDocument> result = converter.apply(Optional.of(term));
        assertThat(result.isPresent(), is(true));
        OntologyDocument document = result.get();
        assertThat(document.isObsolete, is(false));
        assertThat(document.comment, is(nullValue()));
        assertThat(document.definition, is(nullValue()));
        assertThat(document.name, is(nullValue()));
        assertThat(document.ontologyType, is(nullValue()));
        assertThat(document.secondaryIds, is(nullValue()));
        assertThat(document.replacedBy, is(nullValue()));
    }

    // empty optional conversion
    @Test
    public void convertsEmptyOptional() {
        Optional<OntologyDocument> documentOptional = converter.apply(Optional.empty());
        assertThat(documentOptional.isPresent(), is(false));
    }

    @Test
    public void extractDefinitionXref() throws Exception {
        String db1 = "db1";
        String id1 = "id1";

        XRef xref = mock(XRef.class);
        when(xref.getId()).thenReturn(id1);
        when(xref.getDb()).thenReturn(db1);

        when(term.getDefinitionXrefs()).thenReturn(Collections.singletonList(xref));

        List<String> xrefsText = converter.extractDefinitionXrefs(term);

        assertThat(xrefsText, hasSize(1));
        assertThat(xrefsText, hasItems(containsString(db1), containsString(id1)));
    }

    @Test
    public void extractionOfEmptyDefinitionXrefListReturnsEmptyList() throws Exception {
        when(term.getDefinitionXrefs()).thenReturn(Collections.emptyList());

        List<String> xrefsText = converter.extractDefinitionXrefs(term);

        assertThat(xrefsText, hasSize(0));
    }
}