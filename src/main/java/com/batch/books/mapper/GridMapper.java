package com.batch.books.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GridMapper {

    @Autowired
    private ObjectMapper jsonObjectMapper;

    public String mapColumns(Map<Object, Object> jsonMap, String key, Map<Object, Object> envMap) throws JsonProcessingException {

        Map<String, Object> returnMap = new HashMap<>();

        returnMap.put("Key", key);

        returnMap.put("Year First Published",
                Optional.ofNullable(jsonMap.get("publish_date")).orElse(""));

        returnMap.put("Year Latest Edition",
                parseValue(Optional.ofNullable(jsonMap.get("created")).orElse("")).split("-")[0]);

        returnMap.put("Name",
                Optional.ofNullable(jsonMap.get("title")).orElse(""));

        returnMap.put("Description",
                parseValue(Optional.ofNullable(jsonMap.get("subjects")).orElse("")));

        returnMap.put("Author",
                Optional.ofNullable(jsonMap.get("name")).orElse(""));

        returnMap.put("Publisher",
                parseValue(Optional.ofNullable(jsonMap.get("publishers")).orElse("")));

        returnMap.put("Location",
                parseValue(Optional.ofNullable(jsonMap.get("location")).orElse("")));

        ((List)((Map)envMap.get("insert")).get("rows")).add(returnMap);

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
        } else if (o instanceof List)
            return String.join(",", ((List) o));
        else return String.valueOf(o);
    }
}
