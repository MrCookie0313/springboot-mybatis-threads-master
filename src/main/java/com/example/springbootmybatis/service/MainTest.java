package com.example.springbootmybatis.service;

import com.example.springbootmybatis.entity.Student;
import com.example.springbootmybatis.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @ClassName MainTest
 * @Description TODO
 * @Author JohnChen
 * @Date 2021/8/29 10:21
 * @Version 1.0
 */
public class MainTest {

    @Autowired
    private static StudentsTransactionThread studentsTransactionThread;

    public static void main(String[] args) throws InterruptedException {
        studentsTransactionThread.updateStudentWithThreadsAndTrans();
    }
}
