package uk.ac.ebi.quickgo.webservice.model;

import java.util.List;

/**
 * @Author Tony Wardell
 * Date: 16/06/2015
 * Time: 16:25
 * Created with IntelliJ IDEA.
 */
public class FilterJson {

	private String type;
	private List values;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List getValues() {
		return values;
	}

	public void setValues(List values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return "Filter{" +
				"type='" + type + '\'' +
				", value='" + values + '\'' +
				'}';
	}

}
