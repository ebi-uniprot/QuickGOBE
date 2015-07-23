/**
 * 
 */
package uk.ac.ebi.quickgo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to represent a cross-reference to an object in some database
 * 
 * @author tonys
 * 
 */
public class XRef {
	private final static Pattern xrefPattern = Pattern.compile("^([^:\\s]+):(\\S+)$");
	private final static Matcher xrefMatcher = xrefPattern.matcher("");

	protected String db;
	protected String id;

	public XRef(String db, String id) {
		this.db = db;
		this.id = id;
	}

	public static XRef parse(String s) {
		xrefMatcher.reset(s);
		if (xrefMatcher.matches()) {
			return new XRef(xrefMatcher.group(1), xrefMatcher.group(2));
		}
		else {
			return null;
		}
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

	public String getXRef() {
		return db + ":" + id;
	}

	@Override
	public String toString() {
		return "XRef{" + "db='" + db + '\'' + ", id='" + id + '\'' + '}';
	}
}
