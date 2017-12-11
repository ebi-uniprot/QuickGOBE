package uk.ac.ebi.quickgo.ff.loader.ontology;

import uk.ac.ebi.quickgo.ff.files.ontology.OntologySourceFiles;
import uk.ac.ebi.quickgo.ff.loader.SourceInfoLoader;
import uk.ac.ebi.quickgo.ff.phaseout.ColourList;
import uk.ac.ebi.quickgo.model.ontology.generic.*;

import static uk.ac.ebi.quickgo.ff.files.ontology.OntologySourceFiles.*;

/**
 * This class specialises {@link SourceInfoLoader} to one responsible for loading ontology information
 * and creating new instances of ontology models.
 *
 * Created 10/12/15
 * @author Edd
 */
public abstract class AbstractGenericOLoader<S extends OntologySourceFiles, T extends GenericOntology>
        extends SourceInfoLoader<S, T> {

    AbstractGenericOLoader(S sources) {
        super(sources);
    }

    protected T createWithGenericOInfo(String nameSpace, String optionalRootTermId) throws Exception {
        T genericOntology = getInstance();
        genericOntology.namespace = nameSpace;

        if (sourceFiles.terms != null) {
            createTerms(genericOntology);
        }

        if (sourceFiles.relations != null) {
            createAncestors(optionalRootTermId, genericOntology);
        }

        if (sourceFiles.synonyms != null) {
            createSynonyms(genericOntology);
        }

        if (sourceFiles.definitions != null) {
            createDefinitions(genericOntology);
        }

        if (sourceFiles.comments != null) {
            createComments(genericOntology);
        }

        if (sourceFiles.subsets != null) {
            createSubsets(genericOntology);
        }

        if (sourceFiles.xrefs != null) {
            createCrossReferences(genericOntology);
        }

        if (sourceFiles.definitionXrefs != null) {
            createDefinitionCrossReferences(genericOntology);
        }

        if (sourceFiles.crossOntologyRelations != null) {
            createCrossOntologyRelations(genericOntology);
        }

        if (sourceFiles.fundingBodies != null) {
            genericOntology.fundingBodies = new CV(sourceFiles.fundingBodies
                    .reader(EFundingBody.CODE, EFundingBody.URL));
        }

        if (sourceFiles.credits != null) {
            createCredits(genericOntology);
        }

        if (sourceFiles.history != null) {
            createHistory(genericOntology);
        }
        return genericOntology;
    }

    private void createHistory(T genericOntology) throws Exception {
        for (String[] row : sourceFiles.history
                .reader(ETermHistory.TERM_ID,
                        ETermHistory.NAME,
                        ETermHistory.TIMESTAMP,
                        ETermHistory.ACTION,
                        ETermHistory.CATEGORY,
                        ETermHistory.TEXT)) {
            AuditRecord ar = new AuditRecord(row[0], row[1], row[2], row[3], row[4], row[5]);
            genericOntology.history.add(ar);

            GenericTerm term = genericOntology.getTerm(row[0]);
            if (term != null) {
                if (term.getId().equals(row[0])) {
                    term.addHistoryRecord(ar);
                }
            }
        }
    }

    private void createCredits(T genericOntology) throws Exception {
        for (String[] row : sourceFiles.credits.reader(
                ETermCredit.TERM_ID, ETermCredit.CREDIT_CODE)) {
            GenericTerm term = genericOntology.getTerm(row[0]);

            if (term != null) {
                TermCredit credit = genericOntology.termCredits.get(row[1]);

                if (credit == null) {
                    CV.Item fundingBody = genericOntology.fundingBodies.get(row[1]);
                    credit = new TermCredit(row[1], (fundingBody != null ? fundingBody.description : null));
                    genericOntology.termCredits.put(row[1], credit);
                }
                term.addCredit(credit);
            }
        }
    }

    private void createCrossOntologyRelations(T genericOntology) throws Exception {
        for (String[] row : sourceFiles.crossOntologyRelations.reader(
                ECrossOntologyRelation.TERM_ID,
                ECrossOntologyRelation.RELATION,
                ECrossOntologyRelation.FOREIGN_NAMESPACE,
                ECrossOntologyRelation.FOREIGN_ID,
                ECrossOntologyRelation.FOREIGN_TERM,
                ECrossOntologyRelation.URL)) {
            GenericTerm term = genericOntology.getTerm(row[0]);

            if (term != null) {
                term.addCrossOntologyRelation(row[1], row[2], row[3], row[4], row[5]);
            }
        }
    }

    private void createDefinitionCrossReferences(T genericOntology) throws Exception {
        for (String[] row : sourceFiles.definitionXrefs.reader(
                ETermDefinitionXref.TERM_ID,
                ETermDefinitionXref.DB_CODE,
                ETermDefinitionXref.DB_ID)) {
            GenericTerm term = genericOntology.getTerm(row[0]);

            if (term != null) {
                term.definitionXrefs.add(new XRef(row[1], row[2]));
            }
        }
    }

    private void createCrossReferences(T genericOntology) throws Exception {
        for (String[] row : sourceFiles.xrefs.reader(
                ETermXref.TERM_ID,
                ETermXref.DB_CODE,
                ETermXref.DB_ID,
                ETermXref.NAME)) {
            GenericTerm term = genericOntology.getTerm(row[0]);

            if (term == null) {
                continue;
            }

            NamedXRef ref = new NamedXRef(row[1], row[2], row[3]);

            if (GenericTerm.REPLACED_BY.equals(row[1]) || GenericTerm.CONSIDER.equals(row[1])) {
                GenericTerm obsolete = genericOntology.getTerm(row[2]);
                if (obsolete != null) {
                    TermRelation tr = new TermRelation(obsolete, term, row[1]);
                    obsolete.replacements.add(tr);
                    term.replaces.add(tr);
                }
            } else if (genericOntology.namespace.equals(row[1])) {
                // ignore self-referential cross-references
            } else if (GenericTerm.ALT_ID.equals(row[1])) {
                term.altIds.add(ref);
                genericOntology.xrefFind.put(row[2], term);
            } else {
                term.xrefs.add(ref);
            }
        }
    }

    private void createSubsets(T genericOntology) throws Exception {
        ColourList colour = new ColourList(0x00000000);

        for (String[] row : sourceFiles.subsets.reader(
                ETermSubset.TERM_ID,
                ETermSubset.SUBSET,
                ETermSubset.TYPE)) {
            GenericTerm term = genericOntology.getTerm(row[0]);

            if (term == null) {
                continue;
            }

            if ("SLIM".equals(row[2])) {
                GenericTermSet subset = genericOntology.subsets.get(row[1]);
                if (subset == null) {
                    genericOntology.subsets.put(row[1], subset = new GenericTermSet(genericOntology, row[1],
                            colour.getColourCode(genericOntology.subsets.size())));
                }

                term.subsets.add(subset);
                subset.add(term);
            } else if ("QCCK".equals(row[2])) {
                term.addQCCheck(row[1]);
            }
        }
    }

    private void createComments(T genericOntology) throws Exception {
        for (String[] row : sourceFiles.comments.reader(ETermComment.TERM_ID, ETermComment.COMMENT_TEXT)) {
            GenericTerm term = genericOntology.getTerm(row[0]);

            if (term != null) {
                term.setComment(row[1]);
            }
        }
    }

    private void createDefinitions(T genericOntology) throws Exception {
        for (String[] row : sourceFiles.definitions.reader(ETermDefinition.TERM_ID, ETermDefinition.DEFINITION)) {
            GenericTerm term = genericOntology.getTerm(row[0]);

            if (term != null) {
                term.setDefinition(row[1]);
            }
        }
    }

    private void createSynonyms(T genericOntology) throws Exception {
        for (String[] row : sourceFiles.synonyms.reader(
                ETermSynonym.TERM_ID,
                ETermSynonym.NAME,
                ETermSynonym.TYPE)) {
            GenericTerm term = genericOntology.getTerm(row[0]);

            if (term != null) {
                term.synonyms.add(new Synonym(row[2], row[1]));
            }
        }
    }

    private void createAncestors(String optionalRootTermId, T genericOntology) throws Exception {
        for (String[] row : sourceFiles.relations.reader(
                ETermRelation.CHILD_ID,
                ETermRelation.PARENT_ID,
                ETermRelation.RELATION_TYPE)) {

            if (row[1].equals(optionalRootTermId)) {
                continue;
            }

            GenericTerm child = genericOntology.getTerm(row[0]);
            GenericTerm parent = genericOntology.getTerm(row[1]);

            if (child == null || parent == null) {
                continue;
            }

            TermRelation tr = new TermRelation(child, parent, row[2].intern());
            child.parents.add(tr);
            parent.children.add(tr);
        }

        for (String id : genericOntology.terms.keySet()) {
            genericOntology.terms.get(id).getAncestors();
        }
    }

    private void createTerms(T genericOntology) throws Exception {
        for (String[] row : sourceFiles.terms.reader(
                ETerm.TERM_ID,
                ETerm.NAME,
                ETerm.IS_OBSOLETE)) {
            genericOntology.addTerm(new GenericTerm(row[0], row[1], row[2]));
        }
    }
}
