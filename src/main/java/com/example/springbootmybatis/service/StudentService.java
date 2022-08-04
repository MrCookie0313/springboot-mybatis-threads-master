package com.example.springbootmybatis.service;

import com.example.springbootmybatis.entity.Student;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName StudentService
 * @Description TODO
 * @Author JohnChen
 * @Date 2021/8/28 15:49
 * @Version 1.0
 */
public interface StudentService {
    void updateStudentsThread(List<Student> students, CountDownLatch threadLatch, CountDownLatch mainLatch, StudentTaskError taskStatus);
    void updateStudents(List<Student> students, CountDownLatch threadLatch);
    void updateStudentsTransaction(PlatformTransactionManager transactionManager, List<TransactionStatus> transactionStatuses, List<Student> students);
}
