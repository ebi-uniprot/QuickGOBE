/**
 * 
 */
package uk.ac.ebi.quickgo.ontology.generic;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import uk.ac.ebi.quickgo.ontology.eco.ECOTerm;
import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.render.JSONSerialise;
import uk.ac.ebi.quickgo.util.NamedXRef;
import uk.ac.ebi.quickgo.util.XRef;

/**
 * Class that represents a term in a GenericOntology
 * 
 * @author tonys
 *
 */
public class GenericTerm implements JSONSerialise,Comparable<GenericTerm> {
	public String id;
	public String name;
	public boolean isObsolete;
	
    public static final String ALT_ID = "ALT_ID";
    public static final String CONSIDER = "consider";
    public static final String REPLACED_BY = "replaced_by";
    
    public List<TermRelation> parents = new ArrayList<>();
    public List<TermRelation> children = new ArrayList<>();
    private List<TermRelation> ancestors;
    public List<Synonym> synonyms = new ArrayList<>();
    public List<NamedXRef> xrefs = new ArrayList<>();
    public List<XRef> definitionXrefs = new ArrayList<>();
    public List<XRef> altIds = new ArrayList<>();
    // in an 'obsoletes' replacement the parent is the replacing term and the child is the obsolete term
    public List<TermRelation> replacements = new ArrayList<>();
    public List<TermRelation> replaces = new ArrayList<>();
    public List<GenericTermSet> subsets = new ArrayList<>();
	public TermOntologyHistory history = new TermOntologyHistory();
	public List<TermCredit> credits = new ArrayList<>();
    public List<CrossOntologyRelation> crossOntologyRelations = new ArrayList<>();
    public String definition;
    public String comment;
    
    public Map<String, List<GenericTerm>> ancestry = new HashMap<>();
     
    public GenericTerm() {
	}

	public GenericTerm(String id, String name, String isObsolete) {
    	this.id = id;
    	this.name = name;
    	this.isObsolete = "Y".equals(isObsolete);
    }

	public void addHistoryRecord(AuditRecord ar) {
		history.add(ar);
	}

	public void addCredit(TermCredit credit) {
		credits.add(credit);
	}
	
    public void addCrossOntologyRelation(String relation, String foreignNamespace, String foreignID, String foreignTerm, String url) {
        crossOntologyRelations.add(new CrossOntologyRelation(relation, foreignNamespace, foreignID, foreignTerm, url));
    }

	public String secondaries() {
		StringBuilder ids = new StringBuilder();
		for (XRef x : altIds) {
			if (ids.length() > 0) {
				ids.append(", ");
			}
			ids.append(x.getId());
		}
		return ids.toString();
	}

    public boolean hasComment() {
        return comment != null;
    }

	public List<TermRelation> getChildren() {
		List<TermRelation> kids = new ArrayList<>();

		for (TermRelation tr : children) {
			if (tr.ofAnyType(EnumSet.of(RelationType.ISA, RelationType.PARTOF, RelationType.OCCURSIN))) {
				kids.add(tr);
			}
		}

		return kids;
	}

    public List<TermRelation> getAncestors() {
        if (ancestors == null) {
			Set<TermRelation> anc = new HashSet<>();
			anc.add(new TermRelation(this, this, RelationType.IDENTITY));
			for (TermRelation relation : parents) {
				for (TermRelation parent : relation.parent.getAncestors()) {
					TermRelation combined = TermRelation.combine(relation, parent);
					if (combined.typeof != RelationType.UNDEFINED) {
						anc.add(combined);
					}
				}
			}
			ancestors = new ArrayList<>(anc);
        }

        return ancestors;
    }

    public List<GenericTerm> getFilteredAncestors(EnumSet<RelationType> types) {
    	if (types == null) {
    		types = EnumSet.of(RelationType.UNDEFINED);
    	}

    	Set<GenericTerm> results = new HashSet<>();
        for (TermRelation relation : getAncestors()) {
            if (relation.ofAnyType(types)) {
	            results.add(relation.parent);
            }
        }
        return new ArrayList<>(results);
    }

    public List<GenericTerm> getSlimAncestors() {
        return getFilteredAncestors(EnumSet.of(RelationType.ISA, RelationType.PARTOF, RelationType.OCCURSIN));
    }

    public List<GenericTerm> getAllAncestors() {
        return getFilteredAncestors(null);
    }
    
    /**
     * return the set of terms that feature somewhere in this term's ancestry, as traversed over a specified set of relation type
     * 
     * @param relationCodes - a string containing codes that represent the relation types (cf {@link RelationType} for details)
     *                        examples:
     *                           relation types for is_a: "I="
     *                           relation types for is_a, part_of, occurs_in: "I=PO"
     *                           relation types for is_a, part_of, occurs_in & all regulates relations: "I=POR+-"
     *                           
     * @return the set of GenericTerm objects that are part of this term's ancestry
     */
    public List<GenericTerm> getAncestry(String relationCodes) {
    	// have we passed this way before? 
    	List<GenericTerm> anc = ancestry.get(relationCodes);
    	if (anc == null) {
    		// no, so calculate the set of ancestor terms...
    		anc = getFilteredAncestors((relationCodes != null && relationCodes.trim().length() > 0) ? RelationType.toSet(relationCodes) : TermRelation.defaultRelationTypes());
    		// ...and stash it away for future reference
    		ancestry.put(relationCodes, anc);
    	}
    	return anc;
    }

    /**
     * getAncestry version using Set of RelationType
     * @param relationTypes List of relation types
     * @return The set of GenericTerm objects that are part of this term's ancestry
     */
    public List<GenericTerm> getAncestry(EnumSet<RelationType> relationTypes) {
    	return this.getAncestry(RelationType.toCodes(relationTypes));
    }
    
    public BitSet getAncestors(GenericTerm[] terms, EnumSet<RelationType> relations) {
        BitSet results = new BitSet();
        List<GenericTerm> anc = getFilteredAncestors(relations);
        for (int i = 0; i < terms.length; i++) {
	        if (anc.contains(terms[i])) {
		        results.set(i);
	        }
        }
        return results;
    }

	public boolean hasAncestor(String id) {
		for (TermRelation r : ancestors) {
			if (id.equals(r.parent.getId())) {
				return true;
			}
		}
		return false;
	}

	public boolean hasAncestor(GenericTerm term) {
		for (TermRelation r : ancestors) {
			if (term == r.parent) {
				return true;
			}
		}
		return false;
	}

    public List<TermRelation> isa() {
        List<TermRelation> relations = new ArrayList<>();

	    for (TermRelation tr : parents) {
		    if (tr.ofType(RelationType.ISA)) {
			    relations.add(tr);
		    }
	    }

        return relations;
    }

    public List<TermRelation> otherParents() {
	    List<TermRelation> relations = new ArrayList<>();

		for (TermRelation tr : parents) {
			if (!tr.ofType(RelationType.ISA) && !tr.ofType(RelationType.OCCURSIN) && !tr.ofType(RelationType.HASPART)) {
				relations.add(tr);
			}
		}

        return relations;
    }

    public static Comparator<GenericTerm> ancestorComparator = new Comparator<GenericTerm>() {
        public int compare(GenericTerm t1, GenericTerm t2) {
            return t1.getAllAncestors().size()- t2.getAllAncestors().size();
        }
    };

	public ArrayList<String> xrefsText() {
	    ArrayList<String> list = new ArrayList<>();
	    for (XRef xr : xrefs) {
	        list.add(xr.getId());
	    }
		for (XRef xr : altIds) {
		    list.add(xr.getId());
		}
		for (TermRelation tr : replacements) {
		    list.add(tr.parent.getId());
		}
		for (TermRelation tr : replaces) {
		    list.add(tr.child.getId());
		}
	    return list;
	}

	public ArrayList<String> synonymText() {
	    ArrayList<String> list = new ArrayList<>();
	    for (Synonym s : synonyms) {
	        list.add(s.name);
	    }
	    return list;
	}

    public int compareTo(GenericTerm term) {
        return getId().compareTo(term.getId());
    }

	public boolean active() {
		return !isObsolete;
	}

	public ArrayList<GenericTerm> replacedBy() {
		ArrayList<GenericTerm> replacedBy = new ArrayList<>();
	    for (TermRelation r : replacements) {
		    if (r.ofType(RelationType.REPLACEDBY)) {
			    replacedBy.add(r.parent);
		    }
	    }
	    return replacedBy;
	}

	public ArrayList<GenericTerm> consider() {
		ArrayList<GenericTerm> replacedBy = new ArrayList<>();
	    for (TermRelation r : replacements) {
		    if (r.ofType(RelationType.CONSIDER)) {
			    replacedBy.add(r.parent);
		    }
	    }
	    return replacedBy;
	}

    @Override
    public String toString() {
        return getId();
    }

    public static class MinimalTermInfo {
		public String id;
		public String name;
	
		public MinimalTermInfo(String id, String name) {
			this.id = id;
			this.name = name;
		}
		
		public MinimalTermInfo(String id) {
			this.id = id;			
		}
	}

	/**
	 * get minimal information about the term
	 * 
	 * may be overridden in child classes to extend the set of data returned
	 * 
	 * @return a MinimalTermInfo object containing the id and name of the term
	 */
    public MinimalTermInfo getMinimalTermInfo() {
		return new MinimalTermInfo(id, name);
	}

	/**
     * See {@link JSONSerialise#serialise()}
     */
	public Map<String, Object> serialise() {
		Map<String, Object> map = new HashMap<>();
		map.put("id", id);
		map.put("name", name);
		map.put("obsolete", isObsolete);
		map.put("comment", comment);
		map.put("definition", definition);
		
	    Set<String> lineage = new HashSet<>();
	    for (TermRelation r : getAncestors()) {
		    if (r.typeof.polarity != RelationType.Polarity.NEGATIVE) {
				lineage.add(r.parent.getId());
		    }
	    }	    
		map.put("ancestors", lineage);	 
		return map;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isObsolete() {
		return isObsolete;
	}

	public void setObsolete(boolean isObsolete) {
		this.isObsolete = isObsolete;
	}

	public List<TermRelation> getParents() {
		return parents;
	}

	public void setParents(List<TermRelation> parents) {
		this.parents = parents;
	}

	public List<Synonym> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(List<Synonym> synonyms) {
		this.synonyms = synonyms;
	}

	public List<NamedXRef> getXrefs() {
		return xrefs;
	}

	public void setXrefs(List<NamedXRef> xrefs) {
		this.xrefs = xrefs;
	}

	public List<XRef> getAltIds() {
		return altIds;
	}

	public String getAltIdsString() {
		List<String> altIdsString = new ArrayList<>();
		for (XRef xRef : this.altIds) {
			altIdsString.add(xRef.getId());
		}
		return StringUtils.arrayToDelimitedString(altIdsString.toArray(), ", ");
	}
	
	public void setAltIds(List<XRef> altIds) {
		this.altIds = altIds;
	}

	public List<TermRelation> getReplacements() {
		return replacements;
	}

	public void setReplacements(List<TermRelation> replacements) {
		this.replacements = replacements;
	}

	public List<TermRelation> getReplaces() {
		return replaces;
	}

	public void setReplaces(List<TermRelation> replaces) {
		this.replaces = replaces;
	}

	public List<GenericTermSet> getSubsets() {
		return subsets;
	}

	public List<String> getSubsetsNames() {
		List<String> subSetNames = new ArrayList<>();
		for(GenericTermSet genericTermSet : this.subsets){
			subSetNames.add(genericTermSet.name);
		}
		return subSetNames;
	}
	
	public void setSubsets(List<GenericTermSet> subsets) {
		this.subsets = subsets;
	}

	public TermOntologyHistory getHistory() {
		return history;
	}

	public void setHistory(TermOntologyHistory history) {
		this.history = history;
	}

	public List<TermCredit> getCredits() {
		return credits;
	}

	public void setCredits(List<TermCredit> credits) {
		this.credits = credits;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}	
	
	public List<XRef> getDefinitionXrefs() {
		return definitionXrefs;
	}

	public void setDefinitionXrefs(List<XRef> definitionXrefs) {
		this.definitionXrefs = definitionXrefs;
	}

	public static Comparator<GenericTerm> getAncestorComparator() {
		return ancestorComparator;
	}

	public static void setAncestorComparator(
			Comparator<GenericTerm> ancestorComparator) {
		GenericTerm.ancestorComparator = ancestorComparator;
	}

	public static String getAltId() {
		return ALT_ID;
	}

	public static String getConsider() {
		return CONSIDER;
	}

	public static String getReplacedBy() {
		return REPLACED_BY;
	}

	public void setChildren(List<TermRelation> children) {
		this.children = children;
	}

	public void setAncestors(List<TermRelation> ancestors) {
		this.ancestors = ancestors;
	}

	public int getCode() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void addQCCheck(String s) {
		// default is a no-op, as this will mean different things for different types of term
	}
	
	public boolean isGOTerm(){
		return this.getId().startsWith(GOTerm.GO);
	}
	
	public boolean isECOTerm(){
		return this.getId().startsWith(ECOTerm.ECO);
	}

	public List<CrossOntologyRelation> getCrossOntologyRelations() {
		return crossOntologyRelations;
	}

	public void setCrossOntologyRelations(
			List<CrossOntologyRelation> crossOntologyRelations) {
		this.crossOntologyRelations = crossOntologyRelations;
	}	
}
