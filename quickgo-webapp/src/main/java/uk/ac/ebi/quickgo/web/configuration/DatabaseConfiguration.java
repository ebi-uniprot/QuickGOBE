package uk.ac.ebi.quickgo.web.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import uk.ac.ebi.quickgo.util.StringUtils;
import uk.ac.ebi.quickgo.web.util.XMLUtils;

public class DatabaseConfiguration {
	public static class Database {
		public String name;
		public String urlGeneric;
		public String urlItem;
		
		public Database(String name, String urlGeneric, String urlItem) {
			this.name = name;
			this.urlGeneric = urlGeneric;
			this.urlItem = urlItem;
		}
	}
	
	private Map<String, Database> dbs = new HashMap<>();

	private String defaultItemURL = "";

	public DatabaseConfiguration(Element databasesRoot) {
		if (databasesRoot != null) {
			defaultItemURL = StringUtils.nvl(databasesRoot.getAttribute("default_item_url"), "");

			List<Element> databases = XMLUtils.getChildElements(databasesRoot, "database");
			for (Element db : databases) {
				Attr att = db.getAttributeNode("name");
				if (att != null) {
					String dbName = att.getValue();
					
					att = db.getAttributeNode("generic_url");
					String urlGeneric = (att != null) ? att.getValue() : "";

					att = db.getAttributeNode("item_url");
					String urlItem = (att != null) ? att.getValue() : null;

					dbs.put(dbName, new Database(dbName, urlGeneric, urlItem));
				}
			}
		}
	}

	public String getGenericURL(String dbName) {
		Database db = dbs.get(dbName);
		return db != null ? db.urlGeneric : null;
	}

	public String getItemURL(String dbName) {
		Database db = dbs.get(dbName);
		if (db != null) {
			String url = db.urlItem;
			if (url == null || url.length() == 0) {
				url = defaultItemURL;
			}
			return url;
		}
		else {
			return null;
		}
	}

	public String getDefaultItemURL() {
		return defaultItemURL;
	}
}
