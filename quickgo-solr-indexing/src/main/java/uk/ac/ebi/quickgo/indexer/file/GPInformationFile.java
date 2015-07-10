/**
 * 
 */
package uk.ac.ebi.quickgo.indexer.file;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.ebi.quickgo.data.SourceFiles.NamedFile;
import uk.ac.ebi.quickgo.geneproduct.GeneProduct;
import uk.ac.ebi.quickgo.indexer.IIndexer;
import uk.ac.ebi.quickgo.util.KeyValuePair;

/**
 * class to represent (and read on a row-by-row basis) a gp_information file
 * 
 * @author tonys
 * 
 */
public class GPInformationFile extends GPDataFile {
	// columns in GPI 1.2 format files:
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
	// 8 DB_Xrefs - this is currently not populated in the files that we process (or, if it is, we ignore it, as we get our cross-references from elsewhere)
	//private static final int COLUMN_DB_XREFS = 8;
	// 9 Properties
	private static final int COLUMN_PROPERTIES = 9;

	private static final int columnCount = 10;

	// regexp used in the extraction of taxon ID
	private final static Pattern taxonIDPattern = Pattern.compile("taxon:([0-9]+)");
	private final static Matcher taxonIDMatcher = taxonIDPattern.matcher("");

	public GPInformationFile(NamedFile f) throws Exception {
		super(f, columnCount, "gpi-version", "1.2");
	}

	/**
	 * Builds a new Gene Product object using the columns information 
	 * @param columns Columns values
	 * @throws Exception
	 */
	public GeneProduct calculateRow (String[] columns) throws Exception {
		if (columns[COLUMN_PARENT_OBJECT_ID] == null || columns[COLUMN_PARENT_OBJECT_ID].trim().isEmpty()) { // Just index GPs with no parent
			String db = columns[COLUMN_DB];
			String dbObjectId = columns[COLUMN_DB_OBJECT_ID];
			String dbObjectSymbol = columns[COLUMN_DB_OBJECT_SYMBOL];
			String dbObjectName = columns[COLUMN_DB_OBJECT_NAME];
			String[] synonyms = columns[COLUMN_DB_OBJECT_SYNONYM].split("\\|");
			
			String dbObjectType = columns[COLUMN_DB_OBJECT_TYPE];
			taxonIDMatcher.reset(columns[COLUMN_TAXON]);
			int taxonId = taxonIDMatcher.matches() ? Integer.valueOf(taxonIDMatcher.group(1)) : 0;

			String[] properties = columns[COLUMN_PROPERTIES].split("\\|");
			List<KeyValuePair> geneProductProperties = new ArrayList<>();
			for (String property : properties) {		
				if (!property.trim().isEmpty()) {
					String[] kv = property.split("=");
					if (kv.length == 2) {
						geneProductProperties.add(new KeyValuePair(kv[0], kv[1]));
					}
				}
			}
			
			return new GeneProduct(db, dbObjectId, dbObjectSymbol, dbObjectName, Arrays.asList(synonyms), dbObjectType, taxonId, geneProductProperties);
		}
		else {
			return null;
		}
	}

	@Override
	public boolean index(IIndexer indexer, String[] columns) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
}