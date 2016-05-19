package uk.ac.ebi.quickgo.geneproduct.service.search;

import uk.ac.ebi.quickgo.geneproduct.common.document.GeneProductDocument;
import uk.ac.ebi.quickgo.geneproduct.model.GeneProduct;
import uk.ac.ebi.quickgo.geneproduct.service.converter.GeneProductDocConverter;
import uk.ac.ebi.quickgo.rest.search.solr.AbstractSolrQueryResultConverter;
import uk.ac.ebi.quickgo.rest.search.solr.SolrQueryResultHighlightingConverter;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts the Solr results into {@link GeneProduct} instances.
 *
 * Created 06/04/16
 * @author Edd
 */
public class GeneProductSolrQueryResultConverter extends AbstractSolrQueryResultConverter<GeneProduct> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneProductSolrQueryResultConverter.class);

    private final DocumentObjectBinder documentObjectBinder;
    private final GeneProductDocConverter geneProductDocConverter;

    public GeneProductSolrQueryResultConverter(DocumentObjectBinder documentObjectBinder,
            GeneProductDocConverter geneProductDocConverter,
            Map<String, String> fieldNameMap){
        super(new SolrQueryResultHighlightingConverter(fieldNameMap));

        Preconditions.checkArgument(documentObjectBinder != null, "Document Object Binder cannot be null");
        Preconditions.checkArgument(geneProductDocConverter != null, "Gene product document converter cannot be null");

        this.documentObjectBinder = documentObjectBinder;
        this.geneProductDocConverter = geneProductDocConverter;
    }

    @Override protected List<GeneProduct> convertResults(SolrDocumentList results) {
        assert results != null : "Results list cannot be null";

        List<GeneProductDocument> solrTermDocs = documentObjectBinder.getBeans(GeneProductDocument.class, results);

        return solrTermDocs.stream()
                .map(geneProductDocConverter::convert)
                .collect(Collectors.toList());
    }
}
