package com.example.springbootmybatis;

import com.example.springbootmybatis.entity.Student;
import com.example.springbootmybatis.mapper.StudentMapper;
import com.example.springbootmybatis.service.StudentService;
import com.example.springbootmybatis.service.StudentServiceSingle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName StudentTestSingle
 * @Description TODO
 * @Author JohnChen
 * @Date 2021/8/28 19:33
 * @Version 1.0
 */
@SpringBootTest
public class StudentTestSingle {

    @Autowired
    private DataSourceTransactionManager dataSourceTransactionManager;

    @Autowired
    private TransactionDefinition transactionDefinition;

    @Autowired
    private StudentServiceSingle studentService;

    @Autowired
    private StudentMapper studentMapper;

    @Test
    void updateStudentWithThreadsSingle(){
        //查询总数据
        List<Student> allStudents = studentMapper.getAll();
        // 线程数量
        final Integer threadCount = 5;

        //每个线程处理的数据量
        final Integer dataPartionLength = (allStudents.size() + threadCount - 1) / threadCount;

        // 创建多线程处理任务
        ExecutorService studentThreadPool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch threadLatchs = new CountDownLatch(threadCount);

        // 使用公共的连接操作
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        for (int i = 0; i < threadCount; i++) {
            // 每个线程处理的数据
            List<Student> threadDatas = allStudents.stream()
                    .skip(i * dataPartionLength).limit(dataPartionLength).collect(Collectors.toList());
            studentThreadPool.execute(() -> {
                studentService.updateStudents(studentMapper,threadDatas, threadLatchs);
            });
        }
        try {
            // 倒计时锁设置超时时间 30s
            threadLatchs.await(600, TimeUnit.SECONDS);
            dataSourceTransactionManager.commit(transactionStatus);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("主线程完成");
    }
}
