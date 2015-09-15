package uk.ac.ebi.quickgo.service.term;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.springframework.cache.annotation.Cacheable;

import uk.ac.ebi.quickgo.ontology.eco.ECOTerm;
import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.generic.Synonym;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.output.EntityToStream;
import uk.ac.ebi.quickgo.render.Format;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm.SolrTermDocumentType;
import uk.ac.ebi.quickgo.solr.query.model.ontology.enums.TermField;
import uk.ac.ebi.quickgo.solr.query.service.ontology.TermRetrieval;
import uk.ac.ebi.quickgo.util.NamedXRef;

/**
 * Service responsible for the main operations over Terms
 *
 * @author cbonill
 *
 */
public class TermServiceImpl implements TermService,Serializable {

	private static final long serialVersionUID = 4202735332039846712L;

	// Log
	private static final Logger logger = LoggerFactory.getLogger(TermServiceImpl.class);

	TermRetrieval goTermRetrieval;

	EntityToStream<GOTerm> goTermEntityToStream;

	EntityToStream<ECOTerm> ecoTermEntityToStream;

	/**
	 * {@link TermService#retrieveTerm(String)}
	 */
	@Cacheable(value="term")
	public GOTerm retrieveTerm(String id) {

		GOTerm goTerm = new GOTerm();
		try {
			goTerm = goTermRetrieval.findById(id);
			if (goTerm.getId() == null) {
				return retrievePrimaryTerm(id);
			}
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		return goTerm;
	}


	/**
	 * {@link TermService#convertToStream(GenericTerm, Format, OutputStream)}
	 */
	@Override
	public void convertToStream(GenericTerm genericTerm, Format format, OutputStream outputStream) {
		switch (format) {
		case JSON:
			convertToJSON(genericTerm, outputStream);
			break;
		case OBOXML:
		case XML:
			convertToXML(genericTerm, format, outputStream);
			break;
		}
	}

	/**
	 * {@link TermService#convertToXML(GenericTerm, Format, OutputStream)}
	 */
	public void convertToXML(GenericTerm genericTerm, Format format, OutputStream outputStream) {
		try {
			if(genericTerm.isGOTerm()){
				goTermEntityToStream.convertToXMLStream((GOTerm)genericTerm, format, outputStream);
			}else if(genericTerm.isECOTerm()){
				ecoTermEntityToStream.convertToXMLStream((ECOTerm)genericTerm, Format.XML, outputStream);
			}
		} catch (JAXBException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * {@link TermService#convertToJSON(GenericTerm, OutputStream)}
	 */
	public void convertToJSON(GenericTerm genericTerm, OutputStream outputStream) {
		try {
			if(genericTerm.isGOTerm()){
				goTermEntityToStream.convertToJSONStream((GOTerm) genericTerm, outputStream);
			}else if(genericTerm.isECOTerm()){
				ecoTermEntityToStream.convertToJSONStream((ECOTerm) genericTerm, outputStream);
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	public String convertToOBO(GenericTerm genericTerm){
		String obo = "[Term]\n";
		obo = obo + "id: " + genericTerm.getId() + "\n";
		obo = obo + "name: " + genericTerm.getName() + "\n";
		obo = obo + "def: " + genericTerm.getDefinition() + "\n";
		List<Synonym> synonyms = genericTerm.getSynonyms();
		if (synonyms != null) {
			for (Synonym synonym : synonyms) {
				obo = obo + "synonym: " + synonym.getName() + "\n";
			}
		}
		List<NamedXRef> xrefs = genericTerm.getXrefs();
		if (xrefs != null) {
			for (NamedXRef namedXRef : xrefs) {
				obo = obo + "xref: " + namedXRef.getXRef() + "\n";
			}
		}
		return obo;
	}

	@Override
	public Map<String, Map<String, String>> retrieveNames() {
		Map<String, Map<String, String>> values = new HashMap<>();
		try {
			values = goTermRetrieval.getFieldValues(TermField.TYPE.getValue()
					+ ":" + SolrTermDocumentType.TERM.getValue(),
					TermField.ID.getValue(), TermField.NAME.getValue());
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		return values;
	}

	/**
	 * Given an ID, check if it's secondary one and in that case retrieve the term that contains it
	 * @param id Secondary ID
	 * @return Primary term
	 * @throws SolrServerException
	 */
	private GOTerm retrievePrimaryTerm(String id) throws SolrServerException{
		String query = TermField.SECONDARYID.getValue() + ":" + ClientUtils.escapeQueryChars(id);
		List<GOTerm> goTerms = goTermRetrieval.findByQuery(query, -1);
		if (goTerms != null && goTerms.size() > 0) {
			GenericTerm term = goTerms.get(0);
			return goTermRetrieval.findById(term.getId());
		}

		return new GOTerm();
	}


	@Override
	public List<GenericTerm> autosuggest(String text, String filterQuery, int numResults) throws SolrServerException {
		return goTermRetrieval.autosuggest(text,filterQuery,numResults);
	}

	@Override
	public List<GenericTerm> autosuggestOnlyGoTerms(String text, String filterQuery, int numResults) throws SolrServerException {
		return goTermRetrieval.autosuggestOnlyGoTerms(text,filterQuery,numResults);
	}

	@Override
	public List<GenericTerm> highlight(String text, String fq, int start, int rows) {
		List<GenericTerm> terms = new ArrayList<>();
		try {
			terms = goTermRetrieval.highlight(text, fq, start, rows);
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		return terms;
	}

	public long getTotalNumberHighlightResults(String text, String fq) {
		long results = 0;
		try {
			results = goTermRetrieval.getTotalNumberHighlightResults(text, fq);
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		return results;
	}

	public List<GOTerm> retrieveByHistoryDate(Date from, Date to, int limit){
		List<GOTerm> terms = new ArrayList<>();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		String query = TermField.HISTORYTIMESTAMP.getValue() + ":[" + df.format(from) + " TO " + df.format(to) + "]";
		try {
			terms = goTermRetrieval.findByQuery(query, limit);
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		}
		return terms;
	}


	public EntityToStream<GOTerm> getGoTermEntityToStream() {
		return goTermEntityToStream;
	}

	public void setGoTermEntityToStream(EntityToStream<GOTerm> goTermEntityToStream) {
		this.goTermEntityToStream = goTermEntityToStream;
	}

	public EntityToStream<ECOTerm> getEcoTermEntityToStream() {
		return ecoTermEntityToStream;
	}

	public void setEcoTermEntityToStream(
			EntityToStream<ECOTerm> ecoTermEntityToStream) {
		this.ecoTermEntityToStream = ecoTermEntityToStream;
	}

	public void setGoTermRetrieval(TermRetrieval goTermRetrieval) {
		this.goTermRetrieval = goTermRetrieval;
	}
}
