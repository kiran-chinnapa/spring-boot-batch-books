package com.batch.books.entity;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import java.util.Date;


@Entity
@NamedQuery(name="find_all_persons", query="select p from Person p")
@NamedQuery(name="find_person_using_like",query="select p from Person p where p.name like :param")
public @Data class Person {

    @Id
    @GeneratedValue
    private int id;
    private String name;
    private String location;
    private Date birthDate;

}