package uk.ac.ebi.quickgo.solr.mapper.miscellaneous;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uk.ac.ebi.quickgo.miscellaneous.Miscellaneous;
import uk.ac.ebi.quickgo.solr.mapper.EntityMapper;
import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;
import uk.ac.ebi.quickgo.solr.model.miscellaneous.SolrMiscellaneous;
import uk.ac.ebi.quickgo.solr.model.miscellaneous.SolrMiscellaneous.SolrMiscellaneousDocumentType;

/**
 * For converting Solr miscellaneous data into Java entities
 */
public class EntityMiscellaneousMapper implements EntityMapper<SolrMiscellaneous, Miscellaneous>{

	@Override
	public Miscellaneous toEntityObject(Collection<SolrMiscellaneous> solrObjects) {
		return toEntityObject(solrObjects, SolrMiscellaneousDocumentType.getAsInterfaces());
	}

	@Override
	public Miscellaneous toEntityObject(Collection<SolrMiscellaneous> solrObjects, List<SolrDocumentType> solrDocumentTypes) {
		Miscellaneous miscellaneous = new Miscellaneous();

		for (SolrDocumentType miscDocumentType : solrDocumentTypes) {
			List<SolrMiscellaneous> l;
			switch ((SolrMiscellaneousDocumentType) miscDocumentType) {

			case TAXONOMY:
				l = getAssociatedSolrTerms(solrObjects, SolrMiscellaneousDocumentType.TAXONOMY);
				if (l.size() > 0) {
					mapTaxonomy(l.get(0), miscellaneous);
				}
				break;
			case STATS:
				l = getAssociatedSolrTerms(solrObjects, SolrMiscellaneousDocumentType.STATS);
				if (l.size() > 0) {
					mapCoOccurrenceStats(l.get(0), miscellaneous);
				}
				break;
			case SEQUENCE:
				l = getAssociatedSolrTerms(solrObjects, SolrMiscellaneousDocumentType.SEQUENCE);
				if (l.size() > 0){
					mapSequence(l.get(0), miscellaneous);
				}
				break;
			case PUBLICATION:
				l = getAssociatedSolrTerms(solrObjects, SolrMiscellaneousDocumentType.PUBLICATION);
				if (l.size() > 0) {
					mapPublication(l.get(0), miscellaneous);
				}
				break;
			case GUIDELINE:
				l = getAssociatedSolrTerms(solrObjects, SolrMiscellaneousDocumentType.GUIDELINE);
				if (l.size() > 0) {
					mapGuideline(l.get(0), miscellaneous);
				}
				break;
			case BLACKLIST:
				l = getAssociatedSolrTerms(solrObjects, SolrMiscellaneousDocumentType.BLACKLIST);
				if (l.size() > 0) {
					mapBlacklist(l.get(0), miscellaneous);
				}
				break;
			case EXTENSION:
				l = getAssociatedSolrTerms(solrObjects, SolrMiscellaneousDocumentType.EXTENSION);
				if (l.size() > 0) {
					mapExtensionRelation(l.get(0), miscellaneous);
				}
				break;
			case XREFDB:
				l = getAssociatedSolrTerms(solrObjects, SolrMiscellaneousDocumentType.XREFDB);
				if (l.size() > 0){
					mapXrefDatabase(l.get(0), miscellaneous);
				}
				break;
			case SUBSETCOUNT:
				l = getAssociatedSolrTerms(solrObjects, SolrMiscellaneousDocumentType.SUBSETCOUNT);
				if (l.size() > 0) {
					mapSubset(l.get(0), miscellaneous);
				}
				break;
			case EVIDENCE:
				l = getAssociatedSolrTerms(solrObjects, SolrMiscellaneousDocumentType.EVIDENCE);
				if (l.size() > 0) {
					mapEvidence(l.get(0), miscellaneous);
				}
				break;
			case  POSTPROCESSINGRULE:
				l = getAssociatedSolrTerms(solrObjects, SolrMiscellaneousDocumentType.POSTPROCESSINGRULE);
				if (l.size() > 0) {
					mapPostProcessingRule(l.get(0), miscellaneous);
				}
				break;
			}
		}
		return miscellaneous;		
	}

	/**
	 * Map taxonomy information
	 * @param solrMiscellaneous Solr taxonomy object
	 * @param miscellaneous Miscellaneous object built from the Solr information
	 */
	private void mapTaxonomy(SolrMiscellaneous solrMiscellaneous, Miscellaneous miscellaneous) {
		miscellaneous.setTaxonomyId(solrMiscellaneous.getTaxonomyId());
		miscellaneous.setTaxonomyName(solrMiscellaneous.getTaxonomyName());
		miscellaneous.setTaxonomyClosure(solrMiscellaneous.getTaxonomyClosures());
	}
	
	/**
	 * Map stats information
	 * @param solrMiscellaneous Solr stats object
	 * @param miscellaneous Miscellaneous object built from the Solr information
	 */
	private void mapCoOccurrenceStats(SolrMiscellaneous solrMiscellaneous, Miscellaneous miscellaneous) {
		miscellaneous.setAll(solrMiscellaneous.getAll());
		miscellaneous.setCompared(solrMiscellaneous.getCompared());
		miscellaneous.setComparedTerm(solrMiscellaneous.getComparedTerm());
		miscellaneous.setSelected(solrMiscellaneous.getSelected());
		miscellaneous.setStatsType(solrMiscellaneous.getStatsType());
		miscellaneous.setTerm(solrMiscellaneous.getTerm());
		miscellaneous.setTogether(solrMiscellaneous.getTogether());
	}

	/**
	 * Map sequences information
	 * @param solrMiscellaneous Solr sequence object
	 * @param miscellaneous Miscellaneous object built from the Solr information
	 */
	private void mapSequence(SolrMiscellaneous solrMiscellaneous, Miscellaneous miscellaneous) {
		miscellaneous.setDbObjectID(solrMiscellaneous.getDbObjectID());
		miscellaneous.setSequence(solrMiscellaneous.getSequence());		
	}

	/**
	 * Map publication information
	 * @param solrMiscellaneous Solr publication object
	 * @param miscellaneous Miscellaneos object built from the Solr information
	 */
	private void mapPublication(SolrMiscellaneous solrMiscellaneous, Miscellaneous miscellaneous) {		
		miscellaneous.setPublicationID(solrMiscellaneous.getPublicationID());
		miscellaneous.setPublicationTitle(solrMiscellaneous.getPublicationTitle());
	}
	
	/**
	 * Map annotation guideline information
	 * @param solrMiscellaneous Solr annotation guideline object
	 * @param miscellaneous Miscellaneous object built from the Solr information
	 */
	private void mapGuideline(SolrMiscellaneous solrMiscellaneous, Miscellaneous miscellaneous) {
		miscellaneous.setTerm(solrMiscellaneous.getTerm());
		miscellaneous.setGuidelineTitle(solrMiscellaneous.getGuidelineTitle());
		miscellaneous.setGuidelineURL(solrMiscellaneous.getGuidelineURL());		
	}
	
	/**
	 * Map blacklist information
	 * @param solrMiscellaneous Solr annotation blacklist
	 * @param miscellaneous Miscellaneous object built from the Solr information
	 */
	private void mapBlacklist(SolrMiscellaneous solrMiscellaneous, Miscellaneous miscellaneous) {
		miscellaneous.setDbObjectID(solrMiscellaneous.getDbObjectID());
		miscellaneous.setTaxonomyId(solrMiscellaneous.getTaxonomyId());
		miscellaneous.setTerm(solrMiscellaneous.getTerm());
		miscellaneous.setBacklistReason(solrMiscellaneous.getBacklistReason());
		miscellaneous.setBlacklistMethodID(solrMiscellaneous.getBlacklistMethodID());
		miscellaneous.setBacklistCategory(solrMiscellaneous.getBacklistCategory());
		miscellaneous.setBacklistEntryType(solrMiscellaneous.getBacklistEntryType());		
	}
	
	/**
	 * Map Annotation Extension Relation information
	 * @param solrMiscellaneous Solr annotation extension relation
	 * @param miscellaneous Miscellaneous object built from the Solr information 
	 */
	private void mapExtensionRelation(SolrMiscellaneous solrMiscellaneous, Miscellaneous miscellaneous) {
		miscellaneous.setAerDomain(solrMiscellaneous.getAerDomain());
		miscellaneous.setAerName(solrMiscellaneous.getAerName());
		miscellaneous.setAerParents(solrMiscellaneous.getAerParents());
		miscellaneous.setAerSecondaries(solrMiscellaneous.getAerSecondaries());
		miscellaneous.setAerSubsets(solrMiscellaneous.getAerSubsets());
		miscellaneous.setAerUsage(solrMiscellaneous.getAerUsage());
		miscellaneous.setAerRange(solrMiscellaneous.getAerRange());
	}
	
	/**
	 * Map xref databases information
	 * @param solrMiscellaneous Solr xref database
	 * @param miscellaneous Miscellaneous object with xref database information
	 */
	private void mapXrefDatabase(SolrMiscellaneous solrMiscellaneous, Miscellaneous miscellaneous) {
		miscellaneous.setXrefAbbreviation(solrMiscellaneous.getXrefAbbreviation());
		miscellaneous.setXrefDatabase(solrMiscellaneous.getXrefDatabase());
		miscellaneous.setXrefGenericURL(solrMiscellaneous.getXrefGenericURL());
		miscellaneous.setXrefUrlSyntax(solrMiscellaneous.getXrefUrlSyntax());
	}
	
	/**
	 * Map subsets counts information
	 * @param solrMiscellaneous Solr subset count
	 * @param miscellaneous Miscellaneous object with subsets information
	 */
	private void mapSubset(SolrMiscellaneous solrMiscellaneous, Miscellaneous miscellaneous) {
		miscellaneous.setSubset(solrMiscellaneous.getSubset());
		miscellaneous.setSubsetCount(solrMiscellaneous.getSubsetCount());
	}
	
	/**
	 * Map evidence information
	 * @param solrMiscellaneous Solr evidence
	 * @param miscellaneous Miscellaneous object with evidence type information
	 */
	private void mapEvidence(SolrMiscellaneous solrMiscellaneous, Miscellaneous miscellaneous) {
		miscellaneous.setEvidenceCode(solrMiscellaneous.getEvidenceCode());
		miscellaneous.setEvidenceName(solrMiscellaneous.getEvidenceName());
	}

	/**
	 * Map post processing rule information
	 * @param solrMiscellaneous Solr evidence
	 * @param miscellaneous Miscellaneous object with post processing type information
	 */
	private void mapPostProcessingRule(SolrMiscellaneous solrMiscellaneous, Miscellaneous miscellaneous) {
		miscellaneous.setPprAffectedTaxGroup(solrMiscellaneous.getPprAffectedTaxGroup());
		miscellaneous.setPprAncestorGoId(solrMiscellaneous.getPprAncestorGoId());
		miscellaneous.setPprAncestorTerm(solrMiscellaneous.getPprAncestorTerm());
		miscellaneous.setPprCleanupAction(solrMiscellaneous.getPprCleanupAction());
		miscellaneous.setPprCuratorNotes(solrMiscellaneous.getPprCuratorNotes());
		miscellaneous.setPprOriginalGoId(solrMiscellaneous.getPprOriginalGoId());
		miscellaneous.setPprOriginalTerm(solrMiscellaneous.getPprOriginalTerm());
		miscellaneous.setPprRelationship(solrMiscellaneous.getPprRelationship());
		miscellaneous.setPprRuleId(solrMiscellaneous.getPprRuleId());
		miscellaneous.setPprSubstitutedGoId(solrMiscellaneous.getPprSubstitutedGoId());
		miscellaneous.setPprTaxonName(solrMiscellaneous.getPprTaxonName());
	}
	
	/**
	 * Given a list of Solr terms, returns the ones that match with the
	 * specified document type
	 * 
	 * @param solrObjects
	 *            Solr miscellaneous objects
	 * @param solrMiscellaneousDocumentType
	 *            Type to check
	 * @return Solr miscellaneous information that match with the specified document type
	 */
	protected List<SolrMiscellaneous> getAssociatedSolrTerms(Collection<SolrMiscellaneous> solrObjects, SolrMiscellaneousDocumentType solrMiscellaneousDocumentType) {
		List<SolrMiscellaneous> solrMiscellaneousList = new ArrayList<>();
		for (SolrMiscellaneous solrMiscellaneous : solrObjects) {
			if (SolrMiscellaneousDocumentType.valueOf(solrMiscellaneous.getDocType().toUpperCase()) == solrMiscellaneousDocumentType) {
				solrMiscellaneousList.add(solrMiscellaneous);
			}
		}
		return solrMiscellaneousList;
	}
}
