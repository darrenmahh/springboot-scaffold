package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.entity.LogInfo;

@Mapper
public interface LogMapper {

    // 插入数据库的日志语句
    void insert(LogInfo logInfo);
}
