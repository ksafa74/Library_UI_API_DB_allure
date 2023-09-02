package com.library.steps;

import com.github.javafaker.Faker;
import com.library.pages.BasePage;
import com.library.pages.BookPage;
import com.library.pages.LoginPage;
import com.library.utility.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.it.Ma;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.asynchttpclient.util.Assertions;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;

import static org.hamcrest.Matchers.*;


public class LibrarySteps {

    private String token;
    private RequestSpecification reqSpec;
    private ResponseSpecification respSpec;
    private Response response;
    private String pathPAramValue;

    Map<String,String> bookAsMapFromApi;
    Map<String,String> userAsMapFromApi;

    LoginPage loginPage;
    BasePage basePage;
    BookPage bookPage;
    Faker faker = new Faker();
    String baseURI = ConfigurationReader.getProperty("library.baseUri");


    @Given("I logged Library api as a {string}")
    public void iLoggedLibraryApiAsA(String arg0) {
        token = LibraryAPI_Util.getToken(arg0);
    }

    @And("Accept header is {string}")
    public void acceptHeaderIs(String contentType) {
        reqSpec = RestAssured.given()
                .baseUri("https://library2.cydeo.com/rest/v1")
                .accept(contentType)
                .header("x-library-token",token);
    }

    @When("I send GET request to {string} endpoint")
    public void iSendGETRequestToEndpoint(String endpoint) {
        response = RestAssured.given()
                .spec(reqSpec)
                .when()
                .get(endpoint);
    }

    @Then("status code should be {int}")
    public void statusCodeShouldBe(int statusCode) {
        response
                .then()
                .statusCode(statusCode);
    }

    @And("Response Content type is {string}")
    public void responseContentTypeIs(String contentType) {
        response
                .then()
                .contentType(contentType);
    }

    @And("{string} field should not be null")
    public void fieldShouldNotBeNull(String field) {
        response
                .then()
                .body(field,is(notNullValue()));
    }
    //end 1

    @And("Path param {string} is {string}")
    public void pathParamIs(String pathParamName,String value) {
        pathPAramValue = value;
        reqSpec = reqSpec.pathParam(pathParamName,Integer.parseInt(value));
    }

    @And("{string} field should be same with path param")
    public void fieldShouldBeSameWithPathParam(String field) {
        response.then().body(field,is(equalTo(pathPAramValue)));

    }

    @Then("following fields should not be null")
    public void following_fields_should_not_be_null(List<String> fields) {
        fields.forEach(p -> response.then().body(p,is(notNullValue())));
    }

    //end 2

    @And("Request Content Type header is {string}")
    public void requestContentTypeHeaderIs(String arg0) {
        reqSpec.contentType(arg0);
    }

    @And("I create a random {string} as request body")
    public void iCreateARandomAsRequestBody(String arg0) {
        reqSpec.body(LibraryAPI_Util.getRandomBookMap());
    }

    @When("I send POST request to {string} endpoint")
    public void iSendPOSTRequestToEndpoint(String arg0) {

    }

    @And("the field value for {string} path should be equal to {string}")
    public void theFieldValueForPathShouldBeEqualTo(String arg0, String arg1) {
    }

    @And("I logged in Library UI as {string}")
    public void iLoggedInLibraryUIAs(String arg0) {
    }

    @And("I navigate to {string} page")
    public void iNavigateToPage(String arg0) {
    }

    @And("UI, Database and API created book information must match")
    public void uiDatabaseAndAPICreatedBookInformationMustMatch() {
    }

    @And("created user name should appear in Dashboard Page")
    public void createdUserNameShouldAppearInDashboardPage() {

    }

    @Given("I logged Library api with credentials {string} and {string}")
    public void iLoggedLibraryApiWithCredentialsAnd(String arg0, String arg1) {
    }

    @And("I send token information as request body")
    public void iSendTokenInformationAsRequestBody() {
    }
}
