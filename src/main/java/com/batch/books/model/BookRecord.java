package com.batch.books.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter @RequiredArgsConstructor @ToString
public class BookRecord {
    private List<String> authors,locations,subjects;
    private String copyright_date,description,first_publish_date,title;
    private final Long id;
}