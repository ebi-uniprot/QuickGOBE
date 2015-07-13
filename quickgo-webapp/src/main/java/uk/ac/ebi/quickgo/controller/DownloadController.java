package uk.ac.ebi.quickgo.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.ebi.quickgo.service.annotation.AnnotationService;
import uk.ac.ebi.quickgo.service.annotation.parameter.AnnotationParameters;
import uk.ac.ebi.quickgo.web.util.FileService;
import uk.ac.ebi.quickgo.web.util.annotation.AnnotationColumn;
import uk.ac.ebi.quickgo.web.util.annotation.AppliedFilterSet;

/**
 * Controller for the download of annotations
 * @author cbonill
 *
 */

@Controller
public class DownloadController {

	@Autowired
	AnnotationService annotationService;

	@Autowired
	FileService fileService;

	private static final Logger logger = LoggerFactory.getLogger(DownloadController.class);

	@RequestMapping(value = { "/", "annotation" }, method = {RequestMethod.POST, RequestMethod.GET }, params = { "format" })
	public void download(
			@RequestParam(value = "format", defaultValue = "tab") String format,
			HttpSession session, HttpServletResponse httpServletResponse) {

		// Get columns to display
		AnnotationColumn[] visibleColumns = (AnnotationColumn[]) session.getAttribute("visibleAnnotationsColumns");

		// Get applied filters and convert them into Solr query
		AnnotationParameters annotationParameters = new AnnotationParameters();
		AppliedFilterSet appliedFilterSet = (AppliedFilterSet) session.getAttribute("appliedFilters");
		annotationParameters.setParameters(appliedFilterSet.getParameters());
		String query = annotationParameters.toSolrQuery();

		// Get total number annotations
		long totalAnnotations = annotationService.getTotalNumberAnnotations(query);
		int numResults = 10000; //TODO Add parameter in request to specify limit

		// Check file format
		StringBuffer sb = null;
		try {
			switch (FileService.FILE_FORMAT.valueOf(format.toUpperCase())) {
			case TSV:
				sb = fileService.generateTSVfile(FileService.FILE_FORMAT.TSV, "", query, totalAnnotations, visibleColumns, numResults);
				break;
			case FASTA:
				sb = fileService.generateFastafile(query, totalAnnotations, numResults);
				break;
			case GAF:
				sb = fileService.generateGAFFile(query, totalAnnotations, numResults);
				break;
			case GPAD:
				sb = fileService.generateGPADFile(query, totalAnnotations, numResults);
				break;
			case PROTEINLIST:
				sb = fileService.generateProteinListFile(query, totalAnnotations, numResults);
				break;
			case GENE2GO:
				sb = fileService.generateGene2GoFile(query, totalAnnotations, 10000);
				break;
			case JSON:
				sb = fileService.generateJsonFile(query, totalAnnotations, numResults);
				break;
			}
			InputStream in = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
			IOUtils.copy(in, httpServletResponse.getOutputStream());

			// Set response header and content
			httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			httpServletResponse.setHeader("Content-Disposition","attachment; filename=annotations." + format);
			httpServletResponse.setContentLength(sb.length());

			httpServletResponse.flushBuffer();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
}
