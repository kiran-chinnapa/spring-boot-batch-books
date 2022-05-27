package com.batch.books.batch;

import com.batch.books.BooksApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;


@Component
public class Writer implements ItemWriter<String> {

//    @PostConstruct
//    void truncateFile() throws IOException {
//        Files.write(Paths.get(fileName), new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
//    }

    private Logger logger = LoggerFactory.getLogger(Writer.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${grid.qa.authId}")
    private String authId;

    private String fileName = "src/main/resources/out.json";

    @Override
    public void write(List<? extends String> items) throws Exception {
        logger.info("........in Writer.......");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");
        headers.set("authId", authId);

//        items.stream().forEach((String bookRecord) -> postToApi(bookRecord, headers));
        items.stream().forEach((String bookRecord) -> writeToFile(bookRecord, fileName));
    }

    private void postToApi(String json, HttpHeaders headers) {
        logger.info("addRow json for:" + BooksApplication.gridType + " -->" + json);
        HttpEntity<String> httpEntity = new HttpEntity<>(json, headers);
        String response = restTemplate.postForObject(
                "https://qa.bigparser.com/api/v2/grid/" + System.getProperty("gridId") + "/rows/bulk_create",
                httpEntity,
                String.class
        );
        logger.info("response object ::" + response);
    }

    private void writeToFile(String json, String fileName)  {
        try {
            logger.info("............in writeToFile.......");
            Files.write(Paths.get(fileName), (json+"\r\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
