package uk.ac.ebi.quickgo.indexer.file;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import uk.ac.ebi.quickgo.data.SourceFiles.NamedFile;
import uk.ac.ebi.quickgo.util.CountingInputStream;
import uk.ac.ebi.quickgo.util.RowIterator;
import uk.ac.ebi.quickgo.util.RowReader;

@SuppressWarnings("serial")
public class TSVFile extends File {
	int nCols;

	public TSVFile(String parent, String child, int nCols) {
		super(parent, child);
		this.nCols = nCols;
	}

	public TSVFile(String pathname, int nCols) {
		super(pathname);
		this.nCols = nCols;

	}

	public TSVFile(File f, int nCols) {
		this(f.getAbsolutePath(), nCols);
	}

	public TSVFile(NamedFile f, int nCols) {
		this(f.file(), nCols);
	}

	public int getNCols() {
		return nCols;
	}

	public RowIterator reader() throws Exception {
	    //return new RowIterator(Progress.monitor(getName(), new TSVFileReader(this, null)));
	    return new RowIterator(new TSVFileReader(this, null));
	}

	public class TSVFileReader implements RowReader {

		public String toString() {
		    return from.toString();
		}

		TSVFile from;
		int nCols;
		CountingInputStream cis;
		long inputsize;

		String[] columns;
		String[] columnNames;
		BufferedReader rdr;

		public TSVFileReader(TSVFile from, PrintWriter monitor) {
		    if (monitor != null) monitor.println("Read TSV from " + from);
		    this.from = from;
			this.nCols = from.getNCols();

			this.columnNames = new String[nCols];
			for (int i = 0; i < nCols; i++) {
				this.columnNames[i] = "Column " + i;
			}
		}

		public void open() throws Exception {
			cis = new CountingInputStream(new BufferedInputStream(new FileInputStream(this.from)));
		    rdr = new BufferedReader(new InputStreamReader(cis, "UTF8"));
			inputsize = from.length();
		}

		public boolean read(String[] result) throws Exception {
		    if (directRead()) {
		        System.arraycopy(columns, 0, result, 0, columns.length);
				return true;
		    }
			else {
			    return false;
		    }
		}

		public boolean directRead() throws Exception {
			String line;

			// discard comment lines (those which start with !) and blank lines
			while (true) {
		        line = rdr.readLine();
		        if (line == null) {
					return false;
		        }
				if (line.length() == 0) {
					continue;
				}
				if (!line.startsWith("!")) {
					break;
				}
			}

			columns = line.split("\\t", nCols);
			return true;
		}

		public void close() throws Exception {
		    if (rdr != null) {
		    	rdr.close();
		    }
		    rdr = null;
		}

		public double getFraction() {
		    return (1.0 * cis.getCount()) / inputsize;
		}

		public String[] getColumns() {
			return columnNames;			
		}
	}

}
