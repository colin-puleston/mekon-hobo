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

import java.util.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.mechanism.*;

/**
 * @author Colin Puleston
 */
public class MekonTest extends FramesTestUtils {

	private CModel model;
	private IReasoner iReasoner;

	private class DynamicSlotInsertionReasoner extends DefaultIReasoner {

		private CSlot slotType;
		private CFrame valueType;

		private boolean firstInsert = true;

		public void updateFrame(IEditor iEditor, IFrame frame, Set<IUpdateOp> ops) {

			ISlots slots = frame.getSlots();

			if (!slots.isEmpty() && allValuesSet(slots) && !isDynamicSlot(slots)) {

				List<ISlot> startSlots = slots.asList();

				slots.clear();
				addInsertSlot(frame);
				slots.addAll(startSlots);
			}
		}

		DynamicSlotInsertionReasoner(CFrame frameType, String typesPrefix) {

			valueType = createValueType(typesPrefix);
			slotType = createSlotType();

			frameType.asModelFrame().setIReasoner(this);
		}

		private CFrame createValueType(String typesPrefix) {

			return createCFrame(typesPrefix + "InsertSlotValue");
		}

		private CSlot createSlotType() {

			return createCSlot("insertSlot", CCardinality.FREE, valueType);
		}

		private boolean isDynamicSlot(ISlots slots) {

			for (ISlot slot : slots.asList()) {

				if (slot.getType().equals(slotType)) {

					return true;
				}
			}

			return false;
		}

		private boolean allValuesSet(ISlots slots) {

			for (ISlot slot : slots.asList()) {

				if (slot.getValues().isEmpty()) {

					return false;
				}
			}

			return true;
		}

		private void addInsertSlot(IFrame frame) {

			ISlot slot = frame.createEditor().addSlot(slotType);

			if (firstInsert) {

				firstInsert = false;

				slot.getValuesEditor().add(valueType.instantiate());
			}
		}
	}

	public MekonTest(CModel model) {

		this(model, null);
	}

	public IFrame createComplexInstance() {

		return createComplexInstance("", false);
	}

	public IFrame createComplexInstance(
						String typesPrefix,
						boolean dynamicSlotInsertion) {

		IFrame fa = createIFrame(typesPrefix + "A");
		IFrame fb = createIFrame(typesPrefix + "B");
		IFrame fc = createIFrame(typesPrefix + "C");
		IFrame fd = createIFrame(typesPrefix + "D");

		CFrame te = createCFrame(typesPrefix + "E");
		CFrame tex = createCFrame(typesPrefix + "EX");
		CFrame tey = createCFrame(typesPrefix + "EY");

		CNumber n = CIntegerDef.range(1, 10).createNumber();

		addSuperFrame(tex, te);
		addSuperFrame(tey, te);

		if (dynamicSlotInsertion) {

			new DynamicSlotInsertionReasoner(fa.getType(), typesPrefix);
		}

		createISlotWithValue(fa, "sab", fb);
		createISlotWithValue(fa, "sac", fc);
		createISlotWithValue(fb, "sbd", fd);
		createISlotWithValues(fb, "sbe", te.getType(), tex, tey);
		createISlotWithValues(fb, "sbn", n, n.getMax());

		return fa;
	}

	public CModelFrame createCFrame(String name) {

		return createCFrame(name, false);
	}

	public CModelFrame createHiddenCFrame(String name) {

		return createCFrame(name, true);
	}

	public void addSuperFrame(CFrame sub, CFrame sup) {

		sub.asModelFrame().addSuper(sup.asModelFrame());
	}

	public CProperty createCProperty(String name) {

		return model.addProperty(createIdentity(name));
	}

	public CSlot createCSlot(CCardinality cardinality) {

		return createCSlot(cardinality, createSlotValueType());
	}

	public CSlot createCSlot(CCardinality cardinality, CValue<?> valueType) {

		return createCSlot("Slot", cardinality, valueType);
	}

	public CSlot createCSlot(
					String propName,
					CCardinality cardinality,
					CValue<?> valueType) {

		return createCSlot(createCSlotContainer(), propName, cardinality, valueType);
	}

	public CSlot createCSlot(CFrame container, CCardinality cardinality) {

		return createCSlot(container, cardinality, createSlotValueType());
	}

	public CSlot createCSlot(
					CFrame container,
					CCardinality cardinality,
					CValue<?> valueType) {

		String propName = createDefaultSlotPropertyName(container);

		return createCSlot(container, propName, cardinality, valueType);
	}

	public CSlot createCSlot(
					CFrame container,
					String propName,
					CCardinality cardinality,
					CValue<?> valueType) {

		CProperty prop = createCProperty(propName);

		return createCSlot(container, prop, cardinality, valueType);
	}

	public CSlot createCSlot(
					CFrame container,
					CProperty property,
					CCardinality cardinality,
					CValue<?> valueType) {

		return container
					.asModelFrame()
					.createEditor()
					.addSlot(
						property,
						cardinality,
						valueType);
	}

	public IFrame createIFrame(String typeName) {

		return new IFrame(createCFrame(typeName), IFrameCategory.CONCRETE);
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

		String propName = createDefaultSlotPropertyName(container.getType());

		return createISlot(container, propName, cardinality, valueType);
	}

	public ISlot createISlot(
					IFrame container,
					String propName,
					CCardinality cardinality,
					CValue<?> valueType) {

		CSlot type = createCSlot(container.getType(), propName, cardinality, valueType);

		return container.createEditor().addSlot(type);
	}

	public void createISlotWithValue(IFrame container, String propName, IFrame value) {

		createISlotWithValues(container, propName, value.getType(), value);
	}

	public void createISlotWithValues(
					IFrame container,
					String propName,
					CValue valueType,
					IValue... values) {

		ISlot slot = createISlot(container, propName, CCardinality.FREE, valueType);

		for (IValue value : values) {

			slot.getValuesEditor().add(value);
		}
	}

	public void normaliseCFramesHierarchy() {

		new CHierarchyNormaliser(model);
	}

	public CModel getModel() {

		return model;
	}

	MekonTest() {

		this(new CModel(), null);
	}

	MekonTest(IReasoner iReasoner) {

		this(new CModel(), iReasoner);
	}

	private MekonTest(CModel model, IReasoner iReasoner) {

		this.model = model;
		this.iReasoner = iReasoner;
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

	private CModelFrame createCFrame(String name, boolean hidden) {

		CModelFrame frame = model.addFrame(createIdentity(name), hidden);

		if (iReasoner != null) {

			frame.setIReasoner(iReasoner);
		}

		return frame;
	}

	private String createDefaultSlotPropertyName(CFrame container) {

		return container.getIdentity().getLabel() + "-slot";
	}

	private CIdentity createIdentity(String name) {

		return new CIdentity("id:" + name, name);
	}
}
