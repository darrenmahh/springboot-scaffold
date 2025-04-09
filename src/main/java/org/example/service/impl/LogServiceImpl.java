package org.example.service.impl;

import org.example.entity.LogInfo;
import org.example.mapper.LogMapper;
import org.example.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogMapper logMapper;

    @Override
    @Async("logTaskExecutor")
    public void saveLog(LogInfo logInfo) {
        filterSensitiveInfo(logInfo);
        logMapper.insert(logInfo);
    }

    private void filterSensitiveInfo(LogInfo logInfo) {
        if (logInfo.getRequestParams() != null && logInfo.getRequestParams().contains("password")) {
            logInfo.setResponseData("---");
        }
    }
}
