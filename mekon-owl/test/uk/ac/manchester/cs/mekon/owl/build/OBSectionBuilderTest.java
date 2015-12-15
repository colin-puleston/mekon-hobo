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

import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.demomodel.*;
import uk.ac.manchester.cs.mekon.owl.*;

/**
 * @author Colin Puleston
 */
public class OBSectionBuilderTest extends DemoModelBasedTest {

	static private final String DOMAIN_CONCEPT = "DomainConcept";
	static private final String DATA_TYPE = "DataTypeConcept";
	static private final String TRAVEL_CLASS = "TravelClass";
	static private final String TRAIN = "Train";

	static private final String CITIZEN_ASPECT_PROPERTY = "citizenAspect";
	static private final String AGE_PROPERTY = "age";
	static private final String TRAVEL_CLASS_PROPERTY = "travelClass";

	static private final List<IValue> NO_IVALUES = Collections.emptyList();

	private OBSectionBuilder sectionBuilder;
	private boolean cFrameValuedSlotsEnabled = false;

	private enum FrameStatus {

		EXPOSED, HIDDEN, ABSENT;
	}

	private enum SlotStatus {

		PRESENT, ABSENT;
	}

	@Before
	public void setUp() {

		sectionBuilder = new OBSectionBuilder(ODemoModel.create());
	}

	@Test
	public void test_frames() {

		buildModel();

		testFrameStatus(DOMAIN_CONCEPT, FrameStatus.EXPOSED);
		testFrameStatus(CITIZEN, FrameStatus.EXPOSED);
		testFrameStatus(JOB, FrameStatus.EXPOSED);
		testFrameStatus(ACADEMIC_JOB, FrameStatus.EXPOSED);
		testFrameStatus(DATA_TYPE, FrameStatus.EXPOSED);

		testFrameSupers(CITIZEN, DOMAIN_CONCEPT);
		testFrameSupers(ACADEMIC_JOB, JOB);
	}

	@Test
	public void test_frames_byGroupIncludingRoot() {

		addConcepts(DOMAIN_CONCEPT);

		buildModel();

		testFrameStatus(DOMAIN_CONCEPT, FrameStatus.EXPOSED);
		testFrameStatus(JOB, FrameStatus.EXPOSED);
		testFrameStatus(ACADEMIC_JOB, FrameStatus.EXPOSED);
		testFrameStatus(DATA_TYPE, FrameStatus.ABSENT);
	}

	@Test
	public void test_frames_byGroupExcludingRoot() {

		addConceptsExcludingRoot(DOMAIN_CONCEPT);

		buildModel();

		testFrameStatus(DOMAIN_CONCEPT, FrameStatus.ABSENT);
		testFrameStatus(JOB, FrameStatus.EXPOSED);
		testFrameStatus(ACADEMIC_JOB, FrameStatus.EXPOSED);
		testFrameStatus(DATA_TYPE, FrameStatus.ABSENT);
	}

	@Test
	public void test_frames_hidingCandidates_ALL() {

		addConcepts(DOMAIN_CONCEPT, OBEntitySelection.ALL);

		buildModel();

		testFrameStatus(DOMAIN_CONCEPT, FrameStatus.HIDDEN);
		testFrameStatus(JOB, FrameStatus.HIDDEN);
		testFrameStatus(ACADEMIC_JOB, FrameStatus.HIDDEN);
	}

	@Test
	public void test_frames_hidingCandidates_ROOTS_ONLY() {

		addConcepts(DOMAIN_CONCEPT, OBEntitySelection.ROOTS_ONLY);

		buildModel();

		testFrameStatus(DOMAIN_CONCEPT, FrameStatus.HIDDEN);
		testFrameStatus(JOB, FrameStatus.EXPOSED);
		testFrameStatus(ACADEMIC_JOB, FrameStatus.EXPOSED);
	}

	@Test
	public void test_frames_hidingCandidates_NON_ROOTS_ONLY() {

		addConcepts(DOMAIN_CONCEPT, OBEntitySelection.NON_ROOTS_ONLY);

		buildModel();

		testFrameStatus(DOMAIN_CONCEPT, FrameStatus.EXPOSED);
		testFrameStatus(JOB, FrameStatus.HIDDEN);
		testFrameStatus(ACADEMIC_JOB, FrameStatus.HIDDEN);
	}

	@Test
	public void test_frames_hidingFilter_ANY() {

		addConcepts(JOB, OBConceptHidingFilter.ANY);
		addConcepts(JOB_TYPE, OBConceptHidingFilter.ANY);

		buildModel();

		testFrameStatus(JOB, FrameStatus.HIDDEN);
		testFrameStatus(ACADEMIC_JOB, FrameStatus.HIDDEN);
		testFrameStatus(ACADEMIC_TEACHING_JOB, FrameStatus.HIDDEN);
		testFrameStatus(JOB_TYPE, FrameStatus.HIDDEN);
		testFrameStatus(SPECIALIST, FrameStatus.HIDDEN);
		testFrameStatus(TEACHER, FrameStatus.HIDDEN);
	}

	@Test
	public void test_frames_hidingFilter_DEFINIED_CONCEPTS_ONLY() {

		addConcepts(JOB, OBConceptHidingFilter.DEFINIED_CONCEPTS_ONLY);
		addConcepts(JOB_TYPE, OBConceptHidingFilter.DEFINIED_CONCEPTS_ONLY);

		buildModel();

		testFrameStatus(JOB, FrameStatus.EXPOSED);
		testFrameStatus(ACADEMIC_JOB, FrameStatus.HIDDEN);
		testFrameStatus(ACADEMIC_TEACHING_JOB, FrameStatus.HIDDEN);
		testFrameStatus(JOB_TYPE, FrameStatus.EXPOSED);
		testFrameStatus(SPECIALIST, FrameStatus.HIDDEN);
		testFrameStatus(TEACHER, FrameStatus.EXPOSED);
	}

	@Test
	public void test_frames_hidingFilter_DEFINIED_SUB_TREES_ONLY() {

		addConcepts(JOB, OBConceptHidingFilter.DEFINIED_SUB_TREES_ONLY);
		addConcepts(JOB_TYPE, OBConceptHidingFilter.DEFINIED_SUB_TREES_ONLY);

		buildModel();

		testFrameStatus(JOB, FrameStatus.EXPOSED);
		testFrameStatus(ACADEMIC_JOB, FrameStatus.HIDDEN);
		testFrameStatus(ACADEMIC_TEACHING_JOB, FrameStatus.HIDDEN);
		testFrameStatus(JOB_TYPE, FrameStatus.EXPOSED);
		testFrameStatus(SPECIALIST, FrameStatus.EXPOSED);
		testFrameStatus(TEACHER, FrameStatus.EXPOSED);
	}

	@Test
	public void test_properties() {

		buildModel();

		testSlotStatus(CITIZEN, TAX_PROPERTY, SlotStatus.PRESENT);
		testSlotStatus(EMPLOYMENT, JOBS_PROPERTY, SlotStatus.PRESENT);
		testSlotStatus(PERSONAL, AGE_PROPERTY, SlotStatus.PRESENT);
	}

	@Test
	public void test_properties_byGroupIncludingRoot() {

		addProperties(CITIZEN_ASPECT_PROPERTY);
		addProperties(JOBS_PROPERTY);

		buildModel();

		testSlotStatus(CITIZEN, TAX_PROPERTY, SlotStatus.PRESENT);
		testSlotStatus(EMPLOYMENT, JOBS_PROPERTY, SlotStatus.PRESENT);
		testSlotStatus(PERSONAL, AGE_PROPERTY, SlotStatus.ABSENT);
	}

	@Test
	public void test_properties_byGroupExcludingRoot() {

		addPropertiesExcludingRoot(CITIZEN_ASPECT_PROPERTY);
		addPropertiesExcludingRoot(JOBS_PROPERTY);

		buildModel();

		testSlotStatus(CITIZEN, TAX_PROPERTY, SlotStatus.PRESENT);
		testSlotStatus(EMPLOYMENT, JOBS_PROPERTY, SlotStatus.ABSENT);
		testSlotStatus(PERSONAL, AGE_PROPERTY, SlotStatus.ABSENT);
	}

	@Test
	public void test_slots_cFrameValuedSlotsEnabled() {

		enableCFrameValuedSlots();
		buildModel();

		testSlots();
	}

	@Test
	public void test_slots_cFrameValuedSlotsDisabled() {

		buildModel();

		testSlots();
	}

	@Test
	public void test_slotValues_cFrameValuedSlotsEnabled() {

		enableCFrameValuedSlots();
		buildModel();

		testSlotValues();
	}

	@Test
	public void test_slotValues_cFrameValuedSlotsDisabled() {

		buildModel();

		testSlotValues();
	}

	private void enableCFrameValuedSlots() {

		sectionBuilder
			.setDefaultFrameSlotsPolicy(
				OBFrameSlotsPolicy.CFRAME_VALUED_IF_NO_STRUCTURE);

		cFrameValuedSlotsEnabled = true;
	}

	private void testSlots() {

		testSlot(
			CITIZEN,
			TAX_PROPERTY,
			CCardinality.SINGLE_VALUE,
			getNoStructureFrameSlotValueType(TAX));
		testSlot(
			UNEMPLOYED_CITIZEN,
			TAX_PROPERTY,
			CCardinality.SINGLE_VALUE,
			getNoStructureFrameSlotValueType(ZERO_TAX));
		testSlot(
			EMPLOYMENT,
			JOBS_PROPERTY,
			CCardinality.REPEATABLE_TYPES,
			getCFrame(JOB));
		testSlot(
			TRAIN,
			TRAVEL_CLASS_PROPERTY,
			CCardinality.SINGLE_VALUE,
			getNoStructureFrameSlotValueType(TRAVEL_CLASS));
		testSlot(
			PERSONAL,
			AGE_PROPERTY,
			CCardinality.SINGLE_VALUE,
			CNumber.min(0));
	}

	private void testSlotValues() {

		testSlotValues(
			UNEMPLOYED_CITIZEN,
			TAX_PROPERTY,
			getNoStructureFrameSlotFixedValues(ZERO_TAX));
		testSlotValues(
			UNEMPLOYED_CITIZEN,
			BENEFIT_PROPERTY,
			getNoStructureFrameSlotFixedValues(UNEMPLOYMENT_BENEFIT));
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

	private void testFrameStatus(String name, FrameStatus expectedStatus) {

		assertEquals(expectedStatus, getFrameStatus(name));
	}

	private void testFrameSupers(String subName, String... expectedSupersNames) {

		CFrame sub = getCFrame(subName);
		Set<CFrame> supers = new HashSet<CFrame>(sub.getSupers());

		MekonTestUtils.testSet(supers, getCFrames(expectedSupersNames));
	}

	private void testSlotStatus(
					String containerName,
					String slotName,
					SlotStatus expectedStatus) {

		assertEquals(expectedStatus, getSlotStatus(containerName, slotName));
	}

	private void testSlot(
					String containerName,
					String slotName,
					CCardinality expectedCardinality,
					CValue<?> expectedValueType) {

		CSlot slot = getSlot(containerName, slotName);
		CCardinality cardinality = slot.getCardinality();

		assertTrue(
			"Unexpected CSlot cardinality: " + cardinality,
			cardinality == expectedCardinality);
		assertEquals(expectedValueType, slot.getValueType());
	}

	private void testSlotValues(
					String containerName,
					String slotName,
					List<IValue> expectedValues) {

		List<IValue> values = getSlotValues(containerName, slotName);

		MekonTestUtils.testListContents(values, expectedValues);
	}

	private FrameStatus getFrameStatus(String name) {

		if (!isCFrame(name)) {

			return FrameStatus.ABSENT;
		}

		if (getCFrame(name).hidden()) {

			return FrameStatus.HIDDEN;
		}

		return FrameStatus.EXPOSED;
	}

	private SlotStatus getSlotStatus(String containerName, String slotName) {

		return isSlot(containerName, slotName)
					? SlotStatus.PRESENT
					: SlotStatus.ABSENT;
	}

	private boolean isSlot(String containerName, String slotName) {

		return getSlots(containerName).containsValueFor(nameToIdentity(slotName));
	}

	private CSlot getSlot(String containerName, String slotName) {

		return getSlots(containerName).get(nameToIdentity(slotName));
	}

	private CSlots getSlots(String containerName) {

		return getCFrame(containerName).getSlots();
	}

	private List<IValue> getSlotValues(String containerName, String slotName) {

		return getSlotValues(containerName).getIValues(nameToIdentity(slotName));
	}

	private CSlotValues getSlotValues(String containerName) {

		return getCFrame(containerName).getSlotValues();
	}

	private CValue<?> getNoStructureFrameSlotValueType(String frameName) {

		CFrame frame = getCFrame(frameName);

		return cFrameValuedSlotsEnabled ? frame.getType() : frame;
	}

	private List<IValue> getNoStructureFrameSlotFixedValues(String frameName) {

		List<IValue> values = new ArrayList<IValue>();

		if (cFrameValuedSlotsEnabled) {

			values.add(getCFrame(frameName));
		}

		return values;
	}

	static private IRI nameToIRI(String name) {

		return IRI.create(nameToIdentifier(name));
	}
}
