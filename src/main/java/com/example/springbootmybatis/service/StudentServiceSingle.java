package com.example.springbootmybatis.service;

import com.example.springbootmybatis.entity.Student;
import com.example.springbootmybatis.mapper.StudentMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName StudentServiceSingle
 * @Description TODO
 * @Author JohnChen
 * @Date 2021/8/28 19:34
 * @Version 1.0
 */
public interface StudentServiceSingle {
    void updateStudents(StudentMapper studentMapper, List<Student> students, CountDownLatch threadLatch);
}
