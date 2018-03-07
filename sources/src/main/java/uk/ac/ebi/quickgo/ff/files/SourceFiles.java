package uk.ac.ebi.quickgo.ff.files;

import uk.ac.ebi.quickgo.ff.reader.Progress;
import uk.ac.ebi.quickgo.ff.reader.RowIterator;
import uk.ac.ebi.quickgo.ff.reader.TSVRowReader;

import java.io.File;

public class SourceFiles {

	File baseDirectory;

	public SourceFiles(File directory) {
		this.baseDirectory = directory;
	}

	public static NamedFile[] holder(NamedFile... files) {
	    return files;
	}

	public static class NamedFile {
		public File directory;
		public String name;

	    public NamedFile(File directory, String name) {
	    	this.directory = directory;
	        this.name = name;
	    }

	    public File file() {
	        return new File(directory, name);
	    }

	    public String getName() {
	        return name;
	    }

	}

	public static class TSVDataFile<X extends Enum<X>> extends NamedFile {

	    public TSVDataFile(File directory, String name) {
	    	super(directory, name + ".dat.gz");
	    }

	    @SuppressWarnings("unchecked")
		public RowIterator reader(X... columns) throws Exception {
	        String[] names = new String[columns.length];
	        for (int i = 0; i < columns.length; i++) {
	        	names[i] = columns[i].name();
	        }
	        return new RowIterator(Progress.monitor(name, new TSVRowReader(file(), names, true, true, null)));
	    }
	}
}
