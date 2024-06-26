package org.openmrs.module.mirebalais.smoke.pageobjects;

import org.openmrs.module.mirebalais.smoke.helper.SmokeTestProperties;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.Keys.RETURN;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;

public class CheckInFormPage extends AbstractPageObject {

	private static final String CONFIRM_TEXT = "Konfime";

    public static final By SEARCH_FIELD = By.id("patient-search");

	public CheckInFormPage(WebDriver driver) {
		super(driver);
	}

    public void findPatientAndClickOnCheckIn(String patientIdentifier) throws Exception {
        findPatient(patientIdentifier);
        clickOnCheckIn();
    }

    public void enterInfo(Boolean paperRecordEnabled) {
        selectThirdOptionFor("typeOfVisit");
        selectSecondOptionFor("paymentAmount");
        findInputInsideSpan("receiptNumber").sendKeys("receipt #" + Keys.RETURN);
        selectNotToPrintWristbandIfQuestionPresent();
        clickConfirm();
        if (paperRecordEnabled) {
            confirmPaperRecordPopup();
        }
        wait2minutes.until(invisibilityOfElementLocated(By.className("submitButton")));  // make sure the submit is complete
    }

    public void enterInfoWithMultipleEnterKeystrokesOnSubmit()  {
        selectThirdOptionFor("typeOfVisit");
        selectSecondOptionFor("paymentAmount");
        findInputInsideSpan("receiptNumber").sendKeys("receipt #" + Keys.RETURN);
        selectNotToPrintWristbandIfQuestionPresent();

        WebElement confirmButton = driver.findElement(By.id("confirmationQuestion")).findElement(By.className("confirm"));
        confirmButton.sendKeys(RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,
                RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,
                RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN,RETURN);

    }

	public void enterInfoFillingTheFormTwice(Boolean paperRecordEnabled) throws Exception {
        selectThirdOptionFor("typeOfVisit");
        selectSecondOptionFor("paymentAmount");
        findInputInsideSpan("receiptNumber").sendKeys("receipt #" + Keys.RETURN);
        selectNotToPrintWristbandIfQuestionPresent();
        clickOnNoButton();
        selectSecondOptionFor("typeOfVisit");
        selectSecondOptionFor("paymentAmount");
        findInputInsideSpan("receiptNumber").sendKeys("receipt #" + Keys.RETURN);
        selectNotToPrintWristbandIfQuestionPresent();
		clickConfirm();
        if (paperRecordEnabled) {
            confirmPaperRecordPopup();
        }
        wait15seconds.until(invisibilityOfElementLocated(By.className("submitButton")));  // make sure the submit is complete
	}

	private void findPatient(String patientIdentifier) throws Exception {
		super.findPatientById(patientIdentifier, SEARCH_FIELD);
	}

	private void clickOnCheckIn() {
		clickOn(By.id("pih.checkin.registrationAction"));
	}

	private void clickConfirm() {
        clickUntil(driver.findElement(By.cssSelector("#confirmationQuestion .confirm")),
                ExpectedConditions.visibilityOfElementLocated(By.className("icon-spinner")),
                15);
	}

    private void confirmDataForScheduleAppointment() {
        clickOn(By.cssSelector("#confirmationQuestion .confirm"));
    }

	private void clickOnNoButton() {
		clickOnConfirmationTab();
		clickOn(By.cssSelector("#confirmationQuestion input.cancel"));
	}

	private void clickOnConfirmationTab() {
		List<WebElement> elements = driver.findElements(By.cssSelector("#formBreadcrumb span"));
		for (WebElement element : elements) {
	        if(element.getText().contains(CONFIRM_TEXT)) {
	        	element.click();
	        }
	    }
	}

	private void confirmPaperRecordPopup() {
        new WebDriverWait(driver, 5).until(ExpectedConditions.visibilityOfElementLocated(By.id("create-paper-record-dialog")));
        clickOn(By.cssSelector("#create-paper-record-dialog button"));
	}

    // TODO: revert https://github.com/PIH/mirebalais-smoke-tests/commit/e9ab41b02f4c263362b3627cd9e9b3cde951bd4f
    // TODO: after this chrome bug is fixed: https://code.google.com/p/chromium/issues/detail?id=513768
    private void selectOptionFor(String spanId, int option) {
        WebElement select = findSelectInsideSpan(spanId);
        new Select(select).selectByIndex(option);
        select.sendKeys(RETURN);
    }

    private void selectFirstOptionFor(String spanId) {
        selectOptionFor(spanId, 0);
    }

    private void selectSecondOptionFor(String spanId) {
        selectOptionFor(spanId, 1);
    }

    private void selectThirdOptionFor(String spanId) {
        selectOptionFor(spanId, 2);
    }

    private WebElement findSelectInsideSpan(String spanId) {
        return driver.findElement(By.id(spanId)).findElement(By.tagName("select"));
    }

    private WebElement findInputInsideSpan(String spanId) {
        return driver.findElement(By.cssSelector("#" + spanId + " input"));

    }

	public boolean isPatientSearchDisplayed() {
		return driver.findElement(SEARCH_FIELD).isDisplayed();
	}

    private void selectNotToPrintWristbandIfQuestionPresent() {
        try {
            driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
            driver.findElement(By.id("print-wristband-question"));
            selectSecondOptionFor("print-wristband-question");
        }
        catch (NoSuchElementException e) {
            // ignore if question not present
        }
        finally {
            driver.manage().timeouts().implicitlyWait(SmokeTestProperties.IMPLICIT_WAIT_TIME, SECONDS);
        }
    }

}
