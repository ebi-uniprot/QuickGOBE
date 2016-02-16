package uk.ac.ebi.quickgo.ontology.common;

import uk.ac.ebi.quickgo.common.solr.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocMocker;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyDocument;
import uk.ac.ebi.quickgo.ontology.common.document.OntologyType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.solr.client.solrj.SolrServerException;
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

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertTrue;

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
    private SolrTemplate ontologyTemplate;

    @Before
    public void before() {
        ontologyRepository.deleteAll();
    }

    @Test
    public void add1DocumentThenFind1Documents() throws IOException, SolrServerException {
        ontologyRepository.save(OntologyDocMocker.createGODoc("A", "Alice Cooper"));

        assertThat(ontologyRepository.findAll(new PageRequest(0, 10)).getTotalElements(), is(1L));
    }

    @Test
    public void add3DocumentsThenFind3Documents() {
        ontologyRepository.save(OntologyDocMocker.createGODoc("A", "Alice Cooper"));
        ontologyRepository.save(OntologyDocMocker.createGODoc("B", "Bob The Builder"));
        ontologyRepository.save(OntologyDocMocker.createGODoc("C", "Clint Eastwood"));

        assertThat(ontologyRepository.findAll(new PageRequest(0, 10)).getTotalElements(), is(3L));
    }

    @Test
    public void retrieves1DocCoreFieldsOnly() {
        String id = "GO:0000001";
        ontologyRepository.save(OntologyDocMocker.createGODoc(id, "GO name 1"));

        List<OntologyDocument> optionalDoc =
                ontologyRepository.findCoreByTermId(OntologyType.GO.name(), buildIdList(id));
        assertThat(optionalDoc.size(), is(1));

        OntologyDocument ontologyDocument = optionalDoc.get(0);
        assertThat(ontologyDocument.name, is(notNullValue()));
        assertThat(ontologyDocument.considers, is(nullValue()));    // not a core field
        assertThat(ontologyDocument.history, is(nullValue()));      // not a core field
    }

    @Test
    public void retrieves2DocsCoreFieldsOnly() {
        String id1 = "GO:0000001";
        String id2 = "GO:0000002";
        ontologyRepository.save(OntologyDocMocker.createGODoc(id1, "GO name 1"));
        ontologyRepository.save(OntologyDocMocker.createGODoc(id2, "GO name 2"));
        ontologyRepository.save(OntologyDocMocker.createGODoc("GO:0000003", "GO name 3"));

        List<OntologyDocument> results =
                ontologyRepository.findCoreByTermId(OntologyType.GO.name(), buildIdList(id1, id2));
        assertThat(results.size(), is(2));

        results.forEach(ontologyDocument -> {
            assertThat(ontologyDocument.name, is(notNullValue()));
            assertThat(ontologyDocument.considers, is(nullValue()));    // not a core field
            assertThat(ontologyDocument.history, is(nullValue()));      // not a core field
        });
    }

    private List<String> buildIdList(String... ids) {
        if (ids.length == 1) {
            return singletonList(ClientUtils.escapeQueryChars(ids[0]));
        } else {
            List<String> escapedList = new ArrayList<>();
            for (String id : ids) {
                escapedList.add(ClientUtils.escapeQueryChars(id));
            }
            return escapedList;
        }
    }

    @Test
    public void retrievesAllFields() {
        String id = "GO:0000001";
        ontologyRepository.save(OntologyDocMocker.createGODoc(id, "GO name 1"));

        List<OntologyDocument> resultList =
                ontologyRepository.findCompleteByTermId(OntologyType.GO.name(), buildIdList(id));

        assertThat(resultList, is(notNullValue()));
        assertThat(resultList.size(), is(1));

        OntologyDocument ontologyDocument = resultList.get(0);
        assertThat(ontologyDocument.name, is(notNullValue()));
        assertThat(ontologyDocument.considers, is(notNullValue()));
    }

    @Test
    public void retrievesHistoryField() {
        String id = "GO:0000001";
        ontologyRepository.save(OntologyDocMocker.createGODoc(id, "GO name 1"));

        List<OntologyDocument> resultList =
                ontologyRepository.findHistoryByTermId(OntologyType.GO.name(), buildIdList(id));

        assertThat(resultList, is(notNullValue()));
        assertThat(resultList.size(), is(1));
        assertThat(resultList.get(0).history, is(notNullValue()));
    }

    @Test
    public void retrievesXrefsField() {
        String id = "GO:0000001";
        ontologyRepository.save(OntologyDocMocker.createGODoc(id, "GO name 1"));

        List<OntologyDocument> resultList =
                ontologyRepository.findXRefsByTermId(OntologyType.GO.name(), buildIdList(id));

        assertThat(resultList, is(notNullValue()));
        assertThat(resultList.size(), is(1));
        assertThat(resultList.get(0).xrefs, is(notNullValue()));
    }

    @Test
    public void retrievesAnnotationGuidelinesField() {
        String id = "GO:0000001";
        ontologyRepository.save(OntologyDocMocker.createGODoc(id, "GO name 1"));

        List<OntologyDocument> resultList =
                ontologyRepository
                        .findAnnotationGuidelinesByTermId(OntologyType.GO.name(), buildIdList(id));

        assertThat(resultList, is(notNullValue()));
        assertThat(resultList.size(), is(1));
        assertThat(resultList.get(0).annotationGuidelines, is(notNullValue()));
    }

    @Test
    public void retrievesTaxonConstraintsField() {
        String id = "GO:0000001";
        ontologyRepository.save(OntologyDocMocker.createGODoc(id, "GO name 1"));

        List<OntologyDocument> resultList =
                ontologyRepository
                        .findTaxonConstraintsByTermId(OntologyType.GO.name(), buildIdList(id));

        assertThat(resultList, is(notNullValue()));
        assertThat(resultList.size(), is(1));
        assertThat(resultList.get(0).taxonConstraints, is(notNullValue()));
        assertThat(resultList.get(0).blacklist, is(notNullValue()));
        assertTrue(resultList.get(0).blacklist.get(0).contains("IER12345"));
        assertTrue(resultList.get(0).blacklist.get(1).contains("IER12346"));
    }

    @Test
    public void retrievesXOntologyRelationsField() {
        String id = "GO:0000001";
        ontologyRepository.save(OntologyDocMocker.createGODoc(id, "GO name 1"));

        List<OntologyDocument> resultList =
                ontologyRepository
                        .findXOntologyRelationsByTermId(OntologyType.GO.name(), buildIdList(id));

        assertThat(resultList, is(notNullValue()));
        assertThat(resultList.size(), is(1));
        assertThat(resultList.get(0).xRelations, is(notNullValue()));
    }

    @Test
    public void add1DocumentAndFailToFindForWrongId() {
        ontologyRepository.save(OntologyDocMocker.createGODoc("A", "Alice Cooper"));

        assertThat(ontologyRepository.findCoreByTermId(OntologyType.GO.name(), buildIdList("B")).size(), is(0));
        assertThat(ontologyRepository.findCompleteByTermId(OntologyType.GO.name(), buildIdList("B")).size(),
                is(0));
    }

    @Test
    public void add1GoDocumentAndFindItById() {
        ontologyRepository.save(OntologyDocMocker.createGODoc("A", "Alice Cooper"));

        assertThat(ontologyRepository.findCoreByTermId(OntologyType.GO.name(), buildIdList("A")).size(), is(1));
        assertThat(ontologyRepository.findCompleteByTermId(OntologyType.GO.name(), buildIdList("A")).size(), is(1));
        assertThat(ontologyRepository.findHistoryByTermId(OntologyType.GO.name(), buildIdList("A")).size(), is(1));
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
        ontologyTemplate.getSolrServer().addBean(OntologyDocMocker.createGODoc("A", "Alice Cooper"));
        ontologyTemplate.getSolrServer().addBean(OntologyDocMocker.createGODoc("B", "Alice Cooper"));
        ontologyTemplate.getSolrServer().addBean(OntologyDocMocker.createGODoc("C", "Alice Cooper"));
        ontologyTemplate.getSolrServer().addBeans(
                Arrays.asList(
                        OntologyDocMocker.createGODoc("D", "Alice Cooper"),
                        OntologyDocMocker.createGODoc("E", "Alice Cooper")));

        assertThat(ontologyRepository.findAll(new PageRequest(0, 10)).getTotalElements(), is(0L));

        ontologyTemplate.getSolrServer().commit();

        assertThat(ontologyRepository.findAll(new PageRequest(0, 10)).getTotalElements(), is(5L));
    }
}