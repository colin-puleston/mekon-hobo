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

package uk.ac.manchester.cs.mekon.owl.classifier;

import java.util.*;

import org.junit.Test;
import org.junit.Before;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.sanctions.*;
import uk.ac.manchester.cs.mekon.owl.classifier.semantics.*;

/**
 * @author Colin Puleston
 */
public class OCClassifierTest extends OTest {

	static private final String CITIZEN_CONCEPT = "Citizen";
	static private final String EMPLOYED_CITIZEN_CONCEPT = "EmployedCitizen";
	static private final String UNEMPLOYED_CITIZEN_CONCEPT = "UnemployedCitizen";
	static private final String EMPLOYMENT_CONCEPT = "Employment";
	static private final String JOB_CONCEPT = "Job";
	static private final String ACADEMIC_JOB_CONCEPT = "AcademicJob";
	static private final String ACADEMIC_TEACHING_JOB_CONCEPT = "AcademicTeachingJob";
	static private final String ACADEMIA_CONCEPT = "Academia";
	static private final String TEACHER_CONCEPT = "Teacher";

	static private final String EMPLOYMENT_PROPERTY = "employment";
	static private final String JOBS_PROPERTY = "job";
	static private final String INDUSTRY_PROPERTY = "industry";
	static private final String JOB_TYPE_PROPERTY = "jobType";
	static private final String TAX_PAID_PROPERTY = "taxPaid";
	static private final String BENEFIT_RECEIVED_PROPERTY = "benefitReceived";
	static private final String TEACHES_PROPERTY = "teaches";

	private OCClassifier classifier;

	private String[] unemployedCitizenConcepts = new String[]{UNEMPLOYED_CITIZEN_CONCEPT};
	private String[] employedCitizenConcepts = new String[]{EMPLOYED_CITIZEN_CONCEPT};
	private String[] academicJobConcepts = new String[]{ACADEMIC_JOB_CONCEPT};
	private String[] academicTeachingJobConcepts = new String[]{ACADEMIC_TEACHING_JOB_CONCEPT};

	@Before
	public void setUp() {

		OModel model = TestOModel.create();
		OSSectionBuilder sectionBuilder = new OSSectionBuilder(model);

		classifier = new OCClassifier(model);

		sectionBuilder.setIReasoner(classifier);
		buildModel(sectionBuilder);
	}

	@Test
	public void test_openWorldSemantics() {

		setOpenWorldSemantics();
		testClassifications();
	}

	@Test
	public void test_closedWorldSemanticsByMinimalInclusion() {

		setClosedWorldSemanticsByMinimalInclusion();
		testClassifications();
	}

	@Test
	public void test_closedWorldSemanticsByMinimalExclusion() {

		setClosedWorldSemanticsByMinimalExclusion();
		testClassifications();
	}

	private void testClassifications() {

		IFrame citizen = createIFrame(CITIZEN_CONCEPT);
		IFrame employ = createIFrame(EMPLOYMENT_CONCEPT);
		IFrame job = createIFrame(JOB_CONCEPT);
		IFrame academia = createIFrame(ACADEMIA_CONCEPT);
		IFrame teacher = createIFrame(TEACHER_CONCEPT);

		testInferredTypes(citizen);

		addFrameSlotValue(citizen, EMPLOYMENT_PROPERTY, employ);
		testInferredTypes(citizen, unemployedCitizenConcepts);

		addFrameSlotValue(employ, JOBS_PROPERTY, job);
		testInferredTypes(citizen, employedCitizenConcepts);

		testInferredTypes(job);

		addFrameSlotValue(job, INDUSTRY_PROPERTY, academia);
		testInferredTypes(job, academicJobConcepts);

		addFrameSlotValue(job, JOB_TYPE_PROPERTY, teacher);
		testInferredTypes(job, academicTeachingJobConcepts);
	}

	private void setOpenWorldSemantics() {

		unemployedCitizenConcepts = new String[]{};
	}

	private void setClosedWorldSemanticsByMinimalInclusion() {

		setSemantics(OCSemantics.OPEN_WORLD, JOBS_PROPERTY);
	}

	private void setClosedWorldSemanticsByMinimalExclusion() {

		// Exclude only those properties whose closure would cause inconsistencies
		setSemantics(
			OCSemantics.CLOSED_WORLD,
			TAX_PAID_PROPERTY,
			BENEFIT_RECEIVED_PROPERTY,
			TEACHES_PROPERTY);
	}

	private void setSemantics(
					OCSemantics defaultSemantics,
					String... exceptionPropertyNames) {

		OCSlotSemantics ss = classifier.getSlotSemantics();

		ss.setDefaultSemantics(defaultSemantics);
		ss.clearExceptionProperties();

		for (String exPropName : exceptionPropertyNames) {

			ss.addExceptionProperty(nameToIdentifier(exPropName));
		}
	}

	private void addFrameSlotValue(IFrame container, String slotName, IFrame value) {

		getISlot(container, slotName).getValuesEditor().add(value);
	}

	private void testInferredTypes(IFrame iFrame, String... typeNames) {

		testSet(iFrame.getInferredTypes().asSet(), getCFrames(typeNames));
	}
}
