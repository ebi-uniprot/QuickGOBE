package uk.ac.ebi.quickgo.annotation.model;

import java.util.List;

/**
 * Annotation DTO used by the service layer.
 *
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

    public int taxonId;

    public String assignedBy;

    public List<String> extensions;
}
