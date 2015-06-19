package uk.ac.ebi.quickgo.webservice.definitions;

/**
 * @Author Tony Wardell
 * Date: 18/06/2015
 * Time: 15:15
 * Created with IntelliJ IDEA.
 */
public enum FilterRequestName {

	ecoID("ecoID"),
	ecoTermUse("ecoTermUse");

	String value;

	FilterRequestName(String value)  {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
