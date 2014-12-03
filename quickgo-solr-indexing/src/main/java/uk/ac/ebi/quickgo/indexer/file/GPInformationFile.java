/**
 * 
 */
package uk.ac.ebi.quickgo.indexer.file;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.ac.ebi.quickgo.data.SourceFiles.NamedFile;
import uk.ac.ebi.quickgo.geneproduct.GeneProduct;
import uk.ac.ebi.quickgo.geneproduct.GeneProduct.GeneProductProperty;
import uk.ac.ebi.quickgo.indexer.IIndexer;

/**
 * class to represent (and read on a row-by-row basis) a gp_information file
 * 
 * @author tonys
 * 
 */
public class GPInformationFile extends GPDataFile {
	// columns in GPI 1.1 format files:
	//
	// 0 DB
	private static final int COLUMN_DB = 0;
	// 1 DB_Object_ID
	private static final int COLUMN_DB_OBJECT_ID = 1;
	// 2 DB_Object_Symbol
	private static final int COLUMN_DB_OBJECT_SYMBOL = 2;
	// 3 DB_Object_Name
	private static final int COLUMN_DB_OBJECT_NAME = 3;
	// 4 DB_Object_Synonym
	private static final int COLUMN_DB_OBJECT_SYNONYM = 4;
	// 5 DB_Object_Type
	private static final int COLUMN_DB_OBJECT_TYPE = 5;
	// 6 Taxon
	private static final int COLUMN_TAXON = 6;
	// 7 Parent_Object_ID
	private static final int COLUMN_PARENT_OBJECT_ID = 7;
	// 8 DB_Xrefs
	private static final int COLUMN_DB_XREFS = 8;
	// 9 Properties
	private static final int COLUMN_PROPERTIES = 9;

	private static final int columnCount = 10;

	String namespace;

	public GPInformationFile(NamedFile f) throws Exception {
		super(f, columnCount, "gpi-version", "1.1");
		this.namespace = directives.get("namespace");
		// if (namespace == null || "".equals(namespace)) { //TODO Check this
		// throw new Exception("Namespace not defined in " + f.getName());
		// }
	}

	/**
	 * Builds a new Gene Product object using the columns information 
	 * @param geneProducts List of rows already read
	 * @param columns Columns values
	 * @throws Exception
	 */
	public GeneProduct calculateRow (String[] columns) throws Exception {
			
		if (columns[COLUMN_PARENT_OBJECT_ID] == null || columns[COLUMN_PARENT_OBJECT_ID].trim().isEmpty()){// Just index GPs with no parent		
			String db = columns[COLUMN_DB];
			String dbObjectId = columns[COLUMN_DB_OBJECT_ID];
			String dbObjectSymbol = columns[COLUMN_DB_OBJECT_SYMBOL];
			String dbObjectName = columns[COLUMN_DB_OBJECT_NAME];
			String[] synonyms = columns[COLUMN_DB_OBJECT_SYNONYM].split("\\|");
			
			String dbObjectType = columns[COLUMN_DB_OBJECT_TYPE];
			String taxonId = columns[COLUMN_TAXON];
			taxonId = taxonId.replace("taxon:", "");
			// currentRow.setpparentObjectID = columns[COLUMN_PARENT_OBJECT_ID];		
			
			if (taxonId.trim().isEmpty() || !taxonId.matches("^\\d*$")) {//Some lines in the InTact file don't contain taxonomy		    
				taxonId = "0";
			}		
			GeneProduct geneProduct = new GeneProduct(db, dbObjectId, dbObjectSymbol, dbObjectName, Arrays.asList(synonyms), dbObjectType, Integer.valueOf(taxonId), new ArrayList<GeneProductProperty>());
			
			String[] properties = columns[COLUMN_PROPERTIES].split("\\|");
			List<GeneProductProperty> geneProductProperties = new ArrayList<>();
			for (String property : properties) {		
				if(!property.trim().isEmpty()){
					GeneProductProperty geneProductProperty = geneProduct.new GeneProductProperty(property.split("=")[0], property.split("=")[1]);
					geneProductProperties.add(geneProductProperty);
				}
			}
			
			geneProduct.setGeneProductProperties(geneProductProperties);
			
			return geneProduct;
		}
		return null;
	}

	@Override
	public boolean index(IIndexer indexer, String[] columns) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}


}