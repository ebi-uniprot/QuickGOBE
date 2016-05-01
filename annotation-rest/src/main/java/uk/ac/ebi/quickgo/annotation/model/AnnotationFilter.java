package uk.ac.ebi.quickgo.annotation.model;

import uk.ac.ebi.quickgo.annotation.common.document.AnnotationFields;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelper;
import uk.ac.ebi.quickgo.rest.controller.ControllerValidationHelperImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * A data structure for the annotation filtering parameters passed in from the client.
 * Here are the list of parameters filtering will require. The values shown are the ones the FE currently uses
 * Nearly all the parameters can take multiple values, separated by commas. These types are named as plural.
 * Exceptions exists however
 *
 * Once the comma separated values have been set, then turn then into an object (PrototypeFilter) that
 * encapsulates the list and solr field name to use for that argument.
 *
 * Consumers of this class will call the method {@link AnnotationFilter#requestConsumptionOfPrototypeFilters(Consumer)} ()}
 * to receive each PrototypeFilter at a time.
 *
 * @author Tony Wardell
 * Date: 25/04/2016
 * Time: 11:23
 * Created with IntelliJ IDEA.
 */
public class AnnotationFilter {

    public static final String DEFAULT_ENTRIES_PER_PAGE = "25";
    public static final String DEFAULT_PAGE_NUMBER = "1";
    public static final int MAX_PAGE_RESULTS = 100;
    private static final String COMMA = ",";



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

    //Non-data parameters
    private String limit = DEFAULT_ENTRIES_PER_PAGE;
    private String page = DEFAULT_PAGE_NUMBER;


    //Defaults for required values
    private static final String DEFAULT_GO_TERM_USE="Ancestor";
    private static final String DEFAULT_EVIDENCE_RELATIONSHIP ="Ancestor";

    private List<PrototypeFilter> prototypeFilters = new ArrayList<>();

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
            goTermUse = DEFAULT_GO_TERM_USE;
        }

        //If eco codes or go evidence codes are entered, then use the default relationship if none is specified
        if(((ecoEvidence !=null && ecoEvidence.size()>0) || (goEvidence !=null && goEvidence.size()>0)) &&
        evidenceRelationship==null){
            evidenceRelationship = DEFAULT_EVIDENCE_RELATIONSHIP;
        }

        validationHelper.validateRequestedResults(Integer.parseInt(limit));
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

    // E.g. ASPGD,Agbase,..
    public void setAssignedby(String assignedby) {

        //this.assignedby =  validationHelper.csvToList(assignedby);
        if (!isNullOrEmpty(assignedby)) {
            prototypeFilters.add(buildUsingArgument(AnnotationFields.ASSIGNED_BY, assignedby));
        }

    }

    public void setPage(String page) {
        this.page = page;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }


    public List<String> getQualifier() {
        return qualifier;
    }


    public List<String> getWith() {
        return with;
    }

    public String getLimit() {
        return limit;
    }

    public String getPage() {
        return page;
    }

    /**
     * Pass the prototypeFilters to a PrototypeFilter consumer
     */
    public void requestConsumptionOfPrototypeFilters(Consumer<PrototypeFilter> consumer){

        prototypeFilters.stream()
                .forEach(pr -> consumer.accept(pr));
    }


    /**
     * Create a prototype filter using the passed argument
     * @param solrName
     * @param argIncludingDelimiters
     * @return
     */

    public static final PrototypeFilter buildUsingArgument(String solrName, String argIncludingDelimiters){
        PrototypeFilter prototypeFilter = new PrototypeFilter();
        prototypeFilter.solrName = solrName;
        prototypeFilter.args = Arrays.asList(argIncludingDelimiters.split(COMMA));
        return prototypeFilter;
    }

    /**
     * Holds the content needed to be turned in to a QueryFilter
     */
    public static class PrototypeFilter {


        private String solrName;
        private List<String> args;

        public String getSolrName() {
            return solrName;
        }

        public List<String> getArgs() {
            return args;
        }
    }
}
