package com.taolife.common.utils;

/**
 * 分页工具类
 */
public class PageUtils {

    /**
     * 验证分页参数并返回是否查询全部
     *
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return true表示查询全部数据，false表示分页查询
     * @throws IllegalArgumentException 如果参数无效
     */
    public static boolean validatePageParams(Integer pageNo, Integer pageSize) {
        if (pageNo == null || pageSize == null) {
            throw new IllegalArgumentException("分页参数不能为空");
        }
        if (pageNo <= 0) {
            throw new IllegalArgumentException("页码必须大于0");
        }
        if (pageSize < 0) {
            throw new IllegalArgumentException("每页大小不能小于0");
        }
        return pageNo == 1 && pageSize == 0;
    }
}