package com.jwt.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class LoginDriver {
    public static void main(String[] args){
        String url = "http://localhost:8080/tiantan/";
        System.setProperty("webdriver.chrome.driver", "D:\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get(url);
        String title = driver.getTitle();
        System.out.println(title);
    }
}
