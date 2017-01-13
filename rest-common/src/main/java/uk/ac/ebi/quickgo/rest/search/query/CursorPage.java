package uk.ac.ebi.quickgo.rest.search.query;

/**
 * Created 13/01/17
 * @author Edd
 */
public class CursorPage extends Page {
    // todo: add factory methods for clear creation
    private final String cursor;

    private CursorPage(int pageSize) {
        this("*", pageSize);
    }

    public CursorPage(String cursor, int pageSize) {
        super(pageSize);
        this.cursor = cursor;
    }

    @Override public <V> void accept(PageVisitor<V> visitor, V subject) {
        visitor.visit(this, subject);
    }

    public String getCursor() {
        return cursor;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CursorPage that = (CursorPage) o;

        return cursor != null ? cursor.equals(that.cursor) : that.cursor == null;
    }

    @Override public int hashCode() {
        return cursor != null ? cursor.hashCode() : 0;
    }

    @Override public String toString() {
        return "CursorPage{" +
                "cursor='" + cursor + '\'' +
                '}';
    }
}
