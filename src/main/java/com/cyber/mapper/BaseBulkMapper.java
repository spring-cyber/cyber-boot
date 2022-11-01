package com.cyber.mapper;
import java.util.List;

public interface BaseBulkMapper<T> {

    Integer bulkSave(List<T> tList);

    Integer bulkDelete(List<T> tList);

    Integer bulkUpdate(List<T> tList);
}
