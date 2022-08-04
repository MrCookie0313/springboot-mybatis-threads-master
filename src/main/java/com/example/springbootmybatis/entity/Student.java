package com.example.springbootmybatis.entity;

import lombok.Data;

/**
 * @ClassName Student
 * @Description TODO
 * @Author JohnChen
 * @Date 2021/8/28 11:50
 * @Version 1.0
 */
@Data
public class Student {
    private int id;
    private String name;
    private int age;
    private String teacher;
}
