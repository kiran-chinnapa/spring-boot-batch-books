package com.batch.books.batch;

import com.batch.books.mapper.GridMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Processor implements ItemProcessor<String, String> {

    Logger logger = LoggerFactory.getLogger(Processor.class);

    @Autowired
    private GridMapper gridMapper;

    @Autowired
    private ObjectMapper jsonObjectMapper;

    @Value("${grid.add.row.envelope}")
    private String addRowEnvelope;


    @Override
    public String process(String item) throws Exception {
        String key = item.substring(0, item.indexOf('{')).split("\t")[1];
        String jsonStr = item.substring(item.indexOf('{'));
        Map<Object, Object> envMap = jsonObjectMapper.readValue(addRowEnvelope, Map.class);
        Map<Object, Object> map = jsonObjectMapper.readValue(jsonStr, Map.class);
        return gridMapper.mapColumns(map, key, envMap);
    }

}
