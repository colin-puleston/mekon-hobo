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

	static public final CIdentity CORE_CONCEPT = coreId("CoreConcept");
	static public final CIdentity DYNAMIC_CONCEPT = coreId("DynamicConcept");

	static public final CIdentity INTER_CORE_PROPERTY = coreId("interCoreProperty");
	static public final CIdentity NUMERIC_PROPERTY = coreId("numericValue");

	static public final CIdentity CITIZEN = coreId("Citizen");
	static public final CIdentity EMPLOYED_CITIZEN = coreId("EmployedCitizen");
	static public final CIdentity UNEMPLOYED_CITIZEN = coreId("UnemployedCitizen");

	static public final CIdentity PERSONAL = coreId("Personal");
	static public final CIdentity TAX = coreId("Tax");
	static public final CIdentity ZERO_TAX = coreId("ZeroTax");
	static public final CIdentity BENEFIT = coreId("Benefit");
	static public final CIdentity UNEMPLOYMENT_BENEFIT = coreId("UnemploymentBenefit");
	static public final CIdentity EMPLOYMENT = coreId("Employment");

	static public final CIdentity JOB = coreId("Job");
	static public final CIdentity JOB_TYPE = coreId("JobType");
	static public final CIdentity INDUSTRY = coreId("Industry");
	static public final CIdentity SECTOR = coreId("IndustrySector");

	static public final CIdentity TRAVEL_CLASS = coreId("TravelClass");

	static public final CIdentity TAX_PROPERTY = coreId("taxPaid");
	static public final CIdentity BENEFIT_PROPERTY = coreId("benefitReceived");
	static public final CIdentity EMPLOYMENT_PROPERTY = coreId("employment");
	static public final CIdentity JOB_PROPERTY = coreId("job");
	static public final CIdentity INDUSTRY_PROPERTY = coreId("industry");
	static public final CIdentity JOB_TYPE_PROPERTY = coreId("jobType");
	static public final CIdentity PAY_RATE_PROPERTY = coreId("hourlyPay");
	static public final CIdentity SECTOR_PROPERTY = coreId("sector");
	static public final CIdentity TEACHES_PROPERTY = coreId("teaches");
	static public final CIdentity NAME_PROPERTY = coreId("name");
	static public final CIdentity AGE_PROPERTY = coreId("age");
	static public final CIdentity ADDRESS_PROPERTY = coreId("address");
	static public final CIdentity LOCATION_PROPERTY = coreId("location");
	static public final CIdentity TRAVEL_CLASS_PROPERTY = coreId("travelClass");

	static public final CIdentity SPECIALIST = dynamicId("Specialist");
	static public final CIdentity ACADEMIC_JOB = dynamicId("AcademicJob");
	static public final CIdentity TEACHER = dynamicId("Teacher");
	static public final CIdentity LECTURER = dynamicId("Lecturer");
	static public final CIdentity RESEARCHER = dynamicId("Researcher");
	static public final CIdentity MEDIC = dynamicId("Medic");
	static public final CIdentity DOCTOR = dynamicId("Doctor");
	static public final CIdentity NURSE = dynamicId("Nurse");
	static public final CIdentity PHYSIO = dynamicId("Physio");

	static public final CIdentity ACADEMIA = dynamicId("Academia");
	static public final CIdentity HEALTH = dynamicId("Health");

	static public final CIdentity PUBLIC = dynamicId("Public");
	static public final CIdentity PRIVATE = dynamicId("Private");

	static public final CIdentity STUDENT = dynamicId("Student");
	static public final CIdentity UNI_STUDENT = dynamicId("UniversityStudent");
	static public final CIdentity POSTGRAD = dynamicId("Postgraduate");
	static public final CIdentity UNDERGRAD = dynamicId("Undergraduate");

	static public final CIdentity EU = dynamicId("EU");
	static public final CIdentity UK = dynamicId("UK");
	static public final CIdentity ENGLAND = dynamicId("England");

	static public final CIdentity TRAIN_TRIP = dynamicId("TrainTrip");

	static public final CIdentity ACADEMIC_TEACHING_JOB = dynamicExtnId("AcademicTeachingJob");
	static public final CIdentity RESEARCH_JOB = dynamicExtnId("ResearchJob");

	static private final String IDENTIFIER_FORMAT = "http://mekon/demo-%s.owl#%s";

	static private CIdentity coreId(String name) {

		return createId("core", name);
	}

	static private CIdentity dynamicId(String name) {

		return createId("dynamic", name);
	}

	static private CIdentity dynamicExtnId(String name) {

		return createId("dynamic-extn", name);
	}

	static private CIdentity createId(String fileSuffix, String name) {

		return new CIdentity(getIdentifier(fileSuffix, name), name);
	}

	static private String getIdentifier(String fileSuffix, String name) {

		return String.format(IDENTIFIER_FORMAT, fileSuffix, name);
	}
}
