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

import uk.ac.manchester.cs.mekon.model.motor.*;

/**
 * @author Colin Puleston
 */
public class TestInstances {

	private TestCFrames frameTypes;
	private TestCSlots slotTypes;

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

		public Set<IUpdateOp> reinitialiseFrame(
								IEditor iEditor,
								IFrame frame,
								Set<IUpdateOp> ops) {

			return Collections.<IUpdateOp>emptySet();
		}

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

			valueType = createFrameType("Insert-value");
			slotType = slotTypes.create("insert-slot", valueType);

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

		final IFrame fa = createFrame(ta);
		final IFrame fb = createFrame(tb);
		final IFrame fc = createFrame(tc);
		final IFrame fd = createFrame(td);

		final IFrame fcx = createFrame(tcx);
		final IFrame fcy = createFrame(tcy);

		IFrame get() {

			initialise();

			return fa;
		}

		abstract void initialise();
	}

	private class BasicInstanceCreator extends InstanceCreator {

		void initialise() {

			setSlotValues(fa, sab, fb);
			setSlotValues(fa, sac, fcx);
			setSlotValues(fb, sbd, fd);
			setSlotValues(fb, sbe, tex, tey);
			setSlotValues(fb, sbn, tn.getMax());
		}
	}

	private class SubsumerInstanceCreator extends InstanceCreator {

		void initialise() {

			setSlotValues(fa, sab, fb);
			setSlotValues(fb, sbd, fd);
			setSlotValues(fb, sbe, te);
			setSlotValues(fb, sbn, tn.getMax());
		}
	}

	private class AbstractSubsumerInstanceCreator extends InstanceCreator {

		void initialise() {

			setSlotValues(fa, sab, fb);
			setSlotValues(fa, sac, createDisjunction(fcx, fcy));
			setSlotValues(fb, sbe, createDisjunctionType(tex, tey));
			setSlotValues(fb, sbn, tn.asINumber());
		}
	}

	public void setTypesPrefix(String value) {

		typesPrefix = value;
	}

	public void setDynamicSlotInsertion() {

		new DynamicSlotInsertionReasoner();
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

	TestInstances(TestCFrames frameTypes) {

		this.frameTypes = frameTypes;

		slotTypes = frameTypes.repeatTypesSlots;

		ta = createFrameType("A");
		tb = createFrameType("B");
		tc = createFrameType("C");
		td = createFrameType("D");
		te = createFrameType("E");

		tcx = createFrameType("CX");
		tcy = createFrameType("CY");
		tex = createFrameType("EX");
		tey = createFrameType("EY");

		tn = CNumber.range(1, 10);

		sab = slotTypes.create(ta, "sab", tb);
		sac = slotTypes.create(ta, "sac", tc);

		sbd = slotTypes.create(tb, "sbd", td);
		sbe = slotTypes.create(tb, "sbe", te.getType());
		sbn = slotTypes.create(tb, "sbn", tn);

		addSuperFrameType(tcx, tc);
		addSuperFrameType(tcy, tc);

		addSuperFrameType(tex, te);
		addSuperFrameType(tey, te);
	}

	private CFrame createFrameType(String name) {

		return frameTypes.create(typesPrefix + name);
	}

	private CFrame createDisjunctionType(CFrame... disjuncts) {

		return FramesTestUtils.createCDisjunction(disjuncts);
	}

	private void addSuperFrameType(CFrame sub, CFrame sup) {

		FramesTestUtils.addSuperFrame(sub, sup);
	}

	private IFrame createDisjunction(IFrame... disjuncts) {

		return FramesTestUtils.createIDisjunction(disjuncts);
	}

	private IFrame createFrame(CFrame type) {

		return FramesTestUtils.instantiateCFrame(type, function);
	}

	private void setSlotValues(IFrame container, CSlot type, IValue... values) {

		ISlot slot = container.getSlots().get(type.getIdentity());

		slot.getValuesEditor().update(Arrays.asList(values));
	}
}
