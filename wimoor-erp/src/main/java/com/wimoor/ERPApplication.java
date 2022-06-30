package com.wimoor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.wimoor.util.SpringUtil;


@SpringBootApplication
@EnableFeignClients
@ComponentScan
public class ERPApplication {

    public static void main(String[] args) {
    	ConfigurableApplicationContext context = SpringApplication.run(ERPApplication.class, args);
        SpringUtil.set(context);
    }

}
