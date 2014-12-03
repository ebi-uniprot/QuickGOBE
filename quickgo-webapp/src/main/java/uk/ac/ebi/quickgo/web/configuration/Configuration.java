package uk.ac.ebi.quickgo.web.configuration;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import uk.ac.ebi.quickgo.graphics.ImageArchive;
import uk.ac.ebi.quickgo.web.application.QuickGO;
import uk.ac.ebi.quickgo.web.util.XMLUtils;

/**
 * class that holds the various QuickGO configuration options
 * 
 * @author tonys
 *
 */
public class Configuration {
    public long lastModified;
    public long loaded;

//    public Pages pages;
        
    public Element root;
    public File base;
    public ImageArchive imageArchive = new ImageArchive();
	public DatabaseConfiguration configDatabases;
    public String password;
    public ChartConfiguration configChart;
	public StatisticsConfiguration configStats;

    public Configuration(QuickGO quickGO, String configFile) {
        try {
            File file = new File(configFile);
            base = file.getParentFile();

            loaded = System.currentTimeMillis();
            lastModified = file.lastModified();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(file);
            root = document.getDocumentElement();                       

            configChart = new ChartConfiguration(XMLUtils.getChildElement(root, "chart"));

            configStats = new StatisticsConfiguration(XMLUtils.getChildElement(root, "statistics"));

	        configDatabases = new DatabaseConfiguration(XMLUtils.getChildElement(root, "databases"));

//            Element dataElt = XMLUtils.getChildElement(root, "data");
//            quickGO.dataManager.configure(base, dataElt);
//            quickGO.monitor.configure(base, XMLUtils.getChildElement(root, "monitor"));

//            pages = new Pages(base, root);

            //imageArchive.configure(quickGO.uniqueID);

//            password = root.getAttribute("password");

//            quickGO.updateSchedule.configure(base, dataElt);
        }
        catch (Exception e) {
            failure = e;
            e.printStackTrace();
        }
    }

    public Exception failure;

    public boolean failed() {
        return failure != null;
    }
}
