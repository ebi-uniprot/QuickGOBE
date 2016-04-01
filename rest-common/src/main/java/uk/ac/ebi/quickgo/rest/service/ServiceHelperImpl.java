package uk.ac.ebi.quickgo.rest.service;

import com.google.common.base.Preconditions;
import uk.ac.ebi.quickgo.rest.search.QueryStringSanitizer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Tony Wardell
 * Date: 29/03/2016
 * Time: 11:01
 * Created with IntelliJ IDEA.
 */
public class ServiceHelperImpl implements ServiceHelper{

	private QueryStringSanitizer queryStringSanitizer;

	public ServiceHelperImpl(QueryStringSanitizer queryStringSanitizer) {
		this.queryStringSanitizer = queryStringSanitizer;
	}

	@Override
	public List<String> buildIdList(List<String> ids) {
		Preconditions.checkArgument(ids != null, "List of IDs cannot be null");

		return ids.stream().map(queryStringSanitizer::sanitize).collect(Collectors.toList());
	}
}
