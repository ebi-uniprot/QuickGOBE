package uk.ac.ebi.quickgo.model.ontology.converter;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocMocker;
import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.model.ontology.OBOTerm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static uk.ac.ebi.quickgo.document.FlatFieldBuilder.newFlatField;
import static uk.ac.ebi.quickgo.document.FlatFieldLeaf.newFlatFieldLeaf;

/**
 * Created 24/11/15
 * @author Edd
 */
public class AbstractOntologyDocConverterTest {
    private GeneralOntologyDocConverter converter;

    // basic implementation that makes available protected methods which are to be tested
    private static class GeneralOntologyDocConverter extends AbstractOntologyDocConverter<OBOTerm> {
        @Override public OBOTerm convert(OntologyDocument ontologyDocument) {
            return null;
        }
    }

    @Before
    public void setup() {
        this.converter = new GeneralOntologyDocConverter();
    }

    /**
     * Check the flatted document synonyms are converted correctly to a
     * {@link uk.ac.ebi.quickgo.model.ontology.OBOTerm.Synonym}
     * DTO.
     */
    @Test
    public void convertingBadlyFlattenedSynonymsFailsWithoutError() {
        List<String> rawSynonyms = Arrays.asList("syn name 0-syn type 0", "syn name 1-syn type 1");
        List<OBOTerm.Synonym> synonyms = converter.retrieveSynonyms(rawSynonyms);
        assertThat(synonyms.size(), is(0));
    }

    /**
     * Check the flatted document synonyms are converted correctly to a
     * {@link uk.ac.ebi.quickgo.model.ontology.OBOTerm.Synonym}
     * DTO.
     */
    @Test
    public void converts0FlattenedSynonymsToSynonymsDTO() {
        List<String> rawSynonyms = Collections.emptyList();
        List<OBOTerm.Synonym> synonyms = converter.retrieveSynonyms(rawSynonyms);
        assertThat(synonyms.size(), is(0));
    }

    /**
     * Check the flatted document synonyms are converted correctly to a
     * {@link uk.ac.ebi.quickgo.model.ontology.OBOTerm.Synonym}
     * DTO.
     */
    @Test
    public void converts1FlattenedSynonymToSynonymsDTO() {
        List<String> rawSynonyms = Collections.singletonList(newFlatField()
                .addField(newFlatFieldLeaf("syn name 0"))
                .addField(newFlatFieldLeaf("syn type 0"))
                .buildString());
        List<OBOTerm.Synonym> synonyms = converter.retrieveSynonyms(rawSynonyms);
        assertThat(synonyms.size(), is(1));
        assertThat(synonyms.get(0).synonymName, is(equalTo("syn name 0")));
        assertThat(synonyms.get(0).synonymType, is(equalTo("syn type 0")));
    }

    /**
     * Check the flatted document synonyms are converted correctly to a
     * {@link uk.ac.ebi.quickgo.model.ontology.OBOTerm.Synonym}
     * DTO.
     */
    @Test
    public void converts2FlattenedSynonymsToSynonymsDTO() {
        List<String> rawSynonyms = Arrays.asList(
                newFlatField()
                        .addField(newFlatFieldLeaf("syn name 0"))
                        .addField(newFlatFieldLeaf("syn type 0"))
                        .buildString(),
                newFlatField()
                        .addField(newFlatFieldLeaf("syn name 1"))
                        .addField(newFlatFieldLeaf("syn type 1"))
                        .buildString()
        );
        List<OBOTerm.Synonym> synonyms = converter.retrieveSynonyms(rawSynonyms);
        assertThat(synonyms.size(), is(2));
        assertThat(synonyms.get(0).synonymName, is(equalTo("syn name 0")));
        assertThat(synonyms.get(0).synonymType, is(equalTo("syn type 0")));

        assertThat(synonyms.get(1).synonymName, is(equalTo("syn name 1")));
        assertThat(synonyms.get(1).synonymType, is(equalTo("syn type 1")));
    }

    /**
     * Check that all common OBO fields are converted
     */
    @Test
    public void convertsCommonFieldsWithoutError() {
        OntologyDocument goOntologyDoc = OntologyDocMocker.createGODoc("id1", "name1");
        OBOTerm oboTerm = new OBOTerm();
        converter.addCommonFields(goOntologyDoc, oboTerm);
        assertThat(oboTerm.id, is(equalTo("id1")));
        assertThat(oboTerm.name, is(equalTo("name1")));
        assertThat(oboTerm.ancestors, is(goOntologyDoc.ancestors));
        assertThat(oboTerm.children, is(goOntologyDoc.children));
        assertThat(oboTerm.comment, is(goOntologyDoc.comment));
        assertThat(oboTerm.definition, is(goOntologyDoc.definition));
        assertThat(oboTerm.replacedBy, is(goOntologyDoc.replacedBy));
        assertThat(oboTerm.isObsolete, is(goOntologyDoc.isObsolete));
        assertThat(oboTerm.subsets, is(goOntologyDoc.subsets));
        assertThat(oboTerm.synonyms.size(), is(equalTo(2)));
    }

    /**
     * Check that a partially populated document can be successfully converted in
     * to a corresponding OBOTerm
     */
    @Test
    public void documentWithNullFieldsCanBeConverted() {
        OntologyDocument doc = new OntologyDocument();
        doc.id = "id field";
        doc.ancestors = Arrays.asList("ancestor 0", "ancestor 1");
        OBOTerm term = new OBOTerm();
        converter.addCommonFields(doc, term);
        assertThat(term.id, is("id field"));
        assertThat(term.ancestors, containsInAnyOrder("ancestor 0", "ancestor 1"));
        assertThat(term.name, is(nullValue()));
    }

    @Test
    public void convertsHistory() {
        List<String> rawHistory = new ArrayList<>();
        rawHistory.add(
                newFlatField()
                        .addField(newFlatFieldLeaf("Gonna do something like it's ..."))
                        .addField(newFlatFieldLeaf("11:59, 31 Dec, 1999"))
                        .addField(newFlatFieldLeaf("PARTY!"))
                        .addField(newFlatFieldLeaf("Must be done"))
                        .addField(newFlatFieldLeaf("Textual description"))
                        .buildString()
        );
        rawHistory.add(
                newFlatField()
                        .addField(newFlatFieldLeaf("History name"))
                        .addField(newFlatFieldLeaf("Tuesday next week"))
                        .addField(newFlatFieldLeaf("PARTY!"))
                        .addField(newFlatFieldLeaf("Must be done"))
                        .addField(newFlatFieldLeaf("Okay"))
                        .buildString()
        );

        List<OBOTerm.History> history = converter.retrieveHistory(rawHistory);
        assertThat(history.size(), is(2));
        assertThat(history.get(0).name, is("Gonna do something like it's ..."));
        assertThat(history.get(1).text, is("Okay"));
    }

    @Test
    public void convertsXrefs() {
        String dbCode0 = "db code 0";
        String dbId0 = "db id 0";
        String dbName0 = "db name 0";

        String dbCode1 = "db code 1";
        String dbId1 = "db id 1";
        String dbName1 = "db name 1";

        List<String> rawXrefs = new ArrayList<>();
        rawXrefs.add(
                newFlatField()
                        .addField(newFlatFieldLeaf(dbCode0))
                        .addField(newFlatFieldLeaf(dbId0))
                        .addField(newFlatFieldLeaf(dbName0))
                        .buildString()
        );
        rawXrefs.add(
                newFlatField()
                        .addField(newFlatFieldLeaf(dbCode1))
                        .addField(newFlatFieldLeaf(dbId1))
                        .addField(newFlatFieldLeaf(dbName1))
                        .buildString()
        );

        List<OBOTerm.XRef> xRefs = converter.retrieveXRefs(rawXrefs);
        assertThat(xRefs.size(), is(2));
        assertThat(xRefs.get(0).dbCode, is(dbCode0));
        assertThat(xRefs.get(0).name, is(dbName0));
        assertThat(xRefs.get(1).dbCode, is(dbCode1));
        assertThat(xRefs.get(1).name, is(dbName1));
    }

    @Test
    public void convertsTaxonConstraints() {
        List<String> rawTaxonConstraints = new ArrayList<>();

        String ancestorId = "GO:0005623";
        String taxIdType = "NCBITaxon";
        String taxId = "131568";
        String citationId = "PMID:00000003";

        rawTaxonConstraints.add(newFlatField()
                .addField(newFlatFieldLeaf(ancestorId))
                .addField(newFlatFieldLeaf("cell"))
                .addField(newFlatFieldLeaf("only_in_taxon"))
                .addField(newFlatFieldLeaf("131567"))
                .addField(newFlatFieldLeaf(taxIdType))
                .addField(newFlatFieldLeaf("cellular organisms"))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf("PMID:00000001"))
                        .addField(newFlatFieldLeaf("PMID:00000002")))
                .buildString());
        rawTaxonConstraints.add(newFlatField()
                .addField(newFlatFieldLeaf("GO:0005624"))
                .addField(newFlatFieldLeaf("cell"))
                .addField(newFlatFieldLeaf("only_in_taxon"))
                .addField(newFlatFieldLeaf(taxId))
                .addField(newFlatFieldLeaf(taxIdType))
                .addField(newFlatFieldLeaf("cellular organisms"))
                .addField(newFlatField()
                        .addField(newFlatFieldLeaf(citationId))
                        .addField(newFlatFieldLeaf("PMID:00000004")))
                .buildString());

        List<OBOTerm.TaxonConstraint> taxonConstraints = converter.retrieveTaxonConstraints(rawTaxonConstraints);
        assertThat(taxonConstraints.size(), is(2));
        assertThat(taxonConstraints.get(0).ancestorId, is(ancestorId));
        assertThat(taxonConstraints.get(0).taxIdType, is(taxIdType));
        assertThat(taxonConstraints.get(1).taxId, is(taxId));
        assertThat(taxonConstraints.get(1).citations.get(0).id, is(citationId));
    }

    @Test
    public void convertsBlacklist() {
        List<String> rawBlacklist = new ArrayList<>();

        String gp0 = "GP:00000";
        rawBlacklist.add(newFlatField()
                .addField(newFlatFieldLeaf(gp0))
                .addField(newFlatFieldLeaf("GP"))
                .addField(newFlatFieldLeaf("because it's bad"))
                .addField(newFlatFieldLeaf("category 0"))
                .addField(newFlatFieldLeaf("automatic"))
                .buildString());
        String cat1 = "category 1";
        rawBlacklist.add(newFlatField()
                .addField(newFlatFieldLeaf("XX:00001"))
                .addField(newFlatFieldLeaf("XX"))
                .addField(newFlatFieldLeaf("because it's also bad"))
                .addField(newFlatFieldLeaf(cat1))
                .addField(newFlatFieldLeaf()) // no parameter means it's got no value
                .buildString());

        List<OBOTerm.BlacklistItem> blacklistItems = converter.retrieveBlackListedItems(rawBlacklist);
        assertThat(blacklistItems.size(), is(2));
        assertThat(blacklistItems.get(0).geneProductId, is(gp0));
        assertThat(blacklistItems.get(1).category, is(cat1));
        assertThat(blacklistItems.get(1).method, is(nullValue()));
    }

    @Test
    public void convertsXOntologyRelations() {
        String id0 = "id0";
        String term0 = "term0";
        String namespace0 = "ns0";
        String url0 = "url0";
        String relation0 = "http://www.ebi.ac.uk/chebi/searchId.do?chebiId=CHEBI:16919";

        String id1 = "id1";
        String term1 = "term1";
        String namespace1 = "ns1";
        String url1 = "url1";
        String relation1 = "http://www.ebi.ac.uk/chebi/searchId.do?chebiId=CHEBI:16912";

        List<String> rawXORels = new ArrayList<>();
        rawXORels.add(
                newFlatField()
                        .addField(newFlatFieldLeaf(id0))
                        .addField(newFlatFieldLeaf(term0))
                        .addField(newFlatFieldLeaf(namespace0))
                        .addField(newFlatFieldLeaf(url0))
                        .addField(newFlatFieldLeaf(relation0))
                        .buildString()
        );
        rawXORels.add(
                newFlatField()
                        .addField(newFlatFieldLeaf(id1))
                        .addField(newFlatFieldLeaf(term1))
                        .addField(newFlatFieldLeaf(namespace1))
                        .addField(newFlatFieldLeaf(url1))
                        .addField(newFlatFieldLeaf(relation1))
                        .buildString()
        );

        List<OBOTerm.XORelation> xORefs = converter.retrieveXOntologyRelations(rawXORels);
        assertThat(xORefs.size(), is(2));
        assertThat(xORefs.get(0).id, is(id0));
        assertThat(xORefs.get(0).url, is(url0));
        assertThat(xORefs.get(1).namespace, is(namespace1));
        assertThat(xORefs.get(1).url, is(url1));
    }

}