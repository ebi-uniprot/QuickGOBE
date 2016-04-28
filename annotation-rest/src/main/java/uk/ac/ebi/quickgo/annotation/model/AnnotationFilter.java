package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelper;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * A data structure for the annotation filtering parameters passed in from the client.
 * Here are the list of parameters filtering will require. The values shown are the ones the FE currently uses
 * Nearly all the parameters can take multiple values, separated by commas. These types are named as plural.
 * Exceptions exists however
 *
 * @author Tony Wardell
 * Date: 25/04/2016
 * Time: 11:23
 * Created with IntelliJ IDEA.
 */
public class AnnotationFilter {

    private static final int DEFAULT_ENTRIES_PER_PAGE = 25;
    private static final int DEFAULT_PAGE_NUMBER = 1;
    public static final int MAX_PAGE_RESULTS = 100;

    private List<String> taxon;               // Taxon ids                E.g. 1234,343434

    private List<String> gp;                  //  Gene Product ids;       E.g. A0A000,A0A001,..
    private List<String> gpSet;               //  Gene Product Sets       E.g. BHF-UCL,Exosome,..
    private List<String> gpType;              //  Gene Product Types      I.E protein,rna,.. (3 choices)

    //Choices of gpType
    private static Map<String, Object> gpTypeChoices  = Arrays.asList("proteins","RNAs","complexes").stream().collect
            (Collectors.toMap(Function.identity(),Function.identity()));

    //Choices of gpSets todo these values in the beta are NOT lowercased.. needs to be for the new version
    private static Map<String, Object> gpSetChoices  = Arrays.asList("bhf-ucl","exsome","kruk", "parkinsonsuk-ucl",
            "referencegenome").stream().collect(Collectors.toMap(Function.identity(),Function.identity()));

    //Go Terms
    private List<String> goTerm;              // Go Term ids              E.g. GO:0016021,GO:0016022,..
    private List<String> goTermSet;           // Go Term Set ids          E.g. goslim_chembl,goSlimGeneric,..

    //..the following 2 members are only applicable if goTerm ids or sets have been selected
    private String goTermUse;                 // Go Term use              I.e. ancestor or slim or exact (singular)
    private String goTermRelationship;        // Go Term relationship     I.e. I or IPO or IPOR (singular)

    private List<String> aspect;               //Aspect                    I.e. F, P or C (singular)

    private static Map<String, Object> aspectChoices  = Arrays.asList("c","f","p").stream()
            .collect(Collectors.toMap(Function.identity(),Function.identity()));

    private List<String> ecoEvidence;         //Eco evidence ids          E.g. ECO:12345, ECO:34535,..
    private List<String> goEvidence;          //Go evidence ids           E.g. IEA,..

    //..the following is only applicable if an evidence code has been requested
    private String evidenceRelationship; //Evidence relationships         I.e. ancestor or exact (singular)

    // Qualifier specifies the relationship between the annotated gene product and the GO term
    private List<String> qualifier;            //                          E.g enables, not_enables,..

    private List<String> reference;            //                          E.g. DOI,GO_REF,..

    private List<String> with;                 //                          E.g. AGI_LocusCode,CGD,..

    private List<String> assignedby;                 //                          E.g. ASPGD,Agbase,..

    //Non-data parameters
    private int limit = DEFAULT_ENTRIES_PER_PAGE;
    private int page = DEFAULT_PAGE_NUMBER;
    private List<String> facets;        //todo not required atm
    boolean highlighting = false;       //todo not required atm


    //Defaults for required values
    private GoTermUse DEFAULT_GO_TERM_USE=GoTermUse.ANCESTOR;
    private EvidenceRelationship DEFAULT_EVIDENCE_RELATIONSHIP = EvidenceRelationship.ANCESTOR;

    //todo @Autowired
    public ControllerValidationHelper validationHelper = new ControllerValidationHelperImpl(MAX_PAGE_RESULTS);


    /**
     * After filters have been loaded, ensure values are valid and defaults are in place.
     */
    public void validation() {

        //Some filters have a small number of potential values, defined by enums
        verifyField(gpType, gpTypeChoices, " is an invalid option for gene product type");
        verifyField(gpSet,  gpSetChoices,  " is an invalid option for gene product set.");
        verifyField(aspect, aspectChoices, " is an invalid option for aspect.");

        //If go terms or sets are entered, then use the default relationship if none is specified
        if(((goTerm != null && goTerm.size()>0) || (goTermSet !=null && goTermSet.size()>0))&& goTermUse==null){
            goTermUse = DEFAULT_GO_TERM_USE.toString();
        }

        //If eco codes or go evidence codes are entered, then use the default relationship if none is specified
        if(((ecoEvidence !=null && ecoEvidence.size()>0) || (goEvidence !=null && goEvidence.size()>0)) &&
        evidenceRelationship==null){
            evidenceRelationship = DEFAULT_EVIDENCE_RELATIONSHIP.toString();
        }

        validationHelper.validateRequestedResults(limit);
    }

    private void verifyField(List<String> args, Map<String, Object> choices, String message) {
        if(args!=null){
            args.stream()
                    .map(e -> {
                        if(!choices.containsKey(e.toLowerCase())){
                            throw new IllegalArgumentException(e + message);
                        }
                        return e;
                    });
            }
    }

    public void setTaxon(String taxon) {
        this.taxon = validationHelper.csvToList(taxon);
    }

    public void setGp(String gp) {
        this.gp =  validationHelper.csvToList(gp);
    }

    public void setGpSet(String gpSet) {
        this.gpSet =  validationHelper.csvToList(gpSet);
    }

    public void setGpType(String gpType) {
        this.gpType =  validationHelper.csvToList(gpType);
    }

    public void setGoTerm(String goTerm) {
        this.goTerm =  validationHelper.csvToList(goTerm);
    }

    public void setGoTermSet(String goTermSet) {
        this.goTermSet =  validationHelper.csvToList(goTermSet);
    }

    public void setGoTermUse(String goTermUse) {
        this.goTermUse = goTermUse;
    }

    public void setGoTermRelationship(String goTermRelationship) {
        this.goTermRelationship = goTermRelationship;
    }

    public void setAspect(String aspect) {
        this.aspect =  validationHelper.csvToList(aspect);
    }

    public void setEcoEvidence(String ecoEvidence) {
        this.ecoEvidence =  validationHelper.csvToList(ecoEvidence);
    }

    public void setGoEvidence(String goEvidence) {
        this.goEvidence =  validationHelper.csvToList(goEvidence);
    }

    public void setEvidenceRelationship(String evidenceRelationship) {
        this.evidenceRelationship = evidenceRelationship;
    }

    public void setQualifier(String qualifier) {
        this.qualifier =  validationHelper.csvToList(qualifier);
    }

    public void setReference(String reference) {
        this.reference =  validationHelper.csvToList(reference);
    }

    public void setWith(String with) {
        this.with =  validationHelper.csvToList(with);
    }

    public void setAssignedby(String assignedby) {
        this.assignedby =  validationHelper.csvToList(assignedby);
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List<String> getTaxon() {
        return taxon;
    }

    public List<String> getGp() {
        return gp;
    }

    public List<String> getGpSet() {
        return gpSet;
    }

    public List<String> getGpType() {
        return gpType;
    }

    public List<String> getGoTerm() {
        return goTerm;
    }

    public List<String> getGoTermSet() {
        return goTermSet;
    }

    public String getGoTermUse() {
        return goTermUse;
    }

    public String getGoTermRelationship() {
        return goTermRelationship;
    }

    public List<String> getAspect() {
        return aspect;
    }

    public List<String> getEcoEvidence() {
        return ecoEvidence;
    }

    public List<String> getGoEvidence() {
        return goEvidence;
    }

    public String getEvidenceRelationship() {
        return evidenceRelationship;
    }

    public List<String> getQualifier() {
        return qualifier;
    }

    public List<String> getReference() {
        return reference;
    }

    public List<String> getWith() {
        return with;
    }

    public List<String> getAssignedby() {
        return assignedby;
    }

    public int getLimit() {
        return limit;
    }

    public int getPage() {
        return page;
    }

    public enum GeneProductType{
        PROTEIN, RNA, COMPLEXES;
    }

    public enum  GoTermRelationship {
        I,IPO,IPOR
    }

    public enum GoTermUse {
        ANCESTOR, SLIM, EXACT
    }

    public enum Aspect{
        F, P, C;
    }

    public enum EvidenceRelationship{
        ANCESTOR, EXACT;
    }
}
