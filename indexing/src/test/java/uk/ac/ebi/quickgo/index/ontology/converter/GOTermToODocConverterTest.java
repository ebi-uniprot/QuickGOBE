package uk.ac.ebi.quickgo.index.ontology.converter;

import uk.ac.ebi.quickgo.model.ontology.generic.AuditRecord;
import uk.ac.ebi.quickgo.model.ontology.generic.TermOntologyHistory;
import uk.ac.ebi.quickgo.model.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GOTermBlacklist;
import uk.ac.ebi.quickgo.model.ontology.go.TaxonConstraint;
import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.index.ontology.converter.GenericTermToODocConverterTest.extractFieldFromDocument;

/**
 * Created 14/12/15
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class GOTermToODocConverterTest {
    private static final String TERM_ID = "id1";

    @Mock
    public GOTerm term;

    private GOTermToODocConverter converter = new GOTermToODocConverter();

    @Before
    public void setup() {
        when(term.getId()).thenReturn(TERM_ID);
    }

    // annotation guidelines
    @Test
    public void extractsAnnGuideLinesWhenExists() {
        GOTerm.NamedURL namedURL = mock(GOTerm.NamedURL.class);
        when(namedURL.getTitle()).thenReturn("title");
        when(namedURL.getUrl()).thenReturn("url");
        when(term.getGuidelines()).thenReturn(Collections.singletonList(namedURL));

        OntologyDocument docConverted = converter.apply(term);
        List<String> extractedAnnGuidelines =
                extractFieldFromDocument(docConverted, (OntologyDocument doc) -> doc.annotationGuidelines);

        assertThat(extractedAnnGuidelines, hasSize(1));
        assertThat(extractedAnnGuidelines, hasItems(containsString("title"), containsString("url")));
    }

    @Test
    public void extractsAnnGuideLinesWhenNotExists() {
        when(term.getGuidelines()).thenReturn(null);

        OntologyDocument docConverted = converter.apply(term);
        List<String> extractedAnnGuidelines =
                extractFieldFromDocument(docConverted, (OntologyDocument doc) -> doc.annotationGuidelines);

        assertThat(extractedAnnGuidelines, is(nullValue()));
    }

    // taxon constraints
    @Test
    public void extractsTaxonConstraintsWhenExists() {
        TaxonConstraint taxonConstraint = mock(TaxonConstraint.class);
        when(taxonConstraint.getSourcesIds()).thenReturn(Arrays.asList("pubmed1", "pubmed2"));
        when(taxonConstraint.getGoId()).thenReturn("goId1");
        when(taxonConstraint.getName()).thenReturn("name1");
        when(taxonConstraint.relationship()).thenReturn("rel1");
        when(taxonConstraint.getTaxId()).thenReturn("taxId1");
        when(taxonConstraint.taxIdType()).thenReturn("taxIdType1");
        when(taxonConstraint.getTaxonName()).thenReturn("taxName1");

        when(term.getTaxonConstraints()).thenReturn(Collections.singletonList(taxonConstraint));

        OntologyDocument docConverted = converter.apply(term);
        List<String> extractedTaxonConstraints = extractFieldFromDocument(docConverted,
                (OntologyDocument doc) -> doc.taxonConstraints);

        assertThat(extractedTaxonConstraints, hasSize(1));
        assertThat(extractedTaxonConstraints, hasItems(
                containsString("goId1"),
                containsString("name1"),
                containsString("rel1"),
                containsString("taxId1"),
                containsString("taxIdType1"),
                containsString("taxName1"),
                containsString("pubmed1"),
                containsString("pubmed2")
        ));
    }

    @Test
    public void extractsTaxonConstraintsWhenNotExists() {
        when(term.getTaxonConstraints()).thenReturn(null);

        OntologyDocument docConverted = converter.apply(term);
        List<String> extractedTaxonConstraints = extractFieldFromDocument(docConverted,
                (OntologyDocument doc) -> doc.taxonConstraints);

        assertThat(extractedTaxonConstraints, is(nullValue()));
    }

    // simple fields
    @Test
    public void convertsSimpleFieldsWhenExists() {
        when(term.getUsage()).thenReturn(GOTerm.ETermUsage.E);
        when(term.getAspect()).thenReturn(GOTerm.EGOAspect.C);

        OntologyDocument result = converter.apply(term);
        OntologyDocument document = result;
        assertThat(document.aspect, is(GOTerm.EGOAspect.C.text));
        assertThat(document.usage, is(GOTerm.ETermUsage.E.text));
    }

    @Test
    public void convertsSimpleFieldsWhenNotExists() {
        when(term.getUsage()).thenReturn(null);
        when(term.getAspect()).thenReturn(null);

        OntologyDocument result = converter.apply(term);
        OntologyDocument document = result;
        assertThat(document.usage, is(nullValue()));
        assertThat(document.aspect, is(nullValue()));
    }

    // blacklist
    @Test
    public void extractsBlacklistWhenExists() {
        GOTermBlacklist goTermBlacklist = mock(GOTermBlacklist.class);
        when(goTermBlacklist.getAncestorGOID()).thenReturn("GO:0007005");
        when(goTermBlacklist.getGoId()).thenReturn("GO:0000001");
        when(goTermBlacklist.getCategory()).thenReturn("NOT-qualified manual");
        when(goTermBlacklist.getEntityName()).thenReturn("A5I1R9_CLOBH");
        when(goTermBlacklist.getEntityType()).thenReturn("protein");
        when(goTermBlacklist.getTaxonId()).thenReturn(441771);
        when(goTermBlacklist.getProteinAc()).thenReturn("A5I1R9");
        when(goTermBlacklist.getReason()).thenReturn(
                "1 NOT-qualified manual annotation exists with evidence code ECO:0000318 from this reference: " +
                        "GO_REF:0000033");
        when(goTermBlacklist.getMethodId()).thenReturn("IPR1234567");

        when(term.getBlacklist()).thenReturn(Collections.singletonList(goTermBlacklist));

        OntologyDocument docConverted = converter.apply(term);
        List<String> extractedBlacklist =
                extractFieldFromDocument(docConverted, (OntologyDocument doc) -> doc.blacklist);

        assertThat(extractedBlacklist, hasSize(1));
        assertThat(extractedBlacklist, hasItems(containsString("GO:0007005"),
                containsString("GO:0000001"),
                containsString("NOT-qualified manual"),
                containsString("A5I1R9_CLOBH"),
                containsString("protein"),
                containsString("441771"),
                containsString("A5I1R9"),
                containsString(
                        "1 NOT-qualified manual annotation exists with evidence code ECO:0000318 from this reference:" +
                                " " +
                                "GO_REF:0000033"),
                containsString("IPR1234567")));
    }

    @Test
    public void extractsBlacklistWhenNotExists() {
        when(term.getBlacklist()).thenReturn(null);

        OntologyDocument docConverted = converter.apply(term);
        List<String> extractedBlacklist =
                extractFieldFromDocument(docConverted, (OntologyDocument doc) -> doc.blacklist);

        assertThat(extractedBlacklist, is(nullValue()));
    }

    //GO discussions
    @Test
    public void extractingGoDiscussionsFromEmptyPlannedChangesListReturnsNull() {
        when(term.getPlannedChanges()).thenReturn(null);

        OntologyDocument docConverted = converter.apply(term);
        List<String> extractedGoDiscussions =
                extractFieldFromDocument(docConverted, (OntologyDocument doc) -> doc.goDiscussions);

        assertThat(extractedGoDiscussions, is(nullValue()));
    }

    @Test
    public void extracts1GoDiscussionsFrom1ElementPlannedChangesList() {
        String title = "Viral Processes";
        String url = "http://wiki.geneontology.org/index.php/Virus_terms";

        GOTerm.NamedURL plannedChange = new GOTerm.NamedURL(title, url);

        when(term.getPlannedChanges()).thenReturn(Collections.singletonList(plannedChange));

        OntologyDocument docConverted = converter.apply(term);
        List<String> extractedGoDiscussions =
                extractFieldFromDocument(docConverted, (OntologyDocument doc) -> doc.goDiscussions);

        assertThat(extractedGoDiscussions, hasSize(1));

        assertThat(extractedGoDiscussions, hasItems(containsString(title), containsString(url)));
    }

    @Test
    public void extracts2GoDiscussionsFrom2ElementPlannedChangesList() {
        String title1 = "Viral Processes";
        String url1 = "http://wiki.geneontology.org/index.php/Virus_terms";

        GOTerm.NamedURL plannedChange1 = new GOTerm.NamedURL(title1, url1);

        String title2 = "signalling";
        String url2 = "http://wiki.geneontology.org/index.php/Signaling";

        GOTerm.NamedURL plannedChange2 = new GOTerm.NamedURL(title2, url2);

        when(term.getPlannedChanges()).thenReturn(Arrays.asList(plannedChange1, plannedChange2));

        OntologyDocument docConverted = converter.apply(term);
        List<String> extractedGoDiscussions =
                extractFieldFromDocument(docConverted, (OntologyDocument doc) -> doc.goDiscussions);
        assertThat(extractedGoDiscussions, hasSize(2));

        assertThat(goDiscussionExists(plannedChange1, extractedGoDiscussions), is(true));
        assertThat(goDiscussionExists(plannedChange2, extractedGoDiscussions), is(true));
    }

    //Protein complexes
    @Test
    public void extracts2ProteinComplexFromProteinComplexList() {
        String db = "Intact";
        String id1 = "EBI-2410732";
        String symbol1 = "nef1_yeast";
        String name1 = "Nucleotide-excision repair factor 1 complex";

        GOTerm.ProteinComplex proteinComplex1 = new GOTerm.ProteinComplex(db, id1, symbol1, name1);

        String id2 = "EBI-2353861";
        String symbol2 = "hat-b_yeast";
        String name2 = "Histone acetyltransferase B";
        GOTerm.ProteinComplex proteinComplex2 = new GOTerm.ProteinComplex(db, id2, symbol2, name2);

        when(term.getProteinComplexes()).thenReturn(Arrays.asList(proteinComplex1, proteinComplex2));

        OntologyDocument docConverted = converter.apply(term);
        List<String> extractedProteinComplexes =
                extractFieldFromDocument(docConverted, (OntologyDocument doc) -> doc.proteinComplexes);

        assertThat(extractedProteinComplexes, hasSize(2));

        assertThat(proteinComplexExists(proteinComplex1, extractedProteinComplexes), is(true));
        assertThat(proteinComplexExists(proteinComplex2, extractedProteinComplexes), is(true));
    }

    @Test
    public void extractingProteinComplexesFromEmptyProteinComplexesListReturnsNull() {
        when(term.getProteinComplexes()).thenReturn(null);

        OntologyDocument docConverted = converter.apply(term);
        List<String> extractedProteinComplexes =
                extractFieldFromDocument(docConverted, (OntologyDocument doc) -> doc.proteinComplexes);

        assertThat(extractedProteinComplexes, is(nullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotConvertNullGOTerm() {
        converter.apply(null);
    }

    private boolean goDiscussionExists(GOTerm.NamedURL goDiscussion, Collection<String> expectedGoDiscussions) {
        return expectedGoDiscussions.stream()
                .filter(expectedDiscussionText -> expectedDiscussionText.contains(goDiscussion.getTitle()) &&
                        expectedDiscussionText.contains(goDiscussion.getUrl()))
                .findFirst().isPresent();
    }

    private boolean proteinComplexExists(GOTerm.ProteinComplex proteinComplex,
            Collection<String> expectedProteinComplexes) {
        return expectedProteinComplexes.stream()
                .filter(expectedDiscussionText -> expectedDiscussionText.contains(proteinComplex.db)
                        && expectedDiscussionText.contains(proteinComplex.id)
                        && expectedDiscussionText.contains(proteinComplex.symbol)
                        && expectedDiscussionText.contains(proteinComplex.name))
                .findFirst().isPresent();
    }

    @Test
    public void historyCategoryIsSlim() {
        when(term.getHistory()).thenReturn(termOntologyHistoryForSlim());

        OntologyDocument result = converter.apply(term);

        final List<String> history = result.history;
        assertThat(history, hasSize(1));
        assertThat(history.get(0), containsString("SLIM"));
    }

    @Test
    public void historyCategoryIsConstraint() {
        when(term.getHistory()).thenReturn(termOntologyHistoryForConstraint());

        OntologyDocument document = converter.apply(term);

        final List<String> history = document.history;
        assertThat(history, hasSize(1));
        assertThat(history.get(0), containsString("CONSTRAINT"));
    }

    private TermOntologyHistory termOntologyHistoryForSlim() {
        TermOntologyHistory termOntologyHistory = new TermOntologyHistory();
        String goId = "GO:0000003";
        String termName = "reproduction";
        String timeStamp = "2017-03-04";
        String action = "Added";
        String category = "SLIM";
        String text = "goslim_agr";
        AuditRecord auditRecord = new AuditRecord(goId, termName, timeStamp, action, category, text);
        termOntologyHistory.add(auditRecord);
        return termOntologyHistory;
    }

    private TermOntologyHistory termOntologyHistoryForConstraint() {
        TermOntologyHistory termOntologyHistory = new TermOntologyHistory();
        String goId = "GO:0000131";
        String termName = "incipient cellular bud site";
        String timeStamp = "2008-05-13";
        String action = "Added";
        String category = "CONSTRAINT";
        String text = "never_in_taxon NCBITaxon:4930 (Saccharomyces)";
        AuditRecord auditRecord = new AuditRecord(goId, termName, timeStamp, action, category, text);
        termOntologyHistory.add(auditRecord);
        return termOntologyHistory;
    }
}
