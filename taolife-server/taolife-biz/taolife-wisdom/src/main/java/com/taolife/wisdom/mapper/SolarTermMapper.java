package com.taolife.wisdom.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.taolife.wisdom.entity.SolarTerm;
import com.taolife.wisdom.param.SolarTermPageAdminParam;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

/**
 * 节气Mapper接口
 *
 * @author 文二
 * @date 2026-04-02
 */
@Mapper
public interface SolarTermMapper extends BaseMapper<SolarTerm> {

    /**
     * 根据ID查询节气（带逻辑删除过滤）
     *
     * @param id 节气ID
     * @return 节气对象
     */
    default SolarTerm selectById(String id) {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(SolarTerm::getId).eq(id)
                .and(SolarTerm::getIsDeleted).eq(0)
                .limit(1)
        );
    }

    /**
     * 分页查询节气列表
     *
     * @param param 查询参数
     * @return 分页结果
     */
    default Page<SolarTerm> selectPageByParam(SolarTermPageAdminParam param) {
        QueryWrapper wrapper = QueryWrapper.create()
                .where(SolarTerm::getIsDeleted).eq(0)
                .orderBy(SolarTerm::getTermOrder, true);
        return paginate(param.getPageNo(), param.getPageSize(), wrapper);
    }

    /**
     * 查询当前日期对应的节气
     *
     * @param currentDate 当前日期
     * @return 节气对象
     */
    default SolarTerm selectCurrentSolarTerm(LocalDate currentDate) {
        int currentMonth = currentDate.getMonthValue();
        int currentDay = currentDate.getDayOfMonth();

        // 先查询所有节气，然后在Service层进行日期匹配
        List<SolarTerm> allTerms = selectListByQuery(
            QueryWrapper.create()
                .where(SolarTerm::getIsDeleted).eq(0)
                .orderBy(SolarTerm::getTermOrder).asc()
        );

        // 查找匹配当前日期的节气
        for (SolarTerm term : allTerms) {
            // 计算节气开始日期和结束日期的月份和日期
            int startMonth = term.getStartMonth();
            int startDay = term.getStartDay();
            int endMonth = term.getEndMonth();
            int endDay = term.getEndDay();

            // 判断当前日期是否在节气范围内
            if (isDateInRange(currentMonth, currentDay, startMonth, startDay, endMonth, endDay)) {
                return term;
            }
        }

        return null;
    }

    /**
     * 判断日期是否在范围内
     *
     * @param currentMonth 当前月份
     * @param currentDay 当前日期
     * @param startMonth 开始月份
     * @param startDay 开始日期
     * @param endMonth 结束月份
     * @param endDay 结束日期
     * @return 是否在范围内
     */
    private boolean isDateInRange(int currentMonth, int currentDay,
                               int startMonth, int startDay,
                               int endMonth, int endDay) {
        // 如果开始和结束月份相同
        if (startMonth == endMonth) {
            return currentMonth == startMonth && currentDay >= startDay && currentDay <= endDay;
        }

        // 如果当前月份在开始和结束月份之间
        if (currentMonth > startMonth && currentMonth < endMonth) {
            return true;
        }

        // 如果当前月份等于开始月份，且日期大于等于开始日期
        if (currentMonth == startMonth && currentDay >= startDay) {
            return true;
        }

        // 如果当前月份等于结束月份，且日期小于等于结束日期
        if (currentMonth == endMonth && currentDay <= endDay) {
            return true;
        }

        return false;
    }

    /**
     * 查询所有节气列表（按节气序号排序）
     *
     * @return 节气列表
     */
    default List<SolarTerm> selectAllSolarTerms() {
        return selectListByQuery(
            QueryWrapper.create()
                .where(SolarTerm::getIsDeleted).eq(0)
                .orderBy(SolarTerm::getTermOrder).asc()
        );
    }

    /**
     * 查询第一个节气（立春）
     *
     * @return 节气对象
     */
    default SolarTerm selectFirstSolarTerm() {
        return selectOneByQuery(
            QueryWrapper.create()
                .where(SolarTerm::getIsDeleted).eq(0)
                .and(SolarTerm::getTermOrder).eq(1)
                .limit(1)
        );
    }
}