package uk.ac.ebi.quickgo.ontology.generic;

import java.util.List;

/**
 * interface that can (should) be implemented by anything that contains a set of GenericTerm objects
 */
public interface ITermContainer {
	/**
	 * get the namespace (= ontology) to which the terms in the container belong
	 */
	public String getNamespace();
	/**
	 * get the number of terms in the container
	 */
	public int getTermCount();
	/**
	 * get the terms in the container as a List 
	 */
	public List<GenericTerm> getTerms();
	/**
	 * get the identifiers of the terms in the container
	 */
	public List<String> getTermIds();
	/**
	 * get the terms in the container as an array
	 */
	public GenericTerm[] toArray();
	/**
	 * get a term by its identifier
	 */
	public GenericTerm getTerm(String id);
	/**
	 * add a term to the container
	 */
	public void addTerm(GenericTerm t);
}
