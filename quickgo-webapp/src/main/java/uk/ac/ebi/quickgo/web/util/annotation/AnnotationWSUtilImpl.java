package uk.ac.ebi.quickgo.web.util.annotation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import uk.ac.ebi.quickgo.ontology.go.GOTerm;
import uk.ac.ebi.quickgo.service.annotation.AnnotationService;
import uk.ac.ebi.quickgo.service.term.TermService;
import uk.ac.ebi.quickgo.web.util.FileService;
import uk.ac.ebi.quickgo.web.util.url.AnnotationTotal;

/**
 * Annotation WS util methods
 * @author cbonill
 *
 */
@Service
public class AnnotationWSUtilImpl implements AnnotationWSUtil{


	@Autowired
	AnnotationService annotationService;

	@Autowired
	TermService goTermService;

	@Autowired
	FileService fileService;

	private static final Logger logger = Logger.getLogger(AnnotationWSUtilImpl.class);

	/**
	 * Map GAnnotation columns to the *new* QuickGO ones
	 * @param cols Columns to map
	 * @return COlumns mapped
	 */
	public AnnotationColumn[] mapColumns(String cols) {
		List<AnnotationColumn> enumColumnsList = new ArrayList<>();
		String[] columns = cols.split(",");
		for(String col : columns){
			AnnotationColumn enumColumn = mapColumn(col);
			if (enumColumn != null){
				enumColumnsList.add(mapColumn(col));
			}
		}
		AnnotationColumn[] enumColumns = new AnnotationColumn[enumColumnsList.size()];
		return enumColumnsList.toArray(enumColumns);
	}

	/**
	 * Map a specific QuickGO *old* column to one of the *new* ones
	 * @param col
	 * @return
	 */
	private AnnotationColumn mapColumn(String col) {
		switch(col){
		case "protein_db":
		case "proteinDB":
			return AnnotationColumn.DATABASE;
		case "protein_id":
		case "proteinID":
			return AnnotationColumn.PROTEIN;
		case "protein_symbol":
		case "proteinSymbol":
			return AnnotationColumn.SYMBOL;
		case "qualifier":
			return AnnotationColumn.QUALIFIER;
		case "go_id":
		case "goID":
			return AnnotationColumn.GOID;
		case "go_name":
		case "goName":
			return AnnotationColumn.TERMNAME;
		case "aspect":
			return AnnotationColumn.ASPECT;
		case "evidence":
			return AnnotationColumn.EVIDENCE;
		case "ref":
			return AnnotationColumn.REFERENCE;
		case "with":
			return AnnotationColumn.WITH;
		case "protein_taxonomy":
		case "proteinTaxon":
			return AnnotationColumn.TAXON;
		case "date":
			return AnnotationColumn.DATE;
		case "from":
			return AnnotationColumn.ASSIGNEDBY;
		case "splice":
			return AnnotationColumn.EXTENSION;
		case "protein_name":
		case "proteinName":
			return AnnotationColumn.NAME;
		case "protein_synonym":
		case "proteinSynonym":
			return AnnotationColumn.SYNONYM;
		case "protein_type":
		case "proteinType":
			return AnnotationColumn.TYPE;
		case "protein_taxonomy_name":
		case "proteinTaxonName":
			return AnnotationColumn.TAXONNAME;
		case "original_term_id":
		case "originalTermID":
			return AnnotationColumn.ORIGINALTERMID;
		case "original_go_name":
		case "originalGOName":
			return AnnotationColumn.ORIGINALTERMNAME;
		}
		return null;
	}

	/**
	 * Generate a file with the annotations results
	 * @param format File format
	 * @param gzip
	 * @param query Query to run
	 * @param columns Columns to display
	 */
	public void downloadAnnotations(String format, boolean gzip, String query, AnnotationColumn[] columns, int limit,
									HttpServletResponse httpServletResponse){

		// Get total number annotations
		long totalAnnotations = annotationService.getTotalNumberAnnotations(query);

		// Check file format
		StringBuffer sb = null;
		try {
			switch (FileService.FILE_FORMAT.valueOf(format.toUpperCase())) {
			case TSV:
				sb = fileService.generateTSVfile(FileService.FILE_FORMAT.TSV, "", query, totalAnnotations, columns, limit);
				break;
			case FASTA:
				sb = fileService.generateFastafile(query, totalAnnotations, limit);
				break;
			case GAF:
				sb = fileService.generateGAFFile(query, totalAnnotations, limit);
				break;
			case GPAD:
				sb = fileService.generateGPADFile(query, totalAnnotations, limit);
				break;
			case PROTEINLIST:
				sb = fileService.generateProteinListFile(query, totalAnnotations, limit);
				break;
			case GENE2GO:
				sb = fileService.generateGene2GoFile(query, totalAnnotations, limit);
				break;
			case JSON:
				sb = fileService.generateJsonFile(query, totalAnnotations, limit );
				break;
			}

			String extension = format;
			if (gzip) {
				extension = extension + ".gz";
				httpServletResponse.setContentType("application/x-gzip");
				httpServletResponse.setHeader("Content-Disposition", "attachment; filename=annotations." + extension);
				OutputStream os = new GZIPOutputStream(httpServletResponse.getOutputStream());

				Writer wr = new OutputStreamWriter(os, "ASCII");
				wr.write(sb.toString());
				wr.close();
			} else {
				InputStream in = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
				IOUtils.copy(in, httpServletResponse.getOutputStream());
				// Set response header and content
				httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
				httpServletResponse.setHeader("Content-Disposition", "attachment; filename=annotations." + extension);
				httpServletResponse.setContentLength(sb.length());
				httpServletResponse.flushBuffer();
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	/**
	 * Generate a file with the annotations results
	 * @param format File format
	 * @param query Query to run
	 * @param columns Columns to display
	 */
	public void downloadAnnotationsInternal(String format, String query, AnnotationColumn[] columns, int limit,
									int start, int rows, HttpServletResponse httpServletResponse){

		// Get total number annotations
		long totalAnnotations = annotationService.getTotalNumberAnnotations(query);

		// Check file format
		StringBuffer sb = null;
		try {
			switch (FileService.FILE_FORMAT.valueOf(format.toUpperCase())) {
				case TSV:
					sb = fileService.generateTSVfile(FileService.FILE_FORMAT.TSV, "", query, totalAnnotations, columns, limit);
					break;
				case FASTA:
					sb = fileService.generateFastafile(query, totalAnnotations, limit);
					break;
				case GAF:
					sb = fileService.generateGAFFile(query, totalAnnotations, limit);
					break;
				case GPAD:
					sb = fileService.generateGPADFile(query, totalAnnotations, limit);
					break;
				case PROTEINLIST:
					sb = fileService.generateProteinListFile(query, totalAnnotations, limit);
					break;
				case GENE2GO:
					sb = fileService.generateGene2GoFile(query, totalAnnotations, limit);
					break;
				case JSON:
					sb = fileService.generateJsonFileWithPageAndRow(query, totalAnnotations, start, rows);
					break;
			}

			String extension = format;

			InputStream in = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
			IOUtils.copy(in, httpServletResponse.getOutputStream());
			// Set response header and content
			httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			httpServletResponse.setHeader("Content-Disposition", "attachment; filename=annotations." + extension);
			httpServletResponse.setContentLength(sb.length());
			httpServletResponse.flushBuffer();

		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}


	/**
	 * Generate a file with the annotations results
	 * @param query Query to run
	 */
	public void downloadAnnotationsTotalInternal( String query,HttpServletResponse httpServletResponse){

		// Get total number annotations
		long totalAnnotations = annotationService.getTotalNumberAnnotations(query);

		// Check file format
		StringBuffer sb = null;
		try {
			sb = fileService.generateJsonFileWithTotalAnnotations(totalAnnotations);

			InputStream in = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
			IOUtils.copy(in, httpServletResponse.getOutputStream());

			// Set response header and content
			httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			httpServletResponse.setHeader("Content-Disposition", "attachment; filename=annotations." + "json");
			httpServletResponse.setContentLength(sb.length());
			httpServletResponse.flushBuffer();

		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	@Override
	public void downloadTerm(String termId, HttpServletResponse httpServletResponse){
		StringBuffer sb = null;
		try {
			GOTerm term = goTermService.retrieveTerm(termId);

			sb = fileService.generateJsonFileForTerm(term);

			InputStream in = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
			IOUtils.copy(in, httpServletResponse.getOutputStream());

			// Set response header and content
			httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
			httpServletResponse.setHeader("Content-Disposition", "attachment; filename=annotations." + "json");
			httpServletResponse.setContentLength(sb.length());
			httpServletResponse.flushBuffer();

		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}
}
