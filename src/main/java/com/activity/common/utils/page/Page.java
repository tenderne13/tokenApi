package com.activity.common.utils.page;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liumeng on 2016/6/19.
 */
public class Page<T> {
    public final static int DEFAULT_PAGE_SIZE = 10;

    private int pageSize;//页大小
    private int totalCount;//总数量
    private int currentPage;//当前页数
    private int totalPage;//总页数
    private boolean header;//是否是首页
    private boolean tail;//是否是尾页
    private int startIndex;//开始的索引
    private boolean empty;//是否为空
    private boolean onePage;//是否只有一页
    private boolean noMore;//如果为true表示没有更多
    private int startPage;
    private int endPage;
    private int endArray;
    private List<T> records=new ArrayList<T>();
    private String pageUrl;

    private List pages=new ArrayList();

    public Page() {
    }


    public Page(int pageSize, int totalCount, int currentPage) {
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.totalPage = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;
        if (this.totalPage == 0) {
            this.totalPage = 1;
        }
        this.currentPage = currentPage > totalPage ? totalPage : currentPage;
        this.header = this.currentPage == 1;
        this.tail = this.currentPage == totalPage;
        this.empty = totalCount == 0;
        this.onePage = totalPage == 1;
        this.startIndex = (this.currentPage - 1) * pageSize;
        endPage = currentPage + 4;
        endArray = currentPage * pageSize > totalCount ? totalCount : currentPage * pageSize;
        startPage = currentPage - 4;
        if (endPage > totalPage) {
            endPage = totalPage;
            startPage = endPage - 5;
        }
        if (startPage < 1) {
            startPage = 1;
        }
        this.noMore = this.currentPage == this.totalPage;
        for(int i=startPage;i<=endPage;i++) {
            pages.add(i);
        }
    }

    public List getPages() {
        return pages;
    }

    public void setPages(List pages) {
        this.pages = pages;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    public boolean isTail() {
        return tail;
    }

    public void setTail(boolean tail) {
        this.tail = tail;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public boolean isOnePage() {
        return onePage;
    }

    public void setOnePage(boolean onePage) {
        this.onePage = onePage;
    }

    public List<T> getRecords() {
        return records;
    }

    public Page<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    public int getEndArray() {
        return endArray;
    }

    public void setEndArray(int endArray) {
        this.endArray = endArray;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public boolean isNoMore() {
		return noMore;
	}

	public void setNoMore(boolean noMore) {
		this.noMore = noMore;
	}

	@Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
