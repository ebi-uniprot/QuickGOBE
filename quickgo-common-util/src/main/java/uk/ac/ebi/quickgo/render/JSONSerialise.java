package uk.ac.ebi.quickgo.render;

import java.util.Map;

public interface JSONSerialise {
    
	/**
	 * Return map with all the fields to serialise
	 * @return Map with all the fields to serialise
	 */
	public Map<String, Object> serialise();
}
