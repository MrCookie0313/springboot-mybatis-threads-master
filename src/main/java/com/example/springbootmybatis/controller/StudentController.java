package com.example.springbootmybatis.controller;

import com.example.springbootmybatis.service.StudentsTransactionThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName StudentController
 * @Description TODO
 * @Author JohnChen
 * @Date 2021/8/29 10:28
 * @Version 1.0
 */
@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    StudentsTransactionThread studentsTransactionThread;

    @GetMapping(value = "/update")
    public String update() throws InterruptedException {
        studentsTransactionThread.updateStudentWithThreadsAndTrans();
        return "OK";
    }
}
