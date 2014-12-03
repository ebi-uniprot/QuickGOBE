package uk.ac.ebi.quickgo.web.util.annotation;

import javax.servlet.http.HttpServletResponse;

/**
 * Interface for annotation WS util methods
 * @author cbonill
 *
 */
public interface AnnotationWSUtil {

	public AnnotationColumn[] mapColumns(String cols);

	public void downloadAnnotations(String format, boolean gzip, String query,
			AnnotationColumn[] columns, int limit,
			HttpServletResponse httpServletResponse);
}
