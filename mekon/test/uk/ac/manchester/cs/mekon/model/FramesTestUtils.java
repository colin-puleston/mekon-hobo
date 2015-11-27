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

import uk.ac.manchester.cs.mekon.MekonTestUtils;

/**
 * @author Colin Puleston
 */
public class FramesTestUtils extends MekonTestUtils {

	public void addSuperFrame(CFrame sub, CFrame sup) {

		sub.asAtomicFrame().addSuper(sup.asAtomicFrame());
	}

	public CFrame createCDisjunction(CFrame... disjuncts) {

		return CFrame.resolveDisjunction(Arrays.asList(disjuncts));
	}

	public IFrame instantiateCFrame(CFrame frame) {

		return instantiateCFrame(frame, IFrameFunction.ASSERTION);
	}

	public IFrame instantiateCFrame(CFrame frame, IFrameFunction function) {

		IFrame iFrame = new IFrame(frame, function);

		for (CSlot slot : frame.getSlots().asList()) {

			iFrame.addSlotInternal(slot);
		}

		return iFrame;
	}

	public IFrame createIDisjunction(IFrame... disjuncts) {

		return IFrame.createDisjunction(Arrays.asList(disjuncts));
	}

	public void setIFrameMappedObject(IFrame frame, Object mappedObject) {

		frame.setMappedObject(mappedObject);
	}

	public CIdentity createIdentity(String name) {

		return new CIdentity(name, name);
	}

	public ISlot getISlot(IFrame container, CIdentity slotId) {

		return container.getSlots().get(slotId);
	}

	public void setISlotValues(ISlot slot, IValue... values) {

		slot.getValuesEditor().update(Arrays.asList(values));
	}

	public List<CValue<?>> getValueTypes(List<IValue> values) {

		List<CValue<?>> types = new ArrayList<CValue<?>>();

		for (IValue value : values) {

			types.add(value.getType());
		}

		return types;
	}
}
