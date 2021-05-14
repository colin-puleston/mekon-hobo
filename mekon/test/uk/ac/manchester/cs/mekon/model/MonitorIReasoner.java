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
class MonitorIReasoner extends IReasonerDefault {

	List<IFrame> initialiseds = new ArrayList<IFrame>();
	List<IFrame> updateds = new ArrayList<IFrame>();

	public void initialise(IFrame frame, IEditor iEditor, boolean initSlotValues) {

		super.initialise(frame, iEditor, initSlotValues);

		initialiseds.add(frame);
	}

	public Set<IUpdateOp> update( IFrame frame, IEditor iEditor, Set<IUpdateOp> ops) {

		updateds.add(frame);

		return Collections.<IUpdateOp>emptySet();
	}

	void resetRegisters() {

		initialiseds.clear();
		updateds.clear();
	}

	List<IFrame> getInitialiseds() {

		return initialiseds;
	}

	List<IFrame> getUpdateds() {

		return updateds;
	}
}
