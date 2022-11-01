package com.cyber.service;

import java.util.List;

public interface BaseBulkService<T> {

    Integer bulkSave(List<T> tList);

    Integer bulkDelete(List<T> tList);

    Integer bulkUpdate(List<T> tList);
}
