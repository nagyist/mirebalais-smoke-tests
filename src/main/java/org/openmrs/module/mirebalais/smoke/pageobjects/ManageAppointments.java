package org.openmrs.module.mirebalais.smoke.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

public class ManageAppointments extends AbstractPageObject {
    public ManageAppointments(WebDriver driver) {
        super(driver);
    }

    public void enterPatientIdentifier(String patientID) throws InterruptedException {
        WebElement searchField = driver.findElement(By.id("patient-search"));
        searchField.sendKeys(patientID);
        searchField.sendKeys(Keys.RETURN);
    }

    public boolean isSelectServiceTypeDefined() {
        wait5seconds.until(presenceOfElementLocated(By.id("selectAppointmentType")));
        return (driver.findElement(By.id("selectAppointmentType")) != null) ? true : false;
    }

    public boolean isDateRangePickerDefined() {
        wait5seconds.until(presenceOfElementLocated(By.tagName("daterangepicker")));
        return (driver.findElement(By.tagName("daterangepicker")) != null) ? true : false;
    }

}
