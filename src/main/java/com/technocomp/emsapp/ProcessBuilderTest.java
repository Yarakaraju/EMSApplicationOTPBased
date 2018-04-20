/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.technocomp.emsapp;

/**
 *
 * @author Ravi Varma Yarakaraj
 */
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.IOException;

public class ProcessBuilderTest {

    public static void main(String arg[]) throws IOException {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=password&username=john.doe&password=jwtpass");
        Request request = new Request.Builder()
                .url("http://emsappclient:XY7kmzoNzl100@localhost:8080/oauth/token")
                .post(body)
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Postman-Token", "fe284c28-4a57-4744-9c03-42eb0616cd83")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        Response response = client.newCall(request).execute();
        System.out.println(" Response is "+ response.toString());
    }
}
