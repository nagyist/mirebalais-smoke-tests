package org.openmrs.module.mirebalais.smoke.pageobjects.loginpages;

import org.openmrs.module.mirebalais.smoke.helper.SmokeTestProperties;
import org.openmrs.module.mirebalais.smoke.pageobjects.loginpages.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PeruLoginPage extends LoginPage {

    public static final String MAIN_LOCATION = "Centro Medico Oscar Romero (COR)";

    private SmokeTestProperties properties = new SmokeTestProperties();

    public PeruLoginPage(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    public String getLocale() {
        return "es_PE";
    }

    @Override
    public void logInAsAdmin() {
        this.logIn("admin", properties.getAdminUserPassword(), MAIN_LOCATION);
    }

    @Override
    public void logIn(String user, String password, String location) {
        driver.findElement(By.id("username")).sendKeys(user);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.xpath("//*[text()='" + location + "']")).click();
        driver.findElement(By.id("login-button")).click();
    }
}
