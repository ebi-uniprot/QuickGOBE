package uk.ac.ebi.quickgo.solr.mapper.term;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.ebi.quickgo.ontology.generic.AuditRecord;
import uk.ac.ebi.quickgo.ontology.generic.CrossOntologyRelation;
import uk.ac.ebi.quickgo.ontology.generic.GenericTerm;
import uk.ac.ebi.quickgo.ontology.generic.Synonym;
import uk.ac.ebi.quickgo.ontology.generic.TermCredit;
import uk.ac.ebi.quickgo.ontology.generic.TermRelation;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.solr.mapper.SolrMapper;
import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm.SolrTermDocumentType;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTermComparator;
import uk.ac.ebi.quickgo.util.NamedXRef;
import uk.ac.ebi.quickgo.util.XRef;

/**
 * Converts a GenericTerm object into a Solr one
 * @author cbonill
 *
 */
public abstract class SolrTermMapper implements SolrMapper<GenericTerm, SolrTerm> {
	
	/**
	 * See {@link SolrMapper#toSolrObject(Object))}
	 */
	public Collection<SolrTerm> toSolrObject(GenericTerm genericObject) {
		return toSolrObject(genericObject, SolrTermDocumentType.getAsInterfaces());
	}
	
	/**
	 * See {@link SolrMapper#toSolrObject(Object, List)}
	 */
	public Collection<SolrTerm> toSolrObject(GenericTerm term, List<SolrDocumentType> solrDocumentTypes) {

		List<SolrTerm> solrTerms = new ArrayList<SolrTerm>();
		
		for (SolrDocumentType termDocumentType : solrDocumentTypes) {
			SolrTermDocumentType solrTermDocumentType = ((SolrTermDocumentType) termDocumentType);

			switch (solrTermDocumentType) {
			
			case TERM:
				solrTerms.add(mapBasicInformation(term));
				break;
			case HISTORY:
				solrTerms.addAll(mapHistory(term));
				break;
			case RELATION:
				solrTerms.addAll(mapRelation(term, term.children));
				break;
			case REPLACE:
				solrTerms.addAll(mapReplace(term));
				break;			
			case SYNONYM:
				solrTerms.addAll(mapSynonym(term));
				break;
			case XREF:
				solrTerms.addAll(mapXref(term));
				break;
			case ONTOLOGYRELATION:
				solrTerms.addAll(mapOntologyRelation(term));
				break;
			}			
		}
		mapSpecificFields(term, solrTerms, solrDocumentTypes);

		return solrTerms;
	}
	
	/**
	 * Map fields specific to the term type 
	 */
	public abstract void mapSpecificFields(GenericTerm term, List<SolrTerm> solrTerms, List<SolrDocumentType> solrDocumentTypes);

	/**
     * Map Solr Term for the basic information of a term
     * @param term GO Term
     * @return Solr Term to be indexed
     */
	private SolrTerm mapBasicInformation(GenericTerm term) {
		SolrTerm solrTerm = new SolrTerm();
		solrTerm.setDocType(SolrTermDocumentType.TERM.getValue());
		solrTerm.setId(term.getId());		
		solrTerm.setName(term.getName());		
		solrTerm.setObsolete(term.isObsolete());
		solrTerm.setComments(Arrays.asList(term.getComment()));
		solrTerm.setDefinitions(Arrays.asList(term.getDefinition()));
		solrTerm.setSubsets(term.getSubsetsNames());
		
		List<String> definitionXrefs = new ArrayList<>();
		for(XRef definitionXref : term.getDefinitionXrefs()){
			if(definitionXref.getDb().equalsIgnoreCase("PMID")){//TODO Only taking into account PMID at the moment
				definitionXrefs.add(definitionXref.getDb() + ":" + definitionXref.getId());
			}
		}
		solrTerm.setDefinitionXref(definitionXrefs);
		
		List<String> secondaryIds = new ArrayList<>();
		if (term.altIds != null) {
			for(XRef secondaryId : term.altIds){
				secondaryIds.add(secondaryId.getId());
			}
		}
		solrTerm.setSecondaryIds(secondaryIds);		
		
		// Extra fields for GO Terms
		if (term instanceof GOTerm) {
			solrTerm.setOntology(((GOTerm) term).getOntologyText());
			solrTerm.setUsage(((GOTerm) term).getUsage().text);
		}
		
		// Credits
		List<String> credits = new ArrayList<>();
		for(TermCredit termCredit : term.getCredits()){
			credits.add(termCredit.getCode() + "--" + termCredit.getUrl());
		}
		solrTerm.setCredits(credits);
		
		return solrTerm;
	}
	
	/**
     * Map Solr terms for external references of a GO term
     * @param term GO Term
     * @return SolR terms to be indexed
     */
	private Collection<SolrTerm> mapXref(GenericTerm term) {
		Collection<SolrTerm> solrTermXrefs = new ArrayList<SolrTerm>();
		for (NamedXRef goXref : term.xrefs) {
			SolrTerm solrTermXref = new SolrTerm();
			solrTermXref.setDocType(SolrTermDocumentType.XREF.getValue());
			solrTermXref.setId(term.getId());
			solrTermXref.setXrefDbCode(goXref.getDb());
			solrTermXref.setXrefDbId(goXref.getId());
			solrTermXref.setXrefName(goXref.getName());

			solrTermXrefs.add(solrTermXref);
		}
		return solrTermXrefs;
	}
	
	
	/**
     * Map Solr terms for replaced GO terms
     * @param term GO Term
     * @return Solr terms to be indexed
     */
	private Collection<SolrTerm> mapReplace(GenericTerm term) {
		Collection<SolrTerm> solrTermReplaces = new ArrayList<SolrTerm>();
		for (TermRelation goReplaced : term.replaces) {	
			SolrTerm solrTermReplace = new SolrTerm();
			solrTermReplace.setDocType(SolrTermDocumentType.REPLACE.getValue());
			solrTermReplace.setId(term.getId());
			solrTermReplace.setObsoleteId(goReplaced.child.getId());
			solrTermReplace.setReason(goReplaced.typeof.description);

			solrTermReplaces.add(solrTermReplace);
		}
		return solrTermReplaces;
	}
	
	/**
     * Map Solr terms for relations of a GO term
     * @param term GO Term
     * @param relations Type of relation (parents or children)
     * @return SolR terms to be indexed
     */
	private Collection<SolrTerm> mapRelation(GenericTerm term,
			List<TermRelation> relations) {
		Set<SolrTerm> solrTermRelations = new TreeSet<SolrTerm>(new SolrTermComparator());
		for (TermRelation genericTermRelation : relations) {
			SolrTerm solrTermRelation = new SolrTerm();
			solrTermRelation.setDocType(SolrTermDocumentType.RELATION.getValue());
			solrTermRelation.setChild(genericTermRelation.child.getId());
			solrTermRelation.setParent(genericTermRelation.parent.getId());
			solrTermRelation.setRelationType(genericTermRelation.typeof.code);

			solrTermRelations.add(solrTermRelation);
		}
		return solrTermRelations;
	}
	
	/**
     * Map Solr terms for synonyms of a GO term
     * @param term GO Term
     * @return SolR terms to be indexed
     */
	private Collection<SolrTerm> mapSynonym(GenericTerm term) {
		Collection<SolrTerm> solrTermSynonyms = new ArrayList<SolrTerm>();		
		for (Synonym genericTermSynonym : term.synonyms) {
			SolrTerm solrTermSynonym = new SolrTerm();
			solrTermSynonym.setId(term.getId());
			solrTermSynonym.setDocType(SolrTermDocumentType.SYNONYM.getValue());
			solrTermSynonym.setSynonymName(genericTermSynonym.name);
			solrTermSynonym.setSynonymType(genericTermSynonym.type);

			solrTermSynonyms.add(solrTermSynonym);
		}
		return solrTermSynonyms;
	}

	/**
     * Map Solr terms for change logs of a GO term
     * @param term GO Term
     * @return SolR terms to be indexed
     */
	private Collection<SolrTerm> mapHistory(GenericTerm term) {
		Collection<SolrTerm> solrTermHistories = new ArrayList<SolrTerm>();
		for (AuditRecord genericTermHistory : term.history.auditRecords) {
			SolrTerm solrTermHistory = new SolrTerm();
			solrTermHistory.setDocType(SolrTermDocumentType.HISTORY.getValue());
			solrTermHistory.setId(genericTermHistory.termID);
			solrTermHistory.setHistoryName(genericTermHistory.termName);
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = null;
			try {
				date = formatter.parse(genericTermHistory.timestamp);
			} catch (ParseException e) {				
				e.printStackTrace();
			}
			solrTermHistory.setHistoryTimeStamp(date);
			solrTermHistory.setHistoryAction(genericTermHistory.action.name());
			solrTermHistory.setHistoryCategory(genericTermHistory.category.name());
			solrTermHistory.setHistoryText(genericTermHistory.text);

			solrTermHistories.add(solrTermHistory);
		}
		return solrTermHistories;
	}
	
	/**
	 * Maps cross ontology relations information
	 * @param term Term to map the information from
	 * @return Solr terms to be indexed
	 */
	private Collection<SolrTerm> mapOntologyRelation(GenericTerm term) {
		Collection<SolrTerm> solrTermOntologyRelations = new ArrayList<SolrTerm>();
		for (CrossOntologyRelation crossOntologyRelation : term.getCrossOntologyRelations()) {
			SolrTerm solrTerm = new SolrTerm();
			solrTerm.setDocType(SolrTermDocumentType.ONTOLOGYRELATION.getValue());
			solrTerm.setId(term.getId());
			solrTerm.setCrossOntologyForeignId(crossOntologyRelation.getForeignID());
			solrTerm.setCrossOntologyForeignTerm(crossOntologyRelation.getForeignTerm());
			solrTerm.setCrossOntologyOtherNamespace(crossOntologyRelation.getOtherNamespace());
			solrTerm.setCrossOntologyRelation(crossOntologyRelation.getRelation());
			solrTerm.setCrossOntologyUrl(crossOntologyRelation.getUrl());
			solrTermOntologyRelations.add(solrTerm);
		}
		return solrTermOntologyRelations;
	}

}