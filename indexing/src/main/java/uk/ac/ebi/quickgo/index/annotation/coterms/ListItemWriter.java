package uk.ac.ebi.quickgo.index.annotation.coterms;


import java.util.List;
import org.springframework.batch.item.ItemWriter;

/**
 * Custom Spring Batch writer can process a list produced by an item processor.
 *
 * @author Tony Wardell
 * Date: 09/09/2016
 * Time: 16:47
 * Created with IntelliJ IDEA.
 */
public class ListItemWriter<T> implements ItemWriter<List<T>>{

    private ItemWriter<T> wrapped;

    public ListItemWriter(ItemWriter<T> coOccurringTermItemWriter) {
        wrapped = coOccurringTermItemWriter;
    }

    @Override public void write(List<? extends List<T>> list) throws Exception {
        for (List<T> subList : list) {
            wrapped.write(subList);
        }
    }
}
