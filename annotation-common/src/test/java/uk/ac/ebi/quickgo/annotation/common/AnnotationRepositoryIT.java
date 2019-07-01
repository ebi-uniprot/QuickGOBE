package uk.ac.ebi.quickgo.annotation.common;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.ac.ebi.quickgo.annotation.common.document.AnnotationDocMocker.createAnnotationDoc;

/**
 * Test that the annotation repository can be accessed as expected.
 *
 * Created 14/04/16
 * @author Edd
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AnnotationRepoConfig.class)
public class AnnotationRepositoryIT {

    // temporary data store for solr's data, which is automatically cleaned on exit
    @ClassRule
    public static final TemporarySolrDataStore solrDataStore = new TemporarySolrDataStore();

    @Autowired
    private AnnotationRepository annotationRepository;

    @Before
    public void before() {
        annotationRepository.deleteAll();
    }

    @Test
    public void add1DocAndFind1Doc() {
        AnnotationDocument doc1 = createAnnotationDoc("A0A000");
        annotationRepository.save(doc1);

        assertThat(annotationRepository.findAll(new PageRequest(0, 10)).getTotalElements(), is(1L));
    }

    @Test
    public void add2DocsAndFind2Docs() {
        AnnotationDocument doc1 = createAnnotationDoc("A0A000");
        AnnotationDocument doc2 = createAnnotationDoc("A0A001");

        annotationRepository.save(doc1);
        annotationRepository.save(doc2);

        assertThat(annotationRepository.findAll(new PageRequest(0, 10)).getTotalElements(), is(2L));
    }

    @Test
    public void add3DocsRemove1AndFind2Docs() {
        AnnotationDocument doc1 = createAnnotationDoc("A0A000");
        AnnotationDocument doc2 = createAnnotationDoc("A0A001");
        AnnotationDocument doc3 = createAnnotationDoc("A0A002");

        annotationRepository.save(doc1);
        annotationRepository.save(doc2);
        annotationRepository.save(doc3);
        annotationRepository.delete(doc3);

        assertThat(annotationRepository.findAll(new PageRequest(0, 10)).getTotalElements(), is(2L));
    }

    @Test
    public void add2DocsAndCheckDefaultSortOrder() {
        AnnotationDocument doc1 = createAnnotationDoc("A0A000");
        doc1.defaultSort = "5A0A000";
        AnnotationDocument doc2 = createAnnotationDoc("A0A001");
        doc2.defaultSort = "3A0A001";

        annotationRepository.save(doc1);
        annotationRepository.save(doc2);

        AnnotationDocument firstReturnedDoc = annotationRepository.findAll(new PageRequest(0, 10)).getContent().get(0);
        assertThat(firstReturnedDoc.geneProductId, is("A0A001"));
    }
}