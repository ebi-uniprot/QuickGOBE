package uk.ac.ebi.quickgo.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Useful class to override xref databases URLs from file xrefs_urls.properties
 * @author cbonill
 *
 */

public class XrefAbbsUtil {

	// Log
	private static final Logger logger = Logger.getLogger(XrefAbbsUtil.class);	
	
	private static Properties properties = null;
	
	public static boolean isOverriden(String abbreviation){
		return getProperties().containsKey(abbreviation);		
	}
	
	public static String getGenericURL(String abbreviation){
		 String urls = (String)getProperties().get(abbreviation);
		 return urls.split(",")[0];
	}
	
	public static String getUrlSyntax(String abbreviation){
		String urls = (String)getProperties().get(abbreviation);
		return urls.split(",")[1];		
	}
	
	private static Properties getProperties(){
		if(properties == null){
			properties = new Properties();
			ClassLoader loader = Thread.currentThread().getContextClassLoader();           
			InputStream stream = loader.getResourceAsStream("xrefs_urls.properties");
			try {
				properties.load(stream);
			} catch (IOException e) {
				logger.error("Error loading Xrefs URL properties file");
			}
		}
		return properties;
	}
}
