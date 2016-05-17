package uk.ac.ebi.quickgo.rest.search.query;

import com.google.common.base.Preconditions;

/**
 * Query used to join data between two views (tables).
 *
 * @author Ricardo Antunes
 */
class JoinQuery extends QuickGOQuery {
    private String joinFromAttribute;
    private String joinFromTable;
    private String joinToAttribute;
    private String joinToTable;
    private QuickGOQuery query;

    JoinQuery(String joinFromTable, String joinFromAttribute, String joinToTable, String joinToAttribute) {
        checkNullOrEmpty(joinFromTable, "Join From Table cannot be null or empty");
        checkNullOrEmpty(joinFromAttribute, "Join From Attribute cannot be null or empty");
        checkNullOrEmpty(joinToTable, "Join To Table cannot be null or empty");
        checkNullOrEmpty(joinToAttribute, "Join To Attribute cannot be null or empty");

        this.joinFromAttribute = joinFromAttribute;
        this.joinFromTable = joinFromTable;
        this.joinToAttribute = joinToAttribute;
        this.joinToTable = joinToTable;
    }

    JoinQuery(String joinFromTable, String joinFromAttribute, String joinToTable, String joinToAttribute,
            QuickGOQuery query) {
        this(joinFromTable, joinFromAttribute, joinToTable, joinToAttribute);

        Preconditions.checkArgument(query != null, "Filter query cannot be null");
        this.query = query;
    }

    private void checkNullOrEmpty(String value, String errorMsg) {
        Preconditions.checkArgument(value != null && value.trim().length() > 0,
                errorMsg);
    }

    @Override public <T> T accept(QueryVisitor<T> visitor) {
        return visitor.visit(this);
    }

    String getJoinFromAttribute() {
        return joinFromAttribute;
    }

    String getJoinFromTable() {
        return joinFromTable;
    }

    String getJoinToAttribute() {
        return joinToAttribute;
    }

    String getJoinToTable() {
        return joinToTable;
    }

    QuickGOQuery getQuery() {
        return query;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JoinQuery joinQuery = (JoinQuery) o;

        if (!joinFromAttribute.equals(joinQuery.joinFromAttribute)) {
            return false;
        }
        if (!joinFromTable.equals(joinQuery.joinFromTable)) {
            return false;
        }
        if (!joinToAttribute.equals(joinQuery.joinToAttribute)) {
            return false;
        }
        if (!joinToTable.equals(joinQuery.joinToTable)) {
            return false;
        }
        return query != null ? query.equals(joinQuery.query) : joinQuery.query == null;

    }

    @Override public int hashCode() {
        int result = joinFromAttribute.hashCode();
        result = 31 * result + joinFromTable.hashCode();
        result = 31 * result + joinToAttribute.hashCode();
        result = 31 * result + joinToTable.hashCode();
        result = 31 * result + (query != null ? query.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "JoinQuery{" +
                "joinFromAttribute='" + joinFromAttribute + '\'' +
                ", joinFromTable='" + joinFromTable + '\'' +
                ", joinToAttribute='" + joinToAttribute + '\'' +
                ", joinToTable='" + joinToTable + '\'' +
                ", query=" + query +
                "} " + super.toString();
    }
}
