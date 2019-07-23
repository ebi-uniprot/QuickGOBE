package uk.ac.ebi.quickgo.index.ontology.converter;

import uk.ac.ebi.quickgo.model.ontology.generic.*;
import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;

import java.util.*;
import java.util.function.Function;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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

    //replacements
    @Test
    public void extractsNoReplacementsWhenGenericTermHasNullReplacements() {
        when(term.getReplacements()).thenReturn(null);

        OntologyDocument docOpt = converter.apply(term);
        List<String> extractedReplacement = extractFieldFromDocument(docOpt,
                (OntologyDocument doc) -> doc.replacements);

        assertThat(extractedReplacement, is(nullValue()));
    }

    @Test
    public void extractsAConsiderWhenGenericTermHasReplacementsWithAConsider() {
        RelationType relation = RelationType.CONSIDER;
        String replacedByTermId = "id2";
        String replacedWithTermId = "id3";

        TermRelation mockReplace = mockReplaceRelation(replacedByTermId, replacedWithTermId, relation);
        when(term.getReplacements()).thenReturn(Collections.singletonList(mockReplace));

        OntologyDocument docOpt = converter.apply(term);
        List<String> extractedReplacements = extractFieldFromDocument(docOpt,
                (OntologyDocument doc) -> doc.replacements);

        assertThat(extractedReplacements, hasSize(1));

        assertThat(extractedReplacements,
                hasItems(containsString(replacedWithTermId), containsString(relation.getFormalCode())));
    }

    @Test
    public void extractsAReplacedByWhenGenericTermHasReplacementsWithReplacedBy() {
        RelationType relation = RelationType.REPLACEDBY;
        String replacedByTermId = "id2";
        String replacedWithTermId = "id2";

        TermRelation mockReplace = mockReplaceRelation(replacedByTermId, replacedWithTermId, relation);
        when(term.getReplacements()).thenReturn(Collections.singletonList(mockReplace));

        OntologyDocument docOpt = converter.apply(term);
        List<String> extractedReplacements =
                extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.replacements);

        assertThat(extractedReplacements, hasSize(1));

        assertThat(extractedReplacements,
                hasItems(containsString(replacedByTermId), containsString(relation.getFormalCode())));
    }

    @Test
    public void extracts2ReplacementsWhenGenericTermHas2Replacements() {
        TermRelation replacedByMock = mockReplaceRelation("id2", "id3", RelationType.REPLACEDBY);
        TermRelation considerMock = mockReplaceRelation("id4", "id5", RelationType.CONSIDER);

        List<TermRelation> relations = Arrays.asList(replacedByMock, considerMock);

        when(term.getReplacements()).thenReturn(relations);

        OntologyDocument docOpt = converter.apply(term);
        List<String> extractedReplacements =
                extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.replacements);

        assertThat(extractedReplacements, hasSize(relations.size()));
    }

    @Test
    public void convertsATermWith2RelationsInReplacementsSectionIntoDocWith2ReplacementElementsInReplacementsSection() {
        TermRelation replacedByMock = mockReplaceRelation("id2", "id3", RelationType.REPLACEDBY);
        TermRelation considerMock = mockReplaceRelation("id4", "id5", RelationType.CONSIDER);

        List<TermRelation> relations = Arrays.asList(replacedByMock, considerMock);

        GenericTerm toConvert = mock(GenericTerm.class);
        when(toConvert.getReplacements()).thenReturn(relations);

        OntologyDocument expectedDocOptional = converter.apply(toConvert);
        OntologyDocument expectedDoc = expectedDocOptional;

        assertThat(expectedDoc.replacements, hasSize(relations.size()));
    }

    //replaces
    @Test
    public void extractsNoReplacesWhenGenericTermHasNullReplaces() {
        when(term.getReplacements()).thenReturn(null);

        OntologyDocument docOpt = converter.apply(term);
        List<String> extractedReplacement = extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.replaces);

        assertThat(extractedReplacement, is(nullValue()));
    }

    @Test
    public void extractsAConsiderWhenGenericTermHasReplacesWithAConsider() {
        RelationType relation = RelationType.CONSIDER;
        String replacedWithTermId = "id2";
        String replacedByTermId = "id3";

        TermRelation mockReplace = mockReplaceRelation(replacedWithTermId, replacedByTermId, relation);
        when(term.getReplaces()).thenReturn(Collections.singletonList(mockReplace));

        OntologyDocument docOpt = converter.apply(term);
        List<String> extractedReplaces = extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.replaces);

        assertThat(extractedReplaces, hasSize(1));

        assertThat(extractedReplaces,
                hasItems(containsString(replacedWithTermId), containsString(relation.getFormalCode())));
    }

    @Test
    public void extractsAReplacedByWhenGenericTermHasReplacesWithReplacedBy() {
        RelationType relation = RelationType.REPLACEDBY;
        String replacedWIthTermId = "id2";
        String replacedByTermId = "id2";

        TermRelation mockReplace = mockReplaceRelation(replacedWIthTermId, replacedByTermId, relation);
        when(term.getReplaces()).thenReturn(Collections.singletonList(mockReplace));

        OntologyDocument docOpt = converter.apply(term);
        List<String> extractedReplaces = extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.replaces);

        assertThat(extractedReplaces, hasSize(1));

        assertThat(extractedReplaces,
                hasItems(containsString(replacedWIthTermId), containsString(relation.getFormalCode())));
    }

    @Test
    public void extracts2ReplacesWhenGenericTermHas2Replaces() {
        TermRelation replacedByMock = mockReplaceRelation("id2", "id3", RelationType.REPLACEDBY);
        TermRelation considerMock = mockReplaceRelation("id4", "id5", RelationType.CONSIDER);

        List<TermRelation> relations = Arrays.asList(replacedByMock, considerMock);

        when(term.getReplaces()).thenReturn(relations);

        OntologyDocument docOpt = converter.apply(term);
        List<String> extractedReplaces = extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.replaces);

        assertThat(extractedReplaces, hasSize(relations.size()));
    }

    @Test
    public void convertsATermWith2RelationsInReplacesSectionIntoDocWith2ReplacementElementsInReplacesSection() {
        TermRelation replacedByMock = mockReplaceRelation("id2", "id3", RelationType.REPLACEDBY);
        TermRelation considerModk = mockReplaceRelation("id4", "id5", RelationType.CONSIDER);

        List<TermRelation> relations = Arrays.asList(replacedByMock, considerModk);

        GenericTerm toConvert = mock(GenericTerm.class);
        when(toConvert.getReplaces()).thenReturn(relations);

        OntologyDocument expectedDocOptional = converter.apply(toConvert);
        OntologyDocument expectedDoc = expectedDocOptional;

        assertThat(expectedDoc.replaces, hasSize(relations.size()));
    }

    private TermRelation mockReplaceRelation(String leftTermId, String rightTermId, RelationType relation) {
        GenericTerm leftTerm = mock(GenericTerm.class);
        when(leftTerm.getId()).thenReturn(leftTermId);

        GenericTerm rightTerm = mock(GenericTerm.class);
        when(rightTerm.getId()).thenReturn(rightTermId);

        TermRelation mockReplace = mock(TermRelation.class);
        when(mockReplace.getChild()).thenReturn(leftTerm);
        when(mockReplace.getParent()).thenReturn(rightTerm);
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

        OntologyDocument docOpt = converter.apply(term);
        List<String> extractedHistory = extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.history);

        assertThat(extractedHistory, hasSize(1));
        assertThat(extractedHistory, hasItems(containsString("text")));
    }

    @Test
    public void extractHistoryWhenNotExists() {
        when(term.getHistory()).thenReturn(null);

        OntologyDocument docOpt = converter.apply(term);
        List<String> extractedHistory = extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.history);

        assertThat(extractedHistory, is(nullValue()));
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

        OntologyDocument docOpt = converter.apply(term);
        List<String> extractedXRelations = extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.xRelations);

        assertThat(extractedXRelations, is(not(nullValue())));
        assertThat(extractedXRelations, hasSize(1));
        assertThat(extractedXRelations, hasItems(containsString("otherNamespace")));
    }

    @Test
    public void extractXRelationsWhenNotExist() {
        when(term.getCrossOntologyRelations()).thenReturn(null);

        OntologyDocument docOpt = converter.apply(term);
        List<String> extractedXRelations = extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.xRelations);

        assertThat(extractedXRelations, is(nullValue()));
    }

    // xrefs
    @Test
    public void extractXRefsWhenExist() {
        NamedXRef namedXRef = mock(NamedXRef.class);
        when(namedXRef.getName()).thenReturn("name");
        when(namedXRef.getDb()).thenReturn("db");
        when(namedXRef.getId()).thenReturn("id");

        when(term.getXrefs()).thenReturn(Collections.singletonList(namedXRef));

        OntologyDocument docOpt = converter.apply(term);
        List<String> extractedXrefs = extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.xrefs);

        assertThat(extractedXrefs, hasSize(1));
        assertThat(extractedXrefs, hasItems(containsString("db")));
    }

    @Test
    public void extractXrefsWhenNotExist() {
        when(term.getXrefs()).thenReturn(null);

        OntologyDocument docOpt = converter.apply(term);
        List<String> extractedXrefs = extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.xrefs);

        assertThat(extractedXrefs, is(nullValue()));
    }

    // synonyms
    @Test
    public void extractSynonymsWhenExist() {
        Synonym synonym = mock(Synonym.class);
        when(synonym.getName()).thenReturn("name1");
        when(synonym.getType()).thenReturn("type1");

        when(term.getSynonyms()).thenReturn(Collections.singletonList(synonym));

        // check doc.synonyms
        OntologyDocument docOpt = converter.apply(term);
        List<String> extractedSynonyms = extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.synonyms);

        assertThat(extractedSynonyms, hasSize(1));
        assertThat(extractedSynonyms, hasItems(containsString("name1"), containsString("type1")));
    }

    @Test
    public void extractSynonymsWhenNotExist() {
        when(term.getSynonyms()).thenReturn(null);

        OntologyDocument docOpt = converter.apply(term);
        List<String> extractedSynonyms = extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.synonyms);
        List<String> extractedSynonymNames =
                extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.synonymNames);

        assertThat(extractedSynonyms, is(nullValue()));
        assertThat(extractedSynonymNames, is(nullValue()));
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
        when(term.getAltIds()).thenReturn(Arrays.asList(new XRef(null, "GO:0005676"), new XRef(null,
                "GO:0008620")));
        when(term.getSubsetsNames()).thenReturn(Arrays.asList("goslim_generic", "goslim_yeast"));

        ArrayList<GenericTerm> replacedBy = new ArrayList<>();
        GenericTerm replacementTerm = mock(GenericTerm.class);
        replacedBy.add(replacementTerm);

        OntologyDocument result = converter.apply(term);
        OntologyDocument document = result;
        assertThat(document.isObsolete, is(isObsolete));
        assertThat(document.comment, is("comment1"));
        assertThat(document.definition, is("def1"));
        assertThat(document.name, is("name1"));
        assertThat(document.ontologyType, is("GO"));
        assertThat(document.secondaryIds, contains("GO:0005676", "GO:0008620"));
        assertThat(document.subsets, contains("goslim_generic", "goslim_yeast"));
    }

    @Test
    public void convertsSimpleNullFields() {
        OntologyDocument result = converter.apply(term);
        OntologyDocument document = result;
        assertThat(document.isObsolete, is(false));
        assertThat(document.comment, is(nullValue()));
        assertThat(document.definition, is(nullValue()));
        assertThat(document.name, is(nullValue()));
        assertThat(document.ontologyType, is(nullValue()));
        assertThat(document.secondaryIds, is(nullValue()));
    }

    @Test
    public void extractDefinitionXref() {
        String db1 = "db1";
        String id1 = "id1";

        XRef xref = mock(XRef.class);
        when(xref.getId()).thenReturn(id1);
        when(xref.getDb()).thenReturn(db1);

        when(term.getDefinitionXrefs()).thenReturn(Collections.singletonList(xref));

        OntologyDocument docOpt = converter.apply(term);
        List<String> extractedDefinitionXrefs =
                extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.definitionXrefs);

        assertThat(extractedDefinitionXrefs, hasSize(1));
        assertThat(extractedDefinitionXrefs, hasItems(containsString(db1), containsString(id1)));
    }

    @Test
    public void extractionOfEmptyDefinitionXrefListReturnsEmptyList() {
        when(term.getDefinitionXrefs()).thenReturn(Collections.emptyList());

        OntologyDocument docOpt = converter.apply(term);
        List<String> extractedDefinitionXrefs =
                extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.definitionXrefs);

        assertThat(extractedDefinitionXrefs, hasSize(0));
    }

    //credits
    @Test
    public void extractsNoCreditElementsWhenCreditsInTermIsNull() {
        when(term.getCredits()).thenReturn(null);

        OntologyDocument docOpt = converter.apply(term);
        List<String> extractedCredits = extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.credits);

        assertThat(extractedCredits, is(nullValue()));
    }

    @Test
    public void extractsNoCreditElementsWhenCreditsInTermIsEmpty() {
        when(term.getCredits()).thenReturn(Collections.emptyList());

        OntologyDocument docOpt = converter.apply(term);
        List<String> extractedCredits = extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.credits);

        assertThat(extractedCredits, is(nullValue()));
    }

    @Test
    public void extracts1CreditElementWhenCreditsInTermHas1Element() {
        String code1 = "BHF";
        String url1 = "http://www.ucl.ac.uk/cardiovasculargeneontology/";

        TermCredit credit1 = new TermCredit(code1, url1);

        String code2 = "BHF1";
        String url2 = "http://www.ucl.ac.uk/cardiovasculargeneontology/1";

        TermCredit credit2 = new TermCredit(code2, url2);

        when(term.getCredits()).thenReturn(Arrays.asList(credit1, credit2));

        OntologyDocument docOpt = converter.apply(term);
        List<String> extractedCredits = extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.credits);

        assertThat(extractedCredits, hasSize(2));
        assertThat(creditExists(credit1, extractedCredits), is(true));
        assertThat(creditExists(credit2, extractedCredits), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotConvertNullGenericTerm() {
        converter.apply(null);
    }

    private boolean creditExists(TermCredit credit, Collection<String> extractedCredits) {
        return extractedCredits.stream()
                .filter(extractedCredit -> extractedCredit.contains(credit.getCode())
                        && extractedCredit.contains(credit.getUrl()))
                .findFirst().isPresent();
    }

    public static <T> T extractFieldFromDocument(OntologyDocument docOpt, Function<OntologyDocument, T>
            extractor) {
        return extractor.apply(docOpt);
    }
}
