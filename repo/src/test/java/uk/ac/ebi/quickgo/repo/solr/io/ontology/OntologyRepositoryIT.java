package uk.ac.ebi.quickgo.repo.solr.io.ontology;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.quickgo.repo.solr.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.repo.solr.config.RepoConfig;
import uk.ac.ebi.quickgo.repo.solr.document.geneproduct.GeneProductDocument;
import uk.ac.ebi.quickgo.repo.solr.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.repo.solr.document.ontology.OntologyType;
import uk.ac.ebi.quickgo.repo.solr.io.geneproduct.GeneProductRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertTrue;
import static uk.ac.ebi.quickgo.repo.solr.document.ontology.OntologyDocMocker.createGODoc;

/**
 * Test that the ontology repository can be accessed as expected.
 * <p>
 * Created 11/11/15
 *
 * @author Edd
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RepoConfig.class, loader = SpringApplicationContextLoader.class)
public class OntologyRepositoryIT {

    // temporary data store for solr's data, which is automatically cleaned on exit
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    @Autowired
    private OntologyRepository ontologyRepository;

    @Autowired
    private GeneProductRepository geneProductRepository;

    @Autowired
    private SolrTemplate ontologyTemplate;

    @Autowired
    private SolrTemplate geneProductTemplate;

    @Before
    public void before() {
        ontologyRepository.deleteAll();
    }

    @Test
    public void add1DocumentThenFind1Documents() throws IOException, SolrServerException {
        ontologyRepository.save(createGODoc("A", "Alice Cooper"));

        assertThat(ontologyRepository.findAll(new PageRequest(0, 10)).getTotalElements(), is(1L));
    }

    @Test
    public void add3DocumentsThenFind3Documents() {
        ontologyRepository.save(createGODoc("A", "Alice Cooper"));
        ontologyRepository.save(createGODoc("B", "Bob The Builder"));
        ontologyRepository.save(createGODoc("C", "Clint Eastwood"));

        assertThat(ontologyRepository.findAll(new PageRequest(0, 10)).getTotalElements(), is(3L));
    }

    @Test
    public void retrievesCoreFieldsOnly() {
        String id = "GO:0000001";
        ontologyRepository.save(createGODoc(id, "GO name 1"));

        Optional<OntologyDocument> optionalDoc =
                ontologyRepository.findCoreByTermId(OntologyType.GO.name(), ClientUtils.escapeQueryChars(id));
        assertThat(optionalDoc.isPresent(), is(true));
        OntologyDocument ontologyDocument = optionalDoc.get();
        assertThat(ontologyDocument.name, is(notNullValue()));
        assertThat(ontologyDocument.considers, is(nullValue())); // not a core field
        assertThat(ontologyDocument.history, is(nullValue())); // not a core field
    }

    @Test
    public void retrievesAllFields() {
        String id = "GO:0000001";
        ontologyRepository.save(createGODoc(id, "GO name 1"));

        Optional<OntologyDocument> optionalDoc =
                ontologyRepository.findCompleteByTermId(OntologyType.GO.name(), ClientUtils.escapeQueryChars(id));
        assertThat(optionalDoc.isPresent(), is(true));
        OntologyDocument ontologyDocument = optionalDoc.get();
        assertThat(ontologyDocument.name, is(notNullValue()));
        assertThat(ontologyDocument.considers, is(notNullValue()));
    }

    @Test
    public void retrievesHistoryField() {
        String id = "GO:0000001";
        ontologyRepository.save(createGODoc(id, "GO name 1"));

        Optional<OntologyDocument> optionalDoc =
                ontologyRepository.findHistoryByTermId(OntologyType.GO.name(), ClientUtils.escapeQueryChars(id));
        assertThat(optionalDoc.isPresent(), is(true));
        assertThat(optionalDoc.get().history, is(notNullValue()));
    }

    @Test
    public void retrievesXrefsField() {
        String id = "GO:0000001";
        ontologyRepository.save(createGODoc(id, "GO name 1"));

        Optional<OntologyDocument> optionalDoc =
                ontologyRepository.findXRefsByTermId(OntologyType.GO.name(), ClientUtils.escapeQueryChars(id));
        assertThat(optionalDoc.isPresent(), is(true));
        assertThat(optionalDoc.get().xrefs, is(notNullValue()));
    }

    @Test
    public void retrievesAnnotationGuidelinesField() {
        String id = "GO:0000001";
        ontologyRepository.save(createGODoc(id, "GO name 1"));

        Optional<OntologyDocument> optionalDoc =
                ontologyRepository.findAnnotationGuidelinesByTermId(OntologyType.GO.name(), ClientUtils.escapeQueryChars(id));
        assertThat(optionalDoc.isPresent(), is(true));
        assertThat(optionalDoc.get().annotationGuidelines, is(notNullValue()));
    }

    @Test
    public void retrievesTaxonConstraintsField() {
        String id = "GO:0000001";
        ontologyRepository.save(createGODoc(id, "GO name 1"));

        Optional<OntologyDocument> optionalDoc =
                ontologyRepository.findTaxonConstraintsByTermId(OntologyType.GO.name(), ClientUtils.escapeQueryChars(id));
        assertThat(optionalDoc.isPresent(), is(true));
        assertThat(optionalDoc.get().taxonConstraints, is(notNullValue()));
        assertThat(optionalDoc.get().blacklist, is(notNullValue()));
        assertTrue(optionalDoc.get().blacklist.get(0).contains("IER12345"));
        assertTrue(optionalDoc.get().blacklist.get(1).contains("IER12346"));
    }

    @Test
    public void retrievesXOntologyRelationsField() {
        String id = "GO:0000001";
        ontologyRepository.save(createGODoc(id, "GO name 1"));

        Optional<OntologyDocument> optionalDoc =
                ontologyRepository.findXOntologyRelationsByTermId(OntologyType.GO.name(), ClientUtils.escapeQueryChars(id));
        assertThat(optionalDoc.isPresent(), is(true));
        assertThat(optionalDoc.get().xRelations, is(notNullValue()));
    }

    @Test
    public void add1DocumentAndFailToFindForWrongId() {
        ontologyRepository.save(createGODoc("A", "Alice Cooper"));

        assertThat(ontologyRepository.findCoreByTermId(OntologyType.GO.name(), "B").isPresent(), is(false));
        assertThat(ontologyRepository.findCompleteByTermId(OntologyType.GO.name(), "B").isPresent(), is(false));
    }

    @Test
    public void add1GoDocumentAndFindItById() {
        ontologyRepository.save(createGODoc("A", "Alice Cooper"));

        assertThat(ontologyRepository.findCoreByTermId(OntologyType.GO.name(), "A").isPresent(), is(true));
        assertThat(ontologyRepository.findCompleteByTermId(OntologyType.GO.name(), "A").isPresent(), is(true));
        assertThat(ontologyRepository.findHistoryByTermId(OntologyType.GO.name(), "A").isPresent(), is(true));
    }

    /**
     * Shows how to save directly to a solr server, bypassing transactional
     * operations that are managed by Spring.
     *
     * @throws IOException
     * @throws SolrServerException
     */
    @Test
    public void saveDirectlyToSolrServer() throws IOException, SolrServerException {
        ontologyTemplate.getSolrServer().addBean(createGODoc("A", "Alice Cooper"));
        ontologyTemplate.getSolrServer().addBean(createGODoc("B", "Alice Cooper"));
        ontologyTemplate.getSolrServer().addBean(createGODoc("C", "Alice Cooper"));
        ontologyTemplate.getSolrServer().addBeans(
                Arrays.asList(
                        createGODoc("D", "Alice Cooper"),
                        createGODoc("E", "Alice Cooper")));

        assertThat(ontologyRepository.findAll(new PageRequest(0, 10)).getTotalElements(), is(0L));

        ontologyTemplate.getSolrServer().commit();

        assertThat(ontologyRepository.findAll(new PageRequest(0, 10)).getTotalElements(), is(5L));
    }
}