package com.code.web_homework.service;

import com.code.web_homework.common.PageResult;
import com.code.web_homework.entity.SysLog;
import com.code.web_homework.mapper.SysLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统操作日志业务逻辑层。
 * <p>
 * 日志写入属于「旁路」操作，即使失败也不应影响主业务流程，
 * 因此这里捕获异常并仅做记录，不向上抛出。
 */
@Service
public class LogService {

    private static final Logger log = LoggerFactory.getLogger(LogService.class);

    private final SysLogMapper sysLogMapper;

    public LogService(SysLogMapper sysLogMapper) {
        this.sysLogMapper = sysLogMapper;
    }

    /** 写入一条操作日志 */
    public void save(SysLog sysLog) {
        try {
            sysLogMapper.insert(sysLog);
        } catch (Exception e) {
            log.warn("操作日志写入失败：{}", e.getMessage());
        }
    }

    /** 分页查询操作日志 */
    public PageResult<SysLog> page(String keyword, int pageNum, int pageSize) {
        int offset = (Math.max(pageNum, 1) - 1) * pageSize;
        long total = sysLogMapper.countByQuery(keyword);
        List<SysLog> list = sysLogMapper.selectByQuery(keyword, offset, pageSize);
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    /** 最近的操作日志，用于后台工作台 */
    public List<SysLog> listRecent(int limit) {
        return sysLogMapper.selectRecent(limit);
    }
}
