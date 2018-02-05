package uk.ac.ebi.quickgo.annotation.service.converter;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Holds the configuration data for a section layout.
 * @author Tony Wardell
 * Date: 04/01/2018
 * Time: 14:38
 * Created with IntelliJ IDEA.
 */
public class SectionLayout {
    static final String[] SECTION_COL_HEADINGS = new String[]{"Code", "Name", "Percentage", "Count"};
    final String type;
    final String header;
    final int startingColumn;

    SectionLayout(String type, String header, int startingColumn) {
        checkArgument(Objects.nonNull(type), "The type for the section layout cannot be null.");
        checkArgument(Objects.nonNull(header), "The header for section layout cannot be null.");
        this.type = type;
        this.header = header;
        this.startingColumn = startingColumn;
    }
}
