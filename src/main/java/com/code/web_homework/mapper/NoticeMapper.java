package com.code.web_homework.mapper;

import com.code.web_homework.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统公告数据访问接口。
 */
@Mapper
public interface NoticeMapper {

    /** 查询已发布的公告，按发布时间倒序取前 N 条 */
    List<Notice> selectPublished(@Param("limit") int limit);

    /** 查询全部公告（后台管理用，含草稿） */
    List<Notice> selectAll();

    /** 根据主键查询公告 */
    Notice selectById(@Param("id") Long id);

    /** 新增公告 */
    int insert(Notice notice);

    /** 更新公告 */
    int update(Notice notice);

    /** 浏览次数加一 */
    int increaseViewCount(@Param("id") Long id);

    /** 删除公告 */
    int deleteById(@Param("id") Long id);

    /** 公告总数 */
    int countAll();
}
