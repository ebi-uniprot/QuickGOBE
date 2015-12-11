package uk.ac.ebi.quickgo.ff;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class TSVRowReader implements RowReader.MonitorRowReader {
    protected String[] exportColumnNames;
    protected boolean postgres;
    protected boolean compress;
    protected CountingInputStream cis;
    protected long inputsize;
    protected File from;
    protected List<String> data = new ArrayList<String>();
    protected StringBuffer field = new StringBuffer();
    protected BufferedReader rd;
    protected boolean eof;
    protected int[] columnIndexes;

    public String toString() {
        return from.toString();
    }

    public TSVRowReader(File from, String[] columns, boolean postgres, boolean compress, PrintWriter monitor) {
        this.exportColumnNames = columns;
        this.postgres = postgres;
        this.compress = compress;
        if (monitor!= null) {
        	monitor.println("Read TSV from " + from);
        }
        this.from = from;
    }

    public void open() throws Exception {
        eof = false;
        inputsize = from.length();
        cis = new CountingInputStream(new BufferedInputStream(new FileInputStream(this.from)));
        InputStream is = cis;
        if (compress) {
        	is = new BufferedInputStream(new GZIPInputStream(is));
        }
        rd = new BufferedReader(new InputStreamReader(is, "UTF8"));
        directRead();
        String[] importColumnNames = (String[]) data.toArray(new String[data.size()]);

        if (this.exportColumnNames == null) {
        	exportColumnNames = importColumnNames;
        }

        columnIndexes = findColumnIndexes(exportColumnNames, data.toArray(new String[data.size()]));
    }


    public boolean read(String[] result) throws Exception {
        directRead();

        if (eof) {
        	return false;
        }
        else {
            for (int i = 0; i < columnIndexes.length; i++) {
                if (columnIndexes[i] < 0) {
                	result[i] = null;
                }
                else {
                	result[i] = (String)data.get(columnIndexes[i]);
                }
            }
            return true;
        }
    }

	public void directRead() throws Exception {
	    String line;
	    data.clear();
	    boolean isNull = false;

	    int count = 0;
	    line = rd.readLine();
	    if (line == null) {
		    eof = true;
		    return;
	    }

	    field.setLength(0);
	    boolean escape = false;

	    while (count < line.length()) {
	        char ch = line.charAt(count);
	        if (escape) {
	            if (ch == 't') {
		            field.append('\t');
	            }
	            else if (ch == 'n') {
		            field.append('\n');
	            }
	            else if (ch == 'N') {
		            isNull = true;
	            }
	            else if (ch == '\\') {
		            field.append('\\');
	            }
	            escape = false;
	        }
	        else {
	            if (ch == '\\' && postgres) {
	                escape = true;
	            }
	            else {
	                if (ch == '\t') {
	                    if (isNull) {
	                        data.add(null);
	                    }
	                    else {
	                        data.add(field.toString());
	                    }
	                    field.setLength(0);
	                    isNull = false;
	                }
	                else {
	                    field.append(ch);
	                }
	            }

	        }
	        count++;
	        if (count == line.length() && escape) {
	            line = rd.readLine();
	            if (line == null) {
		            break;
	            }
	            count = 0;
	            escape = false;
	        }
	    }

	    if (isNull){
		    data.add(null);
	    }
	    else {
		    data.add(field.toString());
	    }
	}

    public void close() throws Exception {
        if (rd != null) {
        	rd.close();
        }
        rd = null;
    }

    public String[] getColumns() {
        return exportColumnNames;
    }

    public double getFraction() {
        return (1.0 * cis.getCount()) / inputsize;
    }
    
    public static int[] findColumnIndexes(String[] seek, String[] source) throws IOException {
        int[] index = new int[seek.length];
        for (int i = 0; i < seek.length; i++) {
            index[i] = -1;
            for (int j = 0; j < source.length; j++) {
                if (source[j].equals(seek[i])) {
                	index[i] = j;
                }
            }
            if (index[i]==-1) {
            	throw new IOException("Column " + seek[i] + " not found");
            }
        }
        return index;
    }
}
