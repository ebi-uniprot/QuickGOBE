package uk.ac.ebi.quickgo.annotation.download.http;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Define dispatching an object to an OutputStream.
 *
 * @author Tony Wardell
 * Date: 27/09/2017
 * Time: 14:19
 * Created with IntelliJ IDEA.
 */
public interface OutputStreamWriter {

    void write(Object object, OutputStream out) throws IOException;
}
