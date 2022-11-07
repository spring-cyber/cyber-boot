package com.cyber.infrastructure.repository;
import java.util.List;

public interface BaseMapper<T> {

    Integer save(T entity);

    Integer deleteById(T entity);

    Integer updateById(T entity);

    T selectOne(T entity);

    Integer selectByIndexCount(T entity);

    List<T> selectByIndex(T entity);
}
