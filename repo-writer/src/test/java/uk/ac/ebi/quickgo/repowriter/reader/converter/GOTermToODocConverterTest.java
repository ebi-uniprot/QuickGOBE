package uk.ac.ebi.quickgo.repowriter.reader.converter;

import uk.ac.ebi.quickgo.ff.files.ontology.GOSourceFiles;
import uk.ac.ebi.quickgo.ff.loader.ontology.GOLoader;
import uk.ac.ebi.quickgo.model.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.model.ontology.generic.RelationType;
import uk.ac.ebi.quickgo.model.ontology.generic.TermRelation;
import uk.ac.ebi.quickgo.model.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.model.ontology.go.GOTermBlacklist;
import uk.ac.ebi.quickgo.model.ontology.go.GeneOntology;
import uk.ac.ebi.quickgo.model.ontology.go.TaxonConstraint;
import uk.ac.ebi.quickgo.repo.solr.document.ontology.OntologyDocument;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created 14/12/15
 * @author Edd
 */
@RunWith(MockitoJUnitRunner.class)
public class GOTermToODocConverterTest {

    @Ignore
    @Test
    public void converts1Term() {
        //GOSourceFiles sourceFiles = new GOSourceFiles(new File("/home/eddturner/working/quickgo-local/quickgo-data/ff"));
        GOSourceFiles sourceFiles = new GOSourceFiles(new File("C:\\Users\\twardell\\Projects\\QuickGo\\data_ontology"));

        GOLoader goLoader = new GOLoader(sourceFiles);
        Optional<GeneOntology> geneOntologyOptional = goLoader.load();
        assertThat(geneOntologyOptional.isPresent(), is(true));

        GeneOntology geneOntology = geneOntologyOptional.get();
        GOTermToODocConverter docConverter = new GOTermToODocConverter();

        System.out.println(docConverter.apply(Optional.of((GOTerm) geneOntology.getTerm("GO:0000003"))));
    }

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

        List<String> xrefStrList = converter.extractAnnGuidelines(term);
        assertThat(xrefStrList, is(not(nullValue())));
        assertThat(xrefStrList.size(), is(1));
        assertThat(xrefStrList.get(0).contains("title"), is(true));
        assertThat(xrefStrList.get(0).contains("url"), is(true));
    }

    @Test
    public void extractsAnnGuideLinesWhenNotExists() {
        when(term.getGuidelines()).thenReturn(null);
        assertThat(converter.extractAnnGuidelines(term), is(nullValue()));
    }

    // children
    @Test
    public void extractsChildrenWhenExists() {
        GenericTerm childTermMock = mock(GenericTerm.class);
        when(childTermMock.getId()).thenReturn("child1");

        TermRelation childRel = new TermRelation(childTermMock, term, RelationType.ISA);
        List<TermRelation> children = Collections.singletonList(childRel);

        when(term.getChildren()).thenReturn(children);

        List<String> childrenStrList = converter.extractChildren(term);
        assertThat(childrenStrList, is(not(nullValue())));
        assertThat(childrenStrList.size(), is(1));
        assertThat(childrenStrList.get(0).contains("child1"), is(true));
        System.out.println(childrenStrList.get(0));
    }

    @Test
    public void extractsChildrenWhenNotExists() {
        when(term.getChildren()).thenReturn(null);
        assertThat(converter.extractChildren(term), is(nullValue()));
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

        List<String> taxConsStrList = converter.extractTaxonConstraints(term);
        assertThat(taxConsStrList, is(not(nullValue())));
        assertThat(taxConsStrList.size(), is(1));
        assertThat(taxConsStrList.get(0).contains("goId1"), is(true));
        assertThat(taxConsStrList.get(0).contains("name1"), is(true));
        assertThat(taxConsStrList.get(0).contains("rel1"), is(true));
        assertThat(taxConsStrList.get(0).contains("taxId1"), is(true));
        assertThat(taxConsStrList.get(0).contains("taxIdType1"), is(true));
        assertThat(taxConsStrList.get(0).contains("taxName1"), is(true));
        assertThat(taxConsStrList.get(0).contains("taxName1"), is(true));
        assertThat(taxConsStrList.get(0).contains("pubmed1"), is(true));
        assertThat(taxConsStrList.get(0).contains("pubmed2"), is(true));
    }

    @Test
    public void extractsTaxonConstraintsWhenNotExists() {
        when(term.getTaxonConstraints()).thenReturn(null);
        assertThat(converter.extractTaxonConstraints(term), is(nullValue()));
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
        when(goTermBlacklist.getReason()).thenReturn("1 NOT-qualified manual annotation exists with evidence code ECO:0000318 from this reference: GO_REF:0000033");
        when(goTermBlacklist.getMethodId()).thenReturn("IPR1234567");

        when(term.getBlacklist()).thenReturn(Collections.singletonList(goTermBlacklist));

        List<String> blacklistConStrList = converter.extractBlacklist(term);
        assertThat(blacklistConStrList, is(not(nullValue())));
        assertThat(blacklistConStrList.size(), is(1));
        assertThat(blacklistConStrList.get(0).contains("GO:0007005"), is(true));
        assertThat(blacklistConStrList.get(0).contains("GO:0000001"), is(true));
        assertThat(blacklistConStrList.get(0).contains("NOT-qualified manual"), is(true));
        assertThat(blacklistConStrList.get(0).contains("A5I1R9_CLOBH"), is(true));
        assertThat(blacklistConStrList.get(0).contains("protein"), is(true));
        assertThat(blacklistConStrList.get(0).contains("441771"), is(true));
        assertThat(blacklistConStrList.get(0).contains("A5I1R9"), is(true));
        assertThat(blacklistConStrList.get(0).contains("1 NOT-qualified manual annotation exists with evidence code ECO:0000318 from this reference: GO_REF:0000033"), is(true));
        assertThat(blacklistConStrList.get(0).contains("IPR1234567"), is(true));

    }

    @Test
    public void extractsBlacklistWhenNotExists() {
        when(term.getBlacklist()).thenReturn(null);
        assertThat(converter.extractTaxonConstraints(term), is(nullValue()));
    }
}
