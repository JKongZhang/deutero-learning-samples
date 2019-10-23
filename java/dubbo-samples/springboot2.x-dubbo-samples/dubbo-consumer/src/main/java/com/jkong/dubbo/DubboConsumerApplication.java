package com.jkong.dubbo;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@EnableDubbo(scanBasePackages = "com.jkong.dubbo.controller")
@SpringBootApplication
public class DubboConsumerApplication{

    public static void main(String[] args) {
        SpringApplication.run(DubboConsumerApplication.class, args);
    }

}