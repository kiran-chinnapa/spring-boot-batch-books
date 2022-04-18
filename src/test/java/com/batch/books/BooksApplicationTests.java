package com.batch.books;

import com.batch.books.batch.Processor;
import com.batch.books.batch.Writer;
import com.batch.books.mapper.GridMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

@SpringBootTest
class BooksApplicationTests {

    Logger log = LoggerFactory.getLogger(BooksApplicationTests.class);

    @Test
    void contextLoads() {
    }

    @Autowired
    private FlatFileItemReader<String> itemReader;

    @Test
    public void testMockedItemReader() throws Exception {
        // given
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();

        // when
        StepScopeTestUtils.doInStepScope(stepExecution, () -> {
            String jsonLine;
            itemReader.open(stepExecution.getExecutionContext());
            while ((jsonLine = itemReader.read()) != null) {
                // then
                assertThat(jsonLine.length(), greaterThan(0));
                log.info("mocked reader is working fine::" + jsonLine);
            }
            itemReader.close();
            return null;
        });
    }

    @Autowired
    private Processor itemProcessor;

    @Test
    public void testMockedItemProcessor() throws Exception {
        List<String> bookLines = Files.readAllLines(Paths.get("src/main/resources/dumps/books.txt"));
        List<String> gridColumns = Arrays.asList("Key");
        for (String bookLine : bookLines) {
            String writeJson = itemProcessor.process(bookLine);
            log.info(writeJson);
            Assert.assertTrue(writeJson, gridColumns.stream().allMatch(s -> writeJson.contains(s)));
        }
    }

    @Autowired
    private Writer itemWriter;

    @Test
    public void testMockedItemWriter() throws Exception {

        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();

        StepScopeTestUtils.doInStepScope(stepExecution, () -> {
            String jsonLine = new String(Files.readAllBytes(Paths.get("src/main/resources/dumps/gridRow.json")));
            itemWriter.write(Arrays.asList(jsonLine));
            log.info("mocked writer is working fine");
            return null;
        });
    }

    @Test
    public void testMockLaunchController() throws Exception {
        HttpUriRequest httpUriRequest = new HttpGet("http://localhost:8080/launch");
        HttpResponse response = HttpClientBuilder.create().build().execute(httpUriRequest);
        System.out.println("response status" + response.getStatusLine().getStatusCode());
        System.out.println("response message" + EntityUtils.toString(response.getEntity()));
    }

    @Autowired
    private GridMapper gridMapper;

    @Autowired
    private ObjectMapper jsonObjmapper;

    @Test
    public void testEditionGridMapper() throws Exception {
        String editionJson = new String(Files.readAllBytes(Paths.get("src/main/resources/dumps/editions.json")));
        String writeJson = gridMapper.mapColumns(jsonObjmapper.readValue(editionJson, Map.class), "edition");
        List<String> gridColumns = Arrays.asList("Year First Published", "Year Latest Edition", "Name", "Publisher", "Location");
        log.info(writeJson);
        Assert.assertTrue(writeJson, gridColumns.stream().allMatch(s -> writeJson.contains(s)));
    }

    @Test
    public void testAuthorGridMapper() throws Exception {
        String authorsJson = new String(Files.readAllBytes(Paths.get("src/main/resources/dumps/authors.json")));
        String writeJson = gridMapper.mapColumns(jsonObjmapper.readValue(authorsJson, Map.class), "author");
        List<String> gridColumns = Arrays.asList("Author");
        log.info(writeJson);
        Assert.assertTrue(writeJson, gridColumns.stream().allMatch(s -> writeJson.contains(s)));
    }

    @Test
    public void testWorksGridMapper() throws Exception {
        String worksJson = new String(Files.readAllBytes(Paths.get("src/main/resources/dumps/works.json")));
        String writeJson = gridMapper.mapColumns(jsonObjmapper.readValue(worksJson, Map.class), "work");
        List<String> gridColumns = Arrays.asList("Description");
        log.info(writeJson);
        Assert.assertTrue(writeJson, gridColumns.stream().allMatch(s -> writeJson.contains(s)));
    }
}
