/**
 * 
 */
package uk.ac.ebi.quickgo.ff.load;

import uk.ac.ebi.quickgo.ff.CV;
import uk.ac.ebi.quickgo.ff.ColourList;
import uk.ac.ebi.quickgo.ff.ontology.OntologySourceFiles;
import uk.ac.ebi.quickgo.model.ontology.generic.*;
import uk.ac.ebi.quickgo.model.ontology.go.TaxonConstraintSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.ac.ebi.quickgo.ff.ontology.OntologySourceFiles.*;


/**
 * Class that represents a generic (OBOFoundry-style) ontology
 * 
 * @author tonys
 *
 */
public abstract class GenericOntology implements ITermContainer {
	public String namespace;
	public Map<String, GenericTerm> terms = new HashMap<>();
    public Map<String, GenericTerm> xrefFind = new HashMap<>();
    public Map<String, GenericTermSet> subsets = new HashMap<>();
	public TaxonConstraintSet taxonConstraints = new TaxonConstraintSet();
	public TermOntologyHistory history = new TermOntologyHistory();

	public CV fundingBodies;

	public Map<String, TermCredit> termCredits = new HashMap<String, TermCredit>();
	
    public String version;
    public String timestamp;
    public String url;

    public GenericOntology(String namespace) {
    	this.namespace = namespace;
    }

    public void load(OntologySourceFiles sourceFiles, String rootId) throws Exception {
    	if (sourceFiles.terms != null) {
            for (String[] row : sourceFiles.terms.reader(OntologySourceFiles.ETerm.TERM_ID, OntologySourceFiles.ETerm.NAME, OntologySourceFiles.ETerm.IS_OBSOLETE)) {
                addTerm(new GenericTerm(row[0], row[1], row[2]));
            }
    	}

    	if (sourceFiles.relations != null) {
        	for (String[] row : sourceFiles.relations.reader(OntologySourceFiles.ETermRelation.CHILD_ID, OntologySourceFiles.ETermRelation.PARENT_ID, ETermRelation.RELATION_TYPE)) {
                if (rootId.equals(row[1])) {
                	continue;
                }
                GenericTerm child = getTerm(row[0]);
                GenericTerm parent = getTerm(row[1]);
                if (child == null || parent == null) {
                	continue;
                }
                TermRelation tr = new TermRelation(child, parent, row[2].intern());
                child.parents.add(tr);
                parent.children.add(tr);
            }

        	for (String id  : terms.keySet()) {
    			terms.get(id).getAncestors();
            }
    	}

    	if (sourceFiles.synonyms != null) {
            for (String[] row :sourceFiles.synonyms.reader(ETermSynonym.TERM_ID, ETermSynonym.NAME, ETermSynonym.TYPE)) {
                GenericTerm term = getTerm(row[0]);
                if (term != null) {
                	term.synonyms.add(new Synonym(row[2], row[1]));
                }
            }
    	}

    	if (sourceFiles.definitions != null) {
            for (String[] row : sourceFiles.definitions.reader(ETermDefinition.TERM_ID, ETermDefinition.DEFINITION)) {
                GenericTerm term = getTerm(row[0]);
                if (term != null) {
                	term.setDefinition(row[1]);
                }
            }
    	}

    	if (sourceFiles.comments != null) {
            for (String[] row : sourceFiles.comments.reader(ETermComment.TERM_ID, ETermComment.COMMENT_TEXT)) {
                GenericTerm term = getTerm(row[0]);
                if (term != null) {
    	            term.setComment(row[1]);
                }
            }
    	}
        
        if (sourceFiles.subsets != null) {
            ColourList colour = new ColourList(0x00000000);

            for (String[] row :sourceFiles.subsets.reader(ETermSubset.TERM_ID, ETermSubset.SUBSET, ETermSubset.TYPE)) {
                GenericTerm term = getTerm(row[0]);
                if (term == null) {
                	continue;
                }
    	        if ("SLIM".equals(row[2])) {
                    GenericTermSet subset = subsets.get(row[1]);
                    if (subset == null) {
                    	subsets.put(row[1], subset = new GenericTermSet(this, row[1], colour.getColourCode(subsets.size())));
                    }

                    term.subsets.add(subset);
                    subset.add(term);
    	        }
    	        else if ("QCCK".equals(row[2])) {
    	        	term.addQCCheck(row[1]);
    	        }
            }
        }
 
        if (sourceFiles.xrefs != null) {
            for (String[] row : sourceFiles.xrefs.reader(ETermXref.TERM_ID, ETermXref.DB_CODE, ETermXref.DB_ID, ETermXref.NAME)) {
    			GenericTerm term = getTerm(row[0]);
                if (term == null) {
                	continue;
                }

                NamedXRef ref = new NamedXRef(row[1], row[2], row[3]);
                if (GenericTerm.REPLACED_BY.equals(row[1]) || GenericTerm.CONSIDER.equals(row[1])) {
                    GenericTerm obsolete = getTerm(row[2]);
                    if (obsolete != null) {
    	                TermRelation tr = new TermRelation(obsolete, term, row[1]);
                        obsolete.replacements.add(tr);
                        term.replaces.add(tr);
                    }
                }
                else if (namespace.equals(row[1])) {
                    // ignore self-referential cross-references
                }
                else if (GenericTerm.ALT_ID.equals(row[1])) {
                    term.altIds.add(ref);
                    xrefFind.put(row[2], term);
                }
                else {
                    term.xrefs.add(ref);
                }
            }
        }

        if (sourceFiles.definitionXrefs != null) {
            for (String[] row : sourceFiles.definitionXrefs.reader(ETermDefinitionXref.TERM_ID, ETermDefinitionXref.DB_CODE, ETermDefinitionXref.DB_ID)) {
    			GenericTerm term = getTerm(row[0]);
                if (term != null) {
                    term.definitionXrefs.add(new XRef(row[1], row[2]));
                }
            }
        }

        if (sourceFiles.crossOntologyRelations != null) {
            for (String[] row : sourceFiles.crossOntologyRelations.reader(ECrossOntologyRelation.TERM_ID, ECrossOntologyRelation.RELATION, ECrossOntologyRelation.FOREIGN_NAMESPACE, ECrossOntologyRelation.FOREIGN_ID, ECrossOntologyRelation.FOREIGN_TERM, ECrossOntologyRelation.URL)) {
                GenericTerm term = getTerm(row[0]);
                if (term != null) {
                    term.addCrossOntologyRelation(row[1], row[2], row[3], row[4], row[5]);
                }
            }
        }

        if (sourceFiles.fundingBodies != null) {
    	    fundingBodies = new CV(sourceFiles.fundingBodies.reader(EFundingBody.CODE, EFundingBody.URL));
        }

        if (sourceFiles.credits != null) {
    	    for (String[] row : sourceFiles.credits.reader(ETermCredit.TERM_ID, ETermCredit.CREDIT_CODE)) {
    		    GenericTerm term = getTerm(row[0]);
    		    if (term != null) {
    			    TermCredit credit = termCredits.get(row[1]);
    			    if (credit == null) {
    				    CV.Item fundingBody = fundingBodies.get(row[1]);
    				    credit = new TermCredit(row[1], (fundingBody != null ? fundingBody.description : null));
    				    termCredits.put(row[1], credit);
    			    }
    			    term.addCredit(credit);
    		    }
    	    }
        }

        if (sourceFiles.history != null) {
    	    for (String[] row : sourceFiles.history.reader(ETermHistory.TERM_ID,  ETermHistory.NAME, ETermHistory.TIMESTAMP, ETermHistory.ACTION, ETermHistory.CATEGORY, ETermHistory.TEXT)) {
    		    AuditRecord ar = new AuditRecord(row[0], row[1], row[2], row[3], row[4], row[5]);
    		    history.add(ar);

    		    GenericTerm term = getTerm(row[0]);
    			if (term != null) {
    				if (term.getId().equals(row[0])) {
    					term.addHistoryRecord(ar);
    				}
    			}
    	    }
        }
    }

    // implementation of ITermContainer interface
	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public int getTermCount() {
    	return terms.size();
	}

	@Override
	public List<GenericTerm> getTerms() {
		return new ArrayList<>(terms.values());
	}

	@Override
    public List<String> getTermIds() {
    	return new ArrayList<>(terms.keySet());
    }
	
	@Override
	public GenericTerm[] toArray() {
		return terms.values().toArray(new GenericTerm[terms.size()]);
	}
	
	@Override
	public GenericTerm getTerm(String id) {
    	GenericTerm t = terms.get(id);
    	if (t == null) {
    		t = xrefFind.get(id);
    	}
    	return t;
	}
	
	@Override
	public void addTerm(GenericTerm t) {
    	terms.put(t.getId(), t);
	}
}
