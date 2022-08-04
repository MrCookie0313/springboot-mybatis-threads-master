package com.example.springbootmybatis;

import com.example.springbootmybatis.entity.Student;
import com.example.springbootmybatis.mapper.StudentMapper;
import com.example.springbootmybatis.service.StudentService;
import com.example.springbootmybatis.service.StudentTaskError;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @ClassName StudentTest
 * @Description TODO
 * @Author JohnChen
 * @Date 2021/8/28 14:08
 * @Version 1.0
 * 参考资料 https://blog.csdn.net/laomumu1992/article/details/102956705
 * 源码地址：https://gitee.com/john273766764/springboot-mybatis-threads.git
 */
@SpringBootTest
public class StudentTest {

    @Autowired
    private StudentMapper studentMapper;

    @Test
    void testGetAll() {
        List<Student> all = studentMapper.getAll();
        System.out.println("共查询到：" + all.size() + "条");
    }

    /***
     * 一条一条依次对50000条数据进行更新操作
     * 耗时：2m27s,1m54s
     */
    @Test
    void updateStudent() {

        List<Student> allStudents = studentMapper.getAll();
        allStudents.forEach(s -> {
            //更新教师信息
            String teacher = s.getTeacher();
            String newTeacher = "TNO_" + new Random().nextInt(100);
            s.setTeacher(newTeacher);
            studentMapper.update(s);
        });
    }

    @Autowired
    private DataSourceTransactionManager dataSourceTransactionManager;

    @Autowired
    private TransactionDefinition transactionDefinition;

    /**
     * 由于希望更新操作 一次性完成，需要手动控制添加事务
     * 耗时：24s
     * 从测试结果可以看出，添加事务后插入数据的效率有明显的提升
     */
    @Test
    void updateStudentWithTrans() {
        List<Student> allStudents = studentMapper.getAll();
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        try {
            allStudents.forEach(s -> {
                //更新教师信息
                String teacher = s.getTeacher();
                String newTeacher = "TNO_" + new Random().nextInt(100);
                s.setTeacher(newTeacher);
                studentMapper.update(s);
            });
            dataSourceTransactionManager.commit(transactionStatus);
        } catch (Throwable e) {
            dataSourceTransactionManager.rollback(transactionStatus);
            throw e;
        }
    }

    @Autowired
    private StudentService studentService;

    /**
     * 对用户而言，27s 任是一个较长的时间，我们尝试用多线程的方式来经行修改操作看能否加快处理速度
     * 预计创建10个线程，每个线程进行5000条数据修改操作
     * 耗时统计
     * 1 线程数：1      耗时：25s
     * 2 线程数：2      耗时：14s
     * 3 线程数：5      耗时：15s
     * 4 线程数：10     耗时：15s
     * 5 线程数：100    耗时：15s
     * 6 线程数：200    耗时：15s
     * 7 线程数：500    耗时：17s
     * 8 线程数：1000    耗时：19s
     * 9 线程数：2000    耗时：23s
     * 10 线程数：5000    耗时：29s
     */
    @Test
    void updateStudentWithThreads() {
        //查询总数据
        List<Student> allStudents = studentMapper.getAll();
        // 线程数量
        final Integer threadCount = 100;

        //每个线程处理的数据量
        final Integer dataPartionLength = (allStudents.size() + threadCount - 1) / threadCount;

        // 创建多线程处理任务
        ExecutorService studentThreadPool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch threadLatchs = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            // 每个线程处理的数据
            List<Student> threadDatas = allStudents.stream()
                    .skip(i * dataPartionLength).limit(dataPartionLength).collect(Collectors.toList());
            studentThreadPool.execute(() -> {
                studentService.updateStudents(threadDatas, threadLatchs);
            });
        }
        try {
            // 倒计时锁设置超时时间 30s
            threadLatchs.await(30, TimeUnit.SECONDS);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        System.out.println("主线程完成");
    }

    /**
     * 由于每个线程都是单独的事务，需要添加对线程事务的统一控制
     * 我们这边使用两个 CountDownLatch 对子线程的事务进行控制
     */
    @Test
    void updateStudentWithThreadsAndTrans() {
        //查询总数据
        List<Student> allStudents = studentMapper.getAll();
        // 线程数量
        final Integer threadCount = 20;

        //每个线程处理的数据量
        final Integer dataPartionLength = (allStudents.size() + threadCount - 1) / threadCount;

        // 创建多线程处理任务
        ExecutorService studentThreadPool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch threadLatchs = new CountDownLatch(threadCount); // 用于计算子线程提交数量
        CountDownLatch mainLatch = new CountDownLatch(1); // 用于判断主线程是否提交
        StudentTaskError taskStatus = new StudentTaskError(); // 用于判断子线程任务是否有错误

        for (int i = 0; i < threadCount; i++) {
            // 每个线程处理的数据
            List<Student> threadDatas = allStudents.stream()
                    .skip(i * dataPartionLength).limit(dataPartionLength)
                    .collect(Collectors.toList());
            studentThreadPool.execute(() -> {
                studentService.updateStudentsThread(threadDatas, threadLatchs, mainLatch, taskStatus);
            });
        }
        try {
            // 倒计时锁设置超时时间 30s
            boolean await = threadLatchs.await(600, TimeUnit.SECONDS);
            if (!await) { // 等待超时，事务回滚
                taskStatus.setIsError();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            taskStatus.setIsError();
        }
        mainLatch.countDown(); // 切换到子线程执行
        studentThreadPool.shutdown(); //关闭线程池

        System.out.println("主线程完成");
    }
}
