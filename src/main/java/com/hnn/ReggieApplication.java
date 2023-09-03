package com.hnn;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/*
@Slf4j:来自lombok的注解，作用：通过log输出日志到控制台
* */
@SpringBootApplication
@Slf4j
@ServletComponentScan
@EnableTransactionManagement
public class ReggieApplication {

   // private static final Logger logger = LoggerFactory.getLogger(ReggieApplication.class);
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ReggieApplication.class, args);

        //输出info级别的日志
        log.info("项目启动成功...");
    }

}
