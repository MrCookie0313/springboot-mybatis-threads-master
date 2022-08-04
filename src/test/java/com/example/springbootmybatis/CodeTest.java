package com.example.springbootmybatis;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @ClassName CodeTest
 * @Description TODO
 * @Author JohnChen
 * @Date 2021/8/28 14:53
 * @Version 1.0
 */
@SpringBootTest
public class CodeTest {
    @Test
    void aa() {
        int total = 131;
        int pages = 5;

        System.out.println((total + pages-1) / pages);
    }
}
