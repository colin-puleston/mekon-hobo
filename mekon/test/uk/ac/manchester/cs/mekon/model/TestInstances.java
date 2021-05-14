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

	private TestCModel model;

	private TestCFrames serverFrameTypes;
	private TestCSlots serverSlotTypes;

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

		private boolean slotInserted = true;

		public Set<IUpdateOp> reinitialise(IFrame frame, IEditor iEditor, Set<IUpdateOp> ops) {

			return Collections.<IUpdateOp>emptySet();
		}

		public Set<IUpdateOp> update(IFrame frame, IEditor iEditor, Set<IUpdateOp> ops) {

			ISlots slots = frame.getSlots();

			if (!slotInserted && readyForSlotInsertion(slots)) {

				List<ISlot> startSlots = slots.asList();

				slots.clear();
				addInsertSlotAndValue(frame);
				slots.addAll(startSlots);

				slotInserted = true;
			}

			return Collections.<IUpdateOp>emptySet();
		}

		DynamicSlotInsertionReasoner() {

			valueType = createFrameType("Insert-value");
			slotType = serverSlotTypes.create("insert-slot", valueType);

			ta.asAtomicFrame().setIReasoner(this);
		}

		private boolean readyForSlotInsertion(ISlots slots) {

			return !slots.isEmpty() && allValuesSet(slots);
		}

		private boolean allValuesSet(ISlots slots) {

			for (ISlot slot : slots.asList()) {

				if (slot.getValues().isEmpty()) {

					return false;
				}
			}

			return true;
		}

		private void addInsertSlotAndValue(IFrame frame) {

			ISlot slot = frame.createEditor().addSlot(slotType);

			slot.getValuesEditor().add(createFrame(valueType));
		}
	}

	private abstract class InstanceCreator {

		final IFrame fa = createFrame(ta);
		final IFrame fb = createFrame(tb);
		final IFrame fc = createFrame(tc);
		final IFrame fd = createFrame(td);

		final IFrame fcx = createFrame(tcx);
		final IFrame fcy = createFrame(tcy);

		final IFrame rfbx = createReferenceFrame(tb, "ref-bx");
		final IFrame rfby = createReferenceFrame(tb, "ref-by");

		IFrame get() {

			initialise();

			return fa;
		}

		abstract void initialise();
	}

	private class BasicInstanceCreator extends InstanceCreator {

		void initialise() {

			setSlotValues(fa, sab, fb, rfbx, rfby);
			setSlotValues(fa, sac, fcx);
			setSlotValues(fb, sbd, fd);
			setSlotValues(fb, sbe, tex, tey);
			setSlotValues(fb, sbn, tn.getMax());
		}
	}

	private class SubsumerInstanceCreator extends InstanceCreator {

		void initialise() {

			setSlotValues(fa, sab, fb, rfbx);
			setSlotValues(fb, sbd, fd);
			setSlotValues(fb, sbe, te);
			setSlotValues(fb, sbn, tn.getMax());
		}
	}

	private class AbstractSubsumerInstanceCreator extends InstanceCreator {

		void initialise() {

			setSlotValues(fa, sab, fb, rfbx);
			setSlotValues(fa, sac, createDisjunction(fcx, fcy));
			setSlotValues(fb, sbe, createDisjunctionType(tex, tey));
			setSlotValues(fb, sbn, tn.asINumber());
		}
	}

	public TestInstances(TestCModel model) {

		this.model = model;

		serverFrameTypes = model.serverCFrames;
		serverSlotTypes = serverFrameTypes.repeatTypesSlots;

		ta = createFrameType("A");
		tb = createFrameType("B");
		tc = createFrameType("C");
		td = createFrameType("D");
		te = createFrameType("E");

		tcx = createFrameType("CX");
		tcy = createFrameType("CY");
		tex = createFrameType("EX");
		tey = createFrameType("EY");

		tn = CNumberFactory.range(1, 10);

		sab = serverSlotTypes.create(ta, "sab", tb);
		sac = serverSlotTypes.create(ta, "sac", tc);

		sbd = serverSlotTypes.create(tb, "sbd", td);
		sbe = serverSlotTypes.create(tb, "sbe", te.getType());
		sbn = serverSlotTypes.create(tb, "sbn", tn);

		addSuperFrameType(tcx, tc);
		addSuperFrameType(tcy, tc);

		addSuperFrameType(tex, te);
		addSuperFrameType(tey, te);
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

	private CFrame createFrameType(String name) {

		return serverFrameTypes.create(typesPrefix + name);
	}

	private CFrame createDisjunctionType(CFrame... disjuncts) {

		return FramesTestUtils.createCDisjunction(disjuncts);
	}

	private void addSuperFrameType(CFrame sub, CFrame sup) {

		FramesTestUtils.addSuperFrame(sub, sup);
	}

	private IFrame createFrame(CFrame serverType) {

		return FramesTestUtils.createIFrame(toClientType(serverType), function);
	}

	private IFrame createReferenceFrame(CFrame serverType, String ref) {

		return new IReference(
						toClientType(serverType),
						new CIdentity(ref, ref),
						function,
						false);
	}

	private IFrame createDisjunction(IFrame... disjuncts) {

		return FramesTestUtils.createIDisjunction(disjuncts);
	}

	private void setSlotValues(
					IFrame container,
					CSlot serverType,
					IValue... serverValues) {

		ISlot slot = container.getSlots().get(serverType.getIdentity());
		ISlotValuesEditor valuesEd = slot.getValuesEditor();

		for (IValue serverValue : serverValues) {

			valuesEd.add(toClientValue(serverValue));
		}
	}

	private IValue toClientValue(IValue serverValue) {

		if (serverValue instanceof CFrame) {

			return toClientType((CFrame)serverValue);
		}

		return serverValue;
	}

	private CFrame toClientType(CFrame serverType) {

		List<CFrame> clientDisjuncts = new ArrayList<CFrame>();

		for (CFrame serverDisjunct : serverType.asDisjuncts()) {

			clientDisjuncts.add(atomicToClientType(serverDisjunct));
		}

		return CFrame.resolveDisjunction(clientDisjuncts);
	}

	private CFrame atomicToClientType(CFrame serverType) {

		if (model.remoteModel()) {

			return model.getClientCFrames().get(serverType.getIdentity());
		}

		return serverType;
	}
}
