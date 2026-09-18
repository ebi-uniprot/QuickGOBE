package uk.ac.ebi.quickgo.index.common.writer;

import java.util.List;

import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.lang.NonNull;

/**
 * Custom Spring Batch writer can process a list produced by an item processor.
 *
 * @author Tony Wardell
 * Date: 09/09/2016
 * Time: 16:47
 * Created with IntelliJ IDEA.
 */
public class ListItemWriter<T> implements ItemStreamWriter<List<T>> {

    private final FlatFileItemWriter<T> wrapped;

    public ListItemWriter(FlatFileItemWriter<T> writer) {
        wrapped = writer;
    }

    public void write(List<? extends List<T>> list) throws Exception {
        for (List<T> subList : list) {
            wrapped.write(new Chunk<>(subList));
        }
    }

    @Override
    public void close() throws ItemStreamException {
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
    public void write(@NonNull Chunk<? extends List<T>> chunk) throws Exception {
        write(chunk.getItems());
    }
}