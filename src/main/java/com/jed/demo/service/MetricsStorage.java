package com.jed.demo.service;

import com.jed.demo.model.dto.RequestInfo;

public interface MetricsStorage {
    void saveRequestInfo(RequestInfo requestInfo);
}
