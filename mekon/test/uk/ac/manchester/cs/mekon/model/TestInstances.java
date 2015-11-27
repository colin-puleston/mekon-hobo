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

import uk.ac.manchester.cs.mekon.mechanism.*;

/**
 * @author Colin Puleston
 */
public class TestInstances extends FramesTestUtils {

	private FramesModelTest modelTest;

	private String typesPrefix = "";
	private IFrameFunction function = IFrameFunction.ASSERTION;

	private CFrame ta;
	private CFrame tb;
	private CFrame tc;
	private CFrame td;

	private CFrame te;
	private CFrame tcx;
	private CFrame tcy;
	private CFrame tex;
	private CFrame tey;

	private CNumber tn;

	private CSlot sab;
	private CSlot sac;
	private CSlot sbd;
	private CSlot sbe;
	private CSlot sbn;

	private class DynamicSlotInsertionReasoner extends IReasonerDefault {

		private CSlot slotType;
		private CFrame valueType;

		private boolean firstInsert = true;

		public Set<IUpdateOp> updateFrame(
								IEditor iEditor,
								IFrame frame,
								Set<IUpdateOp> ops) {

			ISlots slots = frame.getSlots();

			if (requireInsertion(slots)) {

				List<ISlot> startSlots = slots.asList();

				slots.clear();
				addInsertSlot(frame);
				slots.addAll(startSlots);
			}

			return Collections.<IUpdateOp>emptySet();
		}

		DynamicSlotInsertionReasoner() {

			valueType = createCFrame("InsertSlotValue");
			slotType = createCSlot("insertSlot", valueType);

			ta.asAtomicFrame().setIReasoner(this);
		}

		private boolean requireInsertion(ISlots slots) {

			return !slots.isEmpty() && allValuesSet(slots) && !dynamicSlot(slots);
		}

		private boolean dynamicSlot(ISlots slots) {

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

	private abstract class InstanceCreator {

		final IFrame fa = createIFrame(ta);
		final IFrame fb = createIFrame(tb);
		final IFrame fc = createIFrame(tc);
		final IFrame fd = createIFrame(td);

		final IFrame fcx = createIFrame(tcx);
		final IFrame fcy = createIFrame(tcy);

		IFrame get() {

			setSlotValues();

			return fa;
		}

		abstract void setSlotValues();
	}

	private class BasicInstanceCreator extends InstanceCreator {

		void setSlotValues() {

			setISlotValues(fa, sab, fb);
			setISlotValues(fa, sac, fcx);
			setISlotValues(fb, sbd, fd);
			setISlotValues(fb, sbe, tex, tey);
			setISlotValues(fb, sbn, tn.getMax());
		}
	}

	private class SubsumerInstanceCreator extends InstanceCreator {

		void setSlotValues() {

			setISlotValues(fa, sab, fb);
			setISlotValues(fb, sbd, fd);
			setISlotValues(fb, sbe, te);
			setISlotValues(fb, sbn, tn.getMax());
		}
	}

	private class AbstractSubsumerInstanceCreator extends InstanceCreator {

		void setSlotValues() {

			setISlotValues(fa, sab, fb);
			setISlotValues(fa, sac, createIDisjunction(fcx, fcy));
			setISlotValues(fb, sbe, createCDisjunction(tex, tey));
			setISlotValues(fb, sbn, tn.asINumber());
		}
	}

	public void setTypesPrefix(String value) {

		typesPrefix = value;
	}

	public void setDynamicSlotInsertion(boolean value) {

		if (value) {

			new DynamicSlotInsertionReasoner();
		}
	}

	public void setFunction(IFrameFunction value) {

		function = value;
	}

	public IFrame getBasic() {

		return new BasicInstanceCreator().get();
	}

	public IFrame getSubsumer() {

		return new SubsumerInstanceCreator().get();
	}

	public IFrame getAbstractSubsumer() {

		return new AbstractSubsumerInstanceCreator().get();
	}

	TestInstances(FramesModelTest modelTest) {

		this.modelTest = modelTest;

		ta = createCFrame("A");
		tb = createCFrame("B");
		tc = createCFrame("C");
		td = createCFrame("D");
		te = createCFrame("E");

		tcx = createCFrame("CX");
		tcy = createCFrame("CY");
		tex = createCFrame("EX");
		tey = createCFrame("EY");

		tn = CNumber.range(1, 10);

		sab = createCSlot(ta, "sab", tb);
		sac = createCSlot(ta, "sac", tc);

		sbd = createCSlot(tb, "sbd", td);
		sbe = createCSlot(tb, "sbe", te.getType());
		sbn = createCSlot(tb, "sbn", tn);

		addSuperFrame(tcx, tc);
		addSuperFrame(tcy, tc);

		addSuperFrame(tex, te);
		addSuperFrame(tey, te);
	}

	private CFrame createCFrame(String name) {

		return modelTest.createCFrame(typesPrefix + name);
	}

	private CSlot createCSlot(String name, CValue<?> valueType) {

		return modelTest.createCSlot(name, valueType);
	}

	private CSlot createCSlot(CFrame container, String name, CValue<?> valueType) {

		return modelTest.createCSlot(container, name, valueType);
	}

	private IFrame createIFrame(CFrame type) {

		return instantiateCFrame(type, function);
	}

	private void setISlotValues(IFrame container, CSlot type, IValue... values) {

		setISlotValues(getISlot(container, type.getIdentity()), values);
	}
}
