package com.batch.books;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

public class BooksApplicationIntegrationTest {


    @Test
    public void testMockLaunchController() throws Exception {
        BooksApplication.main(new String[]{});
        HttpUriRequest httpUriRequest = new HttpGet("http://localhost:8080/launch");
        HttpResponse response = HttpClientBuilder.create().build().execute(httpUriRequest);
        System.out.println("response status" + response.getStatusLine().getStatusCode());
        System.out.println("response message" + EntityUtils.toString(response.getEntity()));
    }

}
