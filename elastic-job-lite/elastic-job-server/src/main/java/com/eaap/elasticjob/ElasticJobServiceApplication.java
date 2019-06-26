package com.eaap.elasticjob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = {"com.dangdang.ddframe","com.eaap.elasticjob"})
public class ElasticJobServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElasticJobServiceApplication.class, args);
    }
}
