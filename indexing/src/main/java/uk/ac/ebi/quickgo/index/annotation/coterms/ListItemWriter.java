package uk.ac.ebi.quickgo.index.annotation.coterms;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;

/**
 * Custom Spring Batch writer can process a list produced by an item processor.
 *
 * @author Tony Wardell
 * Date: 09/09/2016
 * Time: 16:47
 * Created with IntelliJ IDEA.
 */
class ListItemWriter<T> extends FlatFileItemWriter<List<T>> {

    private final FlatFileItemWriter<T> wrapped;

    private final Logger LOGGER = LoggerFactory.getLogger(FlatFileItemWriter.class);

    public ListItemWriter(FlatFileItemWriter<T> coOccurringTermItemWriter) {
        wrapped = coOccurringTermItemWriter;
    }

    @Override public void write(List<? extends List<T>> list) throws Exception {

        LOGGER.info("Writing {} records to co-occurring terms file", list.size());

        for (List<T> subList : list) {
            wrapped.write(subList);
        }
    }

    @Override
    public void close() {
        wrapped.close();
    }

    /**
     * Initialize the reader. This method may be called multiple times before
     * close is called.
     *
     * @see ItemStream#open(ExecutionContext)
     */
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        wrapped.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) {
        wrapped.update(executionContext);
    }

    @Override
    public void setHeaderCallback(FlatFileHeaderCallback headerCallback) {
        wrapped.setHeaderCallback(headerCallback);
    }
}
