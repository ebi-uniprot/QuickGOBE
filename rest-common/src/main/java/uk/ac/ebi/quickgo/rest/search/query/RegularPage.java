package uk.ac.ebi.quickgo.rest.search.query;

/**
 * Created 13/01/17
 * @author Edd
 */
public class RegularPage extends Page {
    private final int pageNum;

    private RegularPage(int pageSize) {
        this(1, pageSize);
    }

    public RegularPage(int pageNum, int pageSize) {
        super(pageSize);
        this.pageNum = pageNum;
    }

    public int getPageNumber() {
        return pageNum;
    }

    @Override public <V> void accept(PageVisitor<V> visitor, V subject) {
        visitor.visit(this, subject);
    }

    @Override public int hashCode() {
        return pageNum;
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RegularPage that = (RegularPage) o;

        return pageNum == that.pageNum;
    }

    @Override public String toString() {
        return "RegularPage{" +
                "pageNum=" + pageNum +
                '}';
    }
}
