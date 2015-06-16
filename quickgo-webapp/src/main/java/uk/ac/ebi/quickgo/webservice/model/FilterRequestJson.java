package uk.ac.ebi.quickgo.webservice.model;

import java.util.List;

/**
 * @Author Tony Wardell
 * Date: 16/06/2015
 * Time: 11:17
 * Created with IntelliJ IDEA.
 */
public class FilterRequestJson {

	private int rows;
	private int page;
	private boolean isSlim;
	private List<Filter> list;

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public List<Filter> getList() {
		return list;
	}

	public void setList(List<Filter> list) {
		this.list = list;
	}

	public boolean isSlim() {
		return isSlim;
	}

	public void setIsSlim(boolean isSlim) {
		this.isSlim = isSlim;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	@Override
	public String toString() {
		return "FilterData{" +
				"rows=" + rows +
				", page=" + page +
				", isSlim=" + isSlim +
				", filterList=" + list +
				'}';
	}

	public class Filter {
		private String type;
		private String value;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return "Filter{" +
					"type='" + type + '\'' +
					", value='" + value + '\'' +
					'}';
		}
	}


}

