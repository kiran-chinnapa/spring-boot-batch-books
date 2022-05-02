package com.batch.books.reader;

import com.batch.books.BooksApplication;
import com.batch.books.batch.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class RestApiReader<T> implements ItemReader<Map<Object,Object>> {

    private Logger logger = LoggerFactory.getLogger(Writer.class);

    @Autowired
    RestTemplate restTemplate;

    @Value("${grid.qa.authId}")
    private String authId;

    @Value("${books.grid.chunk.size}")
    private int chunkSize;

    @Value("${grid.books.edition.grid.id}")
    protected String editionGridId;

    private String query = "";

    @PostConstruct
    void contruct(){
       query = "{\n" +
                "        \"query\": {\n" +
                "            \"pagination\": {\n" +
                "                \"startRow\": 1,\n" +
                "                \"rowCount\": "+chunkSize+"\n" +
                "            },\n" +
                "            \"showColumnNamesInResponse\": true\n" +
                "        }\n" +
                "    }";
    }


    @Override
    public Map<Object, Object> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");
        headers.set("authId", authId);
        Map response = readRecords(query, headers);
        logger.info("response object ::" + response.size());
        return response;
    }

    private Map readRecords(String json, HttpHeaders headers) {
        logger.info("search records:" + BooksApplication.gridType + " -->" + json);
        HttpEntity<String> httpEntity = new HttpEntity<>(json, headers);
        return restTemplate.postForObject(
                "https://qa.bigparser.com/api/v2/grid/" + editionGridId + "/search",
                httpEntity,
                Map.class
        );
    }
}
