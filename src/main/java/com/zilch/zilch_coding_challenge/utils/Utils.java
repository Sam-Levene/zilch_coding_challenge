package com.zilch.zilch_coding_challenge.utils;

import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Utils {
    private static final Logger logger = LogManager.getLogger(Utils.class);
    private static String strURITokenForPost;
    private static Response response;
    private Utils() {
        // Not used.
    }

    public static void setupApiTest(String apiEndpoint) {
        String[] apiEndpoints = apiEndpoint.split("/");

        RestAssured.baseURI = "https://demoqa.com/" + apiEndpoints[0] + "/";
        strURITokenForPost = apiEndpoints[1] + "/" + apiEndpoints[2] + "/";
    }

    public static void getBookByISBN(String isbn) {
        strURITokenForPost += "?ISBN=" + isbn;
        RequestSpecification httpRequest = RestAssured.given();
        response = httpRequest.request(Method.GET, strURITokenForPost);
    }

    public static JsonObject verifyBookTitle(String title) {
        return new JsonObject().getAsJsonObject(response.jsonPath().toString());
    }

    public static void fileWriter(File file, String html) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(html);
        } catch (IOException exception) {
            logger.error(exception.getMessage());
        }
    }

    public static List<String> stringToList(String groups) {
        StringTokenizer stringTokenizer = new StringTokenizer(groups);
        List<String> groupList = new ArrayList<>();
        while(stringTokenizer.hasMoreTokens()) {
            groupList.add(stringTokenizer.nextToken());
        }
        return groupList;
    }

    public static String generateRandomNumber() {
        return String.valueOf(System.currentTimeMillis());
    }
}
