package uk.ac.ebi.quickgo.webservice.model;

import java.util.List;

/**
 * @Author Tony Wardell
 * Date: 16/06/2015
 * Time: 11:17
 * Created with IntelliJ IDEA.
 */
public class FilterRequestJson implements FilterRequest {

	private int rows;
	private int page;
	private boolean isSlim;
	private List<Filter> list;
	private String format;
	private int limit;

	@Override
	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	@Override
	public List<Filter> getList() {
		return list;
	}

	public void setList(List<Filter> list) {
		this.list = list;
	}

	@Override
	public boolean isSlim() {
		return isSlim;
	}

	public void setIsSlim(boolean isSlim) {
		this.isSlim = isSlim;
	}

	@Override
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
}

