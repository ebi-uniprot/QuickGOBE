package uk.ac.ebi.quickgo.annotation.model;

import java.util.List;

/**
 * @author Tony Wardell
 *         Date: 21/04/2016
 *         Time: 11:28
 *         Created with IntelliJ IDEA.
 */
public class Annotation {

    public String id;

    public String geneProductId;

    public String qualifier;

    public String goId;

    public String goEvidence;

    public String ecoId;

    public String reference;

    public List<String> withFrom;

    public String taxonId;

    public String assignedBy;

    public String extension;
}
