package uk.ac.ebi.quickgo.geneproduct.common;

import java.util.function.Supplier;

/**
 * An enumeration of the possible states of proteome membership a gene product can have.
 * @author Tony Wardell
 * Date: 06/03/2018
 * Time: 10:33
 * Created with IntelliJ IDEA.
 */
public enum ProteomeMembership {
    REFERENCE, COMPLETE, NONE, NOTAPPLICABLE;

    /**
     * Define the predicates required and order of importance to work out which Proteome membership category is
     * applicable.
     * @param isProtein is the gene product a protein
     * @param isReferenceProteome is the gene product a reference proteome
     * @param isComplete is the gene product a member of a complete proteome.
     * @return the ProteomeMembership matching the applied constraints
     */
    public static ProteomeMembership membership(Supplier<Boolean> isProtein, Supplier<Boolean> isReferenceProteome,
            Supplier<Boolean> isComplete) {

        if (!isProtein.get()) {
            return NOTAPPLICABLE;
        } else if (isReferenceProteome.get()) {
            return REFERENCE;
        } else if (isComplete.get()) {
            return COMPLETE;
        }
        return NONE;
    }
}
