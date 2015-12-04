package uk.ac.ebi.quickgo.repo.reader;

import uk.ac.ebi.quickgo.document.ontology.OntologyDocument;
import uk.ac.ebi.quickgo.repo.reader.line.OSourceLineConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.core.io.Resource;

/**
 * This provides standard reading of an ontology core source file, which can be
 * hooked into a Spring Batch step, see {@link IndexingJobConfig}.
 *
 * Implementation iterations:
 * #1 make it work with a buffered reader (done)
 * #2 make it work with nio backed reading to read blocks of the file
 *    then submit each block to a similar itemreader which parses the lines
 *    and constructs documents for saving in the repo
 *
 * Created 03/12/15
 * @author Edd
 */
public class ODocReader extends AbstractItemStreamItemReader<OntologyDocument> {
    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(ODocReader.class);

    private Resource fileResource;
    private BufferedReader reader;
    private static final OSourceLineConverter SOURCE_LINE_CONVERTER = new OSourceLineConverter();

    public ODocReader(Resource fileResource) {
        this.fileResource = fileResource;
    }

    @Override public OntologyDocument read() throws Exception{
        if (this.reader == null) {
            return null;
        } else {
            String line;
            if ((line = reader.readLine()) != null) {
                return SOURCE_LINE_CONVERTER.apply(line);
            } else {
                return null;
            }
        }
    }

    @Override public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            LOGGER.error("Error whilst closing ontology input file reader", e);
        }
    }

    @Override public void open(ExecutionContext executionContext) {
        super.open(executionContext);

        try {
            Path file = Paths.get(this.fileResource.getURI());
            this.reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file)));
        } catch (IOException e) {
            LOGGER.error("Could not open ontology input file", e);
        }
    }
}
