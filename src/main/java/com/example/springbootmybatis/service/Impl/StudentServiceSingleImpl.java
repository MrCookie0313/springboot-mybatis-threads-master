package com.example.springbootmybatis.service.Impl;

import com.example.springbootmybatis.entity.Student;
import com.example.springbootmybatis.mapper.StudentMapper;
import com.example.springbootmybatis.service.StudentServiceSingle;
import com.example.springbootmybatis.service.StudentTaskError;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName StudentServiceSingleImpl
 * @Description TODO
 * @Author JohnChen
 * @Date 2021/8/28 19:40
 * @Version 1.0
 */
@Service
public class StudentServiceSingleImpl implements StudentServiceSingle {

    @Override
    public void updateStudents(StudentMapper studentMapper, List<Student> students, CountDownLatch threadLatch) {
        try {
            students.forEach(s -> {
                // 更新教师信息
                // String teacher = s.getTeacher();
                String newTeacher = "TNO_" + new Random().nextInt(100);
                s.setTeacher(newTeacher);
                studentMapper.update(s);
            });
            System.out.println("子线程：" + Thread.currentThread().getName());
            threadLatch.countDown();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
