package uk.ac.ebi.quickgo.webservice.definitions;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Tony Wardell
 * Date: 22/06/2015
 * Time: 09:01
 * Created with IntelliJ IDEA.
 */
public enum FilterParameter {

	Exact("exact"),
	Ancestor("ancestor"),
	Slim("slim"),
	I("i"),
	IPO("ipo"),
	IPOR("ipor");

	private String lc;
	private static Map<String,FilterParameter> map = new HashMap<>();

	static {
		for(FilterParameter aParm:FilterParameter.values()){
			map.put(aParm.getLowerCase(),aParm);
		}
	}

	FilterParameter(String lowerCase) {
		this.lc = lowerCase;
	}

	public String getLowerCase() {
		return lc;
	}

	public static FilterParameter lookup(String value){
		return map.get(value);
	}
}
