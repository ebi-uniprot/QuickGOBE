package uk.ac.ebi.quickgo.index.annotation.coterms;

import java.io.IOException;
import java.io.Writer;
import org.springframework.batch.item.file.FlatFileHeaderCallback;

/**
 * @author Tony Wardell
 * Date: 13/09/2016
 * Time: 16:24
 * Created with IntelliJ IDEA.
 */
class CoTermsFlatFileHeaderCallBack implements FlatFileHeaderCallback {

    private static final String COL_HEADER = "Source Term|Compared Term|Probability Ratio|Significance Ratio|Together" +
            " (Overlap)|Compared count";

    @Override public void writeHeader(Writer writer) throws IOException {
        writer.write(COL_HEADER);

    }
}
