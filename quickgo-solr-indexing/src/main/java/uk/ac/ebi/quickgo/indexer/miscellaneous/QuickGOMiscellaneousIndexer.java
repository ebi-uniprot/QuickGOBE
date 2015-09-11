package uk.ac.ebi.quickgo.indexer.miscellaneous;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.data.GOSourceFiles;
import uk.ac.ebi.quickgo.data.SourceFiles;
import uk.ac.ebi.quickgo.data.SourceFiles.EAnnotationBlacklistEntry;
import uk.ac.ebi.quickgo.data.SourceFiles.EAnnotationGuidelineEntry;
import uk.ac.ebi.quickgo.data.SourceFiles.EEvidenceCode;
import uk.ac.ebi.quickgo.data.SourceFiles.EPublication;
import uk.ac.ebi.quickgo.data.SourceFiles.ESequence;
import uk.ac.ebi.quickgo.data.SourceFiles.ETaxon;
import uk.ac.ebi.quickgo.data.SourceFiles.EXrfAbbsEntry;
import uk.ac.ebi.quickgo.data.SourceFiles.TSVDataFile;
import uk.ac.ebi.quickgo.indexer.file.TaxonomyFile;
import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.generic.GenericTermSet;
import uk.ac.ebi.quickgo.ontology.go.AnnotationExtensionRelations;
import uk.ac.ebi.quickgo.ontology.go.AnnotationExtensionRelations.AnnotationExtensionRelation;
import uk.ac.ebi.quickgo.ontology.go.AnnotationExtensionRelations.Entity;
import uk.ac.ebi.quickgo.ontology.go.GeneOntology;
import uk.ac.ebi.quickgo.solr.indexing.service.miscellaneous.MiscellaneousIndexer;
import uk.ac.ebi.quickgo.solr.model.miscellaneous.SolrMiscellaneous.SolrMiscellaneousDocumentType;
import uk.ac.ebi.quickgo.solr.query.model.miscellaneous.enums.MiscellaneousField;
import uk.ac.ebi.quickgo.util.MemoryMonitor;
import uk.ac.ebi.quickgo.util.RowIterator;
import uk.ac.ebi.quickgo.util.XrefAbbsUtil;

/**
 * Miscellaneous indexing process
 * @author cbonill
 *
 */
@Service("quickGOMiscellaneousIndexer")
public class QuickGOMiscellaneousIndexer {

	/**
	 * Contains all the taxonomies
	 */
	Map<Integer,Miscellaneous> taxonomiesMap = new HashMap<>();

	/**
	 * Miscellaneous indexer
	 */
	@Autowired
	MiscellaneousIndexer miscellaneousIndexer;

	/**
	 * Chunks size to index
	 */
	private final int CHUNK_SIZE = 200000;

	// Log
	private static final Logger logger = LoggerFactory.getLogger(QuickGOMiscellaneousIndexer.class);
	private Properties properties;

	public void index(SourceFiles sourceFiles, GeneOntology geneOntology) throws Exception{
		this.indexTaxonomies(sourceFiles.taxonomy);
		this.indexEvidences(sourceFiles.evidenceInfo);
		this.indexSubsetsCounts(geneOntology.terms.values());
		this.indexSequences(sourceFiles.sequenceSource);
		this.indexPublications(sourceFiles.publications);
		this.indexAnnotationGuidelines(sourceFiles.annotationGuidelines);
		this.indexAnnotationBlacklists(sourceFiles.annotationBlacklist);
		this.indexAnnotationExtensionRelations(sourceFiles.goSourceFiles, geneOntology);
		this.indexXrefDatabases(sourceFiles.xrfAbbsInfo);
	}


	/**
	 * Index taxonomies and their closures
	 * @throws Exception
	 */
	public void indexTaxonomies(TSVDataFile<ETaxon> taxonomies) throws Exception {
		//Delete all taxonomies previously indexed
		miscellaneousIndexer.deleteByQuery(MiscellaneousField.TYPE.getValue() + ":" + SolrMiscellaneousDocumentType.TAXONOMY.getValue());
		//Contains chunk of taxonomies indexed
		Map<Integer,Miscellaneous> taxonomiesMapTemp = new HashMap<>();

		MemoryMonitor mm = new MemoryMonitor(true);
		RowIterator rowIterator = taxonomies.reader(ETaxon.values());

		Iterator<String[]> iterator = rowIterator.iterator();
		TaxonomyFile taxonomyFile = new TaxonomyFile();
		while (iterator.hasNext()) {
			Miscellaneous miscellaneous = taxonomyFile.calculateRow(iterator.next());//Read line
			taxonomiesMapTemp.put(miscellaneous.getTaxonomyId(), miscellaneous);
			if (taxonomiesMapTemp.size() == CHUNK_SIZE) {
				taxonomiesMap.putAll(taxonomiesMapTemp);
				miscellaneousIndexer.index(new ArrayList<>(taxonomiesMapTemp.values()));
				taxonomiesMapTemp = new HashMap<>();
			}
		}
		//Index the rest
		if(taxonomiesMapTemp.size() > 0){
			miscellaneousIndexer.index(new ArrayList<>(taxonomiesMapTemp.values()));
			taxonomiesMap.putAll(taxonomiesMapTemp);
		}
		logger.info("indexTaxonomies done: " + mm.end());
	}

	/**
	 * Index annotation extension relations information
	 * @param goSourceFiles Source files containing information
	 * @param geneOntology Gene ontology information
	 */
	private void indexAnnotationExtensionRelations(GOSourceFiles goSourceFiles, GeneOntology geneOntology) {
		MemoryMonitor mm = new MemoryMonitor(true);

		miscellaneousIndexer.deleteByQuery(MiscellaneousField.TYPE.getValue() + ":" + SolrMiscellaneousDocumentType.EXTENSION.getValue());
		AnnotationExtensionRelations annotationExtensionRelations = null;
		try {
			annotationExtensionRelations = new AnnotationExtensionRelations(geneOntology, goSourceFiles);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		Map<String, AnnotationExtensionRelation> annExtRelations = annotationExtensionRelations.getAnnExtRelations();
		List<Miscellaneous> miscellaneousAERs = new ArrayList<>();
		for(AnnotationExtensionRelation annotationExtensionRelation : annExtRelations.values()){
			Miscellaneous miscellaneous = new Miscellaneous();
			miscellaneous.setAerName(annotationExtensionRelation.getName());
			miscellaneous.setAerParents(annotationExtensionRelation.getParentsNames());
			miscellaneous.setAerSecondaries(annotationExtensionRelation.getSecondaries());
			miscellaneous.setAerSubsets(annotationExtensionRelation.getSubsets());
			miscellaneous.setAerDomain(annotationExtensionRelation.getDomain());
			miscellaneous.setAerUsage(annotationExtensionRelation.getUsage());
			miscellaneousAERs.add(miscellaneous);
			for(Entity entity : annotationExtensionRelation.getRanges().entities){
				miscellaneous.setAerRange(entity.matchers.compositeRegExp);
			}

			if (miscellaneousAERs.size() == CHUNK_SIZE) {
				miscellaneousIndexer.index(miscellaneousAERs);
				miscellaneousAERs = new ArrayList<>();
			}
		}

		if (miscellaneousAERs.size() > 0) {
			miscellaneousIndexer.index(miscellaneousAERs);
		}
		logger.info("indexAnnotationExtensionRelation done: " + mm.end());
	}

	/**
	 * Index protein sequences
	 * @param sequenceSource File that contains sequences
	 * @throws Exception
	 */
	private void indexSequences(TSVDataFile<ESequence> sequenceSource) throws Exception {
		indexMiscellaneousData(sequenceSource, ESequence.values(), SolrMiscellaneousDocumentType.SEQUENCE);
	}

	/**
	 * Index publications
	 * @param publicationSource File that contains publications
	 * @throws Exception
	 */
	private void indexPublications(TSVDataFile<EPublication> publicationSource) throws Exception {
		indexMiscellaneousData(publicationSource, EPublication.values(), SolrMiscellaneousDocumentType.PUBLICATION);
	}

	/**
	 * Index annotation blacklist
	 * @param annotationBlacklistSource File containing blacklist information
	 * @throws Exception
	 */
	private void indexAnnotationBlacklists(TSVDataFile<EAnnotationBlacklistEntry> annotationBlacklistSource) throws Exception {
		indexMiscellaneousData(annotationBlacklistSource, EAnnotationBlacklistEntry.values(), SolrMiscellaneousDocumentType.BLACKLIST);
	}

	/**
	 * Index annotation guidelines
	 * @param guidelineSource File that contains annotation guidelines
	 * @throws Exception
	 */
	private void indexAnnotationGuidelines(TSVDataFile<EAnnotationGuidelineEntry> guidelineSource) throws Exception {
		indexMiscellaneousData(guidelineSource, EAnnotationGuidelineEntry.values(), SolrMiscellaneousDocumentType.GUIDELINE);
	}

	/**
	 * Index Xref databases
	 */
	private void indexXrefDatabases(TSVDataFile<EXrfAbbsEntry> xrfAbbsInfo) throws Exception {
		miscellaneousIndexer.deleteByQuery(MiscellaneousField.TYPE.getValue() + ":" + SolrMiscellaneousDocumentType.XREFDB.getValue());
		indexMiscellaneousData(xrfAbbsInfo, EXrfAbbsEntry.values(), SolrMiscellaneousDocumentType.XREFDB);
	}

	/**
	 * Index evidence types
	 */
	private void indexEvidences(TSVDataFile<EEvidenceCode> evidenceInfo) throws Exception {
		miscellaneousIndexer.deleteByQuery(MiscellaneousField.TYPE.getValue() + ":" + SolrMiscellaneousDocumentType.EVIDENCE.getValue());
		indexMiscellaneousData(evidenceInfo, EEvidenceCode.values(), SolrMiscellaneousDocumentType.EVIDENCE);
	}

	/**
	 * Index different types of miscellaneous data
	 * @param source File contains information
	 * @param columns File's columns
	 * @param type TYpe of information to index (publications, sequences, ..)
	 * @throws Exception
	 */
	private void indexMiscellaneousData(TSVDataFile source, Enum[] columns, SolrMiscellaneousDocumentType type) throws Exception{
		Set<Miscellaneous> data = new HashSet<>();
		//Delete all data previously indexed
		miscellaneousIndexer.deleteByQuery(MiscellaneousField.TYPE.getValue() + ":" + type.getValue());

		MemoryMonitor mm = new MemoryMonitor(true);
		RowIterator rowIterator = source.reader(columns);

		Iterator<String[]> iterator = rowIterator.iterator();
		while (iterator.hasNext()) {
			String[] line = iterator.next();// Read line
			data.add(buildElement(line, type));
			if (data.size() == CHUNK_SIZE) {
				miscellaneousIndexer.index(new ArrayList<>(data));
				data = new HashSet<>();
			}
		}
		//Index the rest
		if (data.size() > 0) {
			miscellaneousIndexer.index(new ArrayList<>(data));
		}
		logger.info("index" + type.getValue() + " done: " + mm.end());
	}

	/**
	 * Build a Miscellaneous object from a read line from a file
	 * @param line Read line
	 * @param type Type of read line
	 * @return Miscellaneous representation of the line
	 */
	private Miscellaneous buildElement(String[] line, SolrMiscellaneousDocumentType type) {
		Miscellaneous miscellaneous = new Miscellaneous();
		switch (type) {
		case PUBLICATION:
			miscellaneous.setPublicationID(Integer.valueOf(line[0]));
			miscellaneous.setPublicationTitle(line[1]);
			break;
		case SEQUENCE:
			miscellaneous.setDbObjectID(line[0]);
			miscellaneous.setSequence(line[1]);
			break;
		case GUIDELINE:
			miscellaneous.setTerm(line[0]);
			miscellaneous.setGuidelineTitle(line[1]);
			miscellaneous.setGuidelineURL(line[2]);
			break;
		case BLACKLIST:
			miscellaneous.setDbObjectID(line[0]);
			miscellaneous.setTaxonomyId(Integer.valueOf(line[1]));
			miscellaneous.setTerm(line[2]);
			miscellaneous.setBacklistReason(line[3]);
			if (line[4] != null) {
				miscellaneous.setBlacklistMethodID(line[4]);
			}
			miscellaneous.setBacklistCategory(line[5]);
			miscellaneous.setBacklistEntryType(line[6]);
			break;
		case XREFDB:
			String abbreviation = line[0];
			miscellaneous.setXrefAbbreviation(abbreviation);
			miscellaneous.setXrefDatabase(line[1]);
			if(XrefAbbsUtil.isOverriden(abbreviation)){
				miscellaneous.setXrefGenericURL(XrefAbbsUtil.getGenericURL(abbreviation));
				miscellaneous.setXrefUrlSyntax(XrefAbbsUtil.getUrlSyntax(abbreviation));
			} else {
				miscellaneous.setXrefGenericURL(line[2]);
				miscellaneous.setXrefUrlSyntax(line[3]);
			}
			break;
		case EVIDENCE:
			miscellaneous.setEvidenceCode(line[0]);
			miscellaneous.setEvidenceName(line[1]);
			break;
		default:
			break;
		}
		return miscellaneous;
	}

	/**
	 * Index subsets counts
	 * @param terms Collection of terms
	 */
	private void indexSubsetsCounts(Collection<GenericTerm> terms) {
		// Calculate subsets counts
		Map<String, Integer> subsetsCount = new HashMap<>();
		for (GenericTerm genericTerm : terms) {
			for (GenericTermSet genericTermSet : genericTerm.getSubsets()) {
				String subsetName = genericTermSet.getName();
				if (subsetsCount.get(subsetName) != null) {
					int currentValue = subsetsCount.get(subsetName);
					int newValue = currentValue + 1;
					subsetsCount.put(subsetName, newValue);
				} else {
					subsetsCount.put(subsetName, 1);
				}
			}
		}
		// Delete old information
		miscellaneousIndexer.deleteByQuery(MiscellaneousField.TYPE.getValue() + ":" + SolrMiscellaneousDocumentType.SUBSETCOUNT.getValue());
		// Index counts
		List<Miscellaneous> subsetsCounts = new ArrayList<>();
		for (String subset : subsetsCount.keySet()) {
			Miscellaneous miscSubset = new Miscellaneous();
			miscSubset.setSubset(subset);
			miscSubset.setSubsetCount(subsetsCount.get(subset));
			subsetsCounts.add(miscSubset);
		}

		miscellaneousIndexer.index(subsetsCounts);
	}

	public Map<Integer, Miscellaneous> getTaxonomiesMap() {
		return taxonomiesMap;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public Properties getProperties() {
		return properties;
	}
}
