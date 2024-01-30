package uk.ac.ebi.quickgo.index.ontology;

import com.redfin.sitemapgenerator.WebSitemapGenerator;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.stubbing.Stubber;
import org.slf4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import uk.ac.ebi.quickgo.common.store.TemporarySolrDataStore;
import uk.ac.ebi.quickgo.index.QuickGOIndexMain;
import uk.ac.ebi.quickgo.index.common.DocumentReaderException;
import uk.ac.ebi.quickgo.index.common.JobTestRunnerConfig;
import uk.ac.ebi.quickgo.ontology.common.OntologyDocument;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.slf4j.LoggerFactory.getLogger;
import static uk.ac.ebi.quickgo.index.ontology.OntologyConfig.ONTOLOGY_INDEXING_STEP_NAME;
import static uk.ac.ebi.quickgo.index.ontology.OntologyIndexingBatchIT.OntologyReadResult.*;
import static uk.ac.ebi.quickgo.index.ontology.OntologySiteMapConfig.DEFAULT_QUICKGO_FRONTEND_TERM_URL;
import static uk.ac.ebi.quickgo.index.ontology.OntologySiteMapConfig.DEFAULT_QUICKGO_FRONTEND_URL;
import static uk.ac.ebi.quickgo.ontology.common.document.OntologyDocMocker.createECODoc;
import static uk.ac.ebi.quickgo.ontology.common.document.OntologyDocMocker.createGODoc;

/**
 * Test specific behaviour of the job executed by {@link QuickGOIndexMain}.
 *
 * Created 18/12/15
 * @author Edd
 */
@ExtendWith(TemporarySolrDataStore.class)
@ActiveProfiles(profiles = {"embeddedServer"})
@SpringBootTest(classes = {JobTestRunnerConfig.class, OntologyConfig.class, OntologyIndexingBatchIT.TestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OntologyIndexingBatchIT {
    @TempDir
    static File siteMapTempFolder;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private OntologyReader reader;

    @Value("${indexing.ontology.skip.limit}")
    private int skipLimit;

    private static final String INVALID_TERM = "INVALID_TERM";
    private static final String GO = "go";
    private static final String ECO = "eco";
    private static final String SITEMAP_INDEX_XML = "sitemap_index.xml";
    private static final String SITEMAP_XML = "sitemap.xml";
    private static final String SITEMAP_URL_REGEX = "^[\t ]+<loc>.*</loc>.*$";
    private static final Logger LOGGER = getLogger(OntologyIndexingBatchIT.class);

    @Test
    void documentReaderExceptionThrownWhenReaderIsOpenedCausesStepFailure() {
        doThrow(new DocumentReaderException("Error!")).when(reader).open(any(ExecutionContext.class));

        JobExecution jobExecution = jobLauncherTestUtils.launchStep(ONTOLOGY_INDEXING_STEP_NAME);
        assertThat(jobExecution.getStatus(), is(BatchStatus.FAILED));
    }

    @Test
    void stepSucceedsWhenNoSkips() throws Exception {
        List<OntologyReadResult> resultsFromReadingSource = asList(
                GO_DOC,
                GO_DOC,
                ECO_DOC,
                ECO_DOC,
                ECO_DOC,
                NULL
        );

        int docCount = validDocsReadCount(resultsFromReadingSource);
        mockResponseFromReader(resultsFromReadingSource);

        JobExecution jobExecution = jobLauncherTestUtils.launchStep(ONTOLOGY_INDEXING_STEP_NAME);
        assertThat(jobExecution.getStatus(), is(BatchStatus.COMPLETED));

        StepExecution step = jobExecution.getStepExecutions().iterator().next();
        assertThat(step.getReadCount(), is(docCount));
        assertThat(step.getWriteCount(), is(docCount));
        assertThat(step.getSkipCount(), is(0));

        checkSiteMapWasWritten(resultsFromReadingSource);
    }

    @Test
    void stepSkipsOnceWhenReaderFindsOneEmptyOptionalDocument() throws Exception {
        List<OntologyReadResult> resultsFromReadingSource = asList(
                GO_DOC,
                DOC_READER_EXCEPTION,
                ECO_DOC,
                NULL
        );

        int docCount = validDocsReadCount(resultsFromReadingSource);
        mockResponseFromReader(resultsFromReadingSource);

        JobExecution jobExecution = jobLauncherTestUtils.launchStep(ONTOLOGY_INDEXING_STEP_NAME);
        assertThat(jobExecution.getStatus(), is(BatchStatus.COMPLETED));

        StepExecution step = jobExecution.getStepExecutions().iterator().next();
        assertThat(step.getReadCount(), is(docCount));
        assertThat(step.getWriteCount(), is(docCount));
        assertThat(step.getSkipCount(), is(1));

        checkSiteMapWasWritten(resultsFromReadingSource);
    }

    @Test
    void tooManySkipsCausesStepToFail() throws Exception {
        List<OntologyReadResult> resultsFromReadingSource = asList(
                GO_DOC,
                DOC_READER_EXCEPTION,
                GO_DOC,
                DOC_READER_EXCEPTION,
                ECO_DOC,
                ECO_DOC,
                // full chunk of documents now created, and should
                // be written

                DOC_READER_EXCEPTION,
                ECO_DOC,
                NULL
        );

        mockResponseFromReader(resultsFromReadingSource);

        JobExecution jobExecution = jobLauncherTestUtils.launchStep(ONTOLOGY_INDEXING_STEP_NAME);
        assertThat(jobExecution.getStatus(), is(BatchStatus.FAILED));

        StepExecution step = jobExecution.getStepExecutions().iterator().next();
        assertThat(step.getReadCount(), is(4));
        assertThat(step.getWriteCount(), is(4));
        assertThat(step.getSkipCount(), is(skipLimit));

        checkSiteMapWasWritten(asList(GO_DOC,
                GO_DOC,
                ECO_DOC,
                ECO_DOC));
    }

    @Test
    void succeedsOn2ChunksButSkips1ChunkWhenSkipLimitExceeded() throws Exception {
        List<OntologyReadResult> resultsFromReadingSource = asList(
                GO_DOC,
                GO_DOC,
                DOC_READER_EXCEPTION,
                ECO_DOC,
                ECO_DOC,
                // a full chunk of documents to write has now been created, so
                // chunk 1 should be written

                DOC_READER_EXCEPTION,
                ECO_DOC,
                ECO_DOC,
                GO_DOC,
                GO_DOC,
                // now chunk 2 should be written

                ECO_DOC,
                GO_DOC,
                DOC_READER_EXCEPTION,
                // ------ BOOM! Skip Limit exceeded now and entire step fails (throwing away latest chunk)

                ECO_DOC,

                // null indicates reading is done
                NULL
        );

        mockResponseFromReader(resultsFromReadingSource);

        JobExecution jobExecution = jobLauncherTestUtils.launchStep(ONTOLOGY_INDEXING_STEP_NAME);
        assertThat(jobExecution.getStatus(), is(BatchStatus.FAILED));

        StepExecution step = jobExecution.getStepExecutions().iterator().next();
        assertThat(step.getReadCount(), is(10));
        assertThat(step.getWriteCount(), is(8));
        assertThat(step.getSkipCount(), Is.is(skipLimit));

        checkSiteMapWasWritten(asList(
                GO_DOC,
                GO_DOC,
                ECO_DOC,
                ECO_DOC,
                ECO_DOC,
                ECO_DOC,
                GO_DOC,
                GO_DOC));
    }

    /**
     * Given a list of intended results (documents / exceptions), captured within {@code readResults},
     * configure the mocked {@link OntologyReader} to produce the corresponding results.
     *
     * @param readResults a list of intended results (documents / exceptions)
     * @throws Exception this exception should never occur because the reading is being done from a mocked
     *         {@link OntologyReader}.
     */
    private void mockResponseFromReader(List<OntologyReadResult> readResults) throws Exception {
        Stubber stubber = null;

        int goCount = 0;
        int ecoCount = 0;
        for (OntologyReadResult readResult : readResults) {
            switch (readResult) {
                case GO_DOC:
                    OntologyDocument goDoc = createGODoc(GO + goCount, GO + goCount++ + "name");
                    stubber = (stubber == null) ? doReturn(goDoc) : stubber.doReturn(goDoc);
                    break;
                case ECO_DOC:
                    OntologyDocument ecoDoc = createECODoc(ECO + ecoCount, ECO + ecoCount++ + "name");
                    stubber = (stubber == null) ? doReturn(ecoDoc) : stubber.doReturn(ecoDoc);
                    break;
                case NULL:
                    stubber = (stubber == null) ? doReturn(null) : stubber.doReturn(null);
                    break;
                case DOC_READER_EXCEPTION:
                    stubber = (stubber == null) ? doThrow(new DocumentReaderException("Error!"))
                            : stubber.doThrow(new DocumentReaderException("Error!"));
                    break;
                default:
                    throw new IllegalStateException("Read result not handled: " + readResult);
            }
        }

        if (stubber != null) {
            stubber.when(reader).read();
        } else {
            LOGGER.warn("Stubbed read results are null!");
        }
    }

    private int validDocsReadCount(List<OntologyReadResult> resultsFromReadingSource) {
        return (int) resultsFromReadingSource.stream().filter(r -> r == ECO_DOC || r == GO_DOC).count();
    }

    /**
     * Check that the site map was written to disk, and contains the valid URLs specified in
     * {@code docsThatShouldHaveBeenWritten}.
     *
     * @param docsThatShouldHaveBeenWritten The documents that should have been written to Solr
     * @throws IOException may be produced during creation of {@link BufferedReader}
     */
    private void checkSiteMapWasWritten(List<OntologyReadResult> docsThatShouldHaveBeenWritten) throws IOException {
        assertThat(new File(siteMapTempFolder, SITEMAP_INDEX_XML).exists(), is(true));
        File siteMapXml = new File(siteMapTempFolder, SITEMAP_XML);
        assertThat(siteMapXml.exists(), is(true));

        int urlMatchCount = 0;
        try (BufferedReader reader = Files.newBufferedReader(siteMapXml.toPath())) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.matches(SITEMAP_URL_REGEX)) {
                    boolean urlMatchFound = false;
                    int goCount = 0;
                    int ecoCount = 0;
                    for (OntologyReadResult readResult : docsThatShouldHaveBeenWritten) {
                        String termUrl = null;
                        if (readResult == GO_DOC) {
                            termUrl = buildTermUrl(GO + goCount++);
                        } else if (readResult == ECO_DOC) {
                            termUrl = buildTermUrl(ECO + ecoCount++);
                        } else {
                            termUrl = INVALID_TERM;
                        }

                        if (line.contains(termUrl)) {
                            urlMatchFound = true;
                            urlMatchCount++;
                        }
                    }
                    assertThat(urlMatchFound, is(true));
                }
            }
        }

        assertThat(urlMatchCount, is(validDocsReadCount(docsThatShouldHaveBeenWritten)));
    }

    private static String buildTermUrl(String termId) {
        return DEFAULT_QUICKGO_FRONTEND_TERM_URL + "/" + termId;
    }

    enum OntologyReadResult {
        GO_DOC,
        ECO_DOC,
        NULL,
        DOC_READER_EXCEPTION
    }

    @Configuration
    static class TestConfig {
        /**
         * A mocked {@link OntologyReader} instance.
         * @return A mocked {@link OntologyReader} instance.
         */
        @Bean
        @Primary
        OntologyReader ontologyReader() {
            return mock(OntologyReader.class);
        }

        /**
         * A real {@link WebSitemapGenerator} instance, which is instructed to write to a temporary folder.
         * @return the test {@link WebSitemapGenerator} instance.
         */
        @Bean
        @Primary
        WebSitemapGenerator sitemapGenerator() {
            try {
                return WebSitemapGenerator
                        .builder(DEFAULT_QUICKGO_FRONTEND_URL, siteMapTempFolder)
                        .build();
            } catch (MalformedURLException e) {
                LOGGER.error("Sitemap URL is malformed", e);
                throw new IllegalStateException(e);
            }
        }
    }
}