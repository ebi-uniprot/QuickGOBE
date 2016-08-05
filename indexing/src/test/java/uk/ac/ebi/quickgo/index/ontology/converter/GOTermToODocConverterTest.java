package uk.ac.ebi.quickgo.index.ontology.converter;

import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.generic.RelationType;
import uk.ac.ebi.quickgo.model.ontology.generic.TermRelation;
import uk.ac.ebi.quickgo.model.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GOTermBlacklist;
import uk.ac.ebi.quickgo.model.ontology.go.TaxonConstraint;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;

import java.util.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
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

        Optional<OntologyDocument> docOpt = converter.apply(Optional.of(term));
        List<String> extractedAnnGuidelines =
                extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.annotationGuidelines);

        assertThat(extractedAnnGuidelines, hasSize(1));
        assertThat(extractedAnnGuidelines, hasItems(containsString("title"), containsString("url")));
    }

    @Test
    public void extractsAnnGuideLinesWhenNotExists() {
        when(term.getGuidelines()).thenReturn(null);

        Optional<OntologyDocument> docOpt = converter.apply(Optional.of(term));
        List<String> extractedAnnGuidelines =
                extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.annotationGuidelines);

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

        Optional<OntologyDocument> docOpt = converter.apply(Optional.of(term));
        List<String> extractedTaxonConstraints = extractFieldFromDocument(docOpt,
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

        Optional<OntologyDocument> docOpt = converter.apply(Optional.of(term));
        List<String> extractedTaxonConstraints = extractFieldFromDocument(docOpt,
                (OntologyDocument doc) -> doc.taxonConstraints);

        assertThat(extractedTaxonConstraints, is(nullValue()));
    }

    // simple fields
    @Test
    public void convertsSimpleFieldsWhenExists() {
        when(term.getUsage()).thenReturn(GOTerm.ETermUsage.E);
        when(term.getAspect()).thenReturn(GOTerm.EGOAspect.C);

        Optional<OntologyDocument> result = converter.apply(Optional.of(term));
        assertThat(result.isPresent(), is(true));
        OntologyDocument document = result.get();
        assertThat(document.aspect, is(GOTerm.EGOAspect.C.text));
        assertThat(document.usage, is(GOTerm.ETermUsage.E.text));
    }

    @Test
    public void convertsSimpleFieldsWhenNotExists() {
        when(term.getUsage()).thenReturn(null);
        when(term.getAspect()).thenReturn(null);

        Optional<OntologyDocument> result = converter.apply(Optional.of(term));
        assertThat(result.isPresent(), is(true));
        OntologyDocument document = result.get();
        assertThat(document.usage, is(nullValue()));
        assertThat(document.aspect, is(nullValue()));
    }

    // empty optional conversion
    @Test
    public void convertsEmptyOptional() {
        Optional<OntologyDocument> documentOptional = converter.apply(Optional.empty());
        assertThat(documentOptional.isPresent(), is(false));
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

        Optional<OntologyDocument> docOpt = converter.apply(Optional.of(term));
        List<String> extractedBlacklist = extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.blacklist);

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

        Optional<OntologyDocument> docOpt = converter.apply(Optional.of(term));
        List<String> extractedBlacklist = extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.blacklist);

        assertThat(extractedBlacklist, is(nullValue()));
    }

    @Test
    public void extractingGoDiscussionsFromEmptyPlannedChangesListReturnsNull() throws Exception {
        when(term.getPlannedChanges()).thenReturn(null);

        Optional<OntologyDocument> docOpt = converter.apply(Optional.of(term));
        List<String> extractedGoDiscussions =
                extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.goDiscussions);

        assertThat(extractedGoDiscussions, is(nullValue()));
    }

    @Test
    public void extracts1GoDiscussionsFrom1ElementPlannedChangesList() throws Exception {
        String title = "Viral Processes";
        String url = "http://wiki.geneontology.org/index.php/Virus_terms";

        GOTerm.NamedURL plannedChange = new GOTerm.NamedURL(title, url);

        when(term.getPlannedChanges()).thenReturn(Collections.singletonList(plannedChange));

        Optional<OntologyDocument> docOpt = converter.apply(Optional.of(term));
        List<String> extractedGoDiscussions =
                extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.goDiscussions);

        assertThat(extractedGoDiscussions, hasSize(1));

        assertThat(extractedGoDiscussions, hasItems(containsString(title), containsString(url)));
    }

    @Test
    public void extracts2GoDiscussionsFrom2ElementPlannedChangesList() throws Exception {
        String title1 = "Viral Processes";
        String url1 = "http://wiki.geneontology.org/index.php/Virus_terms";

        GOTerm.NamedURL plannedChange1 = new GOTerm.NamedURL(title1, url1);

        String title2 = "signalling";
        String url2 = "http://wiki.geneontology.org/index.php/Signaling";

        GOTerm.NamedURL plannedChange2 = new GOTerm.NamedURL(title2, url2);

        when(term.getPlannedChanges()).thenReturn(Arrays.asList(plannedChange1, plannedChange2));

        Optional<OntologyDocument> docOpt = converter.apply(Optional.of(term));
        List<String> extractedGoDiscussions =
                extractFieldFromDocument(docOpt, (OntologyDocument doc) -> doc.goDiscussions);
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
}