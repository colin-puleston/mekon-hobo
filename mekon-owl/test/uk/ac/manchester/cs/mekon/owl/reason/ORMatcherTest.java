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
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.build.*;

/**
 * @author Colin Puleston
 */
public abstract class ORMatcherTest extends OTest {

	static private final String JOB_CONCEPT = "Job";
	static private final String ACADEMIA_CONCEPT = "Academia";
	static private final String HEALTH_CONCEPT = "Health";
	static private final String TEACHER_CONCEPT = "Teacher";
	static private final String RESEARCHER_CONCEPT = "Researcher";
	static private final String DOCTOR_CONCEPT = "Doctor";
	static private final String UNI_STUDENT_CONCEPT = "UniversityStudent";
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

	static private final String NON_OWL_TYPE = "NON-OWL-TYPE";
	static private final String NON_OWL_BUT_OWL_SUBSUMED_TYPE = "NON-OWL-BUT-OWL-SUBSUMED-TYPE";

	static private final int MIN_HOURLY_RATE = 10;
	static private final int LOW_HOURLY_RATE = 14;
	static private final int MID_HOURLY_RATE = 15;
	static private final int HIGH_HOURLY_RATE = 16;
	static private final int MAX_HOURLY_RATE = 20;

	private ORMatcher matcher;
	private Map<CIdentity, IFrame> storedInstancesById = new HashMap<CIdentity, IFrame>();

	private class LocalSectionBuilder extends OBSectionBuilder {

		public void build(CBuilder builder) {

			super.build(builder);

			addFrame(builder, NON_OWL_TYPE);
			addFrame(builder, NON_OWL_BUT_OWL_SUBSUMED_TYPE);
			addSuperFrame(builder, NON_OWL_BUT_OWL_SUBSUMED_TYPE, JOB_CONCEPT);
		}

		LocalSectionBuilder(OModel model) {

			super(model);
		}

		private CFrame addFrame(CBuilder builder, String name) {

			return builder.addFrame(nameToIdentity(name), false);
		}

		private void addSuperFrame(CBuilder builder, String subName, String supName) {

			CFrame sub = getFrame(builder, subName);
			CFrame sup = getFrame(builder, supName);

			builder.getFrameEditor(sub).addSuper(sup);
		}

		private CFrame getFrame(CBuilder builder, String name) {

			return builder.getFrames().get(nameToIdentity(name));
		}
	}

	@Before
	public void setUp() {

		OModel model = TestOModel.create();
		OBSectionBuilder sectionBuilder = new LocalSectionBuilder(model);

		matcher = createMatcher(model);

		sectionBuilder.setIMatcher(matcher);
		sectionBuilder.setIReasoner(new ORClassifier(model));

		buildModel(sectionBuilder);
	}

	@Test
	public void test_handlesType() {

		testHandlesType(JOB_CONCEPT, true);
		testHandlesType(NON_OWL_TYPE, false);
		testHandlesType(NON_OWL_BUT_OWL_SUBSUMED_TYPE, true);
	}

	@Test
	public void test_addAndQuery() {

		populate();

		testMatching(
			createJobQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID,
			ACADEMIC_RESEARCH_JOB_ID,
			DOCTORING_JOB_ID);
	}

	@Test
	public void test_addRemoveAndQuery() {

		populate();

		removeInstance(POSTGRAD_TEACHING_JOB_ID);
		removeInstance(ACADEMIC_RESEARCH_JOB_ID);

		testMatching(
			createJobQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			DOCTORING_JOB_ID);
	}

	@Test
	public void test_conceptBasedQueries() {

		populate();

		testMatching(
			createAcademiaQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID,
			ACADEMIC_RESEARCH_JOB_ID);

		testMatching(
			createAcademicTeachingQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID);

		testMatching(
			createPostgraduateTeachingQuery(),
			POSTGRAD_TEACHING_JOB_ID);

		testMatching(
			createPostOrUndergradTeachingQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID);

		testMatching(
			createUniStudentTeachingQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID);
	}

	@Test
	public void test_numberBasedQueries() {

		populate();

		testMatching(
			createNumericJobQuery(MIN_HOURLY_RATE, MAX_HOURLY_RATE),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID,
			ACADEMIC_RESEARCH_JOB_ID,
			DOCTORING_JOB_ID);

		testMatching(
			createNumericJobQuery(MIN_HOURLY_RATE, LOW_HOURLY_RATE - 1));

		testMatching(
			createNumericJobQuery(MIN_HOURLY_RATE, MID_HOURLY_RATE - 1),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID);

		testMatching(
			createNumericJobQuery(MIN_HOURLY_RATE, HIGH_HOURLY_RATE - 1),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID,
			ACADEMIC_RESEARCH_JOB_ID);

		testMatching(
			createNumericJobQuery(HIGH_HOURLY_RATE + 1, MAX_HOURLY_RATE));

		testMatching(
			createNumericJobQuery(MID_HOURLY_RATE + 1, MAX_HOURLY_RATE),
			DOCTORING_JOB_ID);

		testMatching(
			createNumericJobQuery(LOW_HOURLY_RATE + 1, MAX_HOURLY_RATE),
			ACADEMIC_RESEARCH_JOB_ID,
			DOCTORING_JOB_ID);
	}

	protected abstract ORMatcher createMatcher(OModel model);

	private void testHandlesType(String typeId, boolean shouldHandle) {

		assertTrue(matcher.handlesType(getCFrame(typeId)) == shouldHandle);
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

	private IFrame createUniStudentTeachingQuery() {

		return createStudentTeachingQuery(UNI_STUDENT_CONCEPT);
	}

	private IFrame createPostgraduateTeachingQuery() {

		return createStudentTeachingQuery(POSTGRAD_CONCEPT);
	}

	private IFrame createPostOrUndergradTeachingQuery() {

		CFrame studentTypeConcept = createPostOrUndergradDisjunction();
		IFrame studentType = studentTypeConcept.instantiateQuery();

		return createStudentTeachingQuery(studentType);
	}

	private IFrame createStudentTeachingQuery(String studentTypeConcept) {

		return createStudentTeachingQuery(createQueryIFrame(studentTypeConcept));
	}

	private IFrame createStudentTeachingQuery(IFrame studentType) {

		IFrame job = createAcademicTeachingQuery();

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

		return CFrame.resolveDisjunction(Arrays.asList(postgrad, undergrad));
	}

	private INumber createRangeAsINumber(int min, int max) {

		return CIntegerDef.range(min, max).createNumber().asINumber();
	}

	private void addInstance(IFrame instance, CIdentity id) {

		matcher.add(instance, id);
		storedInstancesById.put(id, instance);
	}

	private void removeInstance(CIdentity id) {

		matcher.remove(id);
		storedInstancesById.remove(id);
	}

	private void testMatching(IFrame query, CIdentity... expectedMatchIds) {

		List<CIdentity> matchIds = matcher.match(query).getAllMatches();

		testListContents(matchIds, Arrays.asList(expectedMatchIds));

		for (CIdentity id : storedInstancesById.keySet()) {

			IFrame instance = storedInstancesById.get(id);
			boolean isMatch = matcher.matches(query, instance);

			assertTrue(isMatch == matchIds.contains(id));
		}
	}
}
