package uk.ac.ebi.quickgo.util;

// simple class to hold a (key, value) pair, as found, for example, in the properties columns of GPAD and GPI files
public class KeyValuePair {
    public String key;
    public String value;

    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
