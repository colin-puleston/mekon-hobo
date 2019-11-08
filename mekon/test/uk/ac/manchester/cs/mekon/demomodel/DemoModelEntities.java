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

/**
 * @author Colin Puleston
 */
public interface DemoModelEntities {

	static public final String CITIZEN = "Citizen";
	static public final String EMPLOYED_CITIZEN = "EmployedCitizen";
	static public final String UNEMPLOYED_CITIZEN = "UnemployedCitizen";

	static public final String PERSONAL = "Personal";
	static public final String TAX = "Tax";
	static public final String ZERO_TAX = "ZeroTax";
	static public final String BENEFIT = "Benefit";
	static public final String UNEMPLOYMENT_BENEFIT = "UnemploymentBenefit";
	static public final String EMPLOYMENT = "Employment";

	static public final String JOB = "Job";

	static public final String JOB_TYPE = "JobType";
	static public final String SPECIALIST = "Specialist";
	static public final String ACADEMIC_JOB = "AcademicJob";
	static public final String ACADEMIC_TEACHING_JOB = "AcademicTeachingJob";
	static public final String RESEARCH_JOB = "ResearchJob";
	static public final String TEACHER = "Teacher";
	static public final String LECTURER = "Lecturer";
	static public final String RESEARCHER = "Researcher";
	static public final String MEDIC = "Medic";
	static public final String DOCTOR = "Doctor";
	static public final String NURSE = "Nurse";
	static public final String PHYSIO = "Physio";

	static public final String INDUSTRY = "Industry";
	static public final String ACADEMIA = "Academia";
	static public final String HEALTH = "Health";

	static public final String SECTOR = "IndustrySector";
	static public final String PUBLIC = "Public";
	static public final String PRIVATE = "Private";

	static public final String STUDENT = "Student";
	static public final String UNI_STUDENT = "UniversityStudent";
	static public final String POSTGRAD = "Postgraduate";
	static public final String UNDERGRAD = "Undergraduate";

	static public final String EU = "EU";
	static public final String UK = "UK";
	static public final String ENGLAND = "England";

	static public final String TAX_PROPERTY = "taxPaid";
	static public final String BENEFIT_PROPERTY = "benefitReceived";
	static public final String EMPLOYMENT_PROPERTY = "employment";
	static public final String JOBS_PROPERTY = "job";
	static public final String INDUSTRY_PROPERTY = "industry";
	static public final String JOB_TYPE_PROPERTY = "jobType";
	static public final String PAY_RATE_PROPERTY = "hourlyPay";
	static public final String SECTOR_PROPERTY = "sector";
	static public final String TEACHES_PROPERTY = "teaches";
	static public final String NAME_PROPERTY = "name";
	static public final String ADDRESS_PROPERTY = "address";
	static public final String LOCATION_PROPERTY = "location";
}
