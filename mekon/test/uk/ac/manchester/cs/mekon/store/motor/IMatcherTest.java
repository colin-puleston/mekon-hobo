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

package uk.ac.manchester.cs.mekon.store.motor;

import java.util.*;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.demomodel.*;

/**
 * @author Colin Puleston
 */
public abstract class IMatcherTest extends DemoModelBasedTest {

	static private final CIdentity UNDERGRAD_TEACHING_JOB_ID = new CIdentity("UndergradTeaching");
	static private final CIdentity POSTGRAD_TEACHING_JOB_ID = new CIdentity("PostgradTeaching");
	static private final CIdentity ACADEMIC_RESEARCHING_JOB_ID = new CIdentity("AcademicResearching");
	static private final CIdentity DOCTORING_JOB_ID = new CIdentity("Doctoring");

	static private final int MIN_PAY_RATE = 10;
	static private final int LOW_PAY_RATE = 14;
	static private final int MID_PAY_RATE = 15;
	static private final int HIGH_PAY_RATE = 16;
	static private final int MAX_PAY_RATE = 20;

	private IMatcher matcher;
	private Map<CIdentity, IFrame> storedInstancesById = new HashMap<CIdentity, IFrame>();

	private IFrame undergradTeachingJob;
	private IFrame postgradTeachingJob;
	private IFrame academicResearchJob;
	private IFrame doctoringJob;

	@Before
	public void setUp() {

		matcher = createMatcher();

		CSectionBuilder sectionBuilder = createSectionBuilder();
		CBuilder cBuilder = buildModel(sectionBuilder);
		IStoreBuilder iStoreBuilder = IStoreManager.getBuilder(cBuilder);

		iStoreBuilder.addMatcher(matcher);
		iStoreBuilder.build();

		undergradTeachingJob = addUndergradTeachingJob();
		postgradTeachingJob = addPostgradTeachingJob();
		academicResearchJob = addAcademicResearchJob();
		doctoringJob = addDoctoringJob();
	}

	@Test
	public void test_addAndQuery() {

		testMatching(
			createJobQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID,
			ACADEMIC_RESEARCHING_JOB_ID,
			DOCTORING_JOB_ID);
	}

	//@Test
	public void test_addRemoveAndQuery() {

		removeInstance(POSTGRAD_TEACHING_JOB_ID);
		removeInstance(ACADEMIC_RESEARCHING_JOB_ID);

		testMatching(
			createJobQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			DOCTORING_JOB_ID);
	}

	//@Test
	public void test_basicConceptBasedQueries() {

		testMatching(
			createAcademiaQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID,
			ACADEMIC_RESEARCHING_JOB_ID);

		testMatching(
			createAcademicTeachingQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID);

		testMatching(
			createPostgradTeachingQuery(),
			POSTGRAD_TEACHING_JOB_ID);
	}

	//@Test
	public void test_conceptAndPropertyBasedQueries() {

		testMatching(
			createUniStudentTeachingQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID);
	}

	//@Test
	public void test_conceptDisjunctionBasedQueries() {

		testMatching(
			createPostOrUndergradTeachingQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID);
	}

	//@Test
	public void test_instanceDisjunctionBasedQueries() {

		if (!handlesInstanceDisjunctionBasedQueries()) {

			return;
		}

		setIndustrySector(undergradTeachingJob, PUBLIC);
		setIndustrySector(postgradTeachingJob, PRIVATE);
		setIndustrySector(academicResearchJob, PRIVATE);
		setIndustrySector(doctoringJob, PRIVATE);

		testMatching(
			createHealthOrPublicSectorAcademicQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			DOCTORING_JOB_ID);
	}

	//@Test
	public void test_numberBasedQueries() {

		setHourlyRate(undergradTeachingJob, LOW_PAY_RATE);
		setHourlyRate(postgradTeachingJob, MID_PAY_RATE);
		setHourlyRate(academicResearchJob, MID_PAY_RATE);
		setHourlyRate(doctoringJob, HIGH_PAY_RATE);

		testMatching(
			createPayRateQuery(MIN_PAY_RATE, MAX_PAY_RATE),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID,
			ACADEMIC_RESEARCHING_JOB_ID,
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
			ACADEMIC_RESEARCHING_JOB_ID);

		testMatching(
			createPayRateQuery(HIGH_PAY_RATE + 1, MAX_PAY_RATE));

		testMatching(
			createPayRateQuery(MID_PAY_RATE + 1, MAX_PAY_RATE),
			DOCTORING_JOB_ID);

		testMatching(
			createPayRateQuery(LOW_PAY_RATE + 1, MAX_PAY_RATE),
			POSTGRAD_TEACHING_JOB_ID,
			ACADEMIC_RESEARCHING_JOB_ID,
			DOCTORING_JOB_ID);
	}

	protected CSectionBuilder createSectionBuilder() {

		return new DemoModelEmulatingSectionBuilder();
	}

	protected abstract IMatcher createMatcher();

	protected boolean handlesInstanceDisjunctionBasedQueries() {

		return true;
	}

	protected IMatcher getMatcher() {

		return matcher;
	}

	private IFrame addUndergradTeachingJob() {

		return addAcademicTeachingJob(UNDERGRAD_TEACHING_JOB_ID, UNDERGRAD);
	}

	private IFrame addPostgradTeachingJob() {

		return addAcademicTeachingJob(POSTGRAD_TEACHING_JOB_ID, POSTGRAD);
	}

	private IFrame addAcademicTeachingJob(CIdentity jobId, String studentTypeConcept) {

		IFrame job = createJob(ACADEMIA, TEACHER);
		IFrame studentType = createIFrame(studentTypeConcept);

		addISlotValue(job, TEACHES_PROPERTY, studentType);

		return addInstance(job, jobId);
	}

	private IFrame addAcademicResearchJob() {

		IFrame job = createJob(ACADEMIA, RESEARCHER);

		return addInstance(job, ACADEMIC_RESEARCHING_JOB_ID);
	}

	private IFrame addDoctoringJob() {

		IFrame job = createJob(HEALTH, DOCTOR);

		return addInstance(job, DOCTORING_JOB_ID);
	}

	private IFrame createJob(String industryConcept, String jobTypeConcept) {

		IFrame job = createIFrame(JOB);

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
		IFrame industry = createQueryIFrame(ACADEMIA);

		addISlotValue(job, INDUSTRY_PROPERTY, industry);

		return job;
	}

	private IFrame createAcademicTeachingQuery() {

		IFrame job = createAcademiaQuery();
		IFrame jobType = createQueryIFrame(TEACHER);

		addISlotValue(job, JOB_TYPE_PROPERTY, jobType);

		return job;
	}

	private IFrame createUniStudentTeachingQuery() {

		return createStudentTeachingQuery(UNI_STUDENT);
	}

	private IFrame createPostgradTeachingQuery() {

		return createStudentTeachingQuery(POSTGRAD);
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
		IFrame health = createQueryIFrame(HEALTH);
		IFrame academia = createQueryIFrame(ACADEMIA);
		IFrame publicSector = createQueryIFrame(PUBLIC);
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

		return createQueryIFrame(JOB);
	}

	private CFrame createPostOrUndergradDisjunction() {

		CFrame postgrad = getCFrame(POSTGRAD);
		CFrame undergrad = getCFrame(UNDERGRAD);

		return CFrame.resolveDisjunction(Arrays.asList(postgrad, undergrad));
	}

	private INumber createRangeAsINumber(int min, int max) {

		return CNumber.range(min, max).asINumber();
	}

	private IFrame createIDisjunction(IFrame... disjuncts) {

		return FramesTestUtils.createIDisjunction(disjuncts);
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

	private <E>void testListContents(List<? extends E> got, List<? extends E> expected) {

		MekonTestUtils.testListContents(got, expected);
	}
}
