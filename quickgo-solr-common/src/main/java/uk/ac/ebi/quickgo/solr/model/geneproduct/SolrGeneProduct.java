package uk.ac.ebi.quickgo.solr.model.geneproduct;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

import uk.ac.ebi.quickgo.solr.model.SolrDocumentType;

/**
 * Class to represent Solr Gene Products
 * 
 * @author cbonill
 * 
 */
public class SolrGeneProduct {

	// Basic information
	@Field
	String docType;	
	@Field
	String db;
	@Field
	String dbObjectId;
	@Field
	String dbObjectSymbol;
	@Field
	String dbObjectName;
	@Field("dbObjectSynonym")
	List<String> dbObjectSynonyms;
	@Field
	String dbObjectType;
	@Field
	int taxonId;
	@Field
	// Cross references
	String xrefDb;
	@Field
	String xrefId;

	// Other properties
	@Field
	String propertyName;
	@Field
	String propertyValue;

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
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

	public String getXrefDb() {
		return xrefDb;
	}

	public void setXrefDb(String xrefDb) {
		this.xrefDb = xrefDb;
	}

	public String getXrefId() {
		return xrefId;
	}

	public void setXrefId(String xrefId) {
		this.xrefId = xrefId;
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
	
	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	/**
	 * Gene products documents types
	 * 
	 * @author cbonill
	 * 
	 */
	public enum SolrGeneProductDocumentType implements SolrDocumentType {

		GENEPRODUCT("geneproduct"),
		PROPERTY("property"),
		XREF("xref");		

		String value;

		private SolrGeneProductDocumentType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		/**
		 * Get values as SolrDocumentType objects
		 * 
		 * @return Enum values as SolrDocumentType objects
		 */
		public static List<SolrDocumentType> getAsInterfaces() {
			List<SolrDocumentType> documentTypes = new ArrayList<SolrDocumentType>();
			for (SolrDocumentType solrDocumentType : values()) {
				documentTypes.add(solrDocumentType);
			}
			return documentTypes;
		}

		/**
		 * Get value as SolrDocumentType object
		 * 
		 * @param solrGeneProductDocumentType
		 *            Value to convert
		 * @return Value as SolrDocumentType object
		 */
		public static SolrDocumentType getAsInterface(SolrGeneProductDocumentType solrGeneProductDocumentType) {
			return solrGeneProductDocumentType;
		}
	}
}