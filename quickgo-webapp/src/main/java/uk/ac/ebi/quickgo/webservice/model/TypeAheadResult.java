package uk.ac.ebi.quickgo.webservice.model;

/**
 * @Author Tony Wardell
 * Date: 09/07/2015
 * Time: 11:05
 * Created with IntelliJ IDEA.
 */
public class TypeAheadResult {

	private String key;
	private String value;
	private String type;

	public TypeAheadResult(String key, String value, SearchResultType searchResultType) {
		this.key = key;
		this.value = value;
		this.type = searchResultType.toString();
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public String getType() {
		return type;
	}
}
