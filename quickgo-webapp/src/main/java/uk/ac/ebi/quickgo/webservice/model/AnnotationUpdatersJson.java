package uk.ac.ebi.quickgo.webservice.model;

/**
 * @Author Tony Wardell
 * Date: 26/02/2015
 * Time: 16:34
 * Created with IntelliJ IDEA.
 */
public class AnnotationUpdatersJson {

	private String name;
	private long count;

	public AnnotationUpdatersJson(String name, long count) {
		this.name = name;
		this.count = count;
	}

	public String getName() {
		return name;
	}

	public long getCount() {
		return count;
	}
}
