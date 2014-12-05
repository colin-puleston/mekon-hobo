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

package uk.ac.manchester.cs.mekon.owl.build;

import java.util.*;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
public class OBSectionBuilderTest extends OTest {

	static private final String DOMAIN_CONCEPT_CLASS = "DomainConcept";
	static private final String DATA_TYPE_CONCEPT_CLASS = "DataTypeConcept";

	static private final String CITIZEN_CLASS = "Citizen";
	static private final String UNEMPLOYED_CITIZEN_CLASS = "UnemployedCitizen";
	static private final String EMPLOYMENT_CLASS = "Employment";
	static private final String PERSONAL_CLASS = "Personal";
	static private final String TAX_CLASS = "Tax";
	static private final String ZERO_TAX_CLASS = "ZeroTax";
	static private final String BENEFIT_CLASS = "Benefit";
	static private final String UNEMPLOYMENT_BENEFIT_CLASS = "UnemploymentBenefit";
	static private final String JOB_CLASS = "Job";
	static private final String ACADEMIC_JOB_CLASS = "AcademicJob";
	static private final String ACADEMIC_TEACHING_JOB_CLASS = "AcademicTeachingJob";
	static private final String TRAIN_CLASS = "Train";
	static private final String TRAVEL_CLASS_CLASS = "TravelClass";
	static private final String JOB_TYPE_CLASS = "JobType";
	static private final String SPECIALIST_CLASS = "Specialist";
	static private final String TEACHER_CLASS = "Teacher";

	static private final String CITIZEN_ASPECT_PROPERTY = "citizenAspect";
	static private final String JOB_PROPERTY = "job";
	static private final String TAX_PAID_PROPERTY = "taxPaid";
	static private final String BENEFIT_RECIEVED_PROPERTY = "benefitReceived";
	static private final String PERSONAL_ASPECT_PROPERTY = "personalAspect";
	static private final String AGE_PROPERTY = "age";
	static private final String TRAVEL_CLASS_PROPERTY = "travelClass";

	static private final List<IValue> NO_IVALUES = Collections.emptyList();

	private OBSectionBuilder sectionBuilder;
	private boolean metaFrameSlotsEnabled = false;

	private enum CFrameStatus {

		EXPOSED, HIDDEN, ABSENT;
	}

	private enum CPropertyStatus {

		PRESENT, ABSENT;
	}

	@Before
	public void setUp() {

		sectionBuilder = new OBSectionBuilder(TestOModel.create());
	}

	@Test
	public void test_frames() {

		buildModel();

		testFrame(DOMAIN_CONCEPT_CLASS, CFrameStatus.EXPOSED);
		testFrame(CITIZEN_CLASS, CFrameStatus.EXPOSED);
		testFrame(JOB_CLASS, CFrameStatus.EXPOSED);
		testFrame(ACADEMIC_JOB_CLASS, CFrameStatus.EXPOSED);
		testFrame(DATA_TYPE_CONCEPT_CLASS, CFrameStatus.EXPOSED);

		testFrameSupers(CITIZEN_CLASS, DOMAIN_CONCEPT_CLASS);
		testFrameSupers(ACADEMIC_JOB_CLASS, JOB_CLASS);
	}

	@Test
	public void test_frames_byGroupIncludingRoot() {

		addConcepts(DOMAIN_CONCEPT_CLASS);

		buildModel();

		testFrame(DOMAIN_CONCEPT_CLASS, CFrameStatus.EXPOSED);
		testFrame(JOB_CLASS, CFrameStatus.EXPOSED);
		testFrame(ACADEMIC_JOB_CLASS, CFrameStatus.EXPOSED);
		testFrame(DATA_TYPE_CONCEPT_CLASS, CFrameStatus.ABSENT);
	}

	@Test
	public void test_frames_byGroupExcludingRoot() {

		addConceptsExcludingRoot(DOMAIN_CONCEPT_CLASS);

		buildModel();

		testFrame(DOMAIN_CONCEPT_CLASS, CFrameStatus.ABSENT);
		testFrame(JOB_CLASS, CFrameStatus.EXPOSED);
		testFrame(ACADEMIC_JOB_CLASS, CFrameStatus.EXPOSED);
		testFrame(DATA_TYPE_CONCEPT_CLASS, CFrameStatus.ABSENT);
	}

	@Test
	public void test_frames_hidingCandidates_ALL() {

		addConcepts(DOMAIN_CONCEPT_CLASS, OBEntitySelection.ALL);

		buildModel();

		testFrame(DOMAIN_CONCEPT_CLASS, CFrameStatus.HIDDEN);
		testFrame(JOB_CLASS, CFrameStatus.HIDDEN);
		testFrame(ACADEMIC_JOB_CLASS, CFrameStatus.HIDDEN);
	}

	@Test
	public void test_frames_hidingCandidates_ROOTS_ONLY() {

		addConcepts(DOMAIN_CONCEPT_CLASS, OBEntitySelection.ROOTS_ONLY);

		buildModel();

		testFrame(DOMAIN_CONCEPT_CLASS, CFrameStatus.HIDDEN);
		testFrame(JOB_CLASS, CFrameStatus.EXPOSED);
		testFrame(ACADEMIC_JOB_CLASS, CFrameStatus.EXPOSED);
	}

	@Test
	public void test_frames_hidingCandidates_NON_ROOTS_ONLY() {

		addConcepts(DOMAIN_CONCEPT_CLASS, OBEntitySelection.NON_ROOTS_ONLY);

		buildModel();

		testFrame(DOMAIN_CONCEPT_CLASS, CFrameStatus.EXPOSED);
		testFrame(JOB_CLASS, CFrameStatus.HIDDEN);
		testFrame(ACADEMIC_JOB_CLASS, CFrameStatus.HIDDEN);
	}

	@Test
	public void test_frames_hidingFilter_ANY() {

		addConcepts(JOB_CLASS, OBConceptHidingFilter.ANY);
		addConcepts(JOB_TYPE_CLASS, OBConceptHidingFilter.ANY);

		buildModel();

		testFrame(JOB_CLASS, CFrameStatus.HIDDEN);
		testFrame(ACADEMIC_JOB_CLASS, CFrameStatus.HIDDEN);
		testFrame(ACADEMIC_TEACHING_JOB_CLASS, CFrameStatus.HIDDEN);
		testFrame(JOB_TYPE_CLASS, CFrameStatus.HIDDEN);
		testFrame(SPECIALIST_CLASS, CFrameStatus.HIDDEN);
		testFrame(TEACHER_CLASS, CFrameStatus.HIDDEN);
	}

	@Test
	public void test_frames_hidingFilter_DEFINIED_CONCEPTS_ONLY() {

		addConcepts(JOB_CLASS, OBConceptHidingFilter.DEFINIED_CONCEPTS_ONLY);
		addConcepts(JOB_TYPE_CLASS, OBConceptHidingFilter.DEFINIED_CONCEPTS_ONLY);

		buildModel();

		testFrame(JOB_CLASS, CFrameStatus.EXPOSED);
		testFrame(ACADEMIC_JOB_CLASS, CFrameStatus.HIDDEN);
		testFrame(ACADEMIC_TEACHING_JOB_CLASS, CFrameStatus.HIDDEN);
		testFrame(JOB_TYPE_CLASS, CFrameStatus.EXPOSED);
		testFrame(SPECIALIST_CLASS, CFrameStatus.HIDDEN);
		testFrame(TEACHER_CLASS, CFrameStatus.EXPOSED);
	}

	@Test
	public void test_frames_hidingFilter_DEFINIED_SUB_TREES_ONLY() {

		addConcepts(JOB_CLASS, OBConceptHidingFilter.DEFINIED_SUB_TREES_ONLY);
		addConcepts(JOB_TYPE_CLASS, OBConceptHidingFilter.DEFINIED_SUB_TREES_ONLY);

		buildModel();

		testFrame(JOB_CLASS, CFrameStatus.EXPOSED);
		testFrame(ACADEMIC_JOB_CLASS, CFrameStatus.HIDDEN);
		testFrame(ACADEMIC_TEACHING_JOB_CLASS, CFrameStatus.HIDDEN);
		testFrame(JOB_TYPE_CLASS, CFrameStatus.EXPOSED);
		testFrame(SPECIALIST_CLASS, CFrameStatus.EXPOSED);
		testFrame(TEACHER_CLASS, CFrameStatus.EXPOSED);
	}

	@Test
	public void test_properties() {

		buildModel();

		testProperty(CITIZEN_ASPECT_PROPERTY, CPropertyStatus.PRESENT);
		testProperty(JOB_PROPERTY, CPropertyStatus.PRESENT);
		testProperty(TAX_PAID_PROPERTY, CPropertyStatus.PRESENT);
		testProperty(BENEFIT_RECIEVED_PROPERTY, CPropertyStatus.PRESENT);
		testProperty(PERSONAL_ASPECT_PROPERTY, CPropertyStatus.PRESENT);
		testProperty(AGE_PROPERTY, CPropertyStatus.PRESENT);
	}

	@Test
	public void test_properties_ByGroupIncludingRoot() {

		addProperties(PERSONAL_ASPECT_PROPERTY);

		buildModel();

		testProperty(PERSONAL_ASPECT_PROPERTY, CPropertyStatus.PRESENT);
		testProperty(AGE_PROPERTY, CPropertyStatus.PRESENT);
		testProperty(JOB_PROPERTY, CPropertyStatus.ABSENT);
		testProperty(TAX_PAID_PROPERTY, CPropertyStatus.ABSENT);
		testProperty(BENEFIT_RECIEVED_PROPERTY, CPropertyStatus.ABSENT);
	}

	@Test
	public void test_properties_ByGroupExcludingRoot() {

		addPropertiesExcludingRoot(PERSONAL_ASPECT_PROPERTY);

		buildModel();

		testProperty(PERSONAL_ASPECT_PROPERTY, CPropertyStatus.ABSENT);
		testProperty(AGE_PROPERTY, CPropertyStatus.PRESENT);
		testProperty(JOB_PROPERTY, CPropertyStatus.ABSENT);
		testProperty(TAX_PAID_PROPERTY, CPropertyStatus.ABSENT);
		testProperty(BENEFIT_RECIEVED_PROPERTY, CPropertyStatus.ABSENT);
	}

	@Test
	public void test_slots_metaFrameSlotsEnabled() {

		enableMetaFrameSlots();
		buildModel();

		test_slots();
	}

	@Test
	public void test_slots_metaFrameSlotsDisabled() {

		buildModel();

		test_slots();
	}

	@Test
	public void test_slotValues_metaFrameSlotsEnabled() {

		enableMetaFrameSlots();
		buildModel();

		test_slotValues();
	}

	@Test
	public void test_slotValues_metaFrameSlotsDisabled() {

		buildModel();

		test_slotValues();
	}

	private void enableMetaFrameSlots() {

		sectionBuilder.setMetaFrameSlotsEnabled(true);

		metaFrameSlotsEnabled = true;
	}

	private void test_slots() {

		testSlot(
			CITIZEN_CLASS,
			TAX_PAID_PROPERTY,
			CCardinality.SINGLETON,
			getNoStructureFrameSlotValueType(TAX_CLASS));
		testSlot(
			UNEMPLOYED_CITIZEN_CLASS,
			TAX_PAID_PROPERTY,
			CCardinality.SINGLETON,
			getNoStructureFrameSlotValueType(ZERO_TAX_CLASS));
		testSlot(
			EMPLOYMENT_CLASS,
			JOB_PROPERTY,
			CCardinality.FREE,
			getCFrame(JOB_CLASS));
		testSlot(
			TRAIN_CLASS,
			TRAVEL_CLASS_PROPERTY,
			CCardinality.SINGLETON,
			getNoStructureFrameSlotValueType(TRAVEL_CLASS_CLASS));
		testSlot(
			PERSONAL_CLASS,
			AGE_PROPERTY,
			CCardinality.SINGLETON,
			getCInteger(0, null));
	}

	private void test_slotValues() {

		testSlotValues(
			UNEMPLOYED_CITIZEN_CLASS,
			TAX_PAID_PROPERTY,
			getNoStructureFrameSlotFixedValues(ZERO_TAX_CLASS));
		testSlotValues(
			UNEMPLOYED_CITIZEN_CLASS,
			BENEFIT_RECIEVED_PROPERTY,
			getNoStructureFrameSlotFixedValues(UNEMPLOYMENT_BENEFIT_CLASS));
	}

	private void buildModel() {

		buildModel(sectionBuilder);
	}

	private OBConceptInclusions addConcepts(String rootName) {

		return addConcepts(createConceptInclusions(rootName));
	}

	private OBConceptInclusions addConceptsExcludingRoot(String rootName) {

		OBConceptInclusions inclusions = createConceptInclusions(rootName);

		inclusions.setInclusion(OBEntitySelection.NON_ROOTS_ONLY);

		return addConcepts(inclusions);
	}

	private OBConceptInclusions addConcepts(
									String rootName,
									OBEntitySelection hidingCandidates) {

		OBConceptInclusions inclusions = createConceptInclusions(rootName);

		inclusions.getConceptHiding().setCandidates(hidingCandidates);

		return addConcepts(inclusions);
	}

	private OBConceptInclusions addConcepts(
									String rootName,
									OBConceptHidingFilter hidingFilter) {

		OBConceptInclusions inclusions = createConceptInclusions(rootName);
		OBConceptHiding hiding = inclusions.getConceptHiding();

		hiding.setCandidates(OBEntitySelection.ALL);
		hiding.setFilter(hidingFilter);

		return addConcepts(inclusions);
	}

	private OBConceptInclusions createConceptInclusions(String rootName) {

		return new OBConceptInclusions(nameToIRI(rootName));
	}

	private OBConceptInclusions addConcepts(OBConceptInclusions inclusions) {

		sectionBuilder.getConcepts().addGroup(inclusions);

		return inclusions;
	}

	private OBPropertyInclusions addProperties(String rootName) {

		return addProperties(createPropertyInclusions(rootName));
	}

	private OBPropertyInclusions addPropertiesExcludingRoot(String rootName) {

		OBPropertyInclusions inclusions = createPropertyInclusions(rootName);

		inclusions.setInclusion(OBEntitySelection.NON_ROOTS_ONLY);

		return addProperties(inclusions);
	}

	private OBPropertyInclusions createPropertyInclusions(String rootName) {

		return new OBPropertyInclusions(nameToIRI(rootName));
	}

	private OBPropertyInclusions addProperties(OBPropertyInclusions inclusions) {

		sectionBuilder.getProperties().addGroup(inclusions);

		return inclusions;
	}

	private void testFrame(String name, CFrameStatus expectedStatus) {

		CFrameStatus status = getCFrameStatus(name);

		assertTrue("Unexpected CFrame status: " + status, status == expectedStatus);
	}

	private void testFrameSupers(String subName, String... expectedSupersNames) {

		CFrame sub = getCFrame(subName);
		Set<CFrame> supers = new HashSet<CFrame>(sub.getSupers());

		testSet(supers, getCFrames(expectedSupersNames));
	}

	private void testProperty(String name, CPropertyStatus expectedStatus) {

		CPropertyStatus status = getCPropertyStatus(name);

		assertTrue("Unexpected CProperty status: " + status, status == expectedStatus);
	}

	private void testSlot(
					String containerName,
					String propertyName,
					CCardinality expectedCardinality,
					CValue<?> expectedValueType) {

		CFrame container = getCFrame(containerName);
		CProperty property = getCProperty(propertyName);
		CSlot slot = container.getSlots().getSlotFor(property);

		CCardinality cardinality = slot.getCardinality();
		CValue<?> valueType = slot.getValueType();

		assertTrue(
			"Unexpected CSlot cardinality: " + cardinality,
			cardinality == expectedCardinality);
		assertTrue(
			"Unexpected CSlot value-type: " + valueType,
			valueType.equals(expectedValueType));
	}

	private void testSlotValues(
					String containerName,
					String propertyName,
					List<IValue> expectedValues) {

		List<IValue> values = getSlotValues(containerName, propertyName);

		testListContents(values, expectedValues);
	}

	private List<IValue> getSlotValues(String containerName, String propertyName) {

		CFrame container = getCFrame(containerName);
		CProperty property = getCProperty(propertyName);

		return container.getSlotValues().getIValues(property);
	}

	private CFrameStatus getCFrameStatus(String name) {

		if (!isCFrame(name)) {

			return CFrameStatus.ABSENT;
		}

		if (getCFrame(name).hidden()) {

			return CFrameStatus.HIDDEN;
		}

		return CFrameStatus.EXPOSED;
	}

	private CPropertyStatus getCPropertyStatus(String name) {

		return isCProperty(name) ? CPropertyStatus.PRESENT : CPropertyStatus.ABSENT;
	}

	private CValue<?> getNoStructureFrameSlotValueType(String frameName) {

		CFrame frame = getCFrame(frameName);

		return metaFrameSlotsEnabled ? frame.getType() : frame;
	}

	private List<IValue> getNoStructureFrameSlotFixedValues(String frameName) {

		List<IValue> values = new ArrayList<IValue>();

		if (metaFrameSlotsEnabled) {

			values.add(getCFrame(frameName));
		}

		return values;
	}
}