package com.example.springbootmybatis;

import com.example.springbootmybatis.entity.Student;
import com.example.springbootmybatis.mapper.StudentMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SpringbootMybatisApplicationTests {

    @Autowired
    private StudentMapper studentMapper;

    @Test
    void contextLoads() {
    }

    /**
     * 测试查询 学生信息
     */
    @Test
    void testGetStudent(){
        List<Student> all = studentMapper.getAll();
        System.out.println("共查询到："+all.size());
    }
}
