package uk.ac.ebi.quickgo.ontology.model;

import uk.ac.ebi.quickgo.common.FieldType;
import uk.ac.ebi.quickgo.common.model.Aspect;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;

/**
 * GO term DTO used by the service layer.
 *
 * Created 13/11/15
 * @author Edd
 */
public class GOTerm extends OBOTerm {
    // refers to the namespace/ontology to which the GO ID belongs
    public Aspect aspect;

    // describes where this term can be used
    public Usage usage;

    public List<BlacklistItem> blacklist;
    public List<GODiscussion> goDiscussions;

    public enum Usage {
        UNRESTRICTED("Unrestricted", "U"),
        ELECTRONIC("Electronic", "E"),
        NONE("None", "N");

        private final String fullName;
        private final String abbreviation;

        Usage(String fullName, String abbreviation) {
            this.fullName = fullName;
            this.abbreviation = abbreviation;
        }

        public static Usage fromFullName(String fullName) {
            for (Usage usage : Usage.values()) {
                if (usage.fullName.equals(fullName)) {
                    return usage;
                }
            }
            throw new IllegalArgumentException("Unrecognized Usage fullName: " + fullName);
        }

        public String getFullName() {
            return fullName;
        }

        public String getAbbreviation() {
            return abbreviation;
        }

        @JsonValue
        public String getName() {
            return getFullName();
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class BlacklistItem implements FieldType {
        public String goId;
        public String reason;
        public String category;
        public String methodId;
        public String entityType;
        public String entityId;
        public String taxonId;
        public String entityName;
        public String ancestorGoId;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class GODiscussion implements FieldType {
        public String title;
        public String url;
    }
}
