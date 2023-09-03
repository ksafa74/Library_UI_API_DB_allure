package com.library.pages;

import com.library.utility.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import java.util.LinkedHashMap;
import java.util.Map;

public class BookSearchPage extends BasePage {

    @FindBy(xpath = "//tr[@role='row']//td[3]")
    public WebElement bookNameTextField;

    @FindBy(xpath = "//tr[@role='row']//td[2]")
    public WebElement bookIsbnTextField;

    public void clickEditByIsbn (String isbn){
        Driver.getDriver().findElement(By.xpath("//td[.='"+isbn+"']/..//a")).click();
    }

    public Map<String,Object> getBookAsMap(){
        Map<String,Object> book = new LinkedHashMap<>();
        book.put("name",Driver.getDriver().findElement(By.xpath("//input[@name='name']")).getAttribute("value"));
        book.put("isbn",Integer.parseInt(Driver.getDriver().findElement(By.xpath("//input[@name='isbn']")).getAttribute("value")) );
        book.put("year",Integer.parseInt(Driver.getDriver().findElement(By.xpath("//input[@name='year']")).getAttribute("value")));
        book.put("author",Driver.getDriver().findElement(By.xpath("//input[@name='author']")).getAttribute("value"));
        Select select = new Select(Driver.getDriver().findElement(By.xpath("//select[@id='book_group_id']")));
        String selectText = select.getFirstSelectedOption().getAttribute("value");
        book.put("book_category_id",selectText);
        book.put("description",Driver.getDriver().findElement(By.xpath("//textarea[@id='description']")).getText());
        return book;
    }
}
