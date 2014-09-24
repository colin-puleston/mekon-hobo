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

package uk.ac.manchester.cs.mekon.owl.reason;

import java.util.*;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.build.*;

/**
 * @author Colin Puleston
 */
public class ORMatcherTest extends OTest {

	static private final String JOB_CONCEPT = "Job";
	static private final String ACADEMIA_CONCEPT = "Academia";
	static private final String HEALTH_CONCEPT = "Health";
	static private final String TEACHER_CONCEPT = "Teacher";
	static private final String RESEARCHER_CONCEPT = "Researcher";
	static private final String DOCTOR_CONCEPT = "Doctor";
	static private final String POSTGRAD_CONCEPT = "Postgraduate";
	static private final String UNDERGRAD_CONCEPT = "Undergraduate";

	static private final String INDUSTRY_PROPERTY = "industry";
	static private final String JOB_TYPE_PROPERTY = "jobType";
	static private final String HOURLY_PAY_PROPERTY = "hourlyPay";
	static private final String TEACHES_PROPERTY = "teaches";

	static private final CIdentity UNDERGRAD_TEACHING_JOB_ID = new CIdentity("UndergradTeaching");
	static private final CIdentity POSTGRAD_TEACHING_JOB_ID = new CIdentity("PostgradTeaching");
	static private final CIdentity ACADEMIC_RESEARCH_JOB_ID = new CIdentity("AcademicResearch");
	static private final CIdentity DOCTORING_JOB_ID = new CIdentity("Doctoring");

	static private final int MIN_HOURLY_RATE = 10;
	static private final int LOW_HOURLY_RATE = 14;
	static private final int MID_HOURLY_RATE = 15;
	static private final int HIGH_HOURLY_RATE = 16;
	static private final int MAX_HOURLY_RATE = 20;

	private ORMatcher matcher;

	@Before
	public void setUp() {

		OModel model = TestOModel.create();
		OBSectionBuilder sectionBuilder = new OBSectionBuilder(model);

		matcher = new ORMatcher(model);

		sectionBuilder.setIMatcher(matcher);
		sectionBuilder.setIReasoner(new ORClassifier(model));

		buildModel(sectionBuilder);
	}

	@Test
	public void test_populate() {

		populate();
	}

	@Test
	public void test_retrieveAll() {

		populate();

		executeQuery(
			createJobQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID,
			ACADEMIC_RESEARCH_JOB_ID,
			DOCTORING_JOB_ID);
	}

	@Test
	public void test_conceptBasedQueries() {

		populate();

		executeQuery(
			createAcademiaQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID,
			ACADEMIC_RESEARCH_JOB_ID);

		executeQuery(
			createAcademicTeachingQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID);

		executeQuery(
			createPostgraduateTeachingQuery(),
			POSTGRAD_TEACHING_JOB_ID);

		executeQuery(
			createPostOrUndergradTeachingQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID);
	}

	@Test
	public void test_numberBasedQueries() {

		populate();

		executeQuery(
			createNumericJobQuery(MIN_HOURLY_RATE, MAX_HOURLY_RATE),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID,
			ACADEMIC_RESEARCH_JOB_ID,
			DOCTORING_JOB_ID);


		executeQuery(
			createNumericJobQuery(MIN_HOURLY_RATE, LOW_HOURLY_RATE - 1));

		executeQuery(
			createNumericJobQuery(MIN_HOURLY_RATE, MID_HOURLY_RATE - 1),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID);

		executeQuery(
			createNumericJobQuery(MIN_HOURLY_RATE, HIGH_HOURLY_RATE - 1),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID,
			ACADEMIC_RESEARCH_JOB_ID);


		executeQuery(
			createNumericJobQuery(HIGH_HOURLY_RATE + 1, MAX_HOURLY_RATE));

		executeQuery(
			createNumericJobQuery(MID_HOURLY_RATE + 1, MAX_HOURLY_RATE),
			DOCTORING_JOB_ID);

		executeQuery(
			createNumericJobQuery(LOW_HOURLY_RATE + 1, MAX_HOURLY_RATE),
			ACADEMIC_RESEARCH_JOB_ID,
			DOCTORING_JOB_ID);
	}

	private void populate() {

		addUndergraduateTeachingJob();
		addPostgraduateTeachingJob();
		addAcademicResearchJob();
		addDoctorJob();
	}

	private void addUndergraduateTeachingJob() {

		addAcademicTeachingJob(UNDERGRAD_TEACHING_JOB_ID, UNDERGRAD_CONCEPT);
	}

	private void addPostgraduateTeachingJob() {

		addAcademicTeachingJob(POSTGRAD_TEACHING_JOB_ID, POSTGRAD_CONCEPT);
	}

	private void addAcademicTeachingJob(CIdentity jobId, String studentTypeConcept) {

		IFrame job = createJob(ACADEMIA_CONCEPT, TEACHER_CONCEPT, LOW_HOURLY_RATE);
		IFrame studentType = createIFrame(studentTypeConcept);

		addISlotValue(job, TEACHES_PROPERTY, studentType);

		addInstance(job, jobId);
	}

	private void addAcademicResearchJob() {

		IFrame job = createJob(ACADEMIA_CONCEPT, RESEARCHER_CONCEPT, MID_HOURLY_RATE);

		addInstance(job, ACADEMIC_RESEARCH_JOB_ID);
	}

	private void addDoctorJob() {

		IFrame job = createJob(HEALTH_CONCEPT, DOCTOR_CONCEPT, HIGH_HOURLY_RATE);

		addInstance(job, DOCTORING_JOB_ID);
	}

	private IFrame createJob(
						String industryConcept,
						String jobTypeConcept,
						int hourlyPay) {

		IFrame job = createIFrame(JOB_CONCEPT);
		IFrame industry = createIFrame(industryConcept);
		IFrame jobType = createIFrame(jobTypeConcept);

		addISlotValue(job, INDUSTRY_PROPERTY, industry);
		addISlotValue(job, JOB_TYPE_PROPERTY, jobType);
		addISlotValue(job, HOURLY_PAY_PROPERTY, new INumber(hourlyPay));

		return job;
	}

	private IFrame createAcademiaQuery() {

		IFrame job = createJobQuery();
		IFrame industry = createQueryIFrame(ACADEMIA_CONCEPT);

		addISlotValue(job, INDUSTRY_PROPERTY, industry);

		return job;
	}

	private IFrame createAcademicTeachingQuery() {

		IFrame job = createAcademiaQuery();
		IFrame jobType = createQueryIFrame(TEACHER_CONCEPT);

		addISlotValue(job, JOB_TYPE_PROPERTY, jobType);

		return job;
	}

	private IFrame createPostgraduateTeachingQuery() {

		IFrame job = createAcademicTeachingQuery();
		IFrame studentType = createQueryIFrame(POSTGRAD_CONCEPT);

		addISlotValue(job, TEACHES_PROPERTY, studentType);

		return job;
	}

	private IFrame createPostOrUndergradTeachingQuery() {

		IFrame job = createAcademicTeachingQuery();
		CFrame studentTypeConcept = createPostOrUndergradDisjunction();
		IFrame studentType = studentTypeConcept.instantiateQuery();

		addISlotValue(job, TEACHES_PROPERTY, studentType);

		return job;
	}

	private IFrame createNumericJobQuery(int minHourlyPay, int maxHourlyPay) {

		IFrame job = createJobQuery();
		INumber hourlyPay = createRangeAsINumber(minHourlyPay, maxHourlyPay);

		addISlotValue(job, HOURLY_PAY_PROPERTY, hourlyPay);

		return job;
	}

	private IFrame createJobQuery() {

		return createQueryIFrame(JOB_CONCEPT);
	}

	private CFrame createPostOrUndergradDisjunction() {

		CFrame postgrad = getCFrame(POSTGRAD_CONCEPT);
		CFrame undergrad = getCFrame(UNDERGRAD_CONCEPT);

		return CFrame.resolveDisjunction(list(postgrad, undergrad));
	}

	private INumber createRangeAsINumber(int min, int max) {

		return CIntegerDef.range(min, max).createNumber().asINumber();
	}

	private void addInstance(IFrame instance, CIdentity id) {

		assertTrue(matcher.add(instance, id));
	}

	private void executeQuery(IFrame query, CIdentity... expectedMatches) {

		List<CIdentity> matches = matcher.match(query).getMatches();

		testListContents(matches, list(expectedMatches));
	}
}
