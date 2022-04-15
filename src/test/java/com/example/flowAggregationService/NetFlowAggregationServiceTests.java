package com.example.flowAggregationService;

import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = {
    FlowAggregationService.class}, webEnvironment = WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
class NetFlowAggregationServiceTests {

  private static String requestBody = "["
      + "{\"src_app\": \"foo\", \"dest_app\": \"bar\", \"vpc_id\": \"vpc-0\", \"bytes_tx\": 100, \"bytes_rx\": 500, \"hour\": 1},"
      + "{\"src_app\": \"foo\", \"dest_app\": \"bar\", \"vpc_id\": \"vpc-0\", \"bytes_tx\": 400, \"bytes_rx\": 500, \"hour\": 2}"
      + "]";

  private static String requestBodyWrong = "["
      + "{\"src\": \"foo\", \"dest_app\": \"bar\", \"vpc_id\": \"vpc-0\", \"bytes_tx\": 100, \"bytes_rx\": 500, \"hour\": 1},"
      + "{\"src_app\": \"foo\", \"dest_app\": \"bar\", \"vpc_id\": \"vpc-0\", \"bytes_tx\": 400, \"bytes_rx\": 500, \"hour\": 2}"
      + "]";

  @BeforeAll
  public static void setup() {
    RestAssured.baseURI = "http://localhost:8080";
  }

  @Test
  public void postRequest200() {
    Response response = given()
        .header("Content-type", "application/json")
        .and()
        .body(requestBody)
        .when()
        .post("/flows")
        .then()
        .extract().response();
    Assertions.assertEquals(200, response.statusCode());
  }

  @Test
  public void postRequest() {
    Response response = given()
        .header("Content-type", "application/json")
        .and()
        .body(requestBodyWrong)
        .when()
        .post("/flows")
        .then()
        .extract().response();
    Assertions.assertEquals(200, response.statusCode());
  }
}