package com.code.web_homework.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用分页结果封装。
 * <p>
 * 分页采用「物理分页 + 总数统计」的方式实现：先由 Mapper 查询总数，
 * 再通过 LIMIT 语句取出当前页数据，避免一次性把全表数据加载进内存。
 *
 * @param <T> 列表元素类型
 */
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前页数据 */
    private List<T> list = new ArrayList<>();
    /** 记录总数 */
    private long total;
    /** 当前页码，从 1 开始 */
    private int pageNum = 1;
    /** 每页条数 */
    private int pageSize = 10;
    /** 总页数 */
    private int pages;

    public PageResult() {
    }

    public PageResult(List<T> list, long total, int pageNum, int pageSize) {
        this.list = list == null ? new ArrayList<>() : list;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = pageSize <= 0 ? 0 : (int) Math.ceil((double) total / pageSize);
    }

    /** 当前页是否有上一页 */
    public boolean isHasPrevious() {
        return pageNum > 1;
    }

    /** 当前页是否有下一页 */
    public boolean isHasNext() {
        return pageNum < pages;
    }

    /** 计算 SQL 的 LIMIT 偏移量 */
    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}
