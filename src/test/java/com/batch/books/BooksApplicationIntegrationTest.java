package com.batch.books;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

public class BooksApplicationIntegrationTest {

   static Logger logger = LoggerFactory.getLogger(BooksApplicationIntegrationTest.class);

    private static RestTemplate restTemplate =new RestTemplate();

    private static void deleteToApi(String json, String gridId) throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");
        headers.set("authId", "7ebe66f0-e8cc-4238-b5b2-b627e86df906");
        logger.info("delete json string-->"+json);
        HttpEntity<String> httpEntity = new HttpEntity<>(json,headers);
        restTemplate.delete(
                "https://qa.bigparser.com/api/v2/grid/"+gridId+"/rows/delete_by_queryObj",
                httpEntity
        );
    }

    @BeforeAll
    public static void truncateGrid() throws Exception {
        String gridId= "624fab6112643c28811d05b3";
        String delete_grid = "{ 'delete': { 'query': { 'globalFilter': { 'filters': [ { 'operator': 'LIKE', 'keyword': '[a-zA-Z0-9_]' } ] } } } }";
        deleteToApi(delete_grid, gridId);
    }


    @Test
    public void testSimpleLaunchController() throws Exception {
        String dataFile = "src/main/resources/dumps/books.txt";
        BooksApplication.main(new String[]{"--books.grid.read.file.path=" + dataFile});
        HttpUriRequest httpUriRequest = new HttpGet("http://localhost:8080/launch");
        HttpResponse response = HttpClientBuilder.create().build().execute(httpUriRequest);
        System.out.println("response status" + response.getStatusLine().getStatusCode());
        System.out.println("response message" + EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void test1EditionManyAuthorsManyWorks() throws Exception {
//        testHelper("src/main/resources/dumps/1EditionManyAuthorsManyWorks.txt");
    }

    @Test
    public void test1WorkManyAuthors() throws Exception {
//        testHelper("src/main/resources/dumps/1WorkManyAuthors.txt");
    }

    @Test
    public void test1Author() throws Exception {
//        testHelper("src/main/resources/dumps/1Author.txt");
    }

}
