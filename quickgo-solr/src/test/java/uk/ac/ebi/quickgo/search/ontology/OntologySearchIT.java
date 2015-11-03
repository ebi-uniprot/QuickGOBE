package uk.ac.ebi.quickgo.search.ontology;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static uk.ac.ebi.quickgo.search.ontology.MockedSolrTerm.createRelation;

/**
 * This class is used to show which queries are necessary to successfully search the index, e.g.,
 * how to search for an ECO term.
 *
 * Changes made to the ontology core's schema.xml are instantly reflected by the tests
 * defined in this class.
 *
 * Created 02/11/15
 * @author Edd
 */
public class OntologySearchIT {
    private static final String TEST_INPUT_DIR = "src/test/resources/it/search/ontology";
    private static final String ECO_ROOT = "root";

    @ClassRule
    public static final OntologySearchEngine searchEngine = new OntologySearchEngine();

    @BeforeClass
    public static void populateIndexWithTestData() throws Exception {
//        SourceFiles sourceFiles = new SourceFiles(new File(TEST_INPUT_DIR));

//        populateIndexWithECODocuments(sourceFiles);
//        populateIndexWithGODocuments(sourceFiles);

//        populateWithTerm();
    }

    @After
    public void cleanIndex() {
        searchEngine.removeAllDocuments();
    }


//    private static void populateIndexWithECODocuments(SourceFiles sourceFiles) throws Exception {
//        EvidenceCodeOntology evidenceCodeOntology = new EvidenceCodeOntology();
//        evidenceCodeOntology.load(sourceFiles.ecoSourceFiles, ECO_ROOT);
//        // index all loaded evidence code terms
//        ECOTermToSolrMapper ecoTermToSolrMapper = new ECOTermToSolrMapper();
//        evidenceCodeOntology.terms.values().stream()
//                .map(ecoTermToSolrMapper::toSolrObject)
//                .flatMap(Collection::stream)
//                .forEach(searchEngine::indexDocument);
//    }
//
//    private static void populateIndexWithGODocuments(SourceFiles sourceFiles) throws Exception {
//        GeneOntology geneOntology = new GeneOntology();
//        geneOntology.load(sourceFiles.goSourceFiles);
//        // index all loaded gene ontology terms
//        GOTermToSolrMapper goTermToSolrMapper = new GOTermToSolrMapper();
//        geneOntology.terms.values().stream()
//                .map(goTermToSolrMapper::toSolrObject)
//                .flatMap(Collection::stream)
//                .forEach(searchEngine::indexDocument);
//    }

    @Test
    public void shouldFindSomething() {
        searchEngine.indexDocument(createRelation());
        searchEngine.printIndexContents();
        QueryResponse queryResponse = searchEngine.getQueryResponse("*:*");

        System.out.println(queryResponse);
    }

    @Test
    public void shouldFindECO_0000002() {
        searchEngine.indexDocument(createRelation());

        QueryResponse queryResponse = searchEngine.getQueryResponse("id:ECO\\:0000002");
        assertThat(queryResponse.getResults().size(), is(greaterThan(0)));
    }
}
