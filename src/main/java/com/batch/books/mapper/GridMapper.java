package com.batch.books.mapper;

import com.batch.books.BooksApplication;
import com.batch.books.batch.Processor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class GridMapper {

    Logger logger = LoggerFactory.getLogger(Processor.class);

    @Value("${grid.books.grid.id}")
    protected String bookGridId;

    @Autowired
    private ObjectMapper jsonObjectMapper;

    private List<String> editionWorkKeys;

    private List<String> editionAuthorKeys;

    @Autowired
    RestTemplate restTemplate;

    @Value("${grid.qa.authId}")
    private String authId;

    @Value("${grid.books.edition.grid.id}")
    private String editionGridId;

    public String mapColumns(Map<Object, Object> jsonMap, String key, Map<Object, Object> envMap) throws JsonProcessingException {

        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("Key", key);

        if ("work".equals(BooksApplication.gridType)) {
            if (null != key && !editionWorkKeys.contains(key.split("/")[2])) return null;

            returnMap.put("Description", parseValue(Optional.ofNullable(jsonMap.get("subjects")).orElse("")));
            returnMap.put("Year Latest Edition", parseValue(Optional.ofNullable(jsonMap.get("created")).orElse("")).split("-")[0]);
            returnMap.put("Name", Optional.ofNullable(jsonMap.get("title")).orElse(""));

        } else if ("author".equals(BooksApplication.gridType)) {
            if (null != key && !editionAuthorKeys.contains(key.split("/")[2])) return null;

            returnMap.put("Author", Optional.ofNullable(jsonMap.get("name")).orElse(""));
            returnMap.put("Year Latest Edition", parseValue(Optional.ofNullable(jsonMap.get("created")).orElse("")).split("-")[0]);
            returnMap.put("Location", parseValue(Optional.ofNullable(jsonMap.get("location")).orElse("")));

        } else if ("edition".equals(BooksApplication.gridType)) {
            returnMap.put("Author Key", parseValue(Optional.ofNullable(jsonMap.get("authors")).orElse("")));
            returnMap.put("Work Key", parseValue(Optional.ofNullable(jsonMap.get("works")).orElse("")));
            returnMap.put("Publisher", parseValue(Optional.ofNullable(jsonMap.get("publishers")).orElse("")));
            returnMap.put("Year First Published", Optional.ofNullable(jsonMap.get("publish_date")).orElse(""));
            returnMap.put("Year Latest Edition", parseValue(Optional.ofNullable(jsonMap.get("created")).orElse("")).split("-")[0]);
            returnMap.put("Name", Optional.ofNullable(jsonMap.get("title")).orElse(""));


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
        } else if (o instanceof List) {
            List<String> returnList = new ArrayList<>();
            if (((List) o).get(0) instanceof Map) {
                for (Object ol : ((List) o)) {
                    returnList.add(parseValue(ol));
                }
                return String.join(",", returnList);
            } else return String.join(",", ((List) o));
        } else return String.valueOf(o);
    }

    private String searchQuery = "{\n" +
            "    \"query\": {\n" +
            "        \"globalFilter\": {\n" +
            "            \"filters\": [\n" +
            "                {\n" +
            "                    \"operator\": \"LIKE\",\n" +
            "                    \"keyword\": \"[a-zA-Z0-9]\"\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        \"selectColumnNames\": [\n" +
            "            \"Author Key\",\n" +
            "            \"Work Key\"\n" +
            "        ],\n" +
            "        \"showColumnNamesInResponse\": true,\n" +
            "        \"pagination\": {\n" +
            "            \"startRow\": 1,\n" +
            "            \"rowCount\": 1169\n" +
            "        }\n" +
            "    }\n" +
            "}";

    @PostConstruct
    void loadEditionKeys() {
        try {
            editionAuthorKeys = new ArrayList<>();
            editionWorkKeys = new ArrayList<>();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Accept", "application/json");
            headers.set("authId", authId);
            logger.info("search records in author grid -->" + searchQuery);
            HttpEntity<String> httpEntity = new HttpEntity<>(searchQuery, headers);
            Map<String, Object> editionMap = restTemplate.postForObject("https://qa.bigparser.com/api/v2/grid/" + editionGridId + "/search", httpEntity, Map.class);

            ((List<Map<String, String>>) editionMap.get("rows")).stream().forEach(m -> m.entrySet().stream().filter(entry -> null != entry.getValue()).forEach(e -> {
                if ("Work Key".equals(e.getKey())) editionWorkKeys.add(e.getValue().split("/")[2]);
                else editionAuthorKeys.add(e.getValue().split("/")[2]);
            }));
            logger.info("editionWorkKeys:size:"+editionWorkKeys.size());
            logger.info("editionAuthorKeys:size:"+editionAuthorKeys.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
