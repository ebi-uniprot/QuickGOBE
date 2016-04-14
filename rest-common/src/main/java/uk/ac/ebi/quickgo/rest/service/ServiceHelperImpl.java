package uk.ac.ebi.quickgo.rest.service;

import com.google.common.base.Preconditions;
import uk.ac.ebi.quickgo.rest.search.QueryStringSanitizer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tony Wardell
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
	public List<String> buildIdList(String[] ids) {
		Preconditions.checkArgument(ids != null, "List of IDs cannot be null");

		return Arrays.stream(ids).map(queryStringSanitizer::sanitize).collect(Collectors.toList());
	}
}
