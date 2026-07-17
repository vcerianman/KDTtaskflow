package com.kdt.taskflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Điểm khởi động ứng dụng.
 * <p>
 * {@code @SpringBootApplication} = @Configuration + @EnableAutoConfiguration + @ComponentScan.
 * Nó bật auto-configuration và quét component trong package vn.ctel.kids.demo.
 */
@SpringBootApplication
public class TaskflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskflowApplication.class, args);
    }
}