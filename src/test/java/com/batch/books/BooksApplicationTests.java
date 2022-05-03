package com.batch.books;

import com.batch.books.batch.BookProcessor;
import com.batch.books.batch.Processor;
import com.batch.books.batch.Writer;
import com.batch.books.config.JobConfiguration;
import com.batch.books.mapper.GridMapper;
import com.batch.books.reader.RestApiReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpServerErrorException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootTest
class BooksApplicationTests {

    Logger log = LoggerFactory.getLogger(BooksApplicationTests.class);

    @Test
    void contextLoads() {
    }

    @BeforeAll
    static void setGridType(){
        BooksApplication.gridType= "edition";
    }

    @Autowired
    private FlatFileItemReader<String> flatFileItemReader;

    @Autowired
    private JobConfiguration jobConfiguration;

    @Test
    public void testFlatFileItemReader() throws Exception{
        FlatFileItemReader<String> flatFileItemReader =jobConfiguration.fileItemReader();
        flatFileItemReader.open(new ExecutionContext());
        log.info(flatFileItemReader.read());
    }

    @Autowired
    private RestApiReader restApiReader;


    @Autowired
    private Processor itemProcessor;

    @Test
    public void testMockedItemProcessor() throws Exception {
//        List<String> bookLines = Files.readAllLines(Paths.get("src/main/resources/dumps/1Work1Author1Edition.txt"));
        List<String> bookLines = Files.readAllLines(Paths.get("src/main/resources/dumps/Edition.txt"));
        List<String> gridColumns = Arrays.asList("Author Key");
        BooksApplication.gridType= "edition";
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
        String jsonLine = new String(Files.readAllBytes(Paths.get("src/main/resources/dumps/gridRow.json")));
        Assert.assertThrows(HttpServerErrorException.class, () -> itemWriter.write(Arrays.asList(jsonLine)));
    }

    @Autowired
    private GridMapper gridMapper;

    @Autowired
    private ObjectMapper jsonObjmapper;

    @Value("${grid.add.row.envelope}")
    private String addRowEnvelope;

    @Autowired
    private ObjectMapper jsonObjectMapper;

    @Test
    public void testEditionGridMapper() throws Exception {
        BooksApplication.gridType= "edition";
        String editionJson = new String(Files.readAllBytes(Paths.get("src/main/resources/dumps/editions.json")));
        Map<Object, Object> envMap = jsonObjectMapper.readValue(addRowEnvelope, Map.class);
        String writeJson = gridMapper.mapColumns(jsonObjmapper.readValue(editionJson, Map.class), "edition", envMap);
        List<String> gridColumns = Arrays.asList("Year First Published", "Year Latest Edition", "Name", "Publisher");
        log.info(writeJson);
        Assert.assertTrue(writeJson, gridColumns.stream().allMatch(s -> writeJson.contains(s)));
    }

    @Test
    public void testAuthorGridMapper() throws Exception {
        BooksApplication.gridType= "author";
        String authorsJson = new String(Files.readAllBytes(Paths.get("src/main/resources/dumps/authors.json")));
        Map<Object, Object> envMap = jsonObjectMapper.readValue(addRowEnvelope, Map.class);
        String writeJson = gridMapper.mapColumns(jsonObjmapper.readValue(authorsJson, Map.class), "author", envMap);
        List<String> gridColumns = Arrays.asList("Author");
        log.info(writeJson);
        Assert.assertTrue(writeJson, gridColumns.stream().allMatch(s -> writeJson.contains(s)));
    }

    @Test
    public void testWorksGridMapper() throws Exception {
        BooksApplication.gridType= "work";
        String worksJson = new String(Files.readAllBytes(Paths.get("src/main/resources/dumps/works.json")));
        Map<Object, Object> envMap = jsonObjectMapper.readValue(addRowEnvelope, Map.class);
        String writeJson = gridMapper.mapColumns(jsonObjmapper.readValue(worksJson, Map.class), "work", envMap);
        List<String> gridColumns = Arrays.asList("Description");
        log.info(writeJson);
        Assert.assertTrue(writeJson, gridColumns.stream().allMatch(s -> writeJson.contains(s)));
    }

    @Autowired
    BookProcessor bookProcessor;

    @Test
    public void testBookProcessor() throws Exception {
        Map map = restApiReader.read();
        String json = bookProcessor.process(map);
        log.info("BookProcessor JSON -->"+ json);
        Assert.assertTrue(null != json && json.length()>0);
    }
}
