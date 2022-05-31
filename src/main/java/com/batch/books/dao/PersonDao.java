//package com.batch.books.dao;
//
//import com.batch.books.entity.Person;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.BeanPropertyRowMapper;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Repository;
//import java.util.List;
//
//@Repository
//public class PersonDao {
//
//    @Autowired
//    JdbcTemplate jdbcTemplate;
//
//    public List<Person> findAll(){
//        return jdbcTemplate.query("select * from person", new BeanPropertyRowMapper<>(Person.class));
//    }
//}
