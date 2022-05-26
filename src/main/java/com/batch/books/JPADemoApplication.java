package com.batch.books;

import com.batch.books.jparepository.PersonJPARepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JPADemoApplication implements CommandLineRunner {

    static Logger logger = LoggerFactory.getLogger(BooksApplication.class);

    @Autowired
    PersonJPARepository jpaRepository;

    public static void main(String[] args) {
        SpringApplication.run(JPADemoApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        logger.info("jpa named query-->{}",jpaRepository.findById(4));
        logger.info("jpa named wild card query-->{}",jpaRepository.findByWildCard("i"));
    }
}
