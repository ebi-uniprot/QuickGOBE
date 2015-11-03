package uk.ac.ebi.quickgo.solr.mapper.term;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.quickgo.ontology.generic.*;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.ontology.go.GOTermSet;
import uk.ac.ebi.quickgo.ontology.go.GeneOntology;
import uk.ac.ebi.quickgo.solr.mapper.EntityMapper;
import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm;
import uk.ac.ebi.quickgo.solr.model.ontology.SolrTerm.SolrTermDocumentType;
import uk.ac.ebi.quickgo.util.NamedXRef;
import uk.ac.ebi.quickgo.util.XRef;

/**
 * Converts a SolrTerm into a GOTerm object
 *
 * @author cbonill
 */
public abstract class EntityTermMapper<T extends GenericTerm> implements EntityMapper<SolrTerm, T> {
    private static final Logger logger = LoggerFactory.getLogger(EntityTermMapper.class);

    @Override
    public T toEntityObject(Collection<SolrTerm> solrObjects) {
        return toEntityObject(solrObjects, SolrTermDocumentType.getAsInterfaces());
    }

    @Override
    public T toEntityObject(Collection<SolrTerm> solrObjects, List<SolrDocumentType> solrDocumentTypes) {
        T term = createEmptyTerm();

        for (SolrDocumentType termDocumentType : solrDocumentTypes) {
            SolrTermDocumentType solrTermDocumentType = ((SolrTermDocumentType) termDocumentType);

            switch (solrTermDocumentType) {
                case TERM:
                    getTermDocument(solrObjects).ifPresent(doc -> mapBasicInformation(doc, term));
                    break;
                case HISTORY:
                    mapHistory(getAssociatedSolrTerms(solrObjects, SolrTermDocumentType.HISTORY), term);
                    break;
                case RELATION:
                    mapRelation(getAssociatedSolrTerms(solrObjects, SolrTermDocumentType.RELATION), term);
                    break;
                case REPLACE:
                    mapReplaces(getAssociatedSolrTerms(solrObjects, SolrTermDocumentType.REPLACE), term);
                    break;
                case SYNONYM:
                    mapSynonyms(getAssociatedSolrTerms(solrObjects, SolrTermDocumentType.SYNONYM), term);
                    break;
                case XREF:
                    mapCrossReferences(getAssociatedSolrTerms(solrObjects, SolrTermDocumentType.XREF), term);
                    break;
            }
        }

        mapSpecificFields(term, solrObjects, solrDocumentTypes);

        return term;
    }

    public abstract void mapSpecificFields(T term, Collection<SolrTerm> solrObjects,
            List<SolrDocumentType> solrDocumentTypes);

    protected abstract T createEmptyTerm();

    /**
     * Map Solr Term for the basic information of a GO term
     *
     * @param solrTerm GO Term
     * @param term     Term to be indexed
     */
    protected void mapBasicInformation(SolrTerm solrTerm, T term) {
        term.setId(solrTerm.getId());
        term.setName(solrTerm.getName());
        term.setObsolete(solrTerm.isObsolete());
        try {
            String comment = solrTerm.getComment();

            if (!"".equals(comment)) {
                term.setComment(comment);
            }

            String definition = solrTerm.getDefinition();

            if (!"".equals(definition)) {
                term.setDefinition(definition);
            }

            List<XRef> definitionsXrefs = new ArrayList<>();

            if (solrTerm.getDefinitionXref() != null) {
                for (String definitionXref : solrTerm.getDefinitionXref()) {
                    String[] xrefDef = definitionXref.split(":");
                    definitionsXrefs.add(new XRef(xrefDef[0], xrefDef[1]));
                }
                term.setDefinitionXrefs(definitionsXrefs);
            }

            term.setAltIds(generateAltIds(solrTerm.getSecondaryIds()));

            // Credits
            List<TermCredit> credits = new ArrayList<>();
            if (solrTerm.getCredits() != null) {
                for (String credit : solrTerm.getCredits()) {
                    String[] creditDbURL = credit.split("--");
                    TermCredit termCredit = new TermCredit(creditDbURL[0], creditDbURL[1]);
                    credits.add(termCredit);
                }
                term.setCredits(credits);
            }
            // Map subsets
            mapSubsets(solrTerm, term);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Get Alternative ids information
     *
     * @param altIds Alternative ids strings
     * @return List of XRef objects
     */
    private List<XRef> generateAltIds(List<String> altIds) {
        List<XRef> refs = new ArrayList<>();
        if (altIds != null) {
            for (String altId : altIds) {
                refs.add(new XRef("", altId));// TODO Need db code?
            }
        }
        return refs;
    }

    /**
     * Map Change Logs information
     *
     * @param associatedSolrTerms Change Logs information
     * @param term                Term with the Change Logs information mapped
     */
    private void mapHistory(List<SolrTerm> associatedSolrTerms, T term) {
        List<AuditRecord> auditRecords = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        for (SolrTerm log : associatedSolrTerms) {
            AuditRecord auditRecord = new AuditRecord(log.getId(),
                    log.getHistoryName(), df.format(log.getHistoryTimeStamp()),
                    log.getHistoryAction(), log.getHistoryCategory(),
                    log.getHistoryText());
            auditRecords.add(auditRecord);
        }
        term.history.auditRecords.addAll(auditRecords);
    }

    /**
     * Map Relations information
     *
     * @param associatedSolrTerms Solr Relations terms
     * @param term                Term with Relations information mapped
     */
    private void mapRelation(List<SolrTerm> associatedSolrTerms, T term) {
        List<TermRelation> children = new ArrayList<>();
        List<TermRelation> parents = new ArrayList<>();
        for (SolrTerm relation : associatedSolrTerms) {
            GOTerm child = new GOTerm();
            child.setId(relation.getChild());

            GOTerm parent = new GOTerm();
            parent.setId(relation.getParent());

            TermRelation termRelation = new TermRelation(child, parent,
                    relation.getRelationType());
            if (parent.getId().equals(term.getId())) {
                children.add(termRelation);
            } else if (child.getId().equals(term.getId())) {
                parents.add(termRelation);
            }
        }
        term.setChildren(children);
        term.setParents(parents);
    }

    /**
     * Map Replaces information
     *
     * @param associatedSolrTerms Solr Replaces terms
     * @param term                Term with Replaces information mapped
     */
    private void mapReplaces(List<SolrTerm> associatedSolrTerms, T term) {
        List<TermRelation> replacedby = new ArrayList<>();
        List<TermRelation> replaces = new ArrayList<>();
        for (SolrTerm relation : associatedSolrTerms) {
            GOTerm obsolete = new GOTerm();
            obsolete.setId(relation.getObsoleteId());

            GOTerm parent = new GOTerm();
            parent.setId(relation.getId());

            TermRelation termRelation = new TermRelation(obsolete, parent,
                    relation.getReason());
            // Replaced by
            if (relation.getObsoleteId().equals(term.getId())) {
                replacedby.add(termRelation);
            } else { // Replaces
                replaces.add(termRelation);
            }
        }
        term.setReplaces(replaces);
        term.setReplacements(replacedby);
    }

    /**
     * Map Subsets information
     *
     * @param solrTerm Solr term
     * @param term     Term with Subsets information mapped
     */
    private void mapSubsets(SolrTerm solrTerm, T term) {
        List<GOTermSet> terms = new ArrayList<>();
        if (solrTerm.getSubsets() != null) {
            for (String subset : solrTerm.getSubsets()) {
                GOTermSet singleTerm = new GOTermSet(new GeneOntology(), subset);
                terms.add(singleTerm);
            }
        }
        term.subsets.addAll(terms);
    }

    /**
     * Map Synonyms information
     *
     * @param associatedSolrTerms Solr Synonim terms
     * @param term                Term with the Synonyms information mapped
     */
    private void mapSynonyms(List<SolrTerm> associatedSolrTerms, T term) {
        List<Synonym> synonyms = new ArrayList<>();
        for (SolrTerm synonym : associatedSolrTerms) {
            synonyms.add(new Synonym(synonym.getSynonymType(), synonym.getSynonymName()));
        }
        term.setSynonyms(synonyms);

        if (!associatedSolrTerms.isEmpty()) {
            if (term.getId() == null) {
                term.setId(associatedSolrTerms.get(0).getId());
            }

            if (term.getName() == null) {
                term.setName(associatedSolrTerms.get(0).getName());
            }
        }
    }

    /**
     * Map Cross References information
     *
     * @param associatedSolrTerms Solr Cross References terms
     * @param term                Term with Cross References information mapped
     */
    private void mapCrossReferences(List<SolrTerm> associatedSolrTerms, T term) {
        List<NamedXRef> xrefs = new ArrayList<>();
        for (SolrTerm xref : associatedSolrTerms) {
            NamedXRef ref = new NamedXRef(xref.getXrefDbCode(), xref.getXrefDbId(), xref.getXrefName());
            xrefs.add(ref);
        }
        term.setXrefs(xrefs);
    }

    /**
     * Given a list of Solr terms, returns the ones that match with the specified document type
     *
     * @param solrObjects          Solr term objects
     * @param solrTermDocumentType Type to check
     * @return Solr terms that match with the specified document type
     */
    protected List<SolrTerm> getAssociatedSolrTerms(Collection<SolrTerm> solrObjects,
            SolrTermDocumentType solrTermDocumentType) {
        List<SolrTerm> solrTerms = new ArrayList<>();
        for (SolrTerm solrTerm : solrObjects) {
            if (SolrTermDocumentType.valueOf(solrTerm.getDocType().toUpperCase()) == solrTermDocumentType) {
                solrTerms.add(solrTerm);
            }
        }
        return solrTerms;
    }

    protected Optional<SolrTerm> getTermDocument(Collection<SolrTerm> solrDocs) {
        return solrDocs.stream().filter(doc -> SolrTermDocumentType.valueOf(doc.getDocType().toUpperCase()) ==
                SolrTermDocumentType.TERM).findFirst();
    }
}
