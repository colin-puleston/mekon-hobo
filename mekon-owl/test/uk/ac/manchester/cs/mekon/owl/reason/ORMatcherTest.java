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
	static private final String PUBLIC_CONCEPT = "Public";
	static private final String PRIVATE_CONCEPT = "Private";
	static private final String UNI_STUDENT_CONCEPT = "UniversityStudent";
	static private final String POSTGRAD_CONCEPT = "Postgraduate";
	static private final String UNDERGRAD_CONCEPT = "Undergraduate";

	static private final String INDUSTRY_PROPERTY = "industry";
	static private final String JOB_TYPE_PROPERTY = "jobType";
	static private final String PAY_RATE_PROPERTY = "hourlyPay";
	static private final String SECTOR_PROPERTY = "sector";
	static private final String TEACHES_PROPERTY = "teaches";

	static private final CIdentity UNDERGRAD_TEACHING_JOB_ID = new CIdentity("UndergradTeaching");
	static private final CIdentity POSTGRAD_TEACHING_JOB_ID = new CIdentity("PostgradTeaching");
	static private final CIdentity ACADEMIC_RESEARCH_JOB_ID = new CIdentity("AcademicResearch");
	static private final CIdentity DOCTORING_JOB_ID = new CIdentity("Doctoring");

	static private final String NON_OWL_TYPE = "NON-OWL-TYPE";
	static private final String NON_OWL_BUT_OWL_SUBSUMED_TYPE = "NON-OWL-BUT-OWL-SUBSUMED-TYPE";

	static private final int MIN_PAY_RATE = 10;
	static private final int LOW_PAY_RATE = 14;
	static private final int MID_PAY_RATE = 15;
	static private final int HIGH_PAY_RATE = 16;
	static private final int MAX_PAY_RATE = 20;

	private ORMatcher matcher;
	private Map<CIdentity, IFrame> storedInstancesById = new HashMap<CIdentity, IFrame>();

	private IFrame undergradTeachingJob;
	private IFrame postgradTeachingJob;
	private IFrame academicResearchJob;
	private IFrame doctoringJob;

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

		undergradTeachingJob = addUndergradTeachingJob();
		postgradTeachingJob = addPostgradTeachingJob();
		academicResearchJob = addAcademicResearchJob();
		doctoringJob = addDoctoringJob();
	}

	@Test
	public void test_handlesType() {

		testHandlesType(JOB_CONCEPT, true);
		testHandlesType(NON_OWL_TYPE, false);
		testHandlesType(NON_OWL_BUT_OWL_SUBSUMED_TYPE, true);
	}

	@Test
	public void test_addAndQuery() {

		testMatching(
			createJobQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID,
			ACADEMIC_RESEARCH_JOB_ID,
			DOCTORING_JOB_ID);
	}

	@Test
	public void test_addRemoveAndQuery() {

		removeInstance(POSTGRAD_TEACHING_JOB_ID);
		removeInstance(ACADEMIC_RESEARCH_JOB_ID);

		testMatching(
			createJobQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			DOCTORING_JOB_ID);
	}

	@Test
	public void test_basicConceptBasedQueries() {

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
			createPostgradTeachingQuery(),
			POSTGRAD_TEACHING_JOB_ID);
	}

	@Test
	public void test_conceptAndPropertyBasedQueries() {

		testMatching(
			createUniStudentTeachingQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID);
	}

	@Test
	public void test_conceptDisjunctionBasedQueries() {

		testMatching(
			createPostOrUndergradTeachingQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID);
	}

	@Test
	public void test_instanceDisjunctionBasedQueries() {

		setIndustrySector(undergradTeachingJob, PUBLIC_CONCEPT);
		setIndustrySector(postgradTeachingJob, PRIVATE_CONCEPT);
		setIndustrySector(academicResearchJob, PRIVATE_CONCEPT);
		setIndustrySector(doctoringJob, PRIVATE_CONCEPT);

		testMatching(
			createHealthOrPublicSectorAcademicQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			DOCTORING_JOB_ID);
	}

	@Test
	public void test_numberBasedQueries() {

		setHourlyRate(undergradTeachingJob, LOW_PAY_RATE);
		setHourlyRate(postgradTeachingJob, MID_PAY_RATE);
		setHourlyRate(academicResearchJob, MID_PAY_RATE);
		setHourlyRate(doctoringJob, HIGH_PAY_RATE);

		testMatching(
			createPayRateQuery(MIN_PAY_RATE, MAX_PAY_RATE),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID,
			ACADEMIC_RESEARCH_JOB_ID,
			DOCTORING_JOB_ID);

		testMatching(
			createPayRateQuery(MIN_PAY_RATE, LOW_PAY_RATE - 1));

		testMatching(
			createPayRateQuery(MIN_PAY_RATE, MID_PAY_RATE - 1),
			UNDERGRAD_TEACHING_JOB_ID);

		testMatching(
			createPayRateQuery(MIN_PAY_RATE, HIGH_PAY_RATE - 1),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID,
			ACADEMIC_RESEARCH_JOB_ID);

		testMatching(
			createPayRateQuery(HIGH_PAY_RATE + 1, MAX_PAY_RATE));

		testMatching(
			createPayRateQuery(MID_PAY_RATE + 1, MAX_PAY_RATE),
			DOCTORING_JOB_ID);

		testMatching(
			createPayRateQuery(LOW_PAY_RATE + 1, MAX_PAY_RATE),
			POSTGRAD_TEACHING_JOB_ID,
			ACADEMIC_RESEARCH_JOB_ID,
			DOCTORING_JOB_ID);
	}

	protected abstract ORMatcher createMatcher(OModel model);

	private void testHandlesType(String typeId, boolean shouldHandle) {

		assertTrue(matcher.handlesType(getCFrame(typeId)) == shouldHandle);
	}

	private IFrame addUndergradTeachingJob() {

		return addAcademicTeachingJob(UNDERGRAD_TEACHING_JOB_ID, UNDERGRAD_CONCEPT);
	}

	private IFrame addPostgradTeachingJob() {

		return addAcademicTeachingJob(POSTGRAD_TEACHING_JOB_ID, POSTGRAD_CONCEPT);
	}

	private IFrame addAcademicTeachingJob(CIdentity jobId, String studentTypeConcept) {

		IFrame job = createJob(ACADEMIA_CONCEPT, TEACHER_CONCEPT);
		IFrame studentType = createIFrame(studentTypeConcept);

		addISlotValue(job, TEACHES_PROPERTY, studentType);

		return addInstance(job, jobId);
	}

	private IFrame addAcademicResearchJob() {

		IFrame job = createJob(ACADEMIA_CONCEPT, RESEARCHER_CONCEPT);

		return addInstance(job, ACADEMIC_RESEARCH_JOB_ID);
	}

	private IFrame addDoctoringJob() {

		IFrame job = createJob(HEALTH_CONCEPT, DOCTOR_CONCEPT);

		return addInstance(job, DOCTORING_JOB_ID);
	}

	private IFrame createJob(String industryConcept, String jobTypeConcept) {

		IFrame job = createIFrame(JOB_CONCEPT);

		addISlotValue(job, INDUSTRY_PROPERTY, createIFrame(industryConcept));
		addISlotValue(job, JOB_TYPE_PROPERTY, createIFrame(jobTypeConcept));

		return job;
	}

	private void setIndustrySector(IFrame job, String sectorConcept) {

		ISlot indSlot = getISlot(job, INDUSTRY_PROPERTY);
		IFrame industry = (IFrame)indSlot.getValues().asList().get(0);

		addISlotValue(industry, SECTOR_PROPERTY, createIFrame(sectorConcept));
		updateInstance(job);
	}

	private void setHourlyRate(IFrame job, int hourlyPay) {

		addISlotValue(job, PAY_RATE_PROPERTY, new INumber(hourlyPay));
		updateInstance(job);
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

	private IFrame createPostgradTeachingQuery() {

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

	private IFrame createHealthOrPublicSectorAcademicQuery() {

		IFrame job = createJobQuery();
		IFrame health = createQueryIFrame(HEALTH_CONCEPT);
		IFrame academia = createQueryIFrame(ACADEMIA_CONCEPT);
		IFrame publicSector = createQueryIFrame(PUBLIC_CONCEPT);
		IFrame industryDisj = createIDisjunction(health, academia);

		addISlotValue(job, INDUSTRY_PROPERTY, industryDisj);
		addISlotValue(academia, SECTOR_PROPERTY, publicSector);

		return job;
	}

	private IFrame createPayRateQuery(int minHourlyPay, int maxHourlyPay) {

		IFrame job = createJobQuery();
		INumber hourlyPay = createRangeAsINumber(minHourlyPay, maxHourlyPay);

		addISlotValue(job, PAY_RATE_PROPERTY, hourlyPay);

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

		return CNumber.range(min, max).asINumber();
	}

	private IFrame addInstance(IFrame instance, CIdentity id) {

		matcher.add(instance, id);
		storedInstancesById.put(id, instance);

		return instance;
	}

	private void removeInstance(CIdentity id) {

		matcher.remove(id);
		storedInstancesById.remove(id);
	}

	private void updateInstance(IFrame instance) {

		CIdentity id = getInstanceId(instance);

		removeInstance(id);
		addInstance(instance, id);
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

	private CIdentity getInstanceId(IFrame instance) {

		for (CIdentity id : storedInstancesById.keySet()) {

			if (storedInstancesById.get(id) == instance) {

				return id;
			}
		}

		throw new Error("No id for: " + instance);
	}
}

