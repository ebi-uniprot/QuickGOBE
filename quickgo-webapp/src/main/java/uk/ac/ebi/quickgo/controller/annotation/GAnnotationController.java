package uk.ac.ebi.quickgo.controller.annotation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.ebi.quickgo.service.annotation.parameter.AnnotationParameters;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.web.util.FileService;
import uk.ac.ebi.quickgo.web.util.annotation.AnnotationColumn;
import uk.ac.ebi.quickgo.web.util.annotation.AnnotationWSUtil;
import uk.ac.ebi.quickgo.web.util.annotation.AppliedFilterSet;
import uk.ac.ebi.quickgo.web.util.query.QueryProcessor;

/**
 * Controller to keep the functionality of the old version QuickGO GAnnotation
 * web service
 * 
 * @author cbonill
 * 
 */
@Controller
public class GAnnotationController {

	@Autowired
	QueryProcessor queryProcessor;
	
	@Autowired
	AnnotationWSUtil annotationWSUtil;
		
	@RequestMapping("/GAnnotation")
	public void filterAnnotations(
			@RequestParam(value = "format", required = false, defaultValue = "gpad") String format,
			@RequestParam(value = "limit", required = false, defaultValue = "1000") String limit,
			@RequestParam(value = "gz", required = false, defaultValue = "") String gz,
			@RequestParam(value = "goid", required = false, defaultValue = "") String goid,
			@RequestParam(value = "aspect", required = false, defaultValue = "") String aspect,
			@RequestParam(value = "relType", required = false, defaultValue = "") String relType,
			@RequestParam(value = "termUse", required = false, defaultValue = "") String termUse,
			@RequestParam(value = "evidence", required = false, defaultValue = "") String evidence,
			@RequestParam(value = "source", required = false, defaultValue = "") String source,
			@RequestParam(value = "ref", required = false, defaultValue = "") String ref,
			@RequestParam(value = "with", required = false, defaultValue = "") String with,
			@RequestParam(value = "tax", required = false, defaultValue = "") String tax,
			@RequestParam(value = "protein", required = false, defaultValue = "") String protein,
			@RequestParam(value = "qualifier", required = false, defaultValue = "") String qualifier,
			@RequestParam(value = "db", required = false, defaultValue = "") String db,
			@RequestParam(value = "col", required = false, defaultValue = "") String cols,
			HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) {
		
		String query = "";
		boolean gzip = false;
		
		if(!format.trim().isEmpty()){
			query = query + "\"format\""  + ":\"" + format + "\",\"";
		}
		if(!limit.trim().isEmpty()){
			query = query + "\"limit\""  + ":\"" + limit + "\",\"";
		}
		if (httpServletRequest.getParameter("gz") != null) {
			gzip = true;
		}
		if(!goid.trim().isEmpty()){
			if(termUse.trim().isEmpty()){
				query = query + "\"" + AnnotationField.ANCESTORSIPO.getValue() + "\""  + ":\"" + goid + "\",\"";
			} else {
				if (termUse.equalsIgnoreCase("slim")) {
					if(!relType.trim().isEmpty()){
						switch(relType){
						case "=":
							query = query + "\"" + "\"goid\""  + "\""  + ":\"" + goid + "\",\"";
						case "I":
							query = query + "\"" + AnnotationField.ANCESTORSI.getValue() + "\""  + ":\"" + goid + "\",\"";
							break;
						case "?":						
						case "POI":
						case "IPO":
							query = query + "\"" + AnnotationField.ANCESTORSIPO.getValue() + "\""  + ":\"" + goid + "\",\"";
							break;
						case "RPOI":
						case "IRPO":
						case "POIR":
						case "PORI":
						case "IPOR":
							query = query + "\"" + AnnotationField.ANCESTORSIPOR.getValue() + "\""  + ":\"" + goid + "\",\"";
							break;
						}
					}					
				} else { 
					query = query + "\"" + AnnotationField.ANCESTORSIPO.getValue() + "\"" + ":\"" + goid + "\",\"";
				}
			}
		}
		if(!aspect.trim().isEmpty()){
			query = query + "\"aspect\""  + ":\"" + aspect + "\",\"";
		}
		if(!evidence.trim().isEmpty()){
			query = query + "\"evidence\""  + ":\"" + evidence + "\",\"";
		}
		if(!source.trim().isEmpty()){
			query = query + "\"source\""  + ":\"" + source + "\",\"";
		}
		if(!ref.trim().isEmpty()){
			query = query + "\"ref\""  + ":\"" + ref + "\",\"";
		}
		if(!with.trim().isEmpty()){
			query = query + "\"with\""  + ":\"" + with + "\",\"";
		}
		if(!tax.trim().isEmpty()){
			query = query + "\"tax\""  + ":\"" + tax + "\",\"";
		}
		if(!protein.trim().isEmpty()){
			query = query + "\"protein\""  + ":\"" + protein + "\",\"";
		}
		if(!qualifier.trim().isEmpty()){
			query = query + "\"qualifier\""  + ":\"" + qualifier + "\",\"";
		}
		if(!db.trim().isEmpty()){
			query = query + "\"db\""  + ":\"" + db + "\",\"";
		}	
		
		AnnotationParameters annotationParameters = new AnnotationParameters();
		
		queryProcessor.processQuery(query, annotationParameters, new AppliedFilterSet(), false);				 		
		
		String solrQuery = annotationParameters.toSolrQuery();
		
		// Get columns to display
		AnnotationColumn[] columns = { AnnotationColumn.DATABASE, AnnotationColumn.PROTEIN, AnnotationColumn.GOID};
		if (format.equals(FileService.FILE_FORMAT.TSV.getValue()) && (cols != null && !cols.trim().isEmpty())) {
			columns = annotationWSUtil.mapColumns(cols);
		}
		
		// Download file
		annotationWSUtil.downloadAnnotations(format, gzip, solrQuery, columns, Integer.valueOf(limit), httpServletResponse);
	}	
}
