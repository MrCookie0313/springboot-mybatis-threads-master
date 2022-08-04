package com.example.springbootmybatis;

import com.example.springbootmybatis.entity.Student;
import com.example.springbootmybatis.mapper.StudentMapper;
import com.example.springbootmybatis.service.StudentService;
import com.example.springbootmybatis.service.StudentsTransactionThread;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName StudentTestTransactionManager
 * @Description TODO
 * @Author JohnChen
 * @Date 2021/8/29 9:43
 * @Version 1.0
 */
@SpringBootTest
public class StudentTestTransactionManager {

    @Autowired
    StudentsTransactionThread studentsTransactionThread;

    @Test
    void updateStudentWithThreadsAndTrans() throws InterruptedException {
        studentsTransactionThread.updateStudentWithThreadsAndTrans();
    }
}
