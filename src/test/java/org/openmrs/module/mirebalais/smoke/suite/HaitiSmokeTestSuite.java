package org.openmrs.module.mirebalais.smoke.suite;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.openmrs.module.mirebalais.smoke.BasicSmokeTest;
import org.openmrs.module.mirebalais.smoke.CaptureVitalsTest;
import org.openmrs.module.mirebalais.smoke.PatientRegistrationHSNFlowTest;
import org.openmrs.module.mirebalais.smoke.PatientRegistrationHaitiFlowTest;
import org.openmrs.module.mirebalais.smoke.VisitNoteTest;
import org.openmrs.module.mirebalais.smoke.helper.SmokeTestDriver;
import org.openqa.selenium.WebDriver;

@RunWith(Suite.class)
@Suite.SuiteClasses({
//        CaptureVitalsTest.class,
        PatientRegistrationHaitiFlowTest.class//,
        // this has been disable since we don't currently have them turned on on HSN
      //  CaptureVitalsHSNTest.class,
       // CheckInHSNTest.class,
       // PatientRegistrationHSNFlowTest.class,
        // this has been disabled since the Thomonde Visit Note vitals flow is slightly different from the Mirebalais one
       // VisitNoteTest.class
                    })
public class HaitiSmokeTestSuite {

    private static WebDriver driver;

    @BeforeClass
    public static void startWebDriver() {
        driver = new SmokeTestDriver().getDriver();
        BasicSmokeTest.setDriver(driver);
    }

    @AfterClass
    public static void stopWebDriver() {
        driver.quit();
    }

    public static WebDriver getDriver() {
        return driver;
    }

}
