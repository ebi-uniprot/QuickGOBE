package uk.ac.ebi.quickgo.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class IOUtils {
    /**
     * Copy bytes from input to output until no more available.
     * Note: Doesn't close either.
     * @param is Input
     * @param os Output
     * @throws java.io.IOException only if thrown by underlying IO
     */

    public static void copy(InputStream is, OutputStream os) throws IOException {
    	byte[] buff = new byte[1024];
		int ct;
		while ((ct = is.read(buff)) > 0) {
			os.write(buff, 0, ct);
		}
    }

    /**
     * Download a URL to a file.
     *
     * @param url Source
     * @param file Target
     * @throws IOException on underlying failure
     */
    public static void copy(URL url, File file) throws IOException {
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(file);
        copy(is, os);
        is.close();
        os.close();
    }
}
