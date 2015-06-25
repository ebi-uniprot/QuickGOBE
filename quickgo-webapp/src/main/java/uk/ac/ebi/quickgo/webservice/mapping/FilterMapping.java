package uk.ac.ebi.quickgo.webservice.mapping;

import org.apache.commons.lang3.StringUtils;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.webservice.model.FilterRequestJson;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Tony Wardell
 * Date: 25/06/2015
 * Time: 13:44
 * Created with IntelliJ IDEA.
 */
public abstract class FilterMapping {

	protected AnnotationField solrField = null;
	protected List<String> args = new ArrayList<>();
	public static final String GO_ID_REG_EXP = "(go:|GO:|gO:|Go:)";

	public String solrQueryFragment(){

		if(args.size()<=0) return "";

		String argList = StringUtils.join(args.toArray(), " OR ");
		return(solrField + ":(" +  (argList.toString()).replaceAll(Evidence.GO_ID_REG_EXP, "*").replaceAll(":","\\\\:"));

	}

	public abstract void processRequestObject(FilterRequestJson filterRequestJson);
}
