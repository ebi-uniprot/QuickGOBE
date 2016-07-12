package uk.ac.ebi.quickgo.rest.search.query;

import uk.ac.ebi.quickgo.rest.search.AggregateFunction;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Data-source representing which supported aggregation fields {@link AggregateFunction} should be executed over
 * the provided field.
 *
 * @author Ricardo Antunes
 */
public class Aggregate {
    private final String field;
    private final Set<AggregateField> fields;
    private final Set<Aggregate> nestedAggregates;

    public Aggregate(String field) {
        Preconditions.checkArgument(field != null, "Cannot create aggregate with null field");
        this.field = field;

        this.fields = new HashSet<>();
        this.nestedAggregates = new HashSet<>();
    }

    public String getName() {
        return field;
    }

    public Set<AggregateField> getFields() {
        return fields;
    }

    public Set<Aggregate> getNestedAggregates() {
        return Collections.unmodifiableSet(nestedAggregates);
    }

    public void addField(String field, AggregateFunction function) {
        fields.add(new AggregateField(field, function));
    }

    public void addNestedAggregate(Aggregate aggregate) {
        Preconditions.checkArgument(aggregate != null, "Cannot add null nested aggregate");
        nestedAggregates.add(aggregate);
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Aggregate aggregate = (Aggregate) o;

        if (!field.equals(aggregate.field)) {
            return false;
        }
        if (!fields.equals(aggregate.fields)) {
            return false;
        }
        return nestedAggregates.equals(aggregate.nestedAggregates);

    }

    @Override public int hashCode() {
        int result = field.hashCode();
        result = 31 * result + fields.hashCode();
        result = 31 * result + nestedAggregates.hashCode();
        return result;
    }

    @Override public String toString() {
        return "Aggregate{" +
                "field='" + field + '\'' +
                ", fields=" + fields +
                ", nestedAggregates=" + nestedAggregates +
                '}';
    }
}
