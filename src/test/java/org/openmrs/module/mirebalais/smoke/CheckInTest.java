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

package org.openmrs.module.mirebalais.smoke;

import org.junit.Test;
import org.openmrs.module.mirebalais.smoke.pageobjects.CheckInFormPage;
import org.openmrs.module.mirebalais.smoke.pageobjects.VisitNote;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class CheckInTest extends DbTest {

    public CheckInFormPage newCheckIn;

	@Test
	public void createCheckInAndRemoveIt() throws Exception {

        logInAsAdmin();

        newCheckIn = new CheckInFormPage(driver);
        appDashboard.startClinicVisit();

        newCheckIn.findPatientAndClickOnCheckIn(adultTestPatient.getIdentifier());
        newCheckIn.enterInfoFillingTheFormTwice(getPaperRecordEnabled());

		assertThat(newCheckIn.isPatientSearchDisplayed(), is(true));

		appDashboard.goToVisitNoteVisitListAndSelectFirstVisit(adultTestPatient.getId());
		assertTrue(visitNote.hasActiveVisit());
		assertThat(visitNote.countEncountersOfType(VisitNote.CHECKIN_CREOLE_NAME), is(1));

		visitNote.deleteFirstEncounter();
        Thread.sleep(2000); // hack, sleep for 2 seconds to see if this stabilitizes this test or if there is something else going on

		assertThat(visitNote.countEncountersOfType(VisitNote.CHECKIN_CREOLE_NAME), is(0));
		assertTrue(visitNote.hasActiveVisit());
	}

    protected Boolean getPaperRecordEnabled() { return false; }
}
