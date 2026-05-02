package com.taolife.common.utils;

import java.util.List;

import com.mybatisflex.core.paginate.Page;

import lombok.Data;

/**
 * 分页结果包装类
 * @param <T> 数据类型
 */
@Data
public class PageResult<T> {
    
    /**
     * 数据列表
     */
    private List<T> list;
    
    /**
     * 页码
     */
    private Integer pageNo;
    
    /**
     * 每页大小
     */
    private Integer pageSize;
    
    /**
     * 总页数
     */
    private Integer totalPage;
    
    /**
     * 总记录数
     */
    private Long totalRow;
    
    /**
     * 构造方法
     */
    public PageResult() {}
    
    /**
     * 构造方法
     */
    public PageResult(List<T> list, Integer pageNo, Integer pageSize, Long totalRow) {
        this.list = list;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalRow = totalRow;
        this.totalPage = (int) Math.ceil((double) totalRow / pageSize);
    }
    
    /**
     * 从MyBatis-Flex的Page对象转换
     */
    public static <T> PageResult<T> of(Page<T> page) {
        return of(page, false);
    }
    
    /**
     * 从MyBatis-Flex的Page对象转换
     * @param page MyBatis-Flex的Page对象
     * @param isQueryAll 是否是查询全部数据（pageSize=0的情况）
     */
    public static <T> PageResult<T> of(Page<T> page, boolean isQueryAll) {
        PageResult<T> result = new PageResult<>();
        result.setList(page.getRecords());
        result.setPageNo((int) page.getPageNumber());
        // 如果是查询全部数据，将pageSize设置为0而不是Integer.MAX_VALUE
        result.setPageSize(isQueryAll ? 0 : (int) page.getPageSize());
        result.setTotalRow(page.getTotalRow());
        result.setTotalPage((int) page.getTotalPage());
        return result;
    }
}