package uk.ac.ebi.quickgo.rest.search.query;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created 13/01/17
 * @author Edd
 */
public class CursorPage extends Page {
    // todo add creation tests
    public static final String FIRST_CURSOR = "*";
    private final String cursor;

    private CursorPage(int pageSize) {
        this(FIRST_CURSOR, pageSize);
    }

    private CursorPage(String cursor, int pageSize) {
        super(pageSize);
        checkArgument(cursor != null && !cursor.isEmpty(), "Cursor cannot be null or empty");
        this.cursor = cursor;
    }

    public static CursorPage createFirstCursorPage(int pageSize) {
        return new CursorPage(pageSize);
    }

    public static CursorPage createCursorPage(String cursor, int pageSize) {
        return new CursorPage(cursor, pageSize);
    }

    public String getCursor() {
        return cursor;
    }

    @Override public <V> void accept(PageVisitor<V> visitor, V subject) {
        visitor.visit(this, subject);
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
