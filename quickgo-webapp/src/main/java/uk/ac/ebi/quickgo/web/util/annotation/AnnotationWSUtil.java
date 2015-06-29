package uk.ac.ebi.quickgo.web.util.annotation;

import uk.ac.ebi.quickgo.ontology.generic.ITermContainer;
import uk.ac.ebi.quickgo.webservice.model.TermJson;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

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

	public void downloadAnnotationsInternal(String query,
											AnnotationColumn[] columns, int limit,
											int start, int row,
											HttpServletResponse httpServletResponse, boolean b, ITermContainer slimTermSet);

	public void downloadAnnotationsFile(String query, int limit, HttpServletResponse httpServletResponse, boolean b,
										ITermContainer slimTermSet, String format);

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

	void downloadChartFullModel(HttpServletResponse httpServletResponse, String ids, String scope);

	void downloadAnnotationOntologyGraph(HttpServletResponse httpServletResponse, String termsIds, String relations, String requestType);

	void downloadStatistics(HttpServletResponse httpServletResponse, String query, String advancedFilter, String solrQuery);

	void downloadOntologyList(HttpServletResponse httpServletResponse, String ontology);

	List<String> goTermsForSlimSet(String slimSet);

//	void downloadSlim(HttpServletResponse httpServletResponse, String termsIds, String proteinIds,
//					  String proteinSets, int page, int rows);
}
