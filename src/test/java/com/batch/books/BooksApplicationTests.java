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


    @Autowired
    private FlatFileItemReader<String> flatFileItemReader;

//    @Test
//    public void testFlatFileItemReader() throws Exception{
//        BooksApplication.gridType= "edition";
//        FlatFileItemReader<String> flatFileItemReader =jobConfiguration.fileItemReader();
//        flatFileItemReader.open(new ExecutionContext());
//        log.info(flatFileItemReader.read());
//    }

    @Value("${grid.books.work.grid.id}")
    private String workGridId;

    @Value("${grid.books.edition.grid.id}")
    private String editionGridId;

    @Value("${grid.books.author.grid.id}")
    private String authorGridId;

    @Value("${grid.books.grid.id}")
    private String bookGridId;

    @Autowired
    private RestApiReader restApiReader;


    @Autowired
    private Processor itemProcessor;

    @Test
    public void testMockedEditionProcessor() throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/dumps/Edition.txt"));
        BooksApplication.gridType= "edition";

        for (String line : lines) {
            log.info("input-->"+line);
            String writeJson = itemProcessor.process(line);
            log.info("output-->"+writeJson);
//            System.setProperty("gridId", editionGridId);
//            itemWriter.write(Arrays.asList(writeJson));
        }
    }

    @Test
    public void testMockedAuthorProcessor() throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/dumps/Author.txt"));
        BooksApplication.gridType= "author";
        for (String line : lines) {
            log.info("input-->"+line);
            String writeJson = itemProcessor.process(line);
            log.info("output-->"+writeJson);
//            System.setProperty("gridId", authorGridId);
//            itemWriter.write(Arrays.asList(writeJson));
        }
    }

    @Test
    public void testMockedWorkProcessor() throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/dumps/Work.txt"));
        BooksApplication.gridType= "work";
        for (String line : lines) {
            log.info("input-->"+line);
            String writeJson = itemProcessor.process(line);
            log.info("output-->"+writeJson);
//            System.setProperty("gridId", workGridId);
//            itemWriter.write(Arrays.asList(writeJson));
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

//    @Test
//    public void testAuthorGridMapper() throws Exception {
//        BooksApplication.gridType= "author";
//        String authorsJson = new String(Files.readAllBytes(Paths.get("src/main/resources/dumps/authors.json")));
//        Map<Object, Object> envMap = jsonObjectMapper.readValue(addRowEnvelope, Map.class);
//        String writeJson = gridMapper.mapColumns(jsonObjmapper.readValue(authorsJson, Map.class), "/author/OL4678677W", envMap);
//        List<String> gridColumns = Arrays.asList("Author");
//        log.info(writeJson);
//        Assert.assertTrue(writeJson, gridColumns.stream().allMatch(s -> writeJson.contains(s)));
//    }
//
//    @Test
//    public void testWorksGridMapper() throws Exception {
//        BooksApplication.gridType= "work";
//        String worksJson = new String(Files.readAllBytes(Paths.get("src/main/resources/dumps/works.json")));
//        Map<Object, Object> envMap = jsonObjectMapper.readValue(addRowEnvelope, Map.class);
//        String writeJson = gridMapper.mapColumns(jsonObjmapper.readValue(worksJson, Map.class), "/works/OL916772A", envMap);
//        List<String> gridColumns = Arrays.asList("Description");
//        log.info(writeJson);
//        Assert.assertTrue(writeJson, gridColumns.stream().allMatch(s -> writeJson.contains(s)));
//    }

    @Autowired
    BookProcessor bookProcessor;

    @Test
    public void testBookProcessor() throws Exception {
        BooksApplication.gridType= "book";
        Map map = restApiReader.read();
        String json = bookProcessor.process(map);
        log.info("BookProcessor JSON -->"+ json);
        System.setProperty("gridId", bookGridId);
        itemWriter.write(Arrays.asList(json));
        Assert.assertTrue(null != json && json.length()>0);
    }
}
