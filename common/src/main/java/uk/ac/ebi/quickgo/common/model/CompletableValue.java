package uk.ac.ebi.quickgo.common.model;

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

    public CompletableValue(String key) {this.key = key;}

    public String getKey() {
        return key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return value;
    }
}
