package com.batch.books.batch;

import com.batch.books.model.BookRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class Processor implements ItemProcessor<String, BookRecord> {

    @Value("#{'${books.grid.columns}'.split(',')}")
    private List<String> gridColumns;

    private String formatList(List l){
//         l.stream().map(Object::toString).collect(Collectors.joining(","));
        return l.toString().replaceAll("\\[","");
    }

    @Override
    public BookRecord process(String item) throws Exception {
        return new BookRecord(1l);
    }

//    @Override
//    public List<String> process(String item) throws Exception {
//
//        String jsonStr = item.substring(item.indexOf('{'));
//        ObjectMapper mapper = new ObjectMapper();
//        Map<Object, Object> map = null;
//        try {
//            map = mapper.readValue(jsonStr, Map.class);
//            map = map.entrySet().stream().filter(e -> gridColumns.contains(e.getKey()))
//                    .collect(Collectors.toMap(e -> e.getKey(),
//                            e -> (e.getValue() instanceof List) ?
//                                    formatList((List)e.getValue())
//                                    : e.getValue()));
////            map.entrySet().stream().forEach(e-> System.out.println("key::"+e.getKey()+"::value type::"+e.getValue().getClass()));
////            map = map.entrySet().stream().filter(e -> gridColumns.contains(e.getKey()))
////                    .collect(Collectors.toMap(e -> e.getKey(),
////                            e -> (e.getValue() instanceof List) ? "need to implement" : e.getValue()));
//            item = new ObjectMapper().writeValueAsString(map);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//            throw e;
//        }
//
//        return Collections.singletonList(item);
//    }
}
