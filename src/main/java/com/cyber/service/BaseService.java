package com.cyber.service;

import com.cyber.entity.PagingData;

import java.util.List;

public interface BaseService<T> {

    Integer save(T entity);

    Integer deleteById(T entity);

    Integer updateById(T entity);

    T selectOne(T entity);

    List<T> selectByIndex(T entity);

    PagingData<T> selectPage(T entity);
}
