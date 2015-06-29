package uk.ac.ebi.quickgo.webservice.definitions;

import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Tony Wardell
 * Date: 18/06/2015
 * Time: 15:15
 * A list of the filters that come in from via the web service front end.
 */
public enum FilterRequest {

	//Limits the scope to annotations where the target (annotated gene product) is of the specified type(s)
	GeneProductType("gptype", WebServiceFilter.GeneProductType , WebServiceFilterType.ArgumentsAsValues , AnnotationField.DBOBJECTTYPE ),

	//Select annotations where the target has an identifier from the specified database.
	//Eg UniProtKB, Ensembl
	Database("db", WebServiceFilter.Database , WebServiceFilterType.ArgumentsAsValues , AnnotationField.DB ),

	//List of gene product identifiers
	//Eg P12345; Q4VCS5-1; URS000053207F_559292
	GeneProductID("gpid", WebServiceFilter.GeneProductId , WebServiceFilterType.ArgumentsAsValues , AnnotationField.DBOBJECTID ),

	Proteome("proteome", WebServiceFilter.GeneProductId , WebServiceFilterType.ArgumentsAsValues , AnnotationField.DBOBJECTID ),

	//A name of a set of gene products eg KRUK
	GeneProductSet("gpset", WebServiceFilter.GeneProductSet , WebServiceFilterType.ArgumentsAsValues , AnnotationField.TARGETSET ),

	// GoTerm - The default behaviour is to match the goIds to the AncestorsIPO list
	// If the exact goIds are to be matched, then goTermUse must be specified
	GoID("goid", WebServiceFilter.GoTerm,  WebServiceFilterType.ArgumentsAsValues, AnnotationField.ANCESTORSIPO),

	// GoTerm - GoTermUse - ancestor, slim or exact. Requires GoID to be specified.
	// One must be specified of course - no default
	GoTermUse("gotermuse", WebServiceFilter.GoTerm,  WebServiceFilterType.ArgumentAsBehaviour, null),

	// GoTerm - GoRelations. Requires GoID to be specified.
	// Determines which set of relations to traverse when calculating ancestry (or which closure field to search in Solr)
	// AncestorI, AncestorIPO, AncestorIPOR
	GoRelations("gorelations", WebServiceFilter.GoTerm, WebServiceFilterType.ArgumentAsBehaviour, null),

	// The name of a pre-defined GO slim set
	GoSlim("goslim", WebServiceFilter.GoSlim, WebServiceFilterType.ArgumentsAsValues, AnnotationField.SUBSET),

	// Evidence
	EcoId("ecoid", WebServiceFilter.EcoEvidence,  WebServiceFilterType.ArgumentsAsValues, AnnotationField.ECOANCESTORSI),
	EcoTermUse("ecotermuse", WebServiceFilter.EcoEvidence,  WebServiceFilterType.ArgumentAsBehaviour, null),

	Qualifier("qualifier", WebServiceFilter.Qualifier, WebServiceFilterType.ArgumentsAsValues, AnnotationField.QUALIFIER),

	Aspect("aspect", WebServiceFilter.Aspect, WebServiceFilterType.ArgumentsAsValues, AnnotationField.GOASPECT),

	Reference("reference", WebServiceFilter.Reference, WebServiceFilterType.ArgumentsAsValues, AnnotationField.REFERENCE),

	AssignedBy("assignedby", WebServiceFilter.AssignedBy,  WebServiceFilterType.ArgumentsAsValues, AnnotationField.ASSIGNEDBY),

	With("with", WebServiceFilter.With, WebServiceFilterType.ArgumentsAsValues, AnnotationField.WITH),

	Taxon("taxon", WebServiceFilter.Taxon,  WebServiceFilterType.ArgumentsAsValues, AnnotationField.TAXONOMYID) ;



	//Allow the enums to be looked up using their lowerCase value;
	private static Map<String, FilterRequest> map = new HashMap<>();

	static {

		for(FilterRequest name: FilterRequest.values()){
			map.put(name.getLowerCase(), name);

		}

	}


	private final WebServiceFilter wsFilter;
	private final String lc;
	private final WebServiceFilterType wsType;
	private final AnnotationField solrField;

	FilterRequest( String lowerCase, WebServiceFilter wsFilter, WebServiceFilterType wsType,
				  AnnotationField defaultSolr)  {
		this.lc = lowerCase;
		this.wsFilter = wsFilter;
		this.wsType = wsType;
		this.solrField=defaultSolr;
	}

	public String getLowerCase(){
		return lc;
	}

	public WebServiceFilter getWsFilter() {
		return wsFilter;
	}

	public WebServiceFilterType getWsType() {
		return wsType;
	}

	public static WebServiceFilter lookupWsFilter(String requestedFilter) {
		return map.get(requestedFilter.toLowerCase()).getWsFilter();
	}

	public static FilterRequest lookup(String filterLowerCase) {
		return map.get(filterLowerCase);
	}

	public AnnotationField getDefaultSolrField() {
		return this.solrField;
	}
}
