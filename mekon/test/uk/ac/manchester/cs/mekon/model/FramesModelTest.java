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

package uk.ac.manchester.cs.mekon.model;

import org.junit.After;

import uk.ac.manchester.cs.mekon.mechanism.*;
import uk.ac.manchester.cs.mekon.mechanism.core.*;

/**
 * @author Colin Puleston
 */
public class FramesModelTest extends FramesTestUtils {

	static private CModel createEmptyModel() {

		return ZCModelAccessor.get().createModel();
	}

	private CModel model;
	private IReasoner iReasoner;

	public FramesModelTest() {

		this(createEmptyModel(), null);
	}

	public FramesModelTest(IReasoner iReasoner) {

		this(createEmptyModel(), iReasoner);
	}

	public FramesModelTest(CModel model) {

		this(model, null);
	}

	@After
	public void clearUp() {

		CManager.clearUp(model);
	}

	public void setQueriesEnabled(boolean enabled) {

		model.setQueriesEnabled(enabled);
	}

	public CAtomicFrame createCFrame(String name) {

		return createCFrame(name, false);
	}

	public CAtomicFrame createHiddenCFrame(String name) {

		return createCFrame(name, true);
	}

	public CSlot createCSlot(CCardinality cardinality) {

		return createCSlot(cardinality, createSlotValueType());
	}

	public CSlot createCSlot(CCardinality cardinality, CValue<?> valueType) {

		return createCSlot("Slot", cardinality, valueType);
	}

	public CSlot createCSlot(String name, CValue<?> valueType) {

		return createCSlot(name, CCardinality.REPEATABLE_TYPES, valueType);
	}

	public CSlot createCSlot(
					String name,
					CCardinality cardinality,
					CValue<?> valueType) {

		return createCSlot(createCSlotContainer(), name, cardinality, valueType);
	}

	public CSlot createCSlot(CFrame container, CCardinality cardinality) {

		return createCSlot(container, cardinality, createSlotValueType());
	}

	public CSlot createCSlot(
					CFrame container,
					CCardinality cardinality,
					CValue<?> valueType) {

		String name = createDefaultSlotName(container);

		return createCSlot(container, name, cardinality, valueType);
	}

	public CSlot createCSlot(
					CFrame container,
					String name,
					CValue<?> valueType) {

		return createCSlot(container, name, CCardinality.REPEATABLE_TYPES, valueType);
	}

	public CSlot createCSlot(
					CFrame container,
					String name,
					CCardinality cardinality,
					CValue<?> valueType) {

		return createCSlot(container, createIdentity(name), cardinality, valueType);
	}

	public CSlot createCSlot(
					CFrame container,
					CIdentity identity,
					CCardinality cardinality,
					CValue<?> valueType) {

		return container
					.asAtomicFrame()
					.createEditor()
					.addSlot(
						identity,
						cardinality,
						valueType);
	}

	public IFrame createIFrame(String typeName) {

		return createIFrame(typeName, IFrameFunction.ASSERTION);
	}

	public IFrame createIFrame(String typeName, IFrameFunction function) {

		return new IFrame(createCFrame(typeName), function);
	}

	public ISlot createISlot(CCardinality cardinality) {

		return createISlot(cardinality, createSlotValueType());
	}

	public ISlot createISlot(CCardinality cardinality, CValue<?> valueType) {

		return createISlot(createISlotContainer(), cardinality, valueType);
	}

	public ISlot createISlot(
					IFrame container,
					CCardinality cardinality,
					CValue<?> valueType) {

		String name = createDefaultSlotName(container.getType());

		return createISlot(container, name, cardinality, valueType);
	}

	public ISlot createISlot(IFrame container, String name, CValue<?> valueType) {

		return createISlot(container, name, CCardinality.REPEATABLE_TYPES, valueType);
	}

	public ISlot createISlot(
					IFrame container,
					String name,
					CCardinality cardinality,
					CValue<?> valueType) {

		CSlot type = createCSlot(container.getType(), name, cardinality, valueType);

		return container.createEditor().addSlot(type);
	}

	public void normaliseCFramesHierarchy() {

		new CHierarchyNormaliser(model);
	}

	public CModel getModel() {

		return model;
	}

	public TestInstances createTestInstances() {

		return new TestInstances(this);
	}

	private FramesModelTest(CModel model, IReasoner iReasoner) {

		this.model = model;
		this.iReasoner = iReasoner;
	}

	private CAtomicFrame createCFrame(String name, boolean hidden) {

		CAtomicFrame frame = model.addFrame(createIdentity(name), hidden);

		if (iReasoner != null) {

			frame.setIReasoner(iReasoner);
		}

		return frame;
	}

	private CFrame createCSlotContainer() {

		return createCFrame("Slot-container");
	}

	private IFrame createISlotContainer() {

		return createIFrame("Slot-container");
	}

	private CValue<?> createSlotValueType() {

		return createCFrame("Slot-value-type");
	}

	private String createDefaultSlotName(CFrame container) {

		return container.getIdentity().getLabel() + "-slot";
	}
}
