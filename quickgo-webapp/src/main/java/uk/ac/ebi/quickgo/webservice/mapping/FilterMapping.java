package uk.ac.ebi.quickgo.webservice.mapping;

import org.apache.commons.lang3.StringUtils;
import uk.ac.ebi.quickgo.solr.query.model.annotation.enums.AnnotationField;
import uk.ac.ebi.quickgo.webservice.model.FilterRequest;

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


	public FilterMapping(AnnotationField solrField) {
		this.solrField = solrField;
	}

	/**
	 * Return the completed solr query for this mapping.
	 * Escape colons in the argument list so they don't conlflict with the required colon.
	 * todo - why doesn't the api for solr do this automatically?
 	 */

	//
	public String toSolrQuery(){

		if(args.size()<=0) return "";
		String argList = StringUtils.join(args.toArray(), " OR ");
		return(solrField.getValue() + ":(" +  (argList.toString()).replaceAll(":","\\\\:") + ")");
	}

	public abstract void processRequestObject(FilterRequest filterRequest);

}
