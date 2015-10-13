package uk.ac.ebi.quickgo.webservice.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * @Author Tony Wardell
 * Date: 13/10/2015
 * Time: 14:10
 * Created with IntelliJ IDEA.
 */
public class FilterTest {

	@Test
	public void testJackson(){

		String testString = "{\"list\":[{\"type\":\"goID\",\"value\":\"GO:0003824\"},{\"type\":\"goTermUse\",\"value\":\"ancestor\"},{\"type\":\"goRelations\",\"value\":\"IPO\"}],\"rows\":25,\"page\":2,\"isSlim\":0}";

		ObjectMapper om = new ObjectMapper();
		String result = testString;
		System.out.println("raw string is " + result);
		try {
			FilterRequestJson filterRequest = om.readValue(result, FilterRequestJson.class);
			System.out.println(filterRequest.toString());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
