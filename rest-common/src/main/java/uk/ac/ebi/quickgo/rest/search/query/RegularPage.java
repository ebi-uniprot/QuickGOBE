package uk.ac.ebi.quickgo.rest.search.query;

import com.google.common.base.Preconditions;

/**
 * Created 13/01/17
 * @author Edd
 */
public class RegularPage extends Page {
    // todo add creation tests
    private final int pageNumber;

    public RegularPage(int pageNumber, int pageSize) {
        super(pageSize);
        Preconditions.checkArgument(pageNumber >= 0, "Page number cannot be less than 0");
        this.pageNumber = pageNumber;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    @Override public <V> void accept(PageVisitor<V> visitor, V subject) {
        visitor.visit(this, subject);
    }

    @Override public int hashCode() {
        return pageNumber;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RegularPage that = (RegularPage) o;

        return pageNumber == that.pageNumber;
    }

    @Override public String toString() {
        return "RegularPage{" +
                "pageNumber=" + pageNumber +
                '}';
    }
}
