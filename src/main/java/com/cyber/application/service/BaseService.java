package com.cyber.application.service;

import com.cyber.domain.entity.PagingData;

import java.util.List;

public interface BaseService<T> {

    Integer save(T entity);

    Integer deleteById(T entity);

    Integer updateById(T entity);

    T selectOne(T entity);

    List<T> selectByIndex(T entity);

    PagingData<List<T>> selectPage(T entity);
}
