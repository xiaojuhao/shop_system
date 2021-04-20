package com.xjh.service.domain;

/**
 * 数据统计分析服务接口
 */
public interface AnalysisService {
    int getTypesCount();

    int getBooksCount();

    int getReadersCount();

    int getAdminsCount();
}
