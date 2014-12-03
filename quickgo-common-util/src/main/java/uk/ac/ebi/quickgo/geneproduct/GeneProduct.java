package uk.ac.ebi.quickgo.geneproduct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.quickgo.render.JSONSerialise;
import uk.ac.ebi.quickgo.util.XRef;

/**
 * Gene Product model
 * 
 * @author cbonill
 * 
 */
public class GeneProduct implements JSONSerialise{

	public static final String INTACT_COMPLEX_ID = "(EBI-|ebi-)"; 
	
	// Basic information		
	private String db;
	private String dbObjectId;
	private String dbObjectSymbol;
	private String dbObjectName;
	private List<String> dbObjectSynonyms = new ArrayList<>();;
	private String dbObjectType;
	private int taxonId;

	// Cross references
	List<XRef> xRefs = new ArrayList<>();	
	
	// Properties
	List<GeneProductProperty> geneProductProperties = new ArrayList<>();
	
	// Extra info
	private String taxonName;
	
	public GeneProduct() {		
	}

	public GeneProduct(String db, String dbObjectId, String dbObjectSymbol,
			String dbObjectName, List<String> dbObjectSynonyms,
			String dbObjectType, int taxonId, List<GeneProductProperty> geneProductProperties) {
		super();
		this.db = db;
		this.dbObjectId = dbObjectId;
		this.dbObjectSymbol = dbObjectSymbol;
		this.dbObjectName = dbObjectName;
		this.dbObjectSynonyms = dbObjectSynonyms;
		this.dbObjectType = dbObjectType;
		this.taxonId = taxonId;
		this.geneProductProperties = geneProductProperties;
	}

	public class GeneProductProperty {

		// Other properties
		String propertyName;
		String propertyValue;

		public GeneProductProperty(String propertyName, String propertyValue) {

			this.propertyName = propertyName;
			this.propertyValue = propertyValue;
		}

		public String getPropertyName() {
			return propertyName;
		}

		public void setPropertyName(String propertyName) {
			this.propertyName = propertyName;
		}

		public String getPropertyValue() {
			return propertyValue;
		}

		public void setPropertyValue(String propertyValue) {
			this.propertyValue = propertyValue;
		}
	}
	
	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public String getDbObjectId() {
		return dbObjectId;
	}

	public void setDbObjectId(String dbObjectId) {
		this.dbObjectId = dbObjectId;
	}

	public String getDbObjectSymbol() {
		return dbObjectSymbol;
	}

	public void setDbObjectSymbol(String dbObjectSymbol) {
		this.dbObjectSymbol = dbObjectSymbol;
	}

	public String getDbObjectName() {
		return dbObjectName;
	}

	public void setDbObjectName(String dbObjectName) {
		this.dbObjectName = dbObjectName;
	}

	public List<String> getDbObjectSynonyms() {
		return dbObjectSynonyms;
	}

	public void setDbObjectSynonyms(List<String> dbObjectSynonyms) {
		this.dbObjectSynonyms = dbObjectSynonyms;
	}

	public String getDbObjectType() {
		return dbObjectType;
	}

	public void setDbObjectType(String dbObjectType) {
		this.dbObjectType = dbObjectType;
	}

	public int getTaxonId() {
		return taxonId;
	}

	public void setTaxonId(int taxonId) {
		this.taxonId = taxonId;
	}
		
	public List<XRef> getXRefs() {
		return xRefs;
	}

	public List<String> getXRefsAsString() {
		List<String> xrefsString = new ArrayList<>();		
		for(XRef xRef : xRefs){			
			xrefsString.add(xRef.getDb()+":"+xRef.getId());
		}
		return xrefsString;
	}
	
	public void setXRefs(List<XRef> xRefs) {
		this.xRefs = xRefs;
	}

	public List<GeneProductProperty> getGeneProductProperties() {
		return geneProductProperties;
	}

	public void setGeneProductProperties(
			List<GeneProductProperty> geneProductProperties) {
		this.geneProductProperties = geneProductProperties;
	}	
	
	public String getTaxonName() {
		return taxonName;
	}

	public void setTaxonName(String taxonName) {
		this.taxonName = taxonName;
	}

	/**
	 * Returns target_set property if any
	 */
	public String getTargetSet() {
		if (!this.geneProductProperties.isEmpty()) {
			for (GeneProductProperty geneProductProperty : this.geneProductProperties) {
				if (geneProductProperty.getPropertyName().equals("target_set")) {
					return geneProductProperty.getPropertyValue();
				}
			}
		}
		return "";
	}

	@Override
	public Map<String, Object> serialise() {
    	Map<String, Object> properties = new HashMap<String, Object>();
    	properties.put("db_object_id", this.getDbObjectId());
    	properties.put("description", this.getDbObjectName());
    	properties.put("db_object_name", this.getDbObjectSymbol());
    	properties.put("taxon_id", this.getTaxonId());
    	properties.put("taxon_name", this.getTaxonName());
    	
        return properties;
	}
}
