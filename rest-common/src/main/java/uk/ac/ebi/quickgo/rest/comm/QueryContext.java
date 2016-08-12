package uk.ac.ebi.quickgo.rest.comm;

import uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery;
import uk.ac.ebi.quickgo.rest.search.results.transformer.ResultTransformer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class represents context information associated with a {@link QuickGOQuery}. It can, for example,
 * be used to post-process results during a separate transformation phase.
 *
 * @see ResultTransformer ResultTransformer
 *
 * Created 09/08/16
 * @author Edd
 */
public class QueryContext {
    private Map<Class<?>, Object> properties;

    public QueryContext() {
        properties = new HashMap<>();
    }

    /**
     * This method stores an instance of a specified class. This is
     * useful to record information associated with the creation of a
     * {@link uk.ac.ebi.quickgo.rest.search.query.QuickGOQuery}.
     * @param key the class being stored
     * @param value the instance of the class to store
     * @param <T> the type of the class being stored
     */
    public <T> void save(Class<T> key, T value) {
        properties.put(key, value);
    }

    /**
     * Retrieves the value associated with a specified {@code key}.
     *
     * @param key the class
     * @param <T> the type of the class that was stored
     * @return an {@link Optional} containing the instance of type, {@code T}, stored previously.
     */
    public <T> Optional<T> get(Class<T> key) {
        T value = key.cast(properties.get(key));
        if (value == null) {
            return Optional.empty();
        } else {
            return Optional.of(value);
        }
    }

    /**
     * Combine this {@link QueryContext} with another
     * @param context the {@link QueryContext} to merge
     * @return the merged {@link QueryContext}
     */
    public QueryContext merge(QueryContext context) {
        QueryContext queryContext = new QueryContext();

        queryContext.getProperties().putAll(this.getProperties());
        queryContext.getProperties().putAll(context.getProperties());

        return queryContext;
    }

    private Map<Class<?>, Object> getProperties() {
        return properties;
    }
}
