package uk.ac.ebi.quickgo.service.geneproduct;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.ebi.quickgo.geneproduct.GeneProduct;
import uk.ac.ebi.quickgo.output.EntityToStream;
import uk.ac.ebi.quickgo.render.Format;
import uk.ac.ebi.quickgo.solr.query.service.geneproduct.GeneProductRetrieval;

/**
 * Gene Products Service implementation
 * @author cbonill
 *
 */
public class GeneProductServiceImpl implements GeneProductService{

	@Autowired
	GeneProductRetrieval geneProductRetrieval;
	
	// Log
	private static final Logger logger = Logger.getLogger(GeneProductServiceImpl.class);
	
	EntityToStream<GeneProduct> gpEntityToStream;
	

	@Override
	public void convertToStream(GeneProduct geneProduct, Format format,
			OutputStream outputStream) {
		switch (format) {
		case JSON:
			convertToJSON(geneProduct, outputStream);
			break;
		case XML:
			convertToXML(geneProduct, outputStream);
			break;
		}		
	}	
	
	@Override
	public void convertToXML(GeneProduct geneProduct, OutputStream outputStream) {
		try {
			gpEntityToStream.convertToXMLStream(geneProduct, Format.XML, outputStream);
		} catch (JAXBException e) {
			logger.error(e.getMessage());
		}		
	}

	@Override
	public void convertToJSON(GeneProduct geneProduct, OutputStream outputStream) {
		try {
			gpEntityToStream.convertToJSONStream(geneProduct, outputStream);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}		
	}

	@Override
	public GeneProduct findById(String id) {
		try {
			return geneProductRetrieval.findById(id);
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		return new GeneProduct();
	}
	
	@Override
	public List<GeneProduct> findByQuery(String query) {
		try {
			return geneProductRetrieval.findByQuery(query, -1);
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		return new ArrayList<>();
	}

	public GeneProductRetrieval getGeneProductRetrieval() {
		return geneProductRetrieval;
	}

	public void setGeneProductRetrieval(GeneProductRetrieval geneProductRetrieval) {
		this.geneProductRetrieval = geneProductRetrieval;
	}

	public EntityToStream<GeneProduct> getGpEntityToStream() {
		return gpEntityToStream;
	}

	public void setGpEntityToStream(EntityToStream<GeneProduct> gpEntityToStream) {
		this.gpEntityToStream = gpEntityToStream;
	}

	@Override
	public List<GeneProduct> autosuggest(String query, String filterQuery, int numResults) {
		try {
			return geneProductRetrieval.autosuggest(query, filterQuery, numResults);
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	
	@Override
	public List<GeneProduct> highlight(String text, String fq, int start, int rows) {
		List<GeneProduct> geneproducts = new ArrayList<>();
		try {
			geneproducts = geneProductRetrieval.highlight(text, fq, start, rows);
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		return geneproducts;
	}
	
	public long getTotalNumberHighlightResults(String text, String fq) {
		long results = 0;
		try {
			results = geneProductRetrieval.getTotalNumberHighlightResults(text, fq);
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}		
		return results;
	}
}