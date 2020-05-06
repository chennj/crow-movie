package org.crow.movie.user.common.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class Page<T> {
	
	/**
	 * 总页数
	 */
	private int pageCount;
	
	/**
	 * 每页记录数
	 */
	private int pageSize;
	
	/**
	 * 从第几页开始,开始页是第几条记录
	 */
	private int limit = 0;
	
	/**
	 * 总记录数
	 */
	private int total;
	
	/**
	 * 当前页，上一页，下一页
	 */
	private int currPage = 1;
	private int prevPage = 0;
	private int nextPage = 2;
	
	/**
	 * 默认显示页数
	 */
	private int defaultShowPage = 5;
	
	/**
	 * beginPage:开始页，分隔页数 （开始页数） 比如总共7页默认显示5页 点击下一页时，beginPage=6
	 * nextEndPage:下一页，分隔页数（截止页数） 比如总共7页默认显示5页 点击下一页时，nextEndPage=7
	 * endPage:尾页
	 */
	private int beginPage = 1;
	private int nextEndPage;
	private int endPage;
		
	/**
	 * 第几页
	 */
	private int page = 1;
	
	/**
	 * 请求地址
	 */
	private String url;
	
	/**
	 * 分页条件参数
	 */
	private Map<String, Object> params = new HashMap<String, Object>();
	
	/**
	 * 分页数据
	 */
	public List<T> results = new ArrayList<T>();
	public List<Map<String,Object>> mapResult = new ArrayList<>();
	
	public Page() {
	}

	public Page(int page) {
		this.page = page;
	}

	public Page(int page, int pageSize) {
		this.pageSize = pageSize;
		this.page = page;
	}
	
	public int getLimit(){
		return this.limit;
	}
	
	/**
	 * 
	 * @param page 第几页开始
	 * @param total
	 */
	public void init(int page, int total) {
		this.setTotal(total);
		this.setPage(page);
		this.limit = (page - 1) * pageSize;

		int currLimitNo = page / defaultShowPage;// 获取当页所属第几个5页
		if (page > defaultShowPage) {
			beginPage = (page / defaultShowPage) * defaultShowPage + 1;
			if (page == beginPage) {
				int currEndPage = defaultShowPage * currLimitNo + defaultShowPage;
				if (currEndPage >= pageCount) {
					nextEndPage = pageCount;
				} else {
					nextEndPage = currEndPage;
				}
			} else if (page % defaultShowPage == 0) {
				nextEndPage = page;
				beginPage = page - 4;
			} else {
				int currEndPage = defaultShowPage * currLimitNo + defaultShowPage;
				if (currEndPage >= pageCount) {
					nextEndPage = pageCount;
				} else {
					nextEndPage = currEndPage;
				}
			}
		}
		if (0 == page) {
			this.setCurrPage(1);
		} else {
			this.setCurrPage(page);
		}

	}
	
	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
		if (total < 0) {
			pageCount = 0;
		} else {
			pageCount = total / pageSize;
			if (total % pageSize > 0) {
				pageCount++;
			}
			if (pageCount <= defaultShowPage) {
				nextEndPage = pageCount;
			} else {
				nextEndPage = defaultShowPage;
			}
		}
		endPage = pageCount;
	}
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setPage(String page) {
		if (StringUtils.isNumeric(page)) {
			this.page = Integer.parseInt(page);
		} else {
			this.page = 1;
		}
	}

	public int getBeginPage() {
		return beginPage;
	}

	public void setBeginPage(int beginPage) {
		this.beginPage = beginPage;
	}

	public int getNextEndPage() {
		return nextEndPage;
	}

	public void setNextEndPage(int nextEndPage) {
		this.nextEndPage = nextEndPage;
	}

	public int getPrevPage() {
		return prevPage;
	}

	public void setPrevPage(int prevPage) {
		this.prevPage = prevPage;
	}

	public int getNextPage() {
		return nextPage;
	}

	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}

	public int getPageCount() {
		if (total < 0) {
			return -1;
		}

		int count = total / pageSize;
		if (total % pageSize > 0) {
			count++;
		}
		return count;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrPage() {
		return currPage;
	}

	public void setCurrPage(int currPage) {
		this.prevPage = currPage - 1;
		this.nextPage = currPage + 1;
		this.currPage = currPage;
	}

	public int getDefaultShowPage() {
		return defaultShowPage;
	}

	public void setDefaultShowPage(int defaultShowPage) {
		this.defaultShowPage = defaultShowPage;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public List<T> getResults() {
		return results;
	}

	public void setResults(List<T> results) {
		this.results = results;
	}

	public List<Map<String,Object>> getMapResult() {
		return mapResult;
	}

	public void setMapResult(List<Map<String,Object>> mapResult) {
		this.mapResult = mapResult;
	}

	public String toJsonString() {
		JSONObject obj = new JSONObject();
		obj.put("data", getResults());
		obj.put("recordsTotal", getTotal());
		obj.put("recordsFiltered", getTotal());
		return JSONObject
				.toJSONStringWithDateFormat(obj, "yyyy-MM-dd HH:mm:ss",
						SerializerFeature.WriteDateUseDateFormat);
	}

}