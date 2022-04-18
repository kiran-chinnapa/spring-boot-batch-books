package com.batch.books.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Component
public class Writer implements ItemWriter<String> {

    private Logger logger = LoggerFactory.getLogger(Writer.class);

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void write(List<? extends String> items) throws Exception {

        // need to add rows logic from python

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");
        headers.set("authId", "7ebe66f0-e8cc-4238-b5b2-b627e86df906");

        items.stream().forEach((String bookRecord)->postToApi(bookRecord, headers));
    }

    private void postToApi(String json, HttpHeaders headers){
        logger.info(json);
        HttpEntity<String> httpEntity = new HttpEntity<>(json,headers);
        String response= restTemplate.postForObject(
                "https://qa.bigparser.com/api/v2/grid/624fab6112643c28811d05b3/rows/bulk_create",
                httpEntity,
                String.class
        );
        logger.info("response object ::"+ response);
    }
}
