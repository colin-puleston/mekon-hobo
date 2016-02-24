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

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.demomodel.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.owl.build.*;

/**
 * @author Colin Puleston
 */
public class ORClassifierTest extends DemoModelBasedTest {

	private ORClassifier classifier;

	private String[] unemployedCitizenConcepts = new String[]{UNEMPLOYED_CITIZEN};
	private String[] employedCitizenConcepts = new String[]{EMPLOYED_CITIZEN};
	private String[] academicJobConcepts = new String[]{ACADEMIC_JOB};
	private String[] lecturerJobConcepts = new String[]{ACADEMIC_TEACHING_JOB, RESEARCH_JOB};

	@Before
	public void setUp() {

		OModel model = ODemoModel.create();
		OBSectionBuilder sectionBuilder = new OBSectionBuilder(model);

		classifier = new ORClassifier(model);

		sectionBuilder.setIReasoner(classifier);
		buildModel(sectionBuilder);
	}

	@Test
	public void test_openWorldSemantics() {

		testOpenWorldSemantics(false);
	}

	@Test
	public void test_closedWorldSemanticsByMinimalInclusion() {

		testClosedWorldSemanticsByMinimalInclusion(false);
	}

	@Test
	public void test_closedWorldSemanticsByMinimalExclusion() {

		testClosedWorldSemanticsByMinimalExclusion(false);
	}

	@Test
	public void test_openWorldSemantics_forceIndividuals() {

		testOpenWorldSemantics(true);
	}

	@Test
	public void test_closedWorldSemanticsByMinimalInclusion_forceIndividuals() {

		testClosedWorldSemanticsByMinimalInclusion(true);
	}

	@Test
	public void test_closedWorldSemanticsByMinimalExclusion_forceIndividuals() {

		testClosedWorldSemanticsByMinimalExclusion(true);
	}

	private void testOpenWorldSemantics(boolean individuals) {

		setOpenWorldSemantics();
		testClassifications(individuals);
	}

	private void testClosedWorldSemanticsByMinimalInclusion(boolean individuals) {

		setClosedWorldSemanticsByMinimalInclusion();
		testClassifications(individuals);
	}

	private void testClosedWorldSemanticsByMinimalExclusion(boolean individuals) {

		setClosedWorldSemanticsByMinimalExclusion();
		testClassifications(individuals);
	}

	private void testClassifications(boolean individuals) {

		classifier.setForceIndividualBasedClassification(individuals);

		IFrame citizen = createIFrame(CITIZEN);
		IFrame employ = createIFrame(EMPLOYMENT);
		IFrame job = createIFrame(JOB);
		IFrame academia = createIFrame(ACADEMIA);
		IFrame lecturer = createIFrame(LECTURER);

		testInferredTypes(citizen);

		addISlotValue(citizen, EMPLOYMENT_PROPERTY, employ);
		testInferredTypes(citizen, unemployedCitizenConcepts);

		addISlotValue(employ, JOBS_PROPERTY, job);
		testInferredTypes(citizen, employedCitizenConcepts);

		testInferredTypes(job);

		addISlotValue(job, INDUSTRY_PROPERTY, academia);
		testInferredTypes(job, academicJobConcepts);

		addISlotValue(job, JOB_TYPE_PROPERTY, lecturer);
		testInferredTypes(job, lecturerJobConcepts);
	}

	private void setOpenWorldSemantics() {

		unemployedCitizenConcepts = new String[]{};
	}

	private void setClosedWorldSemanticsByMinimalInclusion() {

		setSemantics(ORSemanticWorld.OPEN, JOBS_PROPERTY);
	}

	private void setClosedWorldSemanticsByMinimalExclusion() {

		// Exclude only those properties whose closure would cause inconsistencies
		setSemantics(
			ORSemanticWorld.CLOSED,
			TAX_PROPERTY,
			BENEFIT_PROPERTY,
			TEACHES_PROPERTY);
	}

	private void setSemantics(
					ORSemanticWorld defaultWorld,
					String... exceptionPropertyNames) {

		ORSemantics semantics = new ORSemantics();

		semantics.setDefaultWorld(defaultWorld);

		for (String exPropName : exceptionPropertyNames) {

			semantics.addExceptionProperty(nameToIdentifier(exPropName));
		}

		classifier.setSemantics(semantics);
	}

	private void testInferredTypes(IFrame iFrame, String... typeNames) {

		Set<CFrame> infTypes = iFrame.getInferredTypes().asSet();

		MekonTestUtils.testSet(infTypes, getCFrames(typeNames));
	}
}
