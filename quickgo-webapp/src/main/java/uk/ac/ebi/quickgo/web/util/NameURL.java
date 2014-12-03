package uk.ac.ebi.quickgo.web.util;

import java.io.Serializable;

/**
 * To represent text and URLs in the annotations table
 * 
 * @author cbonill
 * 
 */
public class NameURL implements Serializable{

	private static final long serialVersionUID = 3509073854585769533L;
	
	private String name;
	private String url;

	public NameURL(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
