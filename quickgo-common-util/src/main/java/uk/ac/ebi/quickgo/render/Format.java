package uk.ac.ebi.quickgo.render;

/**
 * Possible output formats
 * @author cbonill
 *
 */
public enum Format {

    JSON("json"),
    JSONMINIMAL("jsonminimal"),
    XML("xml"),
    OBOXML("oboxml"),
    OBO("obo"),
    MINI("mini");

    String value;

    Format(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Format typeOf(String formatText) {
        for (Format format : Format.values()) {
            if (format.value.equalsIgnoreCase(formatText)) {
                return format;
            }
        }

        throw new IllegalArgumentException("Format: " + formatText + ", does not exist");
    }
}
