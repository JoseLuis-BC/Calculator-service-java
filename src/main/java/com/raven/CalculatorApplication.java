package com.raven;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import static net.logstash.logback.argument.StructuredArguments.v;

@EnableAsync
@SpringBootApplication
public class CalculatorApplication {

    private static final Logger log = LoggerFactory.getLogger(CalculatorApplication.class);

    public static void main(String[] args) {
        try{
            SpringApplication.run(CalculatorApplication.class, args);
        } catch (Exception e) {
            log.info("Detail", v("Exception_Detail", e));
        }
    }
}