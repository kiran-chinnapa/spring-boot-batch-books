package com.batch.books.mapper;

import com.batch.books.BooksApplication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GridMapper {

    @Value("${grid.books.grid.id}")
    protected String bookGridId;

    @Autowired
    private ObjectMapper jsonObjectMapper;

    public String mapColumns(Map<Object, Object> jsonMap, String key, Map<Object, Object> envMap) throws JsonProcessingException {

        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("Key", key);

        if ("work".equals(BooksApplication.gridType)) {
            returnMap.put("Description",
                    parseValue(Optional.ofNullable(jsonMap.get("subjects")).orElse("")));
            returnMap.put("Year Latest Edition",
                    parseValue(Optional.ofNullable(jsonMap.get("created")).orElse("")).split("-")[0]);
            returnMap.put("Name",
                    Optional.ofNullable(jsonMap.get("title")).orElse(""));

        } else if ("author".equals(BooksApplication.gridType)) {
            returnMap.put("Author",
                    Optional.ofNullable(jsonMap.get("name")).orElse(""));
            returnMap.put("Year Latest Edition",
                    parseValue(Optional.ofNullable(jsonMap.get("created")).orElse("")).split("-")[0]);
            returnMap.put("Location",
                    parseValue(Optional.ofNullable(jsonMap.get("location")).orElse("")));

        } else if ("edition".equals(BooksApplication.gridType)) {
            returnMap.put("Author Key", parseValue(Optional.ofNullable(jsonMap.get("authors")).orElse("")));
            returnMap.put("Work Key", parseValue(Optional.ofNullable(jsonMap.get("works")).orElse("")));
            returnMap.put("Publisher",
                    parseValue(Optional.ofNullable(jsonMap.get("publishers")).orElse("")));
            returnMap.put("Year First Published",
                    Optional.ofNullable(jsonMap.get("publish_date")).orElse(""));
            returnMap.put("Year Latest Edition",
                    parseValue(Optional.ofNullable(jsonMap.get("created")).orElse("")).split("-")[0]);
            returnMap.put("Name",
                    Optional.ofNullable(jsonMap.get("title")).orElse(""));


        } else {
            //logic for book
        }

        ((List) ((Map) envMap.get("insert")).get("rows")).add(returnMap);

        return jsonObjectMapper.writeValueAsString(envMap);
    }

    private String parseValue(Object o) {
        if (o instanceof Map) {
            Collection c = ((Map) o).values();
            Iterator i = c.iterator();
            Object lastElement = i.next();
            while (i.hasNext()) {
                lastElement = i.next();
            }
            return parseValue(lastElement);
        } else if (o instanceof List){
            List<String> returnList = new ArrayList<>();
            if (((List) o).get(0) instanceof Map){
                for(Object ol: ((List) o)){
                    returnList.add(parseValue(ol));
                }
                return String.join(",",returnList);
            }else
            return String.join(",", ((List) o));
        }
        else return String.valueOf(o);
    }
}
