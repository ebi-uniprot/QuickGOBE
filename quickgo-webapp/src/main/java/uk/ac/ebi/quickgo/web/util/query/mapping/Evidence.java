package uk.ac.ebi.quickgo.web.util.query.mapping;


import org.apache.commons.lang3.StringUtils;
import uk.ac.ebi.quickgo.webservice.model.FilterJson;
import uk.ac.ebi.quickgo.webservice.model.FilterRequestJson;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Tony Wardell
 * Date: 19/06/2015
 * Time: 14:49
 * Created with IntelliJ IDEA.
 */
public class Evidence {
	private String solrField = null;
	private List<String> args = new ArrayList<>();
	public static final String GO_ID_REG_EXP = "(go:|GO:|gO:|Go:)";

	public void processRequestObject(FilterRequestJson filterRequestJson){

		for( FilterJson aFilter : filterRequestJson.getList()){

			if("ecoid".equals(aFilter.getType().toLowerCase()) && solrField !=null){
				solrField = "ecoAncestorI";
				args.add(aFilter.getValue());
			}else
			if("ecotermuse".equals(aFilter.getType().toLowerCase())){
				if("exact".equals(aFilter.getValue().toLowerCase())){
					solrField = "ecoID";
				}
			}
		}
	}

	public String solrQueryFragment(){


		if(args==null) return "";

		String argList = StringUtils.join(args.toArray(), " OR ");
		return(solrField + ":(" +  (argList.toString()).replaceAll(GO_ID_REG_EXP, "*").replaceAll(":","\\\\:"));

	}
}
