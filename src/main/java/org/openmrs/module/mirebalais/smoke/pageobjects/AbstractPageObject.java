/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.mirebalais.smoke.pageobjects;

import org.openmrs.module.mirebalais.smoke.helper.SmokeTestProperties;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.function.Function;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

public abstract class AbstractPageObject {

    protected SmokeTestProperties properties = new SmokeTestProperties();

    protected WebDriver driver;
    public WebDriverWait wait5seconds;
    public WebDriverWait wait15seconds;

    public WebDriverWait wait30seconds;
    public WebDriverWait wait2minutes;
    public WebDriverWait wait5minutes;
    private String baseServerUrl;

    public AbstractPageObject(WebDriver driver) {
        this.driver = driver;
        this.wait5seconds = new WebDriverWait(driver, 5);
        this.wait15seconds = new WebDriverWait(driver,15);
        this.wait30seconds = new WebDriverWait(driver,30);
        this.wait2minutes = new WebDriverWait(driver, 120);
        this.wait5minutes = new WebDriverWait(driver, 600);

        setBaseServerUrl();
    }

    protected void gotoPage(String addressSufix) {
        driver.get(baseServerUrl + addressSufix);
    }

    private void setBaseServerUrl() {
        String serverUrl = System.getProperty("baseServerUrl");
        this.baseServerUrl = (serverUrl == null || serverUrl.isEmpty() ? properties.getWebAppUrl() : serverUrl);
    }

    public void clickNext() {
        clickOn(By.id("right-arrow-yellow"));
    }

    public void clickYellowCheckMark() {
    	clickOn(By.id("checkmark-yellow"));
    }

    public void setTextToField(By element, String text) {
        wait5seconds.until(visibilityOfElementLocated(element));  // TODO hack until chromedriver bug is fixed
        setText(driver.findElement(element), text);
    }

    public void setTextToField(String textFieldId, String text) {
        setTextToField(By.id(textFieldId), text);
    }

    public void setClearTextToFieldThruSpan(String spanId, String text) {
    	setText(findTextFieldInsideSpan(spanId), text);
	}

    public void hitTabKey() {
        driver.switchTo().activeElement().sendKeys(Keys.TAB);
    }

    public void hitTabKey(By elementId) {
        driver.findElement(elementId).sendKeys(Keys.TAB);
    }

    public void hitEnterKey() {
        driver.switchTo().activeElement().sendKeys(Keys.ENTER);
    }


    public void hitEnterKey(By elementId) {
        driver.findElement(elementId).sendKeys(Keys.ENTER);
    }

    private void setText(WebElement element, String text) {
    	element.clear();
		element.sendKeys(text);
        element.sendKeys(Keys.RETURN);
    }

    private WebElement findTextFieldInsideSpan(String spanId) {
    	return driver.findElement(By.id(spanId)).findElement(By.tagName("input"));
    }

    public String format(String patientName) {
		int index = patientName.indexOf(" ");
		return new StringBuffer()
					.append(patientName.substring(index).trim())
					.append(", ")
					.append(patientName.substring(0,index).trim())
					.toString();
	}

    public void findPatientById(String patientIdentifier, By idField) throws Exception {
		WebElement element = driver.findElement(idField);
		element.sendKeys(patientIdentifier);
		element.sendKeys(Keys.RETURN);
	}

    private void clickOnTheRightPatient(String patientIdentifier) throws Exception {
	    clickOnOptionLookingForText(patientIdentifier, By.cssSelector("li.ui-menu-item"));
	}

    public void clickOnOptionLookingForText(String text, By by) throws Exception {
		getOptionBasedOnText(text,by).click();
	}

    public WebElement getOptionBasedOnText(String text, By elementIdentifier) throws Exception {
		List<WebElement> options = driver.findElements(elementIdentifier);
		for (WebElement option : options) {
	        if(option.getText().contains(text)) {
	            return option;
	        }
	    }
		throw new Exception("Option not found");
    }

    public void clickOnRandomOption(By elementIdentifier) {
    	getRandomOption(elementIdentifier).click();
    }

    public WebElement getRandomOption(By elementIdentifier) {
    	List<WebElement> options = driver.findElements(elementIdentifier);
    	return options.get((int)(Math.random() * options.size()));
    }

    public void clickOn(WebElement element) {
        // Tricky ways to try to get deterministic click behavior in Selenium
        //   see https://stackoverflow.com/questions/31725033/selenium-click-not-always-working/31725102#31725102
        // Simple
//        element.click();
        // Trick #2
//        Actions actions = new Actions(driver);
//        actions.moveToElement(element).click().build().perform();
        // Trick #3
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", element);
        // Trick #4 (from comments)
//        element.sendKeys(Keys.ENTER);
    }

    public void clickOn(By byClause) {
        // see https://stackoverflow.com/questions/31725033/selenium-click-not-always-working/31725102#31725102
        // Trick #1
        WebElement element = wait15seconds.until(ExpectedConditions.elementToBeClickable(byClause));
        clickOn(element);
	}

	public <V> V clickUntil(WebElement element, Function<? super WebDriver, V> isTrue, int timeout) {
        long startMillis = System.currentTimeMillis();
        while (true) {
            element.click();
            try {
                return (new WebDriverWait(driver, 1)).until(isTrue);
            } catch (TimeoutException e) {
                if ((System.currentTimeMillis() - startMillis) / 1000 > timeout) {
                    throw e;
                }
            }
        }
    }

	/**
	 * This implementation was necessary as some pages had elements on the page that were disconnected from the dom,
	 * or which were hidden as templates.  The behavior desired for several use cases is to click on the first element
	 * matching a given selector that you find, usually by class.  This will first wait for any element to be available
	 * on the page that is in a clickable state, and then iterate across all elements found and click on the first one
	 * that is actually clickable.
	 */
	public void clickOnFirst(By elementId) {
	    wait15seconds.until(anyClickable(elementId));
	    boolean clicked = false;
	    for (WebElement webElement : driver.findElements(elementId)) {
		    try {
			    if (webElement.isEnabled() && webElement.isDisplayed()) {
				    clickOn(webElement);
				    clicked = true;
			    }
		    }
		    catch (StaleElementReferenceException e) {
			    // Do nothing here, this means that this is not attached to the DOM and we want to skip it
		    }
	    }
	    if (!clicked) {
	    	throw new InvalidElementStateException(elementId + " is unable to be clicked");
	    }
    }

    public void clickOnLast(By elementId) {
        List<WebElement> elements = driver.findElements(elementId);
        clickOn(elements.get(elements.size()-1));
    }

    public void hoverOn(By elementId) {
        Actions builder = new Actions(driver);
        Actions hover = builder.moveToElement(driver.findElement(elementId));
        hover.perform();
    }

    public void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public void selectFromDropdown(String id, int elementPosition) {
        findSelectOptions(By.id(id)).get(elementPosition).click();
    }

    public void selectFromDropdown(By element, int elementPosition) {
        findSelectOptions(element).get(elementPosition).click();
    }

    public List<WebElement> findSelectOptions(By element) {
        return driver.findElement(element).findElements(By.tagName("option"));
    }

    protected WebElement findOptionByText(String text, WebElement selectElement) {
        for (WebElement option : selectElement.findElements(By.tagName("option"))) {
            if (option.getText().contains(text)) {
                return option;
            }
        }
        return null;
    }

    // Additional Expected Conditions

	/**
	 * This returns true condition if any clickable element identified by the locator is found
	 */
	public static ExpectedCondition<Boolean> anyClickable(final By locator) {
		return new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				try {
					for (WebElement webElement : driver.findElements(locator)) {
						if (webElement.isEnabled() && webElement.isDisplayed()) {
							return true;
						}
					}
				} catch (StaleElementReferenceException e) {
					// Do nothing,
				}
				return false;
			}

			public String toString() {
				return "at least one element clickable: " + locator;
			}
		};
	}
}
