package org.openmrs.module.mirebalais.smoke;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.openmrs.module.mirebalais.smoke.pageobjects.selects.DurationUnit.DAYS;

import org.junit.Test;
import org.openmrs.module.mirebalais.smoke.dataModel.Patient;
import org.openmrs.module.mirebalais.smoke.helper.PatientDatabaseHandler;
import org.openmrs.module.mirebalais.smoke.pageobjects.AppDashboard;
import org.openmrs.module.mirebalais.smoke.pageobjects.PatientDashboard;
import org.openmrs.module.mirebalais.smoke.pageobjects.forms.DispenseMedicationForm;

public class DispensingTest extends DbTest {
	
	private String paracetamol = "Paracetamol (500mg tablet)";
	
	@Test
	public void pharmacistCanDispenseMedicationForAnExistingActiveVisit() throws Exception {
		AppDashboard appDashboard = new AppDashboard(driver);
		PatientDashboard patientDashboard = new PatientDashboard(driver);
		
		Patient patient = PatientDatabaseHandler.insertNewTestPatient();
		
		logInAsClinicalUser();
		appDashboard.goToPatientPage(patient.getId());
		patientDashboard.startVisit();
		assertThat("Dispense medication.", patientDashboard.canDispenseMedication(), is(false));
		logout();
		
		logInAsPharmacistUser();
		appDashboard.goToPatientPage(patient.getId());
		assertThat("Dispense medication.", patientDashboard.canDispenseMedication(), is(true));
		DispenseMedicationForm dispensingForm = patientDashboard.goToDispenseMedicationForm();
		dispensingForm.fillFirstMedication(paracetamol, "twice per day", "5", "mg", "7", DAYS, "20");
		dispensingForm.submit();
		patientDashboard.clickFirstEncounterDetails();

        assertThat("Medication was dispensed.", patientDashboard.countEncountersOfType("Medication dispensed"), is(1));

        PatientDashboard.MedicationDispensed medicationDispensed = patientDashboard.firstMedication();

        assertThat("Medication name is right.", medicationDispensed.getName(), is(("Paracetamol (500mg tablet)")));
        assertThat("Medication frequency is right.", medicationDispensed.getFrequency(), is("twice per day"));
        assertThat("Medication dose is right.", medicationDispensed.getDose(), is("5"));
        assertThat("Medication dose unit is right.", medicationDispensed.getDoseUnit(), is("mg"));
        assertThat("Medication duration is right.", medicationDispensed.getDuration(), is("7"));
        assertThat("Medication duration unit is right.", medicationDispensed.getDurationUnit(), is("Ane"));
        assertThat("Medication amount is right.", medicationDispensed.getAmount(), is("20"));

    }
	
}