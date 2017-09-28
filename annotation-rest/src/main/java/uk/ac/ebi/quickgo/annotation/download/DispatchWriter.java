package uk.ac.ebi.quickgo.annotation.download;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Define how to dispatch an object.
 *
 * @author Tony Wardell
 * Date: 27/09/2017
 * Time: 14:19
 * Created with IntelliJ IDEA.
 */
public interface DispatchWriter {

    void write(Object object, OutputStream out) throws IOException;
}
