package uk.ac.ebi.quickgo.annotation.download.http;

import uk.ac.ebi.quickgo.annotation.model.Annotation;
import uk.ac.ebi.quickgo.annotation.model.StatisticsGroup;
import uk.ac.ebi.quickgo.annotation.service.converter.StatisticsToWorkbook;
import uk.ac.ebi.quickgo.rest.ResponseExceptionHandler;
import uk.ac.ebi.quickgo.rest.search.results.QueryResult;

import java.io.IOException;
import java.io.OutputStream;

import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes a stream of {@link QueryResult}s containing {@link Annotation} instances to a response's output stream,
 * using the provided converter to format the output.
 *
 * @author Tony Wardell
 * Date: 26/04/2017
 * Time: 14:49
 * Created with IntelliJ IDEA.
 */
public class StatsExcelDispatchWriter implements DispatchWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatsExcelDispatchWriter.class);
    private final StatisticsToWorkbook converter;

    public StatsExcelDispatchWriter(final StatisticsToWorkbook converter) {
        this.converter = converter;
    }

    @SuppressWarnings("unchecked")
   public void write(Object object, OutputStream out) throws IOException {
        if (object instanceof ResponseExceptionHandler.ErrorInfo) {
            writeError(out, (ResponseExceptionHandler.ErrorInfo) object);
        } else {
            if (object instanceof QueryResult) {
                writeDetail(out, (QueryResult<StatisticsGroup>) object);
            } else {
                LOGGER.warn("DispatchWriter write must handle: " + object.getClass());
            }
        }
    }

    private void writeError(OutputStream out, ResponseExceptionHandler.ErrorInfo errorInfo) throws IOException {
        out.write(("URL:\n\t" + errorInfo.getUrl() + "\n").getBytes());
        out.write(("Messages:\n\t" + errorInfo.getMessages().stream().collect(Collectors.joining(",\n"))).getBytes());
    }

    private void writeDetail(OutputStream out, QueryResult<StatisticsGroup> stats) {
        Workbook workbook = converter.convert(stats.getResults());
        try {
            workbook.write(out);
            out.flush();
        } catch (IOException e) {
            LOGGER.error("Failed to send statistics workbook to client", e);
        }
    }
}
