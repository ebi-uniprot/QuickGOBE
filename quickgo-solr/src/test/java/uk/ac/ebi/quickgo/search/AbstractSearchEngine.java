package uk.ac.ebi.quickgo.search;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.core.CoreContainer;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that sets up a search engine for testing purposes.
 * <p>
 * Uses an {@link org.apache.solr.client.solrj.embedded.EmbeddedSolrServer} to create a standalone solr instance
 * <p>
 * The class defines methods to insert/delete/query data from the data source.
 */
public abstract class AbstractSearchEngine<D> extends ExternalResource {
    private static final String SOLR_CONFIG_DIR = "src/main/cores";
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSearchEngine.class);

    private static final int DEFAULT_RETRIEVABLE_ROWS = 1000;
    protected final File indexHome;
    private final String searchEngineName;
    private SolrServer server;

    private int retrievableRows;

    public AbstractSearchEngine(String searchEngineName) {
        this.searchEngineName = searchEngineName;

        this.retrievableRows = DEFAULT_RETRIEVABLE_ROWS;

        this.indexHome = Files.createTempDir();
    }

    public void indexDocuments(Collection<D> documents) {
        if (documents == null) {
            throw new IllegalArgumentException("Entry is null");
        }

        try {
            server.addBeans(documents);
            server.commit();
        } catch (SolrServerException | IOException e) {
            throw new IllegalStateException("Problem indexing document(s).", e);
        }
    }

    public void indexDocument(D document) {
        indexDocuments(Collections.singleton(document));
    }

    public void removeDocument(String queryStr) {
        QueryResponse queryResponse = getQueryResponse(queryStr);
        if (queryResponse.getResults() != null && !queryResponse.getResults().isEmpty()) {
            try {
                server.deleteByQuery(queryStr);
                server.commit();
            } catch (SolrServerException | IOException e) {
                throw new IllegalStateException("Failed to remove entry with id: " + queryStr + " from index.", e);
            }
        }
    }

    public void removeAllDocuments() {
        removeDocument("*:*");
    }

    public QueryResponse getQueryResponse(String query) {
        SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.setRows(retrievableRows);

        try {
            return server.query(solrQuery);
        } catch (SolrServerException e) {
            throw new IllegalStateException(e);
        }
    }

    public void printIndexContents() {
        // show all results
        SolrQuery allQuery = new SolrQuery("*:*");
        allQuery.setRows(retrievableRows);

        SolrDocumentList results = null;
        QueryResponse queryResponse;

        try {
            queryResponse = server.query(allQuery);
            results = queryResponse.getResults();
        } catch (SolrServerException e) {
            LOGGER.error("Failed query: ", e);
        }

        if (results != null) {
            LOGGER.debug("Index contents ({}) start ---------", results.size());
            for (SolrDocument solrDocument : results) {
                LOGGER.debug("index contains: {}", solrDocument);
            }
        }

        LOGGER.debug("Index contents end ---------");
    }

    @Override
    protected void before() throws Throwable {
        // properties used by solrconfig.xml files in the cores' conf directories
        System.setProperty("solr.data.dir", indexHome.getAbsolutePath() + "/solr/data");
        System.setProperty("solr.core.name", "ontology");
        System.setProperty("solr.ulog.dir", indexHome.getAbsolutePath() + "/tlog");

        File solrConfigDir = new File(SOLR_CONFIG_DIR);

        CoreContainer container = new CoreContainer(solrConfigDir.getAbsolutePath());

        container.load();

        if (!container.isLoaded(searchEngineName)) {
            throw new IllegalStateException("Search engine " + searchEngineName + ", has not loaded properly");
        }

        server = new EmbeddedSolrServer(container, searchEngineName);
    }

    @Override
    protected void after() {
        server.shutdown();
        indexHome.deleteOnExit();
        LOGGER.debug("cleaned up solr home in ({}) now that the tests are finished", indexHome);
    }

    protected void setMaxRetrievableRows(int rows) {
        Preconditions.checkArgument(rows > 0, "Provided row value is negative: " + rows);
        retrievableRows = rows;
    }

    /**
     * Given a {@code queryResponse}, select from the results it contains, the terms
     * associated with the specified {@code field}.
     *
     * @param queryResponse
     * @param field
     * @return
     */
    public static List<String> filterResultsTo(QueryResponse queryResponse, String field) {
        return queryResponse.getResults().stream().map(result -> (String) result.get(field))
                .collect(Collectors.toList());
    }
}