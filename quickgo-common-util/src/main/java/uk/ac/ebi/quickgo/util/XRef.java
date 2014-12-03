/**
 * 
 */
package uk.ac.ebi.quickgo.util;

/**
 * Class to represent a cross-reference to an object in some database
 * 
 * @author tonys
 * 
 */
public class XRef {
	protected String db;
	protected String id;

	public XRef(String db, String id) {
		this.db = db;
		this.id = id;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}	

	@Override
	public String toString() {
		return "XRef{" + "db='" + db + '\'' + ", id='" + id + '\'' + '}';
	}
}
