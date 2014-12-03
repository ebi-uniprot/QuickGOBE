package uk.ac.ebi.quickgo.web.util;

import java.io.Serializable;

/**
 * Class to represent top taxonomies
 * 
 * @author cbonill
 * 
 */
public class TopTaxonomy implements Serializable{

	private static final long serialVersionUID = 3537735560324198938L;
	
	private long id;
	private String name;
	private long frequency;

	public TopTaxonomy() {
	}

	public TopTaxonomy(long id, String name, long frequency) {
		this.id = id;
		this.name = name;
		this.frequency = frequency;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getFrequency() {
		return frequency;
	}

	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}

}
