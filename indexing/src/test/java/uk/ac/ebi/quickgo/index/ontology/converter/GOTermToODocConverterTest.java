package uk.ac.ebi.quickgo.index.ontology.converter;
import org.mockito.junit.jupiter.MockitoExtension;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.ac.ebi.quickgo.index.ontology.converter.GenericTermToODocConverterTest.extractFieldFromDocument;

/**
 * Created 14/12/15
 * @author Edd
 */
@ExtendWith(MockitoExtension.class)
class GOTermToODocConverterTest {
    private static final String TERM_ID = "id1";

    @Mock
    public GOTerm term;

    private final GOTermToODocConverter converter = new GOTermToODocConverter();

    @BeforeEach
    void setup() {
        when(term.getId()).thenReturn(TERM_ID);
    }

    // annotation guidelines
    @Test
    void extractsAnnGuideLinesWhenExists() {
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
    void extractsAnnGuideLinesWhenNotExists() {
        when(term.getGuidelines()).thenReturn(null);

        OntologyDocument docConverted = converter.apply(term);
        List<String> extractedAnnGuidelines =
                extractFieldFromDocument(docConverted, (OntologyDocument doc) -> doc.annotationGuidelines);

        assertThat(extractedAnnGuidelines, is(nullValue()));
    }

    // taxon constraints
    @Test
    void extractsTaxonConstraintsWhenExists() {
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
    void extractsTaxonConstraintsWhenNotExists() {
        when(term.getTaxonConstraints()).thenReturn(null);

        OntologyDocument docConverted = converter.apply(term);
        List<String> extractedTaxonConstraints = extractFieldFromDocument(docConverted,
                (OntologyDocument doc) -> doc.taxonConstraints);

        assertThat(extractedTaxonConstraints, is(nullValue()));
    }

    // simple fields
    @Test
    void convertsSimpleFieldsWhenExists() {
        when(term.getUsage()).thenReturn(GOTerm.ETermUsage.E);
        when(term.getAspect()).thenReturn(GOTerm.EGOAspect.C);

        OntologyDocument result = converter.apply(term);
        OntologyDocument document = result;
        assertThat(document.aspect, is(GOTerm.EGOAspect.C.text));
        assertThat(document.usage, is(GOTerm.ETermUsage.E.text));
    }

    @Test
    void convertsSimpleFieldsWhenNotExists() {
        when(term.getUsage()).thenReturn(null);
        when(term.getAspect()).thenReturn(null);

        OntologyDocument result = converter.apply(term);
        OntologyDocument document = result;
        assertThat(document.usage, is(nullValue()));
        assertThat(document.aspect, is(nullValue()));
    }

    // blacklist
    @Test
    void extractsBlacklistWhenExists() {
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
    void extractsBlacklistWhenNotExists() {
        when(term.getBlacklist()).thenReturn(null);

        OntologyDocument docConverted = converter.apply(term);
        List<String> extractedBlacklist =
                extractFieldFromDocument(docConverted, (OntologyDocument doc) -> doc.blacklist);

        assertThat(extractedBlacklist, is(nullValue()));
    }

    //GO discussions
    @Test
    void extractingGoDiscussionsFromEmptyPlannedChangesListReturnsNull() {
        when(term.getPlannedChanges()).thenReturn(null);

        OntologyDocument docConverted = converter.apply(term);
        List<String> extractedGoDiscussions =
                extractFieldFromDocument(docConverted, (OntologyDocument doc) -> doc.goDiscussions);

        assertThat(extractedGoDiscussions, is(nullValue()));
    }

    @Test
    void extracts1GoDiscussionsFrom1ElementPlannedChangesList() {
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
    void extracts2GoDiscussionsFrom2ElementPlannedChangesList() {
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

    private boolean goDiscussionExists(GOTerm.NamedURL goDiscussion, Collection<String> expectedGoDiscussions) {
        return expectedGoDiscussions.stream()
                .filter(expectedDiscussionText -> expectedDiscussionText.contains(goDiscussion.getTitle()) &&
                        expectedDiscussionText.contains(goDiscussion.getUrl()))
                .findFirst().isPresent();
    }

    @Test
    void historyCategoryIsSlim() {
        when(term.getHistory()).thenReturn(termOntologyHistoryForSlim());

        OntologyDocument result = converter.apply(term);

        final List<String> history = result.history;
        assertThat(history, hasSize(1));
        assertThat(history.get(0), containsString("SLIM"));
    }

    @Test
    void historyCategoryIsConstraint() {
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
