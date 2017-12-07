package uk.ac.ebi.quickgo.rest.model;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;

/**
 * Key, value pair where the key is always known and the name is always unknown at instantiation time.
 *
 * @author Tony Wardell
 * Date: 20/10/2017
 * Time: 15:47
 * Created with IntelliJ IDEA.
 */
public class CompletableValue {

    public final String key;
    public String value;

    public CompletableValue(String key) {
        checkArgument(nonNull(key), "The key used with CompletableValue cannot be null.");
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return value;
    }

    @Override public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CompletableValue that = (CompletableValue) o;

        if (!key.equals(that.key)) {
            return false;
        }
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override public String toString() {
        return "CompletableValue{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
