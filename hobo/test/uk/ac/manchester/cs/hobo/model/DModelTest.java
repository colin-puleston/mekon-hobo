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

package uk.ac.manchester.cs.hobo.model;

import java.util.*;

import org.junit.Test;
import static org.junit.Assert.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;

import uk.ac.manchester.cs.hobo.manage.*;
import uk.ac.manchester.cs.hobo.model.testmodel.*;
import uk.ac.manchester.cs.hobo.model.motor.*;

/**
 * @author Colin Puleston
 */
public class DModelTest extends MekonTestUtils {

	static private String INTERFACE_R_ID = InterfaceR.class.getName();
	static private String CLASS_A_ID = ClassA.class.getName();
	static private String CLASS_AX_ID = ClassAX.class.getName();
	static private String CLASS_B_ID = ClassB.class.getName();
	static private String BS_ARRAY_VAR_NAME = "bsArray";
	static private String B_CELL_VAR_NAME = "bCell";
	static private String INT_CELL_VAR_NAME = "intCell";

	static private String A_EXTERNAL_ID = "A-External";
	static private String INT_EXTERNAL_ID = "int-external";

	static private String FRAME_R_ID = INTERFACE_R_ID;
	static private String FRAME_A_ID = A_EXTERNAL_ID;
	static private String FRAME_AX_ID = CLASS_AX_ID;
	static private String FRAME_B_ID = CLASS_B_ID;
	static private String BS_SLOT_ID = BS_ARRAY_VAR_NAME;
	static private String B_SLOT_ID = B_CELL_VAR_NAME;
	static private String INT_SLOT_ID = INT_EXTERNAL_ID;

	static private String CLASS_A_DERIVED_LABEL = "Class-A";
	static private String INT_CELL_DERIVED_LABEL = "int-cell";
	static private String A_EXTERNAL_LABEL = A_EXTERNAL_ID;
	static private String INT_EXTERNAL_LABEL = INT_EXTERNAL_ID;

	static private IValue[] INITIAL_BS_SLOT_VALUES = new IValue[0];
	static private IValue[] INITIAL_B_SLOT_VALUES = new IValue[0];
	static private IValue[] INITIAL_INT_SLOT_VALUES = new IValue[]{new INumber(5)};

	private class ExternalSectionBuilder implements CSectionBuilder {

		public boolean supportsIncrementalBuild() {

			return false;
		}

		public void build(CBuilder builder) {

			CIdentity frameAId = new CIdentity(A_EXTERNAL_ID, A_EXTERNAL_LABEL);
			CIdentity intSlotId = new CIdentity(INT_EXTERNAL_ID, INT_EXTERNAL_LABEL);

			CFrame frameA = builder.addFrame(frameAId, false);
			CFrameEditor frameAEd = builder.getFrameEditor(frameA);

			frameAEd.addSlot(intSlotId, CCardinality.REPEATABLE_TYPES, CNumber.INTEGER);
		}
	}

	@Test
	public void test_framesBuiltForDObjects() {

		DModel model = createModel(true);

		testCFrameCount(model, 4);

		testCFrameBuilt(model, FRAME_R_ID);
		testCFrameBuilt(model, FRAME_A_ID);
		testCFrameBuilt(model, FRAME_AX_ID);
		testCFrameBuilt(model, FRAME_B_ID);

		testCFrameSuperAdded(model, FRAME_A_ID, FRAME_R_ID);
		testCFrameSuperAdded(model, FRAME_AX_ID, FRAME_A_ID);
	}

	@Test
	public void test_slotsBuiltForDFields() {

		DModel model = createModel(true);

		testCSlotCount(model, FRAME_R_ID, 0);
		testCSlotCount(model, FRAME_A_ID, 2);
		testCSlotCount(model, FRAME_AX_ID, 1);
		testCSlotCount(model, FRAME_B_ID, 0);

		testCSlotBuilt(
			model,
			FRAME_A_ID,
			BS_SLOT_ID,
			CCardinality.UNIQUE_TYPES,
			getCFrame(model, FRAME_B_ID),
			CEditability.DEFAULT);

		testCSlotBuilt(
			model,
			FRAME_A_ID,
			INT_SLOT_ID,
			CCardinality.SINGLE_VALUE,
			CNumber.INTEGER,
			CEditability.QUERY_ONLY);

		testCSlotBuilt(
			model,
			FRAME_AX_ID,
			B_SLOT_ID,
			CCardinality.SINGLE_VALUE,
			getCFrame(model, FRAME_B_ID),
			CEditability.DEFAULT);
	}

	@Test
	public void test_labelsFromDirectModel() {

		testLabels(true, CLASS_A_DERIVED_LABEL, INT_CELL_DERIVED_LABEL);
	}

	@Test
	public void test_labelsFromExternalModel() {

		testLabels(false, A_EXTERNAL_LABEL, INT_EXTERNAL_LABEL);
	}

	@Test
	public void test_instantiateDObjects() {

		DModel model = createModel(true);

		ClassA aObject = model.getConcept(ClassA.class).instantiate();
		ClassAX axObject = model.getConcept(ClassAX.class).instantiate();
		ClassB bObject = model.getConcept(ClassB.class).instantiate();

		testDObjectIFrameMapping(aObject, FRAME_A_ID);
		testDObjectIFrameMapping(axObject, FRAME_AX_ID);
		testDObjectIFrameMapping(bObject, FRAME_B_ID);

		testDFieldISlotValues(aObject.bsArray, INITIAL_BS_SLOT_VALUES);
		testDFieldISlotValues(aObject.intCell, INITIAL_INT_SLOT_VALUES);
		testDFieldISlotValues(axObject.bCell, INITIAL_B_SLOT_VALUES);
	}

	private DModel createModel(boolean labelsFromDirectModel) {

		DBuilder dBuilder = DManager.createEmptyBuilder();

		populateModelMap(dBuilder, labelsFromDirectModel);
		dBuilder.addDClasses(getTestModelPackageName());
		dBuilder.getCBuilder().addSectionBuilder(new ExternalSectionBuilder());

		return dBuilder.build();
	}

	private void populateModelMap(DBuilder dBuilder, boolean labelsFromDirectModel) {

		DModelMap map = dBuilder.getModelMap();

		map.setLabelsFromDirectClasses(labelsFromDirectModel);
		map.setLabelsFromDirectFields(labelsFromDirectModel);

		DClassMap classAMap = map.addClassMap(ClassA.class, A_EXTERNAL_ID);

		classAMap.addFieldMap(INT_CELL_VAR_NAME, INT_EXTERNAL_ID);
	}

	private String getTestModelPackageName() {

		return InterfaceR.class.getPackage().getName();
	}

	private void testCFrameCount(DModel model, int expected) {

		int got = model.getCModel().getFrames().size();

		assertTrue("Unexpected number of frames in model: " + got, got == expected);
	}

	private void testCFrameBuilt(DModel model, String id) {

		CIdentifieds<CFrame> frames = model.getCModel().getFrames();

		assertTrue("Frame not built: " + id, frames.containsKey(id));
	}

	private void testCFrameSuperAdded(DModel model, String subId, String expectedSupId) {

		CFrame expectedSup = getCFrame(model, expectedSupId);
		List<CFrame> sups = getCFrame(model, subId).getSupers();

		assertTrue("Unexpected number of super-frames: " + sups.size(), sups.size() == 1);
		assertTrue("Expected super-frame not found", sups.contains(expectedSup));
	}

	private void testCSlotCount(DModel model, String containerId, int expected) {

		int got = getCFrame(model, containerId).getSlots().size();

		assertTrue("Unexpected number of slots: " + got, got == expected);
	}

	private void testCSlotBuilt(
					DModel model,
					String containerId,
					String fieldName,
					CCardinality expectedCardinality,
					CValue<?> expectedValueType,
					CEditability expectedEditability) {

		CSlot slot = getCSlot(model, containerId, fieldName);

		assertEquals(expectedCardinality, slot.getCardinality());
		assertEquals(expectedValueType, slot.getValueType());
		assertEquals(expectedEditability, slot.getEditability());
	}

	private void testLabels(
					boolean fromDirectModel,
					String expectedFrameALabel,
					String expectedIntSlotLabel) {

		DModel model = createModel(fromDirectModel);
		CFrame frameA = getCFrame(model, FRAME_A_ID);
		CSlot intSlot = getCSlot(model, FRAME_A_ID, INT_SLOT_ID);

		testLabel(frameA, expectedFrameALabel);
		testLabel(intSlot, expectedIntSlotLabel);
	}

	private void testLabel(CIdentified identified, String expected) {

		String got = identified.getIdentity().getLabel();

		assertEquals(expected, got);
	}

	private void testDObjectIFrameMapping(DObject dObject, String expectedId) {

		String gotId = dObject.getFrame().getType().getIdentity().getIdentifier();

		assertEquals(expectedId, gotId);
	}

	private void testDFieldISlotValues(DFieldView<?> dFieldView, IValue[] expected) {

		testList(dFieldView.getSlot().getValues().asList(), Arrays.asList(expected));
	}

	private CFrame getCFrame(DModel model, String id) {

		return model.getCModel().getFrames().get(id);
	}

	private CSlot getCSlot(DModel model, String containerId, String fieldName) {

		return getCFrame(model, containerId).getSlots().get(fieldName);
	}
}
