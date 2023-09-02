
package com.library.steps;

import com.github.javafaker.Faker;
import com.library.pages.BasePage;
import com.library.pages.BookPage;
import com.library.pages.DashBoardPage;
import com.library.pages.LoginPage;
import com.library.utility.BrowserUtil;
import com.library.utility.DB_Util;
import com.library.utility.Driver;
import com.library.utility.LibraryAPI_Util;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.it.Ma;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.asynchttpclient.util.Assertions;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;

public class StepDefs {

    private String token;
    private RequestSpecification reqSpec;
    private Response response;
    private String pathParamValue;
    Map<String,String> bookAsMapFromApi;
    Map<String,String> userAsMapFromApi;
    LoginPage loginPage = new LoginPage();
    BasePage basePage = new BookPage();

    BookPage bookPage = new BookPage();

    Faker faker = new Faker();

    DashBoardPage dashBoardPage = new DashBoardPage();

    @Given("I logged Library api as a {string}")
    public void i_logged_library_api_as_a(String string) {
        token = LibraryAPI_Util.getToken(string);
    }

    @Given("Accept header is {string}")
    public void accept_header_is(String contentType) {
        reqSpec = RestAssured.given()
                .baseUri("https://library2.cydeo.com/rest/v1")
                .accept(contentType)
                .header("x-library-token",token);

    }
    @When("I send GET request to {string} endpoint")
    public void i_send_get_request_to_endpoint(String endpoint) {
        response = RestAssured.given()
                .spec(reqSpec)
                .when()
                .get(endpoint);


    }
    @Then("status code should be {int}")
    public void status_code_should_be(Integer statusCode) {
        response
                .then()
                .statusCode(statusCode);
    }
    @Then("Response Content type is {string}")
    public void response_content_type_is(String contentType) {
        response
                .then()
                .contentType(contentType);
    }
    @Then("{string} field should not be null")
    public void field_should_not_be_null(String field) {
        response
                .then()
                .body(field,notNullValue());
    }
    //end 1


    @Given("Path param {string} is {string}")
    public void path_param_is(String pathParam, String value) {
        pathParamValue = value;
        reqSpec = reqSpec.pathParam(pathParam,Integer.parseInt(value));
    }

    @Then("{string} field should be same with path param")
    public void field_should_be_same_with_path_param(String field) {
        response.then().body(field,is(equalTo(pathParamValue)));
    }
    @Then("following fields should not be null")
    public void following_fields_should_not_be_null(List<String> fields) {
        fields.forEach(p -> response.then().body(p,is(notNullValue())));
    }

    @Given("Request Content Type header is {string}")
    public void request_content_type_header_is(String contentType) {
        reqSpec = reqSpec.contentType(contentType);
    }
    @Given("I create a random {string} as request body")
    public void i_create_a_random_as_request_body(String contentItemType) {

        switch (contentItemType){
            case "book":
                bookAsMapFromApi = LibraryAPI_Util.getRandomBookMap();
                reqSpec = reqSpec.formParams(bookAsMapFromApi);
                break;
            case "user":
                userAsMapFromApi = LibraryAPI_Util.getRandomUserMap();
                reqSpec = reqSpec.formParams(userAsMapFromApi);
                break;
            default:
                throw new IllegalArgumentException("Given contentItemType: "
                        + contentItemType
                        + ". But must be \"book\" or \"user\"");
        }


    }
    @When("I send POST request to {string} endpoint")
    public void i_send_post_request_to_endpoint(String endpoint) {
        response = RestAssured
                .given()
                .spec(reqSpec)
                .when()
                .post(endpoint);
    }
    @Then("the field value for {string} path should be equal to {string}")
    public void the_field_value_for_path_should_be_equal_to(String path, String expectedValue) {
        if (expectedValue.equals("The book has been created.") && !response.jsonPath().getString("book_id").isEmpty()){
            bookAsMapFromApi.put("id",response.jsonPath().getString("book_id"));
        } else if (expectedValue.equals("The user has been created.") && !response.jsonPath().getString("user_id").isEmpty()) {
            userAsMapFromApi.put("id",response.jsonPath().getString("user_id"));
        }
        response.then().body(path,is(equalTo(expectedValue)));
    }

    @Given("I logged in Library UI as {string}")
    public void i_logged_in_library_ui_as(String role) {
        loginPage.login(role);
    }
    @Given("I navigate to {string} page")
    public void i_navigate_to_page(String moduleName) {
        basePage.navigateModule(moduleName);
    }
    @Then("UI, Database and API created book information must match")
    public void ui_database_and_api_created_book_information_must_match() {
        Map<String,String> bookAsMapFromUI =
                bookPage.getBookFromUi((String) bookAsMapFromApi.get("id"), (String) bookAsMapFromApi.get("name"));

        String query =
                "SELECT id,name,isbn,year,author,book_category_id,description FROM books WHERE id = "
                        + bookAsMapFromUI.get("id");

        DB_Util.runQuery(query);
        Map<String,String> bookAsMapFromDB = DB_Util.getRowMap(1);


        System.out.println("Number 1");

        Assert.assertEquals(bookAsMapFromApi,bookAsMapFromDB);
        System.out.println("Number 2");
        Assert.assertEquals(bookAsMapFromApi,bookAsMapFromUI);
    }

    @Then("created user information should match with Database")
    public void created_user_information_should_match_with_database() {

        String query =
                "SELECT id,full_name,email,password,user_group_id,status,start_date,end_date,address " +
                        "FROM users " +
                        "WHERE id =" + userAsMapFromApi.get("id");

        DB_Util.runQuery(query);
        Map<String,String> userAsMapFromDb = DB_Util.getRowMap(1);
        userAsMapFromDb.replace("password",userAsMapFromApi.get("password"));
        Assert.assertEquals(userAsMapFromApi,userAsMapFromDb);
    }
    @Then("created user should be able to login Library UI")
    public void created_user_should_be_able_to_login_library_ui() {

        loginPage.login((String) userAsMapFromApi.get("email"),(String)userAsMapFromApi.get("password"));
        BrowserUtil.waitForVisibility(dashBoardPage.dashboardMenuItem,10);
        Assert.assertTrue(Driver.getDriver().getCurrentUrl().contains("dashboard"));
    }
    @Then("created user name should appear in Dashboard Page")
    public void created_user_name_should_appear_in_dashboard_page() {

        Assert.assertEquals(userAsMapFromApi.get("full_name"),dashBoardPage.userNameBar.getAttribute("innerText").trim());
    }

    @Given("I logged Library api with credentials {string} and {string}")
    public void i_logged_library_api_with_credentials_and(String email, String password) {
        token = LibraryAPI_Util.getToken(email,password);
    }
    @Given("I send token information as request body")
    public void i_send_token_information_as_request_body() {
        reqSpec = reqSpec.formParam("token",token);
    }


}