package com.example.springbootmybatis.service;

/**
 * @ClassName StudentTaskStatus
 * @Description TODO
 * @Author JohnChen
 * @Date 2021/8/28 17:40
 * @Version 1.0
 */
public class StudentTaskError {
    private Boolean isError = false;

    public synchronized void setIsError() {
        isError = true;
    }

    public synchronized boolean getIsError() {
        return isError;
    }
}
