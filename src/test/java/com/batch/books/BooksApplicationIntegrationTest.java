package com.batch.books;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class BooksApplicationIntegrationTest {

   static Logger logger = LoggerFactory.getLogger(BooksApplicationIntegrationTest.class);
   private static RestTemplate restTemplate =new RestTemplate();

    @Value("${grid.books.grid.id}")
    private String gridId;

    @Value("${grid.qa.authId}")
    private String authId;

    @Test
    public void truncateGrid() {
        try {
            String delete_grid = "{\n" +
                    "    \"delete\": {\n" +
                    "        \"query\": {\n" +
                    "            \"globalFilter\": {\n" +
                    "                \"filters\": [\n" +
                    "                    {\n" +
                    "                        \"operator\": \"LIKE\",\n" +
                    "                        \"keyword\": \"[a-zA-Z0-9_]\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            }\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
//            headers.set("Accept", "application/json");
            headers.set("authId", authId);
            logger.info("delete json string-->"+delete_grid);
            HttpEntity<String> httpEntity = new HttpEntity<>(delete_grid,headers);
            restTemplate.exchange(
                    "https://qa.bigparser.com/api/v2/grid/"+gridId+"/rows/delete_by_queryObj",
                    HttpMethod.DELETE,
                    httpEntity, String.class
            );
            logger.info("successfully deleted all rows");
        } catch (RestClientException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    @Test
    public void testSimpleLaunchController() throws Exception {
        truncateGrid();
        logger.info("sleeping .......");
        Thread.sleep(1000);
        String dataFile = "src/main/resources/dumps/1Work1Author1Edition.txt";
        BooksApplication.main(new String[]{"--books.grid.read.file.path=" + dataFile});
        HttpUriRequest httpUriRequest = new HttpGet("http://localhost:8080/launch");
        HttpResponse response = HttpClientBuilder.create().build().execute(httpUriRequest);
        System.out.println("response status" + response.getStatusLine().getStatusCode());
        System.out.println("response message" + EntityUtils.toString(response.getEntity()));
    }

//    @Test
//    public void test1EditionManyAuthorsManyWorks() throws Exception {
//        truncateGrid();
//        logger.info("sleeping .......");
//        Thread.sleep(1000);
//        String dataFile = "src/main/resources/dumps/1EditionManyAuthorsManyWorks.txt";
//        BooksApplication.main(new String[]{"--books.grid.read.file.path=" + dataFile});
//        HttpUriRequest httpUriRequest = new HttpGet("http://localhost:8080/launch");
//        HttpResponse response = HttpClientBuilder.create().build().execute(httpUriRequest);
//        logger.info("response status" + response.getStatusLine().getStatusCode());
//        logger.info("response message" + EntityUtils.toString(response.getEntity()));
//    }
//
//    @Test
//    public void test1WorkManyAuthors() throws Exception {
////        testHelper("src/main/resources/dumps/1WorkManyAuthors.txt");
//    }
//
//    @Test
//    public void test1Author() throws Exception {
////        testHelper("src/main/resources/dumps/1Author.txt");
//    }

}
