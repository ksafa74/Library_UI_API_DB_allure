package com.library.steps;

import com.github.javafaker.Faker;
import com.library.pages.BasePage;
import com.library.pages.BookPage;
import com.library.pages.BookSearchPage;
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
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import java.util.*;

import static org.hamcrest.Matchers.*;

import static org.hamcrest.Matchers.*;


public class LibrarySteps {

    private String token;
    private RequestSpecification reqSpec;
    private Response response;
    private String pathPAramValue;
    private Map<String,Object> bookAsMapFromApi;
    private Map<String,Object> userAsMapFromApi;
    LoginPage loginPage;
    BookPage bookPage;
    BookSearchPage bookSearchPage;
    private  Map<String,String> bookAsMapStringFromUi;
    private Map<String,String> bookAsMapStringFromDb;
    private Map<String,String> bookAsMapStringFromApi;
    private  Map<String,String> userAsMapStringFromUi;
    private Map<String,String> userAsMapStringFromDb;
    private Map<String,String> userAsMapStringFromApi;
    private String userType;





    @Given("I logged Library api as a {string}")
    public void iLoggedLibraryApiAsA(String arg0) {
        userType = arg0;
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
    public void iCreateARandomAsRequestBody(String type) {
        if(type.equals("book")){
            bookAsMapFromApi = LibraryAPI_Util.getRandomBookMap();
            reqSpec= reqSpec.formParams(bookAsMapFromApi);
        }else if(type.equals("user")){
            userAsMapFromApi = LibraryAPI_Util.getRandomUserMap();
            reqSpec= reqSpec.formParams(userAsMapFromApi);
        }else {
            throw new RuntimeException("invalid input data");
        }

    }

    @When("I send POST request to {string} endpoint")
    public void iSendPOSTRequestToEndpoint(String arg0) {
        response = RestAssured.given()
                .spec(reqSpec)
                .when()
                .post(arg0).prettyPeek();
    }

    @And("the field value for {string} path should be equal to {string}")
    public void theFieldValueForPathShouldBeEqualTo(String arg0, String arg1) {
      Assert.assertEquals(response.path(arg0),arg1);
    }

    @And("I logged in Library UI as {string}")
    public void iLoggedInLibraryUIAs(String arg0) {
        loginPage = new LoginPage();
        loginPage.login(arg0);
        BrowserUtil.waitFor(1);
    }

    @And("I navigate to {string} page")
    public void iNavigateToPage(String arg0) {
        BrowserUtil.waitFor(1);
        bookPage = new BookPage();
        bookPage.navigateModule(arg0);
        BrowserUtil.waitFor(1);
    }

    @And("UI, Database and API created book information must match")
    public void uiDatabaseAndAPICreatedBookInformationMustMatch() {
        //api

        //ui
        bookSearchPage=new BookSearchPage();
        bookPage.search.sendKeys(bookAsMapFromApi.get("name").toString()+ Keys.ENTER);
        BrowserUtil.waitFor(1);
        bookSearchPage.clickEditByIsbn(bookAsMapFromApi.get("isbn").toString());

        Map<String, Object> bookAsMapFromUi = bookSearchPage.getBookAsMap();

        //db
        String query =
                "SELECT name,isbn,year,author,book_category_id,description FROM books WHERE isbn = "
                        + bookAsMapFromUi.get("isbn");
        DB_Util.runQuery(query);
        Map<String,Object> bookAsMapFromDB = DB_Util.getRowMap(1);


        System.out.println("bookAsMapFromUi = " + bookAsMapFromUi);
        System.out.println("bookAsMapFromDB = " + bookAsMapFromDB);
        System.out.println("bookAsMapFromApi = " + bookAsMapFromApi);

//        bookAsMapStringFromUi = new LinkedHashMap<>();
//        for (Map.Entry<String, Object> entry : bookAsMapFromUi.entrySet()) {
//            String key = entry.getKey();
//            Object value = entry.getValue();
//            String stringValue = String.valueOf(value);
//            bookAsMapStringFromUi.put(key, stringValue);
//        }
//        bookAsMapStringFromDb = new LinkedHashMap<>();
//        for (Map.Entry<String, Object> entry : bookAsMapFromDB.entrySet()) {
//            String key = entry.getKey();
//            Object value = entry.getValue();
//            String stringValue = String.valueOf(value);
//            bookAsMapStringFromDb.put(key, stringValue);
//        }
//        bookAsMapStringFromApi = new LinkedHashMap<>();
//        for (Map.Entry<String, Object> entry : bookAsMapFromApi.entrySet()) {
//            String key = entry.getKey();
//            Object value = entry.getValue();
//            String stringValue = String.valueOf(value);
//            bookAsMapStringFromApi.put(key, stringValue);
//        }

//        //assert  ui vs api
//        Assert.assertEquals(bookAsMapStringFromUi,bookAsMapStringFromApi);
//        //assert db vs api
//        Assert.assertEquals(bookAsMapStringFromDb,bookAsMapStringFromApi);
//        //assert db vs ui
//        Assert.assertEquals(bookAsMapStringFromUi,bookAsMapStringFromDb);

        //assert  ui vs api
        Assert.assertEquals(bookAsMapFromUi.toString(),bookAsMapFromApi.toString());
        //assert db vs api
        Assert.assertEquals(bookAsMapFromDB.toString(),bookAsMapFromApi.toString());
        //assert db vs ui
        Assert.assertEquals(bookAsMapFromUi.toString(),bookAsMapFromDB.toString());


    }

    //us4

    @And("created user name should appear in Dashboard Page")
    public void createdUserNameShouldAppearInDashboardPage() {
        String fullNameDashboardPage = Driver.getDriver().findElement(By.xpath("//a[@class='nav-link dropdown-toggle']//span")).getText();
        Assert.assertEquals(fullNameDashboardPage,userAsMapStringFromApi.get("full_name"));
    }


    @Given("I logged Library api with credentials {string} and {string}")
    public void iLoggedLibraryApiWithCredentialsAnd(String arg0, String arg1) {
        token = LibraryAPI_Util.getToken(arg0,arg1);

    }


    @And("created user information should match with Database")
    public void createdUserInformationShouldMatchWithDatabase() {
        DB_Util.runQuery("select full_name, email, password, user_group_id, status, start_date, end_date, address from users where full_name = '"+userAsMapFromApi.get("full_name")+"'");
        Map<String, Object> userMapDb = DB_Util.getRowMap(1);
        System.out.println("userMapDb = " + userMapDb);
        System.out.println("token = " + token);

        userAsMapStringFromDb = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : userMapDb.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String stringValue = String.valueOf(value);
            userAsMapStringFromDb.put(key, stringValue);
        }
        userAsMapStringFromApi = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : userAsMapFromApi.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String stringValue = String.valueOf(value);
            userAsMapStringFromApi.put(key, stringValue);
        }
        userAsMapStringFromDb.put("password","libraryUser");

        System.out.println("------------------------------------");
        System.out.println("userAsMapStringFromApi = " + userAsMapStringFromApi);
        System.out.println("------------------------------------");
        System.out.println("userAsMapStringFromDb = " + userAsMapStringFromDb);
        System.out.println("------------------------------------");
        //assert api db
        Assert.assertEquals(userAsMapStringFromDb,userAsMapStringFromApi);
    }

    @And("created user should be able to login Library UI")
    public void createdUserShouldBeAbleToLoginLibraryUI() {
        loginPage = new LoginPage();
        loginPage.login(userAsMapStringFromApi.get("email"),userAsMapStringFromApi.get("password"));
        BrowserUtil.waitFor(2);
        String title = Driver.getDriver().getTitle();
        Assert.assertEquals(title,"Library");


    }

    @And("I send token information as request body")
    public void iSendTokenInformationAsRequestBody() {
reqSpec.formParam(token);
    }

    @When("I send POST request to {string} endpoint to decode")
    public void iSendPOSTRequestToEndpointToDecode(String arg0) {
        response = RestAssured.given()
                .spec(reqSpec)
                .when()
                .param("token",token)
                .post(arg0).prettyPeek();
    }
}
