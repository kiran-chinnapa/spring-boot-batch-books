package com.batch.books.batch;

import com.batch.books.model.BookRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Processor implements ItemProcessor<String, BookRecord> {

    @Value("#{'${books.grid.columns}'.split(',')}")
    private List<String> gridColumns;

    private String formatList(List l){
        return l.toString().replaceAll("\\[","");
    }

    @Override
    public BookRecord process(String item) throws Exception {

        String jsonStr = item.substring(item.indexOf('{'));
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        BookRecord book = null;
        try {
            book = mapper.readValue(jsonStr, BookRecord.class);
            item = new ObjectMapper().writeValueAsString(book.toString());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw e;
        }

        return new BookRecord(1l);
    }
}
