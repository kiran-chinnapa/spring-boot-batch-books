package com.batch.books.batch;

import com.batch.books.BooksApplication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class BookProcessor implements ItemProcessor<Map<Object, Object>, String> {
    Logger logger = LoggerFactory.getLogger(BookProcessor.class);

    @Autowired
    private ObjectMapper jsonObjectMapper;

    @Value("${grid.add.row.envelope}")
    private String addRowEnvelope;

    @Value("${grid.books.work.grid.id}")
    private String workGridId;

    @Value("${grid.books.author.grid.id}")
    private String authorGridId;

    @Autowired
    RestTemplate restTemplate;

    @Value("${grid.qa.authId}")
    private String authId;

    @Override
    public String process(Map<Object, Object> item) throws Exception {
        //for each row add author and work fields
        List<Map<String, String>> records = ((List) item.get("rows"));
        records.stream().forEach((Map<String, String> m) -> m.putAll(getWorkAuthorMap(m.get("Author Key"), m.get("Work Key"))));
        Map<Object, Object> envMap = jsonObjectMapper.readValue(addRowEnvelope, Map.class);
        ((List) ((Map) envMap.get("insert")).get("rows")).add(records);
        //return a single json string with all the rows
        return jsonObjectMapper.writeValueAsString(envMap);
    }

    private String searchGrid = "{\n" + "        \"query\": {\n" + "            \"columnFilter\": {\n" + "                \"filters\": [\n" + "                    {\n" + "                        \"column\": \"Key\",\n" + "                        \"operator\": \"LIKE\"\n" + "                    }\n" + "                ]\n" + "            },\n" + "            \"showColumnNamesInResponse\": true\n" + "        }\n" + "    }";

    private Map<String, String> getWorkAuthorMap(String authorKeyword, String workKeyword) {
        authorKeyword= Optional.ofNullable(authorKeyword).orElse("");
        workKeyword= Optional.ofNullable(workKeyword).orElse("");
        Map<String, String> filteredRecords = new HashMap<>();
        Map<String, String> records = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Accept", "application/json");
            headers.set("authId", authId);

            Map<String, Object> map = jsonObjectMapper.readValue(searchGrid, Map.class);
            ((Map) ((List) ((Map) ((Map) map.get("query")).get("columnFilter")).get("filters")).get(0)).put("keyword", authorKeyword);
            String searchQuery = jsonObjectMapper.writeValueAsString(map);
            logger.info("search records:" + BooksApplication.gridType + " -->" + searchQuery);
            HttpEntity<String> httpEntity = new HttpEntity<>(searchQuery, headers);
            Map<String, Object> authorMap = restTemplate.postForObject("https://qa.bigparser.com/api/v2/grid/" + authorGridId + "/search", httpEntity, Map.class);
            if (((List) authorMap.get("rows")).size()>0){
                records = ((Map) ((List) authorMap.get("rows")).get(0));
                filteredRecords.putAll(records.entrySet().stream().filter(e -> "Key".equals(e.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            }else
                logger.info("no records for authorKeyword:"+authorKeyword);

            ((Map) ((List) ((Map) ((Map) map.get("query")).get("columnFilter")).get("filters")).get(0)).put("keyword", workKeyword);
            searchQuery = jsonObjectMapper.writeValueAsString(map);
            logger.info("search records:" + BooksApplication.gridType + " -->" + searchQuery);
            httpEntity = new HttpEntity<>(searchQuery, headers);
            Map<String, Object> workMap = restTemplate.postForObject("https://qa.bigparser.com/api/v2/grid/" + workGridId + "/search", httpEntity, Map.class);
            if (((List) workMap.get("rows")).size()>0){
                records = ((Map) ((List) workMap.get("rows")).get(0));
                filteredRecords.putAll(records.entrySet().stream().filter(e -> "Key".equals(e.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            }else
                logger.info("no records for workKeyword:"+workKeyword);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return filteredRecords;
    }
}
