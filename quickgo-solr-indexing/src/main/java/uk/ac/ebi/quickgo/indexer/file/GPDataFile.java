package uk.ac.ebi.quickgo.indexer.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
*/
import uk.ac.ebi.quickgo.data.SourceFiles.NamedFile;

/**
 * Class that represents a gene_association (gaf), gp_association (gpad) or gp_information (gpi) file.
 *
 * Lines that start with "!" are comments.
 *
 * The file is assumed to have a header that consists of one or more comment lines that contain directives; at present the only
 * type of directive that we recognise is the format version (in gaf, gpad & gpi).
 *
 * @author tonys
 *
 */
public abstract class GPDataFile {
	protected NamedFile gpdf;
	protected int columnCount;
	protected String versionDirective;
	protected String versionSupported;
	protected Map<String, String> directives;

	public GPDataFileReader reader;

	private final static Pattern directivePattern = Pattern.compile("^!\\s*([A-Za-z0-9_\\.-]+)\\s*:\\s*([A-Za-z0-9_\\.-]+)");
	private final static Matcher directiveMatcher = directivePattern.matcher("");

	//private static final Logger logger = LoggerFactory.getLogger(GPDataFile.class);

	public GPDataFile(NamedFile f, int columnCount, String versionDirective, String versionSupported) throws Exception {
		this.gpdf = f;
		this.columnCount = columnCount;
		this.versionDirective = versionDirective;
		this.versionSupported = versionSupported;
		this.reader = new GPDataFileReader();

		this.directives = readHeader();
	}

	public String getName() {
		return gpdf.getName();
	}

	public class GPDataFileReader {
	    BufferedReader reader;

		public void open() throws IOException {
			//reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(gpdf.getDirectory() + File.separator + gpdf.getName())), "UTF8"));
			Path path = FileSystems.getDefault().getPath(gpdf.getDirectory()+ File.separator, gpdf.getName());
			reader = Files.newBufferedReader(path, Charset.defaultCharset());
		}

	    public void close() throws IOException {
	        if (reader != null) {
	        	reader.close();
	        }
	        reader = null;
	    }

		// read a single line of any kind (data, comment, or blank) from the file
		public String readLine() throws Exception {
			return reader.readLine();
		}

		// read a single, non-blank, non-comment line from the file and split it into individual columns
		public String[] readRecord() throws Exception {
			String line;

			// discard comment lines (those which start with !) and blank lines
			while (true) {
		        line = reader.readLine();
		        if (line == null) {
					return null;
		        }
				if (line.length() == 0) {
					continue;
				}
				if (!line.startsWith("!")) {
					break;
				}
			}

			String[] columns = line.split("\\t", columnCount);
			if (columns.length != columnCount) {
				throw new Exception("Reading " + gpdf.getName() + " and expected " + columnCount + " columns, found " + columns.length + ", line is: " + line);
			}
			return columns;
		}
	}

	/**
	 * Read the header of the file and extracts any directives that it finds
	 *
	 * @throws Exception
	 */
	public Map<String, String> readHeader() throws Exception {
		Map<String, String> directives = new HashMap<>();
		reader.open();

		String line;
		while (true) {
			line = reader.readLine();
			if (line == null) {
				// eof
				break;
			}
			else if (line.length() == 0) {
				// blank line
				continue;
			}
			else if (!line.startsWith("!")) {
				// non-blank, non-comment line, meaning we've gone beyond the header
				break;
			}

			// check whether the line contains a directive, or is just a plain ol' comment
			directiveMatcher.reset(line);
			if (directiveMatcher.matches()) {
				directives.put(directiveMatcher.group(1), directiveMatcher.group(2));
			}
		}

		reader.close();
		return directives;
	}

	public void checkVersion() throws Exception {
		String version = directives.get(versionDirective);
		if (version == null || !versionSupported.equals(version)) {
			throw new Exception(versionDirective + ": " + versionSupported + " directive not found");
		}
	}

	public abstract Object calculateRow (String[] columns) throws Exception;

}
