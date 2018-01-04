package uk.ac.ebi.quickgo.annotation.service.converter;

import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Defines a workbook layout, the name to use for it, and the statistics data type it is associated with.
 *
 * @author Tony Wardell
 * Date: 04/01/2018
 * Time: 14:36
 * Created with IntelliJ IDEA.
 */
public class SheetLayout {

    final String typeName;
    final String displayName;
    final List<SectionLayout> sectionLayouts;

    private SheetLayout(String typeName, String displayName, List<SectionLayout> sectionLayouts) {
        this.typeName = typeName;
        this.displayName = displayName;
        this.sectionLayouts = sectionLayouts;
    }

    static SheetLayout buildLayout(String typeName, String displayName, List<SectionLayout> sectionLayouts) {
        checkArgument(Objects.nonNull(typeName), "The type name for the layout cannot be null.");
        checkArgument(Objects.nonNull(displayName), "The display name for the layout cannot be null.");
        checkArgument(Objects.nonNull(sectionLayouts), "The section layouts cannot be null.");
        return new SheetLayout(typeName, displayName, sectionLayouts);
    }
}
