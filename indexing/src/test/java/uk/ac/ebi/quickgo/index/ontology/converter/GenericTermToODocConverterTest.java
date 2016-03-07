package uk.ac.ebi.quickgo.index.ontology.converter;

import uk.ac.ebi.quickgo.model.ontology.generic.*;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;

import java.util.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by edd on 22/12/2015.
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

    // ancestors
    @Test
    public void extractsAncestorsWhenPresent() {
        GenericTerm parentTermMock = mock(GenericTerm.class);
        when(parentTermMock.getId()).thenReturn("parent1");

        TermRelation self = new TermRelation(term, term, RelationType.IDENTITY);
        TermRelation parent1Rel = new TermRelation(term, parentTermMock, RelationType.ISA);
        List<TermRelation> ancestors = Arrays.asList(self, parent1Rel);

        when(term.getAncestors()).thenReturn(ancestors);

        List<String> ancestorStrList = converter.extractAncestors(term);
        assertThat(ancestorStrList, is(not(nullValue())));
        assertThat(ancestorStrList.size(), is(2));
        assertThat(ancestorStrList.get(1).contains("parent1"), is(true));
        System.out.printf(ancestorStrList.get(1));
    }

    @Test
    public void extractsAncestorsWhenOnlySelfRelationExists() {
        TermRelation self = new TermRelation(term, term, RelationType.IDENTITY);
        List<TermRelation> ancestors = Collections.singletonList(self);

        when(term.getAncestors()).thenReturn(ancestors);

        List<String> ancestorStrList = converter.extractAncestors(term);
        assertThat(ancestorStrList, is(not(nullValue())));
        assertThat(ancestorStrList.size(), is(1));
        assertThat(ancestorStrList.get(0).contains("id1"), is(true));
        System.out.printf(ancestorStrList.get(0));
    }

    @Test
    public void extractsNoAncestorsWhenNull() {
        when(term.getAncestors()).thenReturn(null);

        List<String> ancestorStrList = converter.extractAncestors(term);
        assertThat(ancestorStrList, is(nullValue()));
    }

    // considers
    @Test
    public void extractsNoConsideredWhenNull() {
        when(term.consider()).thenReturn(null);

        assertThat(converter.extractConsidersAsList(term), is(nullValue()));
    }

    @Test
    public void extractsConsidersWhenExist() {
        GenericTerm mockConsider = mock(GenericTerm.class);
        String considerId = "id2";
        when(mockConsider.getId()).thenReturn(considerId);
        ArrayList<GenericTerm> toConsider = new ArrayList<>();
        toConsider.add(mockConsider);

        when(term.consider()).thenReturn(toConsider);
        List<String> considersStrList = converter.extractConsidersAsList(term);
        assertThat(considersStrList, is(not(nullValue())));
        assertThat(considersStrList.size(), is(1));
        assertThat(considersStrList.get(0), is(considerId));
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
}