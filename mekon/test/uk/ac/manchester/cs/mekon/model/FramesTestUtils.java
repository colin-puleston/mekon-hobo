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

/**
 * @author Colin Puleston
 */
public class FramesTestUtils {

	static public void addSuperFrame(CFrame sub, CFrame sup) {

		sub.asAtomicFrame().addSuper(sup.asAtomicFrame());
	}

	static public CFrame createCDisjunction(CFrame... disjuncts) {

		return CFrame.resolveDisjunction(Arrays.asList(disjuncts));
	}

	static public IFrame createIFrame(CFrame frame) {

		return createIFrame(frame, IFrameFunction.ASSERTION);
	}

	static public IFrame createIFrame(CFrame frame, IFrameFunction function) {

		IFrame iFrame = new IAtomicFrame(frame, function, false);

		iFrame.completeInitialInstantiation();

		for (CSlot slot : frame.getSlots().asList()) {

			iFrame.addSlotInternal(slot);
		}

		return iFrame;
	}

	static public IFrame createIDisjunction(IFrame... disjuncts) {

		return IFrame.createDisjunction(Arrays.asList(disjuncts));
	}

	static public void setIFrameMappedObject(IFrame frame, Object mappedObject) {

		frame.setMappedObject(mappedObject);
	}
}
