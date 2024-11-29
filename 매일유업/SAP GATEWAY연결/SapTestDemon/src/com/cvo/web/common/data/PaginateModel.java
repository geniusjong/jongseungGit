package com.cvo.web.common.data;

import com.cvo.web.common.constants.CmnConst;

public class PaginateModel {

	// 현재 페이지
	private int pageNumber = -1;

	// 현재 페이지 레코드 사이즈
	private int pageSize;

	// 총 레코드 수
	private int totalCount = 0;

	// 총 페이지 수
	private int totalPages = 1;

	// 현재페이지 시작번호
	private int startNum;

	// 현재페이지 종료번호
	private int endNum;

	public void init(Box box) {
		pageNumber = box.getInt(CmnConst.PARAM_PAGINATE_PAGE);
		if(pageNumber < 1) {
			pageNumber = 1;
		}

		pageSize = box.getInt(CmnConst.PARAM_PAGINATE_RECORD_SIZE);
		if(pageSize < 1) {
			pageSize = 10;
		}

		totalPages = box.getInt(CmnConst.PARAM_PAGINATE_TOTAL_PAGE);
		if(totalPages < 1) {
			totalPages = 1;
		}
	}

	public void setTotalCount(int totalCount) {
		if(pageNumber < 1) {
			return;
		}
		this.totalCount = totalCount;
//		int totalPages = (totalCount - 1) / pageSize + 1;
//		if(pageNumber > totalPages) {
//			pageNumber = totalPages;
//		}
		startNum = (pageNumber - 1) * pageSize + 1;
		endNum = pageNumber * pageSize;
	}

	public void setDirectTotalCount(int totalCount, int directPageSize) {
		if(pageNumber < 1) {
			return;
		}
		this.totalCount = totalCount;
		totalPages = (totalCount - 1) / directPageSize + 1;
		if(pageNumber > totalPages) {
			pageNumber = totalPages;
		}

		startNum = (pageNumber - 1) * directPageSize + 1;
		endNum = pageNumber * directPageSize;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getStartNum() {
		return startNum;
	}

	public void setStartNum(int startNum) {
		this.startNum = startNum;
	}

	public int getEndNum() {
		return endNum;
	}

	public void setEndNum(int endNum) {
		this.endNum = endNum;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	@Override
	public String toString() {
		return "PaginateModel [pageNumber=" + pageNumber + ", pageSize=" + pageSize + ", totalCount=" + totalCount + ", totalPages=" + totalPages
				+ ", startNum=" + startNum + ", endNum=" + endNum + "]";
	}

}
