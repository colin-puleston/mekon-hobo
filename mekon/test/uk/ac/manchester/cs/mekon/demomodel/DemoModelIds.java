/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package uk.ac.manchester.cs.mekon.demomodel;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
public class DemoModelIds {

	static public final CIdentity CORE_CONCEPT = createId("CoreConcept");
	static public final CIdentity CONTENT_CONCEPT = createId("ContentConcept");

	static public final CIdentity INTER_CORE_PROPERTY = createId("interCoreProperty");
	static public final CIdentity NUMERIC_PROPERTY = createId("numericValue");

	static public final CIdentity CITIZEN = createId("Citizen");
	static public final CIdentity EMPLOYED_CITIZEN = createId("EmployedCitizen");
	static public final CIdentity UNEMPLOYED_CITIZEN = createId("UnemployedCitizen");

	static public final CIdentity PERSONAL = createId("Personal");
	static public final CIdentity TAX = createId("Tax");
	static public final CIdentity ZERO_TAX = createId("ZeroTax");
	static public final CIdentity BENEFIT = createId("Benefit");
	static public final CIdentity UNEMPLOYMENT_BENEFIT = createId("UnemploymentBenefit");
	static public final CIdentity EMPLOYMENT = createId("Employment");

	static public final CIdentity JOB = createId("Job");
	static public final CIdentity JOB_TYPE = createId("JobType");
	static public final CIdentity INDUSTRY = createId("Industry");
	static public final CIdentity SECTOR = createId("IndustrySector");

	static public final CIdentity TRAVEL_CLASS = createId("TravelClass");

	static public final CIdentity TAX_PROPERTY = createId("taxPaid");
	static public final CIdentity BENEFIT_PROPERTY = createId("benefitReceived");
	static public final CIdentity EMPLOYMENT_PROPERTY = createId("employment");
	static public final CIdentity JOB_PROPERTY = createId("job");
	static public final CIdentity INDUSTRY_PROPERTY = createId("industry");
	static public final CIdentity JOB_TYPE_PROPERTY = createId("jobType");
	static public final CIdentity PAY_RATE_PROPERTY = createId("hourlyPay");
	static public final CIdentity SECTOR_PROPERTY = createId("sector");
	static public final CIdentity TEACHES_PROPERTY = createId("teaches");
	static public final CIdentity NAME_PROPERTY = createId("name");
	static public final CIdentity AGE_PROPERTY = createId("age");
	static public final CIdentity ADDRESS_PROPERTY = createId("address");
	static public final CIdentity LOCATION_PROPERTY = createId("location");
	static public final CIdentity TRAVEL_CLASS_PROPERTY = createId("travelClass");

	static public final CIdentity SPECIALIST = createId("Specialist");
	static public final CIdentity ACADEMIC_JOB = createId("AcademicJob");
	static public final CIdentity TEACHER = createId("Teacher");
	static public final CIdentity LECTURER = createId("Lecturer");
	static public final CIdentity RESEARCHER = createId("Researcher");
	static public final CIdentity MEDIC = createId("Medic");
	static public final CIdentity DOCTOR = createId("Doctor");
	static public final CIdentity NURSE = createId("Nurse");
	static public final CIdentity PHYSIO = createId("Physio");

	static public final CIdentity ACADEMIA = createId("Academia");
	static public final CIdentity HEALTH = createId("Health");

	static public final CIdentity PUBLIC = createId("Public");
	static public final CIdentity PRIVATE = createId("Private");

	static public final CIdentity STUDENT = createId("Student");
	static public final CIdentity UNI_STUDENT = createId("UniversityStudent");
	static public final CIdentity POSTGRAD = createId("Postgraduate");
	static public final CIdentity UNDERGRAD = createId("Undergraduate");

	static public final CIdentity EU = createId("EU");
	static public final CIdentity UK = createId("UK");
	static public final CIdentity ENGLAND = createId("England");

	static public final CIdentity TRAIN_TRIP = createId("TrainTrip");

	static public final CIdentity ACADEMIC_TEACHING_JOB = createId("AcademicTeachingJob");
	static public final CIdentity RESEARCH_JOB = createId("ResearchJob");

	static private CIdentity createId(String name) {

		return new CIdentity("http://mekon/demo.owl#" + name, name);
	}
}
