package uk.ac.ebi.quickgo.common.query.results;

/**
 * Created by xwatkins on 03/12/2015.
 */
public class Category {
	private String name;
	private Long count;

	public Category(String name, Long count) {
		this.name = name;
		this.count = count;
	}

	public String getName() {
		return name;
	}

	public Long getCount() {
		return count;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Category)) return false;

		Category category = (Category) o;

		if (!name.equals(category.name)) return false;
		return count.equals(category.count);

	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + count.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "Category{" +
				"name='" + name + '\'' +
				", count=" + count +
				'}';
	}
}
