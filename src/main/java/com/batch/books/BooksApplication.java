package com.batch.books;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class BooksApplication {

    static Logger logger = LoggerFactory.getLogger(BooksApplication.class);

    public static String gridType = "";

    public static void main(String[] args) {
        if (StringUtils.isBlank(System.getProperty("gridType"))) {
            logger.error("please pass gridType as argument, example -DgridType=edition/author/work");
            System.exit(-1);
        } else gridType = System.getProperty("gridType");
        SpringApplication.run(BooksApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
