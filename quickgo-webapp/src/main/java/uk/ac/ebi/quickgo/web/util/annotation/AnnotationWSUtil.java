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

	public void downloadAnnotationsInternal(String format, String query,
									AnnotationColumn[] columns, int limit,
									int start, int row,
									HttpServletResponse httpServletResponse);

	void downloadTerm(String termId, HttpServletResponse httpServletResponse);

	void downloadOntologyGraph(String termId, HttpServletResponse httpServletResponse);

	public void downloadPredefinedSlims(HttpServletResponse httpServletResponse);

	void downloadPredefinedSetTerms(HttpServletResponse httpServletResponse, String setName);

	void downloadAnnotationUpdates(HttpServletResponse httpServletResponse);

	void downloadGoTermHistory(HttpServletResponse httpServletResponse, String from, String to, String limit);

	void downloadTaxonConstraints(HttpServletResponse httpServletResponse);

	void downloadAnnotationBlacklist(HttpServletResponse httpServletResponse);

	void getAnnotationPostProcessing(HttpServletResponse httpServletResponse);

	void downloadEvidenceTypes(HttpServletResponse httpServletResponse);

	void downloadWithDBs(HttpServletResponse httpServletResponse);

	void downloadAssignedDBs(HttpServletResponse httpServletResponse);
}
