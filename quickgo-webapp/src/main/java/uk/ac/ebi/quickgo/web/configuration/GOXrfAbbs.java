package uk.ac.ebi.quickgo.web.configuration;

import uk.ac.ebi.quickgo.data.SourceFiles;
import uk.ac.ebi.quickgo.data.SourceFiles.EXrfAbbsEntry;
import uk.ac.ebi.quickgo.util.CV;

/**
 * class that represents an entry in the GO.xrf_abbs file (as filtered through the go.xrf_abbs table)
 * 
 * @author tonys
 *
 */
public class GOXrfAbbs {
	public CV databases;
	public CV genericURLs;
	public CV itemURLs;

	public GOXrfAbbs(SourceFiles files) throws Exception {
		this.databases = new CV(files.xrfAbbsInfo.reader(EXrfAbbsEntry.ABBREVIATION, EXrfAbbsEntry.DATABASE));
		this.genericURLs = new CV(files.xrfAbbsInfo.reader(EXrfAbbsEntry.ABBREVIATION, EXrfAbbsEntry.GENERIC_URL));
		this.itemURLs = new CV(files.xrfAbbsInfo.reader(EXrfAbbsEntry.ABBREVIATION, EXrfAbbsEntry.URL_SYNTAX));
	}

	public String getDatabase(String source) {
		CV.Item item = databases.get(source);
		return (item != null) ? item.description : "";
	}

	public String getGenericURL(String source) {
		CV.Item item = genericURLs.get(source);
		return (item != null && item.description != null) ? item.description : "";
	}

	public String getGenericURL(Configuration config, String source) {
		// URLs defined in the config file override those defined in XRF_ABBS
		String url = config.configDatabases.getGenericURL(source);
		if (url == null) {
			CV.Item item = genericURLs.get(source);
			url = (item != null && item.description != null) ? item.description : "";
		}

		return url;
	}

	public String getItemURL(String source, String id) {
		CV.Item item = itemURLs.get(source);
		return (item != null && item.description != null) ? item.description.replaceAll("\\[example_id\\]", id) : "";
	}

	public String getItemURL(Configuration config, String source, String id) {
		// URLs defined in the config file override those defined in XRF_ABBS
		String url = config.configDatabases.getItemURL(source);
		if (url == null) {
			CV.Item item = itemURLs.get(source);
			url = (item != null && item.description != null) ? item.description : config.configDatabases.getDefaultItemURL();
		}

		return url.replaceAll("(?i)\\[example_id\\]", id);
	}
}
