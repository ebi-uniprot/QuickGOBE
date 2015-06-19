package uk.ac.ebi.quickgo.web.util.query.mapping;

import org.apache.commons.lang3.StringUtils;
import uk.ac.ebi.quickgo.web.util.query.mapping.FilterNameToSolrField;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Tony Wardell
 * Date: 17/06/2015
 * Time: 13:46
 * Holds filter argument values and mapping to use
 */
public class SingleFilter {

	public static final String GO_ID_REG_EXP = "(go:|GO:|gO:|Go:)";

	private FilterNameToSolrField designate = null;
	private List<String> args;

	public SingleFilter(FilterNameToSolrField designate){
		this.designate = designate;
		this.args = new ArrayList<>();
	}

	public void replace(FilterNameToSolrField designate){
		this.designate = designate;
	}


	/**
	 * If no values have been passed to this filter, return an empty String
	 * @return
	 */
	public String writeSolrQueryFragment(){

		if(args==null) return "";

		String argList = StringUtils.join(args.toArray(), " OR ");
		return designate.getSolrField().getValue()
				+ ":("
				+  (argList.toString()).replaceAll(GO_ID_REG_EXP, "*").replaceAll(":","\\\\:")
		        + ")";

	}

	public void addArg(String arg) {
		this.args.add(arg);
	}
}
