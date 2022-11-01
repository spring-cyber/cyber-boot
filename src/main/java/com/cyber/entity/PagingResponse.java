package com.cyber.entity;

public class PagingResponse<T> extends DataResponse {

    private int row;

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }
}
