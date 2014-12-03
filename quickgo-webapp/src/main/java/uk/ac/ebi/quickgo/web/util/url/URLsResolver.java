package uk.ac.ebi.quickgo.web.util.url;

import java.util.List;

import uk.ac.ebi.quickgo.bean.annotation.AnnotationBean;
import uk.ac.ebi.quickgo.util.NamedXRef;
import uk.ac.ebi.quickgo.web.util.term.XRefBean;

/**
 * Useful class to retrieve external databases URLS and set values
 * 
 * @author cbonill
 * 
 */

public interface URLsResolver {

	/**
	 * Set databases URL for a given annotation
	 * @param annotationBean Annotation
	 */
	public void setURLs(AnnotationBean annotationBean);

	/**
	 * Calculate generic URL for a list of cross-references
	 * @param xRefs Cross-references
	 * @return Cross-references generic URLs
	 */
	public List<XRefBean> calculateXrefsUrls(List<NamedXRef> xRefs);
}