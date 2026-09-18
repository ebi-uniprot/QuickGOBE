package uk.ac.ebi.quickgo.model.ontology.generic;

import java.util.EnumSet;

public class AuditRecord {
    public String termID;
    public String termName;
    public String timestamp;
    public AuditAction action;
    public AuditCategory category;
    public String text;

    public AuditRecord(String goId, String term, String timestamp, String action, String category, String text) {
        this.termID = goId;
        this.termName = term;
        this.timestamp = timestamp;
        this.action = AuditAction.fromString(action);
        this.category = AuditCategory.fromString(category);
        this.text = text;
    }

    public boolean isA(EnumSet<AuditCategory> categories) {
        for (AuditCategory ac : categories) {
            if (ac == category) {
                return true;
            }
        }
        return false;
    }

    public String action() {
        return action.description;
    }

    @Override
    public String toString() {
        return "AuditRecord{" +
                "termID='" + termID + '\'' +
                ", term='" + termName + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", action=" + action +
                ", category='" + category + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    public String getTermID() {
        return termID;
    }

    public void setTermID(String termID) {
        this.termID = termID;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public AuditAction getAction() {
        return action;
    }

    public void setAction(AuditAction action) {
        this.action = action;
    }

    public String getActionString() {
        return action.description;
    }

    public AuditCategory getCategory() {
        return category;
    }

    public void setCategory(AuditCategory category) {
        this.category = category;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public enum AuditAction {
        A("Added"),
        U("Updated"),
        D("Deleted"),
        X("Unknown");

        public String description;

        AuditAction(String description) {
            this.description = description;
        }

        public static AuditAction fromString(String s) {
            return switch (s.toLowerCase()) {
                case "added" -> A;
                case "a" -> A;
                case "updated" -> U;
                case "u" -> U;
                case "deleted" -> D;
                case "d" -> D;
                default -> X;
            };
        }
    }

    public enum AuditCategory {
        TERM("TERM"),
        RELATION("RELATION"),
        DEFINITION("DEFINITION"),
        SYNONYM("SYNONYM"),
        XREF("XREF"),
        OBSOLETION("OBSOLETION"),
        SECONDARY("SECONDARY"),
        SUBSET("SUBSET"),
        SLIM("SLIM"),
        CONSTRAINT("CONSTRAINT"),
        OTHER("OTHER");

        public String description;

        AuditCategory(String description) {
            this.description = description;
        }

        public static AuditCategory fromString(String s) {
            return switch (s.toLowerCase()) {
                case "term" -> TERM;
                case "relation" -> RELATION;
                case "definition" -> DEFINITION;
                case "synonym" -> SYNONYM;
                case "xref" -> XREF;
                case "obsoletion" -> OBSOLETION;
                case "secondary" -> SECONDARY;
                case "subset" -> SUBSET;
                case "slim" -> SLIM;
                case "constraint" -> CONSTRAINT;
                default -> OTHER;
            };
        }
    }
}
