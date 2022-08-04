package com.example.springbootmybatis.service.Impl;

import com.example.springbootmybatis.entity.Student;
import com.example.springbootmybatis.mapper.StudentMapper;
import com.example.springbootmybatis.service.StudentService;
import com.example.springbootmybatis.service.StudentTaskError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName StudentServiceImpl
 * @Description TODO
 * @Author JohnChen
 * @Date 2021/8/28 15:50
 * @Version 1.0
 */
@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private DataSourceTransactionManager dataSourceTransactionManager;

    @Autowired
    private TransactionDefinition transactionDefinition;

    @Override
    public void updateStudents(List<Student> students, CountDownLatch threadLatch) {
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        System.out.println("子线程：" + Thread.currentThread().getName());
        try {
            students.forEach(s -> {
                // 更新教师信息
                // String teacher = s.getTeacher();
                String newTeacher = "TNO_" + new Random().nextInt(100);
                s.setTeacher(newTeacher);
                studentMapper.update(s);
            });
            dataSourceTransactionManager.commit(transactionStatus);
            threadLatch.countDown();
        } catch (Throwable e) {
            e.printStackTrace();
            dataSourceTransactionManager.rollback(transactionStatus);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void updateStudentsTransaction(PlatformTransactionManager transactionManager, List<TransactionStatus> transactionStatuses, List<Student> students) {
        // 使用这种方式将事务状态都放在同一个事务里面
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW); // 事物隔离级别，开启新事务，这样会比较安全些。
        TransactionStatus status = transactionManager.getTransaction(def); // 获得事务状态
        transactionStatuses.add(status);

        students.forEach(s -> {
            // 更新教师信息
            // String teacher = s.getTeacher();
            String newTeacher = "TNO_" + new Random().nextInt(100);
            s.setTeacher(newTeacher);
            studentMapper.update(s);
        });
        System.out.println("子线程：" + Thread.currentThread().getName());
    }

    @Override
    public void updateStudentsThread(List<Student> students, CountDownLatch threadLatch, CountDownLatch mainLatch, StudentTaskError taskStatus) {
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        System.out.println("子线程：" + Thread.currentThread().getName());
        try {
            students.forEach(s -> {
                // 更新教师信息
                // String teacher = s.getTeacher();
                String newTeacher = "TNO_" + new Random().nextInt(100);
                s.setTeacher(newTeacher);
                studentMapper.update(s);
            });
        } catch (Throwable e) {
            taskStatus.setIsError();
        } finally {
            threadLatch.countDown(); // 切换到主线程执行
        }
        try {
            mainLatch.await();  //等待主线程执行
        } catch (Throwable e) {
            taskStatus.setIsError();
        }
        // 判断是否有错误，如有错误 就回滚事务
        if (taskStatus.getIsError()) {
            dataSourceTransactionManager.rollback(transactionStatus);
        } else {
            dataSourceTransactionManager.commit(transactionStatus);
        }
    }
}
