package uk.ac.ebi.quickgo.solr.mapper.miscellaneous;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.solr.mapper.SolrMapper;
import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;
import uk.ac.ebi.quickgo.solr.model.miscellaneous.SolrMiscellaneous;
import uk.ac.ebi.quickgo.solr.model.miscellaneous.SolrMiscellaneous.SolrMiscellaneousDocumentType;

/**
 * To map Miscellaneous data into Solr objects
 */
@Service("solrMiscellaneousMapper")
public class SolrMiscellaneousMapper implements SolrMapper<Miscellaneous, SolrMiscellaneous> {
	@Override
	public Collection<SolrMiscellaneous> toSolrObject(Miscellaneous genericObject) {
		return toSolrObject(genericObject, SolrMiscellaneousDocumentType.getAsInterfaces());
	}

	@Override
	public Collection<SolrMiscellaneous> toSolrObject(Miscellaneous miscellaneousObject, List<SolrDocumentType> solrDocumentTypes) {
		List<SolrMiscellaneous> solrMiscellaneous = new ArrayList<>();

		for (SolrDocumentType miscellaneousDocumentType : solrDocumentTypes) {
			switch ((SolrMiscellaneousDocumentType)miscellaneousDocumentType) {
			case TAXONOMY:
				if (miscellaneousObject.getTaxonomyName() != null) {// Contains taxonomy information
					solrMiscellaneous.add(mapTaxonomy(miscellaneousObject));
				}
				break;
			case STATS:
				if (miscellaneousObject.getComparedTerm() != null) {// Contains statistics information
					solrMiscellaneous.add(mapCoOccurrenceStats(miscellaneousObject));
				}
				break;
			case SEQUENCE:
				if (miscellaneousObject.getSequence() != null) {// Contains sequence information
					solrMiscellaneous.add(mapSequence(miscellaneousObject));
				}
				break;
			case PUBLICATION:
				if (miscellaneousObject.getPublicationID() != 0) {// Contains publication information
					solrMiscellaneous.add(mapPublication(miscellaneousObject));
				}
				break;
			case GUIDELINE:
				if (miscellaneousObject.getGuidelineTitle() != null) {// Contains publication information
					solrMiscellaneous.add(mapGuideline(miscellaneousObject));
				}
				break;
			case BLACKLIST:
				if (miscellaneousObject.getBacklistEntryType() != null) {// Contains blacklist information
					solrMiscellaneous.add(mapBlacklist(miscellaneousObject));
				}
				break;
			case EXTENSION:
				if (miscellaneousObject.getAerName() != null) {// Contains annotation extension relations information
					solrMiscellaneous.add(mapAnnotationExtensionRelation(miscellaneousObject));
				}
				break;
			case XREFDB:
				if (miscellaneousObject.getXrefAbbreviation() != null) {// Contains xref databases information
					solrMiscellaneous.add(mapXrefDatabase(miscellaneousObject));
				}
				break;
			case SUBSETCOUNT:
				if (miscellaneousObject.getSubset() != null) {// Contains subsets counts information
					solrMiscellaneous.add(mapSubsetsCounts(miscellaneousObject));
				}
				break;
			case EVIDENCE:
				if (miscellaneousObject.getEvidenceCode() != null) {// Contains evidence information
					solrMiscellaneous.add(mapEvidence(miscellaneousObject));
				}
				break;
			case POSTPROCESSINGRULE:
				if (miscellaneousObject.getPprRuleId() != null) {// Contains post processing rule information
					solrMiscellaneous.add(mapPostProcessingRule(miscellaneousObject));
				}
				break;
			}
		}
		return solrMiscellaneous;
	}

	/**
	 * Maps taxonomy objects
	 * @param miscellaneousObject Miscellaneous object to convert
	 * @return SolrMiscellaneous representation
	 */
	private SolrMiscellaneous mapTaxonomy(Miscellaneous miscellaneousObject) {
		SolrMiscellaneous solrMiscellaneous = new SolrMiscellaneous();
		solrMiscellaneous.setDocType(SolrMiscellaneousDocumentType.TAXONOMY.getValue());
		solrMiscellaneous.setTaxonomyId(miscellaneousObject.getTaxonomyId());
		solrMiscellaneous.setTaxonomyName(miscellaneousObject.getTaxonomyName());
		solrMiscellaneous.setTaxonomyClosures(miscellaneousObject.getTaxonomyClosure());
		
		return solrMiscellaneous;
	}
	
	/**
	 * Maps co-occurrence statistics objects
	 * @param miscellaneous Miscellaneous object to map
	 * @return SolrMiscellaneous representation
	 */
	private SolrMiscellaneous mapCoOccurrenceStats(Miscellaneous miscellaneous){
		SolrMiscellaneous solrMiscellaneous = new SolrMiscellaneous();
		solrMiscellaneous.setDocType(SolrMiscellaneousDocumentType.STATS.getValue());
		solrMiscellaneous.setAll(miscellaneous.getAll());
		solrMiscellaneous.setCompared(miscellaneous.getCompared());
		solrMiscellaneous.setComparedTerm(miscellaneous.getComparedTerm());
		solrMiscellaneous.setSelected(miscellaneous.getSelected());
		solrMiscellaneous.setStatsType(miscellaneous.getStatsType());
		solrMiscellaneous.setTerm(miscellaneous.getTerm());
		solrMiscellaneous.setTogether(miscellaneous.getTogether());
		
		return solrMiscellaneous;
	}
	
	/**
	 * Maps sequences objects
	 * @param miscellaneous Miscellaneous object to map
	 * @return SolrMiscellaneous representation
	 */
	private SolrMiscellaneous mapSequence(Miscellaneous miscellaneous){
		SolrMiscellaneous solrMiscellaneous = new SolrMiscellaneous();
		solrMiscellaneous.setDocType(SolrMiscellaneousDocumentType.SEQUENCE.getValue());
		solrMiscellaneous.setDbObjectID(miscellaneous.getDbObjectID());
		solrMiscellaneous.setSequence(miscellaneous.getSequence());
		
		return solrMiscellaneous;
	}
	
	/**
	 * Map publications information
	 * @param miscellaneousObject Miscellaneous object to map
	 * @return SolrMiscellaneous representation
	 */
	private SolrMiscellaneous mapPublication(Miscellaneous miscellaneousObject) {
		SolrMiscellaneous solrMiscellaneous = new SolrMiscellaneous();
		solrMiscellaneous.setDocType(SolrMiscellaneousDocumentType.PUBLICATION.getValue());
		solrMiscellaneous.setPublicationID(miscellaneousObject.getPublicationID());
		solrMiscellaneous.setPublicationTitle(miscellaneousObject.getPublicationTitle());
		
		return solrMiscellaneous;
	}
	
	/**
	 * Map annotation guideline information
	 * @param miscellaneousObject Miscellaneous object to map
	 * @return SolrMiscellaneous representation
	 */
	private SolrMiscellaneous mapGuideline(Miscellaneous miscellaneousObject) {
		SolrMiscellaneous solrMiscellaneous = new SolrMiscellaneous();
		solrMiscellaneous.setDocType(SolrMiscellaneousDocumentType.GUIDELINE.getValue());
		solrMiscellaneous.setTerm(miscellaneousObject.getTerm());
		solrMiscellaneous.setGuidelineTitle(miscellaneousObject.getGuidelineTitle());
		solrMiscellaneous.setGuidelineURL(miscellaneousObject.getGuidelineURL());
		return solrMiscellaneous;		
	}
	
	/**
	 * Map annotation blacklist information
	 * @param miscellaneousObject Miscellaneous blacklist object
	 * @return SolrMiscellaneous representation
	 */
	private SolrMiscellaneous mapBlacklist(Miscellaneous miscellaneousObject) {
		SolrMiscellaneous solrMiscellaneous = new SolrMiscellaneous();
		solrMiscellaneous.setDocType(SolrMiscellaneousDocumentType.BLACKLIST.getValue());
		solrMiscellaneous.setDbObjectID(miscellaneousObject.getDbObjectID());
		solrMiscellaneous.setTaxonomyId(miscellaneousObject.getTaxonomyId());
		solrMiscellaneous.setTerm(miscellaneousObject.getTerm());
		solrMiscellaneous.setBacklistReason(miscellaneousObject.getBacklistReason());
		solrMiscellaneous.setBlacklistMethodID(miscellaneousObject.getBlacklistMethodID());
		solrMiscellaneous.setBacklistCategory(miscellaneousObject.getBacklistCategory());
		solrMiscellaneous.setBacklistEntryType(miscellaneousObject.getBacklistEntryType());
		
		return solrMiscellaneous;
	}
	
	/**
	 * Map annotation extension relations information
	 * @param miscellaneousObject Miscellaneous annotation extension relation object
	 * @return SolrMiscellaneous representation
	 */
	private SolrMiscellaneous mapAnnotationExtensionRelation(Miscellaneous miscellaneousObject) {
		SolrMiscellaneous solrMiscellaneous = new SolrMiscellaneous();
		solrMiscellaneous.setDocType(SolrMiscellaneousDocumentType.EXTENSION.getValue());
		solrMiscellaneous.setAerDomain(miscellaneousObject.getAerDomain());
		solrMiscellaneous.setAerName(miscellaneousObject.getAerName());
		solrMiscellaneous.setAerParents(miscellaneousObject.getAerParents());
		solrMiscellaneous.setAerSecondaries(miscellaneousObject.getAerSecondaries());
		solrMiscellaneous.setAerSubsets(miscellaneousObject.getAerSubsets());
		solrMiscellaneous.setAerUsage(miscellaneousObject.getAerUsage());
		solrMiscellaneous.setAerRange(miscellaneousObject.getAerRange());
		return solrMiscellaneous;
	}
	
	/**
	 * Map Xref databases name and URLs
	 * @param miscellaneousObject Miscellaneous xref database object
	 * @return SolrMiscellaneous representation
	 */
	private SolrMiscellaneous mapXrefDatabase(Miscellaneous miscellaneousObject) {
		SolrMiscellaneous solrMiscellaneous = new SolrMiscellaneous();
		solrMiscellaneous.setDocType(SolrMiscellaneousDocumentType.XREFDB.getValue());
		solrMiscellaneous.setXrefAbbreviation(miscellaneousObject.getXrefAbbreviation());
		solrMiscellaneous.setXrefDatabase(miscellaneousObject.getXrefDatabase());
		solrMiscellaneous.setXrefGenericURL(miscellaneousObject.getXrefGenericURL());
		solrMiscellaneous.setXrefUrlSyntax(miscellaneousObject.getXrefUrlSyntax());
		return solrMiscellaneous;
	}
	
	/**
	 * Map subsets counts information
	 * @param miscellaneousObject Miscellaneous object with subset information
	 * @return SolrMiscellaneous representation
	 */
	private SolrMiscellaneous mapSubsetsCounts(Miscellaneous miscellaneousObject) {
		SolrMiscellaneous solrMiscellaneous = new SolrMiscellaneous();
		solrMiscellaneous.setDocType(SolrMiscellaneousDocumentType.SUBSETCOUNT.getValue());
		solrMiscellaneous.setSubset(miscellaneousObject.getSubset());
		solrMiscellaneous.setSubsetCount(miscellaneousObject.getSubsetCount());
		return solrMiscellaneous;
	}
	
	/**
	 * Map evidence types information
	 * @param miscellaneousObject Miscellaneous object with evidence information
	 * @return SolrMiscellaneous representation
	 */
	private SolrMiscellaneous mapEvidence(Miscellaneous miscellaneousObject) {
		SolrMiscellaneous solrMiscellaneous = new SolrMiscellaneous();
		solrMiscellaneous.setDocType(SolrMiscellaneousDocumentType.EVIDENCE.getValue());
		solrMiscellaneous.setEvidenceCode(miscellaneousObject.getEvidenceCode());
		solrMiscellaneous.setEvidenceName(miscellaneousObject.getEvidenceName());
		return solrMiscellaneous;
	}
	
	/**
	 * Map post processing rules information
	 * @param miscellaneousObject Miscellaneous object with post processing rule information
	 * @return SolrMiscellaneous representation
	 */
	private SolrMiscellaneous mapPostProcessingRule(Miscellaneous miscellaneousObject) {
		SolrMiscellaneous solrMiscellaneous = new SolrMiscellaneous();
		solrMiscellaneous.setDocType(SolrMiscellaneousDocumentType.POSTPROCESSINGRULE.getValue());
		solrMiscellaneous.setPprAffectedTaxGroup(miscellaneousObject.getPprAffectedTaxGroup());
		solrMiscellaneous.setPprAncestorGoId(miscellaneousObject.getPprAncestorGoId());
		solrMiscellaneous.setPprAncestorTerm(miscellaneousObject.getPprAncestorTerm());
		solrMiscellaneous.setPprCleanupAction(miscellaneousObject.getPprCleanupAction());
		solrMiscellaneous.setPprCuratorNotes(miscellaneousObject.getPprCuratorNotes());
		solrMiscellaneous.setPprOriginalGoId(miscellaneousObject.getPprOriginalGoId());
		solrMiscellaneous.setPprOriginalTerm(miscellaneousObject.getPprOriginalTerm());
		solrMiscellaneous.setPprRelationship(miscellaneousObject.getPprRelationship());
		solrMiscellaneous.setPprRuleId(miscellaneousObject.getPprRuleId());
		solrMiscellaneous.setPprSubstitutedGoId(miscellaneousObject.getPprSubstitutedGoId());
		solrMiscellaneous.setPprTaxonName(miscellaneousObject.getPprTaxonName());		
		return solrMiscellaneous;
	}
}