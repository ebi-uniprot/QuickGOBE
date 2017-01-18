package uk.ac.ebi.quickgo.rest.search.query;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Represents a requested page where the starting position is represented by a
 * cursor position, and where it is also possible to specify the page size.
 *
 * Created 13/01/17
 * @author Edd
 */
public class CursorPage extends Page {
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

    /**
     * Creates the first page instance to be used in a page request, that wants to utilise
     * cursor based paging. The {@link uk.ac.ebi.quickgo.rest.search.results.QueryResult}'s
     * {@link uk.ac.ebi.quickgo.rest.search.results.PageInfo} will contain a corresponding
     * value indicating the location of the next cursor.
     *
     * @param pageSize the maximum number of results to include in the page of results
     * @return the new instance
     */
    public static CursorPage createFirstCursorPage(int pageSize) {
        return new CursorPage(pageSize);
    }

    /**
     * Creates page instances, used in a page request, where the starting cursor position can
     * be specified. Note, it is therefore necessary to call use this method <i>after</i> results
     * have been obtained from an initial page request, created via {@link #createFirstCursorPage(int)}.
     * The {@link uk.ac.ebi.quickgo.rest.search.results.QueryResult}'s
     * {@link uk.ac.ebi.quickgo.rest.search.results.PageInfo} will contain a corresponding
     * value indicating the location of the next cursor.
     *
     * @param cursor the cursor from where to fetch the next result set
     * @param pageSize the maximum number of results to include in the page of results
     * @return the new instance
     */
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
