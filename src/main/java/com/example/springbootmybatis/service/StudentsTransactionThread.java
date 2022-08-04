package com.example.springbootmybatis.service;

import com.example.springbootmybatis.entity.Student;
import com.example.springbootmybatis.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @ClassName StudentsTransactionThread
 * @Description TODO
 * @Author JohnChen
 * @Date 2021/8/29 9:50
 * @Version 1.0
 */
@Service
public class StudentsTransactionThread {

    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private StudentService studentService;
    @Autowired
    private PlatformTransactionManager transactionManager;

    List<TransactionStatus> transactionStatuses = Collections.synchronizedList(new ArrayList<TransactionStatus>());

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void updateStudentWithThreadsAndTrans() throws InterruptedException {

        //查询总数据
        List<Student> allStudents = studentMapper.getAll();

        // 线程数量
        final Integer threadCount = 2;

        //每个线程处理的数据量
        final Integer dataPartionLength = (allStudents.size() + threadCount - 1) / threadCount;

        // 创建多线程处理任务
        ExecutorService studentThreadPool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch threadLatchs = new CountDownLatch(threadCount);
        AtomicBoolean isError = new AtomicBoolean(false);
        try {
            for (int i = 0; i < threadCount; i++) {
                // 每个线程处理的数据
                List<Student> threadDatas = allStudents.stream()
                        .skip(i * dataPartionLength).limit(dataPartionLength).collect(Collectors.toList());
                studentThreadPool.execute(() -> {
                    try {
                        try {
                            studentService.updateStudentsTransaction(transactionManager, transactionStatuses, threadDatas);
                        } catch (Throwable e) {
                            e.printStackTrace();
                            isError.set(true);
                        }finally {
                            threadLatchs.countDown();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        isError.set(true);
                    }
                });
            }

            // 倒计时锁设置超时时间 30s
            boolean await = threadLatchs.await(30, TimeUnit.SECONDS);
            // 判断是否超时
            if (!await) {
                isError.set(true);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            isError.set(true);
        }

        if (!transactionStatuses.isEmpty()) {
            if (isError.get()) {
                transactionStatuses.forEach(s -> transactionManager.rollback(s));
            } else {
                transactionStatuses.forEach(s -> transactionManager.commit(s));
            }
        }

        System.out.println("主线程完成");
    }
}
