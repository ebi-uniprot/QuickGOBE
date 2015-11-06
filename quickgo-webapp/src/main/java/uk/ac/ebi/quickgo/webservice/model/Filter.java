package uk.ac.ebi.quickgo.webservice.model;

/**
 * @author Tony Wardell
 * Date: 16/06/2015
 * Time: 16:25
 * Created with IntelliJ IDEA.
 */
public class Filter {
    private String type;
    private String value;

    Filter() {}

    public Filter(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "type='" + type + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}