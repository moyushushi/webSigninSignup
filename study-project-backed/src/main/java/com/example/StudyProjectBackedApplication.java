package com.example;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // 新增
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudyProjectBackedApplication {
    // 新增：启动日志
    private static final Logger log = LoggerFactory.getLogger(StudyProjectBackedApplication.class);

    public static void main(String[] args) {
        log.info("【应用启动】开始启动 StudyProjectBacked 应用...");
        SpringApplication.run(StudyProjectBackedApplication.class, args);
        log.info("【应用启动】StudyProjectBacked 应用启动成功！");
    }
}
