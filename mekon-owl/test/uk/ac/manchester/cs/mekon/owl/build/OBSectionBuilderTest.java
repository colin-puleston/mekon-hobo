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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.owl.*;
import uk.ac.manchester.cs.mekon.test_util.*;
import uk.ac.manchester.cs.mekon.demomodel.*;

/**
 * @author Colin Puleston
 */
public class OBSectionBuilderTest extends DemoModelBasedTest {

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

		testFrameStatus(CORE_CONCEPT, FrameStatus.EXPOSED);
		testFrameStatus(CITIZEN, FrameStatus.EXPOSED);
		testFrameStatus(JOB, FrameStatus.EXPOSED);
		testFrameStatus(ACADEMIC_JOB, FrameStatus.EXPOSED);
		testFrameStatus(CONTENT_CONCEPT, FrameStatus.EXPOSED);

		testFrameSupers(CITIZEN, CORE_CONCEPT);
		testFrameSupers(ACADEMIC_JOB, JOB);
	}

	@Test
	public void test_frames_byGroupIncludingRoot() {

		addConcepts(CORE_CONCEPT);

		buildModel();

		testFrameStatus(CORE_CONCEPT, FrameStatus.EXPOSED);
		testFrameStatus(JOB, FrameStatus.EXPOSED);
		testFrameStatus(ACADEMIC_JOB, FrameStatus.EXPOSED);
		testFrameStatus(CONTENT_CONCEPT, FrameStatus.ABSENT);
	}

	@Test
	public void test_frames_byGroupExcludingRoot() {

		addConceptsExcludingRoot(CORE_CONCEPT);

		buildModel();

		testFrameStatus(CORE_CONCEPT, FrameStatus.ABSENT);
		testFrameStatus(JOB, FrameStatus.EXPOSED);
		testFrameStatus(ACADEMIC_JOB, FrameStatus.EXPOSED);
		testFrameStatus(CONTENT_CONCEPT, FrameStatus.ABSENT);
	}

	@Test
	public void test_frames_hidingCandidates_ALL() {

		addConcepts(CORE_CONCEPT, OBEntitySelection.ALL);

		buildModel();

		testFrameStatus(CORE_CONCEPT, FrameStatus.HIDDEN);
		testFrameStatus(JOB, FrameStatus.HIDDEN);
		testFrameStatus(ACADEMIC_TEACHING_JOB, FrameStatus.HIDDEN);
	}

	@Test
	public void test_frames_hidingCandidates_ROOTS_ONLY() {

		addConcepts(CORE_CONCEPT, OBEntitySelection.ROOTS_ONLY);

		buildModel();

		testFrameStatus(CORE_CONCEPT, FrameStatus.HIDDEN);
		testFrameStatus(JOB, FrameStatus.EXPOSED);
		testFrameStatus(ACADEMIC_TEACHING_JOB, FrameStatus.EXPOSED);
	}

	@Test
	public void test_frames_hidingCandidates_NON_ROOTS_ONLY() {

		addConcepts(CORE_CONCEPT, OBEntitySelection.NON_ROOTS_ONLY);

		buildModel();

		testFrameStatus(CORE_CONCEPT, FrameStatus.EXPOSED);
		testFrameStatus(JOB, FrameStatus.HIDDEN);
		testFrameStatus(ACADEMIC_TEACHING_JOB, FrameStatus.HIDDEN);
	}

	@Test
	public void test_frames_hidingCandidates_LEAFS_ONLY() {

		addConcepts(CORE_CONCEPT, OBEntitySelection.LEAFS_ONLY);

		buildModel();

		testFrameStatus(CORE_CONCEPT, FrameStatus.EXPOSED);
		testFrameStatus(JOB, FrameStatus.EXPOSED);
		testFrameStatus(ACADEMIC_TEACHING_JOB, FrameStatus.HIDDEN);
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
		testFrameStatus(SPECIALIST, FrameStatus.EXPOSED);
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
		testSlotStatus(EMPLOYMENT, JOB_PROPERTY, SlotStatus.PRESENT);
		testSlotStatus(PERSONAL, AGE_PROPERTY, SlotStatus.PRESENT);
	}

	@Test
	public void test_properties_byGroupIncludingRoot() {

		addProperties(INTER_CORE_PROPERTY);
		addProperties(JOB_TYPE_PROPERTY);

		buildModel();

		testSlotStatus(CITIZEN, TAX_PROPERTY, SlotStatus.PRESENT);
		testSlotStatus(JOB, JOB_TYPE_PROPERTY, SlotStatus.PRESENT);
		testSlotStatus(PERSONAL, AGE_PROPERTY, SlotStatus.ABSENT);
	}

	@Test
	public void test_properties_byGroupExcludingRoot() {

		addPropertiesExcludingRoot(INTER_CORE_PROPERTY);
		addPropertiesExcludingRoot(JOB_TYPE_PROPERTY);

		buildModel();

		testSlotStatus(CITIZEN, TAX_PROPERTY, SlotStatus.PRESENT);
		testSlotStatus(JOB, JOB_TYPE_PROPERTY, SlotStatus.ABSENT);
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
			JOB_PROPERTY,
			CCardinality.REPEATABLE_TYPES,
			getCFrame(JOB));
		testSlot(
			TRAIN_TRIP,
			TRAVEL_CLASS_PROPERTY,
			CCardinality.SINGLE_VALUE,
			getNoStructureFrameSlotValueType(TRAVEL_CLASS));
		testSlot(
			PERSONAL,
			AGE_PROPERTY,
			CCardinality.SINGLE_VALUE,
			CNumberFactory.min(0));
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

	private OBConceptGroup addConcepts(CIdentity rootId) {

		return addConcepts(createConceptInclusions(rootId));
	}

	private OBConceptGroup addConceptsExcludingRoot(CIdentity rootId) {

		OBConceptGroup inclusions = createConceptInclusions(rootId);

		inclusions.setInclusion(OBEntitySelection.NON_ROOTS_ONLY);

		return addConcepts(inclusions);
	}

	private OBConceptGroup addConcepts(
								CIdentity rootId,
								OBEntitySelection hidingCandidates) {

		OBConceptGroup inclusions = createConceptInclusions(rootId);

		inclusions.getConceptHiding().setCandidates(hidingCandidates);

		return addConcepts(inclusions);
	}

	private OBConceptGroup addConcepts(
								CIdentity rootId,
								OBConceptHidingFilter hidingFilter) {

		OBConceptGroup inclusions = createConceptInclusions(rootId);
		OBConceptHiding hiding = inclusions.getConceptHiding();

		hiding.setCandidates(OBEntitySelection.ALL);
		hiding.setFilter(hidingFilter);

		return addConcepts(inclusions);
	}

	private OBConceptGroup createConceptInclusions(CIdentity rootId) {

		return new OBConceptGroup(toIRI(rootId));
	}

	private OBConceptGroup addConcepts(OBConceptGroup inclusions) {

		sectionBuilder.getConcepts().addGroup(inclusions);

		return inclusions;
	}

	private OBPropertyGroup addProperties(CIdentity rootId) {

		return addProperties(createPropertyInclusions(rootId));
	}

	private OBPropertyGroup addPropertiesExcludingRoot(CIdentity rootId) {

		OBPropertyGroup inclusions = createPropertyInclusions(rootId);

		inclusions.setInclusion(OBEntitySelection.NON_ROOTS_ONLY);

		return addProperties(inclusions);
	}

	private OBPropertyGroup createPropertyInclusions(CIdentity rootId) {

		return new OBPropertyGroup(toIRI(rootId));
	}

	private OBPropertyGroup addProperties(OBPropertyGroup inclusions) {

		sectionBuilder.getProperties().addGroup(inclusions);

		return inclusions;
	}

	private void testFrameStatus(CIdentity frameId, FrameStatus expectedStatus) {

		assertEquals(expectedStatus, getFrameStatus(frameId));
	}

	private void testFrameSupers(CIdentity subId, CIdentity... expectedSupersIds) {

		CFrame sub = getCFrame(subId);
		Set<CFrame> supers = new HashSet<CFrame>(sub.getSupers());

		MekonTestUtils.testSet(supers, getCFrames(expectedSupersIds));
	}

	private void testSlotStatus(
					CIdentity containerId,
					CIdentity slotId,
					SlotStatus expectedStatus) {

		assertEquals(expectedStatus, getSlotStatus(containerId, slotId));
	}

	private void testSlot(
					CIdentity containerId,
					CIdentity slotId,
					CCardinality expectedCardinality,
					CValue<?> expectedValueType) {

		CSlot slot = getSlot(containerId, slotId);
		CCardinality cardinality = slot.getCardinality();

		assertEquals(expectedCardinality, cardinality);
		assertEquals(expectedValueType, slot.getValueType());
	}

	private void testSlotValues(
					CIdentity containerId,
					CIdentity slotId,
					List<IValue> expectedValues) {

		List<IValue> values = getSlotValues(containerId, slotId);

		MekonTestUtils.testListContents(values, expectedValues);
	}

	private FrameStatus getFrameStatus(CIdentity frameId) {

		if (!isCFrame(frameId)) {

			return FrameStatus.ABSENT;
		}

		if (getCFrame(frameId).hidden()) {

			return FrameStatus.HIDDEN;
		}

		return FrameStatus.EXPOSED;
	}

	private SlotStatus getSlotStatus(CIdentity containerId, CIdentity slotId) {

		return isSlot(containerId, slotId) ? SlotStatus.PRESENT : SlotStatus.ABSENT;
	}

	private boolean isSlot(CIdentity containerId, CIdentity slotId) {

		return getSlots(containerId).containsValueFor(slotId);
	}

	private CSlot getSlot(CIdentity containerId, CIdentity slotId) {

		return getSlots(containerId).get(slotId);
	}

	private CSlots getSlots(CIdentity containerId) {

		return getCFrame(containerId).getSlots();
	}

	private List<IValue> getSlotValues(CIdentity containerId, CIdentity slotId) {

		return getSlotValues(containerId).getIValues(slotId);
	}

	private CSlotValues getSlotValues(CIdentity containerId) {

		return getCFrame(containerId).getSlotValues();
	}

	private CValue<?> getNoStructureFrameSlotValueType(CIdentity frameId) {

		CFrame frame = getCFrame(frameId);

		return cFrameValuedSlotsEnabled ? frame.getType() : frame;
	}

	private List<IValue> getNoStructureFrameSlotFixedValues(CIdentity frameId) {

		List<IValue> values = new ArrayList<IValue>();

		if (cFrameValuedSlotsEnabled) {

			values.add(getCFrame(frameId));
		}

		return values;
	}

	static private IRI toIRI(CIdentity id) {

		return IRI.create(id.getIdentifier());
	}
}
