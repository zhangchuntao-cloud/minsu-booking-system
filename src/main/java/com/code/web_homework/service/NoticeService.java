package com.code.web_homework.service;

import com.code.web_homework.common.BusinessException;
import com.code.web_homework.entity.Notice;
import com.code.web_homework.mapper.NoticeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统公告业务逻辑层。
 */
@Service
public class NoticeService {

    private final NoticeMapper noticeMapper;

    public NoticeService(NoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

    /** 首页展示的已发布公告 */
    public List<Notice> listPublished(int limit) {
        return noticeMapper.selectPublished(limit);
    }

    /** 后台公告列表 */
    public List<Notice> listAll() {
        return noticeMapper.selectAll();
    }

    /** 查询公告详情，同时累加浏览次数 */
    @Transactional(rollbackFor = Exception.class)
    public Notice getById(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException("公告不存在或已被删除");
        }
        noticeMapper.increaseViewCount(id);
        notice.setViewCount(notice.getViewCount() == null ? 1 : notice.getViewCount() + 1);
        return notice;
    }

    /** 新增公告 */
    @Transactional(rollbackFor = Exception.class)
    public void save(Notice notice, Long operatorId) {
        validate(notice);
        notice.setPublisherId(operatorId);
        notice.setCreatedBy(operatorId);
        if (notice.getStatus() == null) {
            notice.setStatus(1);
        }
        noticeMapper.insert(notice);
    }

    /** 修改公告 */
    @Transactional(rollbackFor = Exception.class)
    public void update(Notice notice, Long operatorId) {
        Notice exist = noticeMapper.selectById(notice.getId());
        if (exist == null) {
            throw new BusinessException("公告不存在");
        }
        validate(notice);
        notice.setModifyBy(operatorId);
        noticeMapper.update(notice);
    }

    /** 删除公告 */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Notice exist = noticeMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException("公告不存在");
        }
        noticeMapper.deleteById(id);
    }

    /** 公告总数 */
    public int countAll() {
        return noticeMapper.countAll();
    }

    private void validate(Notice notice) {
        if (notice.getTitle() == null || notice.getTitle().trim().isEmpty()) {
            throw new BusinessException("请填写公告标题");
        }
        if (notice.getTitle().length() > 80) {
            throw new BusinessException("公告标题不能超过 80 个字");
        }
        if (notice.getContent() == null || notice.getContent().trim().isEmpty()) {
            throw new BusinessException("请填写公告内容");
        }
    }
}
