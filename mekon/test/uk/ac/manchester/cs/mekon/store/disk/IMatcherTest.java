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

package uk.ac.manchester.cs.mekon.store.disk;

import java.util.*;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.disk.*;
import uk.ac.manchester.cs.mekon.demomodel.*;
import uk.ac.manchester.cs.mekon.test_util.*;

/**
 * @author Colin Puleston
 */
public abstract class IMatcherTest extends DemoModelBasedTest {

	static private final CIdentity UNDERGRAD_TEACHING_JOB_ID = createInstanceId("UndergradTeaching");
	static private final CIdentity POSTGRAD_TEACHING_JOB_ID = createInstanceId("PostgradTeaching");
	static private final CIdentity ACADEMIC_RESEARCHING_JOB_ID = createInstanceId("AcademicResearching");
	static private final CIdentity DOCTORING_JOB_ID = createInstanceId("Doctoring");

	static private final CIdentity DOCTOR_EMPLOYMENT_ID = createInstanceId("DoctorEmployment");

	static private final CIdentity UNDERGRAD_TEACHER_ID = createInstanceId("UndergradTeacher");
	static private final CIdentity POSTGRAD_TEACHER_ID = createInstanceId("PostgradTeacher");
	static private final CIdentity ACADEMIC_RESEARCHER_ID = createInstanceId("AcademicResearcher");
	static private final CIdentity DOCTOR_ID = createInstanceId("Doctor");

	static private final CIdentity BOB_ID = createInstanceId("Bob");
	static private final CIdentity BOBS_LODGER_ID = createInstanceId("BobsLodger");

	static private final int MIN_PAY_RATE = 10;
	static private final int MAX_PAY_RATE = 20;

	static private final int LOW_PAY_RATE = 14;
	static private final int MID_PAY_RATE = 15;
	static private final int HIGH_PAY_RATE = 16;

	static private final IString BOB_NAME = createIString("Bob Bell");
	static private final IString BOB_ADDRESS = createIString("66 Bob Street, Bobsville");
	static private final IString BOBS_LODGER_NAME = createIString("Jim the lodger");

	static private CIdentity createInstanceId(String name) {

		return new CIdentity(name + "Id", name);
	}

	static private IString createIString(String value) {

		return CStringFactory.FREE.instantiate(value);
	}

	private IDiskStore store;
	private IMatcher matcher;

	private Map<CIdentity, IFrame> storedInstancesById = new HashMap<CIdentity, IFrame>();

	private IFrame undergradTeachingJob;
	private IFrame postgradTeachingJob;
	private IFrame academicResearchJob;
	private IFrame doctoringJob;

	@Before
	public void setUp() {

		buildModel(createSectionBuilder());

		store = new IDiskStore(getModel());
		matcher = createMatcher();

		store.clear();
		store.addMatcher(matcher);
		store.initialisePostRegistration();

		undergradTeachingJob = addUndergradTeachingJob();
		postgradTeachingJob = addPostgradTeachingJob();
		academicResearchJob = addAcademicResearchJob();
		doctoringJob = addDoctoringJob();

		addDoctorEmploymentViaJobRef();

		addUndergradTeacherViaJobRef();
		addPostgradTeacherViaJobRef();
		addAcademicResearcherViaJobRef();
		addDoctorViaEmploymentAndJobRefs();

		addBobPersonal();
		addBobsLodgerPersonal();
	}

	@After
	public void clearUp() {

		store.clear();
		store.stop();
	}

	@Test
	public void test_addAndQuery() {

		testMatching(
			createJobQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			POSTGRAD_TEACHING_JOB_ID,
			ACADEMIC_RESEARCHING_JOB_ID,
			DOCTORING_JOB_ID);

		testMatching(
			createCitizenQuery(),
			UNDERGRAD_TEACHER_ID,
			POSTGRAD_TEACHER_ID,
			ACADEMIC_RESEARCHER_ID,
			DOCTOR_ID);
	}

	@Test
	public void test_addRemoveAndQuery() {

		removeInstance(POSTGRAD_TEACHING_JOB_ID);
		removeInstance(ACADEMIC_RESEARCHING_JOB_ID);
		removeInstance(ACADEMIC_RESEARCHER_ID);

		testMatching(
			createJobQuery(),
			UNDERGRAD_TEACHING_JOB_ID,
			DOCTORING_JOB_ID);

		testMatching(
			createCitizenQuery(),
			UNDERGRAD_TEACHER_ID,
			POSTGRAD_TEACHER_ID,
			DOCTOR_ID);
	}

	@Test
	public void test_conceptPropertyBasedQueries() {

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

	@Test
	public void test_conceptHierarchyBasedQueries() {

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

	@Test
	public void test_instanceRefBasedQueriesWithoutInstanceLinking() {

		testMatching(
			createCitizenRefQuery(ACADEMIC_RESEARCHING_JOB_ID),
			ACADEMIC_RESEARCHER_ID);

		testMatching(
			createCitizenRefQuery(DOCTORING_JOB_ID),
			DOCTOR_ID);

		testMatching(
			createCitizenWithJobQuery(),
			UNDERGRAD_TEACHER_ID,
			POSTGRAD_TEACHER_ID,
			ACADEMIC_RESEARCHER_ID,
			DOCTOR_ID);

		removeInstance(UNDERGRAD_TEACHING_JOB_ID);
		removeInstance(POSTGRAD_TEACHING_JOB_ID);

		testMatching(
			createCitizenWithJobQuery(),
			ACADEMIC_RESEARCHER_ID,
			DOCTOR_ID);
	}

	@Test
	public void test_instanceRefBasedQueriesWithInstanceLinking() {

		testMatching(
			createUniStudentTeacherQuery(),
			UNDERGRAD_TEACHER_ID,
			POSTGRAD_TEACHER_ID);

		testMatching(
			createDoctorQuery(),
			DOCTOR_ID);

		removeInstance(POSTGRAD_TEACHING_JOB_ID);
		removeInstance(DOCTOR_EMPLOYMENT_ID);

		testMatching(
			createUniStudentTeacherQuery(),
			UNDERGRAD_TEACHER_ID);

		testMatching(
			createDoctorQuery());
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

	@Test
	public void test_stringBasedQueries() {

		testMatching(
			createPersonalNameQuery(BOB_NAME),
			BOB_ID);

		testMatching(
			createPersonalNameAddressQuery(BOB_NAME, BOB_ADDRESS),
			BOB_ID);

		testMatching(
			createPersonalNameQuery(BOBS_LODGER_NAME),
			BOBS_LODGER_ID);

		testMatching(
			createPersonalNameAddressQuery(BOBS_LODGER_NAME, BOB_ADDRESS),
			BOBS_LODGER_ID);

		testMatching(
			createPersonalAddressQuery(BOB_ADDRESS),
			BOB_ID,
			BOBS_LODGER_ID);
	}

	protected CSectionBuilder createSectionBuilder() {

		return new DemoModelEmulatingSectionBuilder();
	}

	protected abstract IMatcher createMatcher();

	protected abstract boolean handlesInstanceDisjunctionBasedQueries();

	protected IMatcher getMatcher() {

		return matcher;
	}

	private IFrame addUndergradTeachingJob() {

		return addAcademicTeachingJob(UNDERGRAD_TEACHING_JOB_ID, UNDERGRAD);
	}

	private IFrame addPostgradTeachingJob() {

		return addAcademicTeachingJob(POSTGRAD_TEACHING_JOB_ID, POSTGRAD);
	}

	private IFrame addAcademicTeachingJob(CIdentity jobId, CIdentity studentTypeConcept) {

		IFrame job = createJob(ACADEMIA, LECTURER);
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

	private IFrame addDoctorEmploymentViaJobRef() {

		return addInstance(
					createEmploymentViaJobRef(DOCTORING_JOB_ID),
					DOCTOR_EMPLOYMENT_ID);
	}

	private IFrame addUndergradTeacherViaJobRef() {

		return addCitizenViaJobRef(UNDERGRAD_TEACHER_ID, UNDERGRAD_TEACHING_JOB_ID);
	}

	private IFrame addPostgradTeacherViaJobRef() {

		return addCitizenViaJobRef(POSTGRAD_TEACHER_ID, POSTGRAD_TEACHING_JOB_ID);
	}

	private IFrame addAcademicResearcherViaJobRef() {

		return addCitizenViaJobRef(ACADEMIC_RESEARCHER_ID, ACADEMIC_RESEARCHING_JOB_ID);
	}

	private IFrame addDoctorViaEmploymentAndJobRefs() {

		return addCitizenViaEmploymentRef(DOCTOR_ID, DOCTOR_EMPLOYMENT_ID);
	}

	private IFrame addCitizenViaJobRef(CIdentity citizenId, CIdentity jobId) {

		return addInstance(createCitizenViaJobRef(jobId), citizenId);
	}

	private IFrame addCitizenViaEmploymentRef(CIdentity citizenId, CIdentity employmentId) {

		return addInstance(createCitizenViaEmploymentRef(employmentId), citizenId);
	}

	private IFrame addBobPersonal() {

		return addPersonal(BOB_ID, BOB_NAME, BOB_ADDRESS);
	}

	private IFrame addBobsLodgerPersonal() {

		return addPersonal(BOBS_LODGER_ID, BOBS_LODGER_NAME, BOB_ADDRESS);
	}

	private IFrame addPersonal(CIdentity personalId, IString name, IString address) {

		IFrame personal = createIFrame(PERSONAL);

		addISlotValue(personal, NAME_PROPERTY, name);
		addISlotValue(personal, ADDRESS_PROPERTY, address);

		return addInstance(personal, personalId);
	}

	private IFrame createCitizenViaJobRef(CIdentity jobId) {

		return createCitizen(createEmploymentViaJobRef(jobId));
	}

	private IFrame createCitizenViaEmploymentRef(CIdentity employmentId) {

		return createCitizen(createRefIFrame(EMPLOYMENT, employmentId));
	}

	private IFrame createCitizen(IFrame employment) {

		IFrame citizen = createIFrame(CITIZEN);

		addISlotValue(citizen, EMPLOYMENT_PROPERTY, employment);

		return citizen;
	}

	private IFrame createEmploymentViaJobRef(CIdentity jobId) {

		IFrame employment = createIFrame(EMPLOYMENT);
		IFrame jobRef = createRefIFrame(JOB, jobId);

		addISlotValue(employment, JOB_PROPERTY, jobRef);

		return employment;
	}

	private IFrame createJob(CIdentity industryConcept, CIdentity jobTypeConcept) {

		IFrame job = createIFrame(JOB);

		addISlotValue(job, INDUSTRY_PROPERTY, createIFrame(industryConcept));
		addISlotValue(job, JOB_TYPE_PROPERTY, createIFrame(jobTypeConcept));

		return job;
	}

	private void setIndustrySector(IFrame job, CIdentity sectorConcept) {

		ISlot indSlot = getISlot(job, INDUSTRY_PROPERTY);
		IFrame industry = (IFrame)indSlot.getValues().asList().get(0);

		addISlotValue(industry, SECTOR_PROPERTY, createIFrame(sectorConcept));
		updateInstance(job);
	}

	private void setHourlyRate(IFrame job, int hourlyPay) {

		addISlotValue(job, PAY_RATE_PROPERTY, new INumber(hourlyPay));
		updateInstance(job);
	}

	private IFrame createAcademicTeachingQuery() {

		IFrame job = createAcademiaQuery();
		IFrame jobType = createQueryIFrame(LECTURER);

		addISlotValue(job, JOB_TYPE_PROPERTY, jobType);

		return job;
	}

	private IFrame createAcademiaQuery() {

		IFrame job = createJobQuery();
		IFrame industry = createQueryIFrame(ACADEMIA);

		addISlotValue(job, INDUSTRY_PROPERTY, industry);

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

	private IFrame createStudentTeachingQuery(CIdentity studentTypeConcept) {

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

	private IFrame createCitizenWithJobQuery() {

		return createCitizenQuery(createJobQuery());
	}

	private IFrame createDoctorQuery() {

		return createCitizenQuery(DOCTOR);
	}

	private IFrame createResearcherQuery() {

		return createCitizenQuery(RESEARCHER);
	}

	private IFrame createUniStudentTeacherQuery() {

		return createCitizenQuery(createUniStudentTeachingQuery());
	}

	private IFrame createCitizenQuery(CIdentity jobTypeConcept) {

		IFrame job = createJobQuery();

		addISlotValue(job, JOB_TYPE_PROPERTY, createQueryIFrame(jobTypeConcept));

		return createCitizenQuery(job);
	}

	private IFrame createCitizenRefQuery(CIdentity jobId) {

		return createCitizenQuery(createRefQueryIFrame(JOB, jobId));
	}

	private IFrame createCitizenQuery(IFrame job) {

		IFrame citizen = createCitizenQuery();
		IFrame employment = createQueryIFrame(EMPLOYMENT);

		addISlotValue(citizen, EMPLOYMENT_PROPERTY, employment);
		addISlotValue(employment, JOB_PROPERTY, job);

		return citizen;
	}

	private IFrame createCitizenQuery() {

		return createQueryIFrame(CITIZEN);
	}

	private IFrame createPersonalNameQuery(IString name) {

		IFrame personal = createQueryIFrame(PERSONAL);

		addISlotValue(personal, NAME_PROPERTY, name);

		return personal;
	}

	private IFrame createPersonalAddressQuery(IString address) {

		IFrame personal = createQueryIFrame(PERSONAL);

		addISlotValue(personal, ADDRESS_PROPERTY, address);

		return personal;
	}

	private IFrame createPersonalNameAddressQuery(IString name, IString address) {

		IFrame personal = createQueryIFrame(PERSONAL);

		addISlotValue(personal, NAME_PROPERTY, name);
		addISlotValue(personal, ADDRESS_PROPERTY, address);

		return personal;
	}

	private CFrame createPostOrUndergradDisjunction() {

		CFrame postgrad = getCFrame(POSTGRAD);
		CFrame undergrad = getCFrame(UNDERGRAD);

		return CFrame.resolveDisjunction(Arrays.asList(postgrad, undergrad));
	}

	private INumber createRangeAsINumber(int min, int max) {

		return INumber.range(new INumber(min), new INumber(max));
	}

	private IFrame createIDisjunction(IFrame... disjuncts) {

		return FramesTestUtils.createIDisjunction(disjuncts);
	}

	private IFrame addInstance(IFrame instance, CIdentity id) {

		store.add(instance, id);
		storedInstancesById.put(id, instance);

		return instance;
	}

	private void removeInstance(CIdentity id) {

		store.remove(id);
		storedInstancesById.remove(id);
	}

	private void updateInstance(IFrame instance) {

		CIdentity id = getInstanceId(instance);

		store.remove(id);
		store.add(instance, id);
	}

	private void testMatching(IFrame query, CIdentity... expectedMatchIds) {

		List<CIdentity> matchIds = matcher.match(query).getAllMatches();

		testListContents(matchIds, Arrays.asList(expectedMatchIds));

		for (CIdentity id : store.getAllIdentities()) {

			IFrame instance = store.get(id).getRootFrame();
			boolean matches = matcher.matches(query, instance);

			assertTrue(matches == matchIds.contains(id));
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

